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
// File:	BookLocatorService.java
// Created:	Mon Dec 27 18:19:58 GMT 2004
//
//////////////////////////////////////////////////////////////////////

package com.townleyenterprises.libro.backend;

import com.townleyenterprises.persistence.ConnectionFactory;
import com.townleyenterprises.persistence.QueryAdapter;
import com.townleyenterprises.persistence.QueryHandler;
import com.townleyenterprises.trace.BasicTrace;

/**
 * This class is a book location service layer implementation which
 * provides a metered, ordered set of results of books in the system.
 *
 * @version $Id: BookLocatorService.java,v 1.1 2004/12/28 11:11:30 atownley Exp $
 * @author <a href="mailto:atownley@users.sourceforge.net">Andrew S. Townley</a>
 */

public class BookLocatorService
{
	/** our trace instance */
	private static final BasicTrace		_trace = new BasicTrace("BookLocatorService", 0);
}
