//////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2004, Andrew S. Townley
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
// File:	Strings.java
// Created:	Thu Jul 29 16:48:47 IST 2004
//
//////////////////////////////////////////////////////////////////////

package com.townleyenterprises.libro;

import com.townleyenterprises.common.ResourceLoader;
import com.townleyenterprises.common.ResourceManager;
import com.townleyenterprises.common.Version;

/**
 * This class is used to track all of the localized strings used for
 * messages in this package.
 *
 * @version $Id: Strings.java,v 1.1 2004/12/28 11:11:30 atownley Exp $
 * @author <a href="mailto:adz1092@yahoo.com">Andrew S. Townley</a>
 * @since 3.0
 */

final class Strings
{
	static String get(String key)
	{
		String rc = _resources.getString(key);
		if(rc == null)
			rc = key;

		return rc;
	}

	static String format(String key, Object[] args)
	{
		String rc = _resources.format(key, args);
		if(rc == null)
			rc = key;

		return rc;
	}

	private static final ResourceManager _resources = new ResourceManager();
	
	static
	{
		_resources.manage(new ResourceLoader(Strings.class));
		_resources.manage(new ResourceLoader(Version.class));
	}
}
