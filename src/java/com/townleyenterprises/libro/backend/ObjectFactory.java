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
// File:	ObjectFactory.java
// Created:	Sun Jan  2 17:17:12 GMT 2005
//
//////////////////////////////////////////////////////////////////////

package com.townleyenterprises.libro.backend;

import java.sql.SQLException;
import net.sourceforge.jaxor.SystemSQLException;

import com.townleyenterprises.config.ConfigSupplier;
import com.townleyenterprises.trace.BasicTrace;

import com.townleyenterprises.libro.Libro;

/**
 * This static class is responsible for encapsulating all of the
 * initialization required to create objects of the types supported in
 * the system.  It provides a set of helper methods that call the
 * appropriate sequence of methods on the various objec Finder
 * classes.  This class also manages the single counter instance which
 * is shared across the application so that consistent unique ID
 * values are generated.
 * <p>
 * For consistency with the other Jaxor-related API calls, all of the
 * exceptions will be wrapped in a Jaxor SystemSQLException (I don't
 * really like that part very much, but there's nothing I can do about
 * it.  The benifits of Jaxor outweigh this minor PITA).
 * </p>
 * <p>
 * This idiom is borrowed from the Jaxor domain example, and it
 * provides a reasonable place to centralize this functionality.
 * </p>
 * @version $Id: ObjectFactory.java,v 1.2 2005/01/09 11:16:33 atownley Exp $
 * @author <a href="mailto:atownley@users.sourceforge.net">Andrew S. Townley</a>
 */

public final class ObjectFactory
{
	/**
	 * This method is used to create a new Publisher instance
	 * based on the next available primary key.
	 *
	 * @param name the name of the publisher
	 * @return the instance
	 */

	public static PublisherEntity createPublisher(String name)
	{
		final String[] pn = new String[] { "name" };
		_trace.methodStart("createPublisher", pn,
				new Object[] { name });

		try
		{
			Long id = new Long(_counter.getNextId("publisher"));
			PublisherEntity pe = PublisherFinder.newInstance(id);
			pe.setName(name);

			Libro.logInfo("log.fCreatedPublisher",
					new Object[] { name, id });

			return (PublisherEntity)_trace.methodReturn(pe);
		}
		catch(SQLException e)
		{
			// use of null here because of missing(?)
			// constructor that makes sense since the
			// second parameter is ignored
			
			SystemSQLException se = new SystemSQLException(e, null);
			throw (SystemSQLException)_trace.methodThrow(se, true);
		}
		finally
		{
			_trace.methodExit();
		}
	}

	/**
	 * This method is used to create a new Author instance
	 * based on the next available primary key.
	 *
	 * @param lname the last name of the author
	 * @param fname the first name of the author
	 * @return the instance
	 */

	public static AuthorEntity createAuthor(String lname, String fname)
	{
		final String[] pn = new String[] { "lname",  "fname" };
		_trace.methodStart("createAuthor", pn, 
				new Object[] { lname, fname });

		try
		{
			Long id = new Long(_counter.getNextId("author"));
			AuthorEntity ae = AuthorFinder.newInstance(id);
			ae.setLastName(lname);
			ae.setFirstName(fname);

			Libro.logInfo("log.fCreatedAuthor",
				new Object[] { lname, fname, id });

			return (AuthorEntity)_trace.methodReturn(ae);
		}
		catch(SQLException e)
		{
			SystemSQLException se = new SystemSQLException(e, null);
			throw (SystemSQLException)_trace.methodThrow(se, true);
		}
		finally
		{
			_trace.methodExit();
		}
	}

	/**
	 * This method is used to create a new Book instance
	 * based on the next available primary key and the appropriate
	 * references to the author and the publisher entities.
	 *
	 * @param title the title of the book
	 * @param publisher the publisher of the book
	 * @param author the author of the book
	 * @return the instance
	 */

	public static BookEntity createBook(String title,
				PublisherEntity publisher,
				AuthorEntity author)
	{
		final String[] pn = new String[] { "title", "publisher",  "author" };

		_trace.methodStart("createBook", pn, 
				new Object[] { title, publisher, author });

		try
		{
			Long id = new Long(_counter.getNextId("book"));
			BookEntity be = BookFinder.newInstance(id);

			be.setPublisherEntity(publisher);
			be.setAuthorEntity(author);
			be.setTitle(title);
			
			Libro.logInfo("log.fCreatedBook",
				new Object[] { title, id });

			return (BookEntity)_trace.methodReturn(be);
		}
		catch(SQLException e)
		{
			SystemSQLException se = new SystemSQLException(e, null);
			throw (SystemSQLException)_trace.methodThrow(se, true);
		}
		finally
		{
			_trace.methodExit();
		}
	}

	/**
	 * This method is used to create a new Book instance
	 * based on the next available primary key and the appropriate
	 * values for the author_id and the publisher_id.
	 * <p>
	 * This method should be used if the author and publisher
	 * entity instances are not available.  Normally, the
	 * alternate form of the method should be used.
	 * </p>
	 *
	 * @param title the title of the book
	 * @param publisher_id the ID for the publisher of the book
	 * @param author_id the ID for the author of the book
	 * @return the instance
	 */

	public static BookEntity createBook(String title,
				long publisher_id, long author_id)
	{
		final String[] pn = new String[] { "title", "publisher_id",  "author_id" };

		_trace.methodStart("createBook", pn, 
				new Object[] { title, 
					new Long(publisher_id),
					new Long(author_id) });

		try
		{
			Long id = new Long(_counter.getNextId("book"));
			BookEntity be = BookFinder.newInstance(id);

			// hook up the author and publisher by
			// validating the supplied ID values.
			be.setPublisherEntity(
				PublisherFinder.selectByPrimaryKey(
					new Long(publisher_id)));
			be.setAuthorEntity(
				AuthorFinder.selectByPrimaryKey(
					new Long(author_id)));

			be.setTitle(title);

			Libro.logInfo("log.fCreatedBook",
				new Object[] { title, id });

			return (BookEntity)_trace.methodReturn(be);
		}
		catch(SQLException e)
		{
			SystemSQLException se = new SystemSQLException(e, null);
			throw (SystemSQLException)_trace.methodThrow(se, true);
		}
		finally
		{
			_trace.methodExit();
		}
	}

	/**
	 * This is a unit-test support method to retrieve a reference
	 * to the shared counter.  We should only have ONE counter
	 * instance per application.
	 */

	static Counter getCounter()
	{
		return _counter;
	}

	/** keep track of our counter instance */
	private static final Counter	_counter;
	
	/** our trace instance */
	private static final BasicTrace	_trace = new BasicTrace("ObjectFactory");
	
	/**
	 * The purpose of this static block is to initialize the value
	 * of the counter cache size based on the configuration
	 * parameter.
	 */

	static
	{
		_trace.methodStart("<static>");

		try
		{
			ConfigSupplier cs = Libro.getConfig();
			_counter = new Counter(Long.parseLong(cs.get("backend.counter.cache.size")));
			_trace.methodReturn();
		}
		finally
		{
			_trace.methodExit();
		}
	}
}
