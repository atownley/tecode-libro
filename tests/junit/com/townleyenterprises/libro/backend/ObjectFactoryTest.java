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
// File:	ObjectFactoryTest.java
// Created:	Sun Jan  2 22:49:37 GMT 2005
//
//////////////////////////////////////////////////////////////////////

package com.townleyenterprises.libro.backend;

import java.util.Date;
import java.sql.Timestamp;

import net.sourceforge.jaxor.JaxorSession;

import junit.framework.TestCase;

import com.townleyenterprises.config.ConfigSupplier;
import com.townleyenterprises.libro.Libro;

/**
 * These are the unit tests for the ObjectFactory class.  The specific
 * tests should be obvious from the names of the methods.
 *
 * @version $Id: ObjectFactoryTest.java,v 1.1 2005/01/09 11:11:21 atownley Exp $
 * @author <a href="mailto:atownley@users.sourceforge.net">Andrew S. Townley</a>
 */

public final class ObjectFactoryTest extends TestCase
{
	public void setUp() throws Exception
	{
		// make sure this is called!!!
		Libro.init();

		ConfigSupplier cs = Libro.getConfig();

		_counter = ObjectFactory.getCounter();
		
		JaxorSession.begin(Libro.getJaxorTransaction());
	}

	public void tearDown() throws Exception
	{
		JaxorSession.end();

		Libro.shutdown();
	}

	public void testCreatePublisher() throws Exception
	{
		final long ival = _counter.peekNextId(PUBLISHER);
		final String name = "Flintstone Books";

		JaxorSession.begin(Libro.getJaxorTransaction());
		PublisherEntity pe = ObjectFactory.createPublisher(name);
		JaxorSession.commit();
		assertEquals(new Long(ival), pe.getId());

		// now, can we find it?
		pe = PublisherFinder.selectByPrimaryKey(new Long(ival));
		assertEquals(name, pe.getName());

		_publisher_id = ival;
	}

	public void testCreateAuthor() throws Exception
	{
		final long ival = _counter.peekNextId(AUTHOR);
		final String lname = "Rubble";
		final String fname = "Barney";

		JaxorSession.begin(Libro.getJaxorTransaction());
		AuthorEntity pe = ObjectFactory.createAuthor(lname, fname);
		JaxorSession.commit();
		assertEquals(new Long(ival), pe.getId());

		// now, can we find it?
		pe = AuthorFinder.selectByPrimaryKey(new Long(ival));
		assertEquals(lname, pe.getLastName());
		assertEquals(fname, pe.getFirstName());

		_author_id = ival;
	}

	public void testCreateBook() throws Exception
	{
		final long ival = _counter.peekNextId(BOOK);
		final String title = "Life with Fred";

		JaxorSession.begin(Libro.getJaxorTransaction());
		BookEntity pe = ObjectFactory.createBook(title,
				_publisher_id, _author_id);

		pe.setPublishDate(new Timestamp(new Date().getTime()));
		pe.setISBN("123456899");
		JaxorSession.commit();
		assertEquals(new Long(ival), pe.getId());

		// now, can we find it?
		pe = BookFinder.selectByPrimaryKey(new Long(ival));
		assertEquals(title, pe.getTitle());
	}

	public ObjectFactoryTest(String name)
	{
		super(name);
	}

	/** make sure we don't have typos */
	static final String		PUBLISHER = "publisher";
	
	static final String		AUTHOR = "author";
	
	static final String		BOOK = "book";

	/** the counter */
	static Counter			_counter;

	/** the publisher id */
	static long			_publisher_id;

	/** the author id */
	static long			_author_id;
}
