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
// File:	Counter.java
// Created:	Tue Dec 28 18:23:19 GMT 2004
//
//////////////////////////////////////////////////////////////////////

package com.townleyenterprises.libro.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.townleyenterprises.persistence.ConnectionFactory;
import com.townleyenterprises.persistence.QueryAdapter;
import com.townleyenterprises.persistence.QueryHandler;
import com.townleyenterprises.persistence.QueryEvent;
import com.townleyenterprises.trace.BasicTrace;

import com.townleyenterprises.libro.Libro;

/**
 * This class provides the next valid ID for the requested object
 * type.  For efficiency, it allocates IDs in blocks based on the
 * value of the parameter to the constructor.  The default constructor
 * provides blocks of 10.
 * <p>
 * There is a disadvantage here in that the actual IDs of objects in
 * the database may have gaps but the offset in performance based on
 * past experience indicates that this is really a non-issue for a
 * production application.
 * </p>
 * <p>
 * The concept here was originally shown to me by my former colleague
 * Phil Hourihane while we were working at Meridian.  I had originally
 * implemented this idiom to go to the database each time, but for a
 * large number of inserts, it turned out to be atrociously expensive
 * when using PostgreSQL 7.[34].  However, any bugs in this
 * implementation are mine alone... ;) </p>
 *
 * @version $Id: Counter.java,v 1.1 2005/01/02 22:00:58 atownley Exp $
 * @author <a href="mailto:atownley@users.sourceforge.net">Andrew S. Townley</a>
 */

public class Counter
{
	/**
	 * This constructor uses the default value of 50 for the
	 * number of ids to allocate from the counter table.
	 */

	public Counter()
	{
		this(10);
	}

	/**
	 * This version of the constructor allows the caller to
	 * specify the number of cached IDs.
	 *
	 * @param count the number of IDs
	 */

	public Counter(long count)
	{
		final String[] pname = { "count" };
		_trace.methodStart("Counter", pname, 
				new Object[] { new Long(count) });
	
		try
		{
			_count = count;
			_trace.methodReturn();
		}
		finally
		{
			_trace.methodExit();
		}
	}

	/**
	 * This method is used to get the next ID for the given object
	 * type.  If the object doesn't exist, an exception is raised.
	 *
	 * @param name the name of the object (table)
	 * @return the next ID
	 * @exception SQLException
	 * 	if there was an error in the database server
	 */

	public synchronized long getNextId(String name) throws SQLException
	{
		final String[] pn = { "name" };
		Object[] params = new Object[] { name };
		_trace.methodStart("getNextId", pn, params);

		try
		{
			CacheCounter cc = getCounter(name);
			return _trace.methodReturn(cc.getNextId());
		}
		finally
		{
			_trace.methodExit();
		}
	}

	/**
	 * This method exists to allow the unit tests to determine the
	 * value which will be assigned for a given object.  This type
	 * of access is guaranteed by making it available to the
	 * package only.  It should not be used by any client code
	 * except the JUnit tests.
	 *
	 * @return the next value which will be assigned to the
	 * 	counter for the given table
	 * @exception SQLException
	 * 	if there was an error in the database server
	 */

	synchronized long peekNextId(String name) throws SQLException
	{
		final String[] pn = { "name" };
		Object[] params = new Object[] { name };
		_trace.methodStart("peekNextId", pn, params);

		try
		{
			// NOTE:  generally, it isn't a good idea to
			// allow objects to be added to the map here,
			// but in this case it is required because the
			// unit tests will need to know the value
			// before the first time the counter has been
			// used.  If this initialization wasn't
			// performed in both places, the unit tests
			// wouldn't work correctly.

			CacheCounter cc = getCounter(name);
			return _trace.methodReturn(cc.peekNextId());
		}
		finally
		{
			_trace.methodExit();
		}
	}

	/**
	 * This method guarantees a reference to a CacheCounter exists
	 * for a given name and then returns it.
	 *
	 * @param name the name of the counter
	 * @return the counter instance
	 */

	private CacheCounter getCounter(String name)
	{
		CacheCounter cc = (CacheCounter)_map.get(name);
		if(cc == null)
		{
			cc = new CacheCounter(name, _count);
			_map.put(name, cc);
		}

		return cc;
	}

	/** the number of ids to cache */
	private final long			_count;

	/** the map of counters */
	private final Map			_map = new HashMap();

	/** our trace instance */
	private static final BasicTrace		_trace = new BasicTrace("Counter", 0);

	/**
	 * This class is used to retrieve our current object counter
	 * value.
	 */

	private static class Result extends QueryAdapter
	{
		public void nextRow(QueryEvent qe) throws SQLException
		{
			ResultSet rs = qe.getResultSet();
			count = rs.getLong(1);
		}

		long count;
	}

	/**
	 * This class is used to manage the counter for each specific
	 * object.
	 */

	private static class CacheCounter
	{
		public CacheCounter(String name, long size)
		{
			_name = name;
			_size = size;

			// ensure cache refresh for first counter
			_count = size + 1;

			_params = new Object[] { name };
		}

		public long getNextId() throws SQLException
		{
			if(++_count > _size)
				refreshCache();
			
			return ++_nextid;
		}

		public long peekNextId() throws SQLException
		{
			// again, allow for the peek to be the first
			// operation

			if((_count  + 1) > _size)
				refreshCache();

			return _nextid + 1;
		}

		/**
		 * This method is responsible for actually fetching
		 * the new cache value from the database.  Once this
		 * has been done, the internal counter is reset and it
		 * allocates values based on the initialized
		 * constructor parameters.
		 */

		public void refreshCache() throws SQLException
		{
			_trace.methodStart("CacheCounter.refreshCache");

			try
			{
				ConnectionFactory cf = Libro.getConnectionFactory();
				QueryHandler qh = new QueryHandler(cf);
				Result res = new Result();
				qh.addQueryListener(res);
				
				qh.execute(SQL.get("counter.get"), _params);

				// increment the extra one so that we
				// don't get duplicate values when the
				// cache is updated the second time

				long id = res.count + _size + 1;
				_nextid = res.count;

				// now, take care of the update
				Connection conn = cf.getConnection();
				PreparedStatement ps = conn.prepareStatement(SQL.get("counter.set"));
				ps.setLong(1, id);
				ps.setString(2, _name);
				boolean rc = ps.execute();
				conn.commit();
		
				if(rc != false && ps.getUpdateCount() != 1)
				{
					RuntimeException re;
					re = new RuntimeException(Strings.format("fInvalidUpdateCount", _params));
					throw (RuntimeException)_trace.methodThrow(re, true);
				}

				_trace.tprintln(5, "stored new counter value for table " + _name + ":  " + id);
				_count = 0;
				_trace.methodReturn();
			}
			finally
			{
				_trace.methodExit();
			}
		}

		/** track our query parameters */
		private final Object[]	_params;

		/** track the cache size */
		private final long	_size;

		/** track the counter name */
		private final String	_name;

		/** track the number of IDs given out */
		private long		_count = 0;

		/** track the next ID to give out */
		private long		_nextid = 0;
	}
}
