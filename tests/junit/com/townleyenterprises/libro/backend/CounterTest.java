//////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2005, Andrew S. Townley
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
// File:	CounterTest.java
// Created:	Sun Jan  2 18:15:07 GMT 2005
//
//////////////////////////////////////////////////////////////////////

package com.townleyenterprises.libro.backend;

import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.TestCase;

import com.townleyenterprises.persistence.ConnectionFactory;
import com.townleyenterprises.persistence.QueryAdapter;
import com.townleyenterprises.persistence.QueryHandler;
import com.townleyenterprises.persistence.QueryEvent;

import com.townleyenterprises.libro.Libro;

/**
 * These are the unit tests for the Counter class.  The specific tests
 * should be obvious from the names of the methods.
 *
 * @version $Id: CounterTest.java,v 1.2 2005/01/09 11:21:19 atownley Exp $
 * @author <a href="mailto:atownley@users.sourceforge.net">Andrew S. Townley</a>
 */

public final class CounterTest extends TestCase
{
	public void setUp() throws Exception
	{
		// make sure this is called!!!
		Libro.init();

		_csize = Long.parseLong(Libro.getConfig().get("backend.counter.cache.size"));
		_counter = new Counter(_csize);
		_cfactory = Libro.getConnectionFactory();
		_rez = new Result();
	}

	public void tearDown() throws Exception
	{
		Libro.shutdown();
	}

	public void testInitialCounterValue() throws Exception
	{
		// first get the current database counter value
		QueryHandler qh = new QueryHandler(_cfactory);
		qh.addQueryListener(_rez);
		qh.execute(SQL.get("counter.get"), new Object[] { TEST_TABLE });

		long ival = _rez.count;

		assertEquals(ival + 1, _counter.getNextId(TEST_TABLE));
	}

	public void testStoredCounterValue() throws Exception
	{
		// first get the current database counter value
		QueryHandler qh = new QueryHandler(_cfactory);
		qh.addQueryListener(_rez);
		qh.execute(SQL.get("counter.get"), new Object[] { TEST_TABLE });

		long ival = _rez.count;
		// increment the counter
		_counter.getNextId(TEST_TABLE);

		// see what was written
		qh.execute(SQL.get("counter.get"), new Object[] { TEST_TABLE });
		assertEquals(ival + _csize + 1, _rez.count);
	}

	public void testUnitTestPeek() throws Exception
	{
		assertEquals(_counter.peekNextId(TEST_TABLE),
				_counter.getNextId(TEST_TABLE));
	}

	public void testMultiThread() throws Exception
	{
		Thread t1 = new CThread();
		Thread t2 = new CThread();
		Thread t3 = new CThread();

		// first get the current database counter value
		QueryHandler qh = new QueryHandler(_cfactory);
		qh.addQueryListener(_rez);
		qh.execute(SQL.get("counter.get"), new Object[] { TEST_TABLE });

		long ival = _rez.count;

		// start our threads
		t1.start();
		t2.start();
		t3.start();

		// wait for them to finish
		t1.join();
		t2.join();
		t3.join();

		// next counter value should be initial value + 3001
		assertEquals(ival + 3001, _counter.getNextId(TEST_TABLE));
	}

	public CounterTest(String name)
	{
		super(name);
	}

	/** make sure we don't have typos */
	static final String		TEST_TABLE = "counter_test";

	/** the connection factory */
	static ConnectionFactory	_cfactory;

	/** our utility query result retriever */
	static Result			_rez;

	/** the counter for the tests */
	static Counter 			_counter;

	/** track the counter cache size */
	static long 			_csize;
	
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
	 * This class provides our thread test implementation.
	 */

	private static class CThread extends Thread
	{
		public void run()
		{
			try
			{
				for(int i = 0; i < 1000; ++i)
				{
					_counter.getNextId(TEST_TABLE);
					sleep(1);
				}
			}
			catch(Exception e)
			{
				// don't care
			}
		}
	}
}
