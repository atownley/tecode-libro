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
// File:	BookResultDTO.java
// Created:	Mon Dec 27 17:53:00 GMT 2004
//
//////////////////////////////////////////////////////////////////////

package com.townleyenterprises.libro.backend;

import java.util.Date;

/**
 * This class is a read-only DTO representing the main search results
 * for books stored in the system.
 *
 * @version $Id: BookResultDTO.java,v 1.1 2004/12/28 11:11:30 atownley Exp $
 * @author <a href="mailto:atownley@users.sourceforge.net">Andrew S. Townley</a>
 */

public final class BookResultDTO
{
	/**
	 * The constructor initializes the object's data fields
	 * completely.
	 *
	 * @param id the book ID
	 * @param title the book title
	 * @param subtitle the book subtitle
	 * @param afname the author's first name
	 * @param alname the author's last name
	 * @param publisher the publisher's name
	 * @param pubdate the publish date
	 * @param isbn the isbn number of the book
	 */

	public BookResultDTO(Integer id, String title, String subtitle,
			String afname, String alname, String publisher,
			long pubdate, String isbn)
	{
		_id = id;
		_title = title;
		_subtitle = subtitle;
		_afname = afname;
		_alname = alname;
		_publisher = publisher;
		_pubdate = new Date(pubdate);
		_isbn = isbn;
	}

	public Integer getId() { return _id; }

	public String getTitle() { return _title; }

	public String getSubTitle() { return _subtitle; }

	public String getAuthorFirstName() { return _afname; }

	public String getAuthorLastName() { return _alname; }

	public String getPublisher() { return _publisher; }

	public Date getPublishDate() { return _pubdate; }

	public String getISBN() { return _isbn; }

	private final Integer	_id;
	private final String	_title;
	private final String	_subtitle;
	private final String	_afname;
	private final String	_alname;
	private final String	_publisher;
	private final Date	_pubdate;
	private final String	_isbn;
}
