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

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

import net.sourceforge.jaxor.db.ConnectionDecorator;

import com.townleyenterprises.libro.Libro;
import com.townleyenterprises.persistence.PersistenceConfig;
import com.townleyenterprises.trace.BasicTrace;

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
 * @version $Id: LibroConnectionFactory.java,v 1.1 2004/12/28 21:54:31 atownley Exp $
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
				_conn = ConnectionDecorator.createNonClosing(initConnection());
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

	public Connection initConnection()
			throws ClassNotFoundException,
				SQLException
	{
		_trace.methodStart("initConnection");

		try
		{
			PersistenceConfig pc = new PersistenceConfig(
						Libro.getConfig());
			Class.forName(pc.getDriverName());

			// now we try and make the connection
			Connection conn = DriverManager.getConnection(
					pc.getConnectionURL(),
					pc.getUser(),
					pc.getPassword());

			return (Connection)_trace.methodReturn(conn);
		}
		finally
		{
			_trace.methodExit();
		}
	}

	/** maintain a reference to our connection */
	private Connection		_conn = null;

	/** our trace instance */
	private static BasicTrace	_trace = new BasicTrace("LibroConnectionFactory");
}
