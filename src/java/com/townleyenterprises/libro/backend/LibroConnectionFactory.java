//////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2004-2005, Andrew S. Townley
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// 
//     * Redistributions of source code must retain the above
//     copyright notice, this list of conditions and the following
//     disclaimer.
// 
//     * Redistributions in binary form must reproduce the above
//     copyright notice, this list of conditions and the following
//     disclaimer in the documentation and/or other materials provided
//     with the distribution.
// 
//     * Neither the names Andrew Townley and Townley Enterprises,
//     Inc. nor the names of its contributors may be used to endorse
//     or promote products derived from this software without specific
//     prior written permission.  
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
// FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
// COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
// INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
// HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
// STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
// OF THE POSSIBILITY OF SUCH DAMAGE.
//
// File:	LibroConnectionFactory.java
// Created:	Tue Dec 28 11:52:18 GMT 2004
//
//////////////////////////////////////////////////////////////////////

package com.townleyenterprises.libro.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sourceforge.jaxor.db.ConnectionDecorator;

import com.townleyenterprises.common.VersionMismatchException;
import com.townleyenterprises.persistence.PersistenceConfig;
import com.townleyenterprises.trace.BasicTrace;

import com.townleyenterprises.libro.Libro;
import com.townleyenterprises.libro.Version;

/**
 * This class creates and manages a single connection to the database.
 * We aren't using a pooled connection in this case because it isn't
 * necessary for the application, however, with a few minor changes to
 * this class, it would be possible to introduce pooled connections.
 * <p>
 * We could also have opted to provide a connection via Jaxor's
 * <code>net.sourceforge.jaxor.db.SingleConnectionFactory</code>, but
 * I chose not to for this application.
 * </p>
 * <p>
 * <em>IMPORTANT:</em>  Note that while this class is an
 * implementation of the GoF singleton pattern, it does not use the
 * "double checked locking" idiom.  The reasons for this are described
 * in detail in the article <em><a
 * href="http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html">The
 * "Double Checked Locking is Broken" Declaration</a></em>.
 * </p>
 *
 * @version $Id: LibroConnectionFactory.java,v 1.4 2005/01/09 11:15:53 atownley Exp $
 * @author <a href="mailto:atownley@users.sourceforge.net">Andrew S. Townley</a>
 */

public final class LibroConnectionFactory
		implements net.sourceforge.jaxor.api.ConnectionFactory,
			com.townleyenterprises.persistence.ConnectionFactory
{
	/**
	 * This method will return a connection based on the settings
	 * in the application's configuration.  It will do so using
	 * the PersistenceConfig class which allows the database
	 * server to be easily changed by simply changing a single
	 * entry in the configuration file.
	 * <p>
	 * One of the drawbacks of this interface is that it does not
	 * throw any checked exceptions.  If there are problems
	 * connecting to the database, all of the exceptions received
	 * are caught and re-thrown as RuntimeExceptions.
	 * </p>
	 * <p>
	 * Another technique here is to wrap our connection in a
	 * non-closing decorator so that when the JaxorSession.end()
	 * method is called, the connection is actually not closed.
	 * </p>
	 *
	 * @return a valid connection
	 * @exception RuntimeException
	 * 	if there is an error making the connection
	 */

	public synchronized Connection getConnection()
	{
		_trace.methodStart("getConnection");

		try
		{
			if(_conn == null)
			{
				_orig = initConnection();
				_conn = ConnectionDecorator.createNonClosing(_orig);
			}

			return (Connection)_trace.methodReturn(_conn);
		}
		catch(Throwable t)
		{
			// NOTE:  this class does not log the error to
			// the system log.  The caller will be
			// responsible for that operation since they
			// will be responsible for logging all
			// "application" type errors.  I know it's a
			// minor distinction, but it helps to prevent
			// things from happening twice.
			
			RuntimeException re = new RuntimeException(t);
			throw (RuntimeException)_trace.methodThrow(re, true);
		}
		finally
		{
			_trace.methodExit();
		}
	}

	/**
	 * This method is used to terminate the connection factory's
	 * connections.
	 */

	public synchronized void end()
	{
		_trace.methodStart("end");

		try
		{
			_conn = null;
			if(_orig != null)
			{
				_orig.close();
				_orig = null;
			}
			
			Libro.logInfo("log.sConnectionClosed");

			_trace.methodReturn();
		}
		catch(SQLException e)
		{
			Libro.logError("log.sErrorClosingConnection",
					new Object[] { e });
			_trace.tprintln(0, "caught exception trying to close database connection");
			_trace.printStackTrace(0, e);
		}
		finally
		{
			_trace.methodExit();
		}
	}

	/**
	 * This method is used to initialize the database driver and
	 * get the connection.
	 * <p>
	 * We aren't tracing the exceptions generated here primarily
	 * because they will be caught by the caller and traced.
	 * There isn't really a general rule I use for this type of
	 * thing, but if there's a choice, I would be more inclined to
	 * err on the side of more logging than less.
	 * </p>
	 *
	 * @return an initialized Connection
	 * @exception ClassNotFoundException
	 * 	if the class for the driver cannot be located
	 * @exception SQLException
	 * 	if the connection cannot be made
	 */

	private Connection initConnection()
			throws ClassNotFoundException,
				SQLException
	{
		_trace.methodStart("initConnection");

		try
		{
			PersistenceConfig pc = new PersistenceConfig(
						Libro.getConfig());
			Class.forName(pc.getDriverName());

			String curl = pc.getConnectionURL();
			String user = pc.getUser();

			// now we try and make the connection
			Connection conn = DriverManager.getConnection(
					curl, user, pc.getPassword());

			Libro.logInfo("log.fCreatedDatabaseConnection",
				new Object[] { curl, user });

			checkSchemaVersion(conn);
			return (Connection)_trace.methodReturn(conn);
		}
		finally
		{
			_trace.methodExit();
		}
	}

	/**
	 * This method is used to make sure that we are connecting to
	 * a compatible database instance.  If the database is not
	 * compatible, an exception is thrown.
	 * Compatibility is based on the REQUIRED_VERSION string
	 * defined here which should be updated whenever the database
	 * schema is changed.
	 *
	 * @param conn the database connection to use
	 * @exception VersionMismatchException
	 * 	if the database version is not sufficient
	 * @exception SQLException
	 * 	if there is any other database problems
	 */

	private static void checkSchemaVersion(Connection conn)
			throws VersionMismatchException,
				SQLException
	{
		final String[] pn = new String[] { "conn" };
		_trace.methodStart("checkDatabaseVersion", pn,
				new Object[] { conn });

		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{
			String sql = SQL.get("version.get");
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, Version.PROJECT);
			
			rs = stmt.executeQuery();
			if(!rs.next())
			{
				SQLException se = new SQLException(Strings.get("sNoVersionInfo"));
				throw (SQLException)_trace.methodThrow(se, true);
			}

			// process our result
			String ver = rs.getString(1);
			String installed = rs.getString(2);
			String rcsid = rs.getString(3);
			
			// manually do the compare since it isn't
			// relative to our own version.
			if(Version.compare(ver, REQUIRED_VERSION) < 0)
			{
				try
				{
					if(conn != null)
						conn.close();
				}
				catch(SQLException e)
				{
					Libro.logError("log.fUnableToCloseConnection", new Object[] { e });
				}

				VersionMismatchException vme;
				vme = new VersionMismatchException(REQUIRED_VERSION, ver);
				throw (VersionMismatchException)_trace.methodThrow(vme, true);
			}

			// if we get here, everything is kosher
			Libro.logInfo("log.fSchemaVersion",
				new Object[] { ver, installed, rcsid });

			_trace.methodReturn();
		}
		finally
		{
			try
			{
				if(rs != null)
					rs.close();
			}
			catch(SQLException e)
			{
				Libro.logError("log.fUnableToCloseResultSet", new Object[] { e });
			}

			try
			{
				if(stmt != null)
					stmt.close();
			}
			catch(SQLException e)
			{
				Libro.logError("log.fUnableToCloseStatement", new Object[] { e });
			}

			_trace.methodExit();
		}
	}
	 
	/** maintain a reference to our connection */
	private Connection		_conn = null;

	/** maintain a reference to the original connection */
	private Connection		_orig = null;

	/** the minimum required version of the database schema */
	private static final String	REQUIRED_VERSION = "0.1.0 (Build 1)";

	/** our trace instance */
	private static final BasicTrace	_trace = new BasicTrace("LibroConnectionFactory");
}
