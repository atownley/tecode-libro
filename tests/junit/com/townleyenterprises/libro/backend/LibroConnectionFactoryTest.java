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
// File:	LibroConnectionFactoryTest.java
// Created:	Sun Jan  2 22:49:37 GMT 2005
//
//////////////////////////////////////////////////////////////////////

package com.townleyenterprises.libro.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import net.sourceforge.jaxor.JaxorSession;

import junit.framework.TestCase;

import com.townleyenterprises.common.VersionMismatchException;
import com.townleyenterprises.persistence.PersistenceConfig;
import com.townleyenterprises.libro.Libro;
import com.townleyenterprises.libro.Version;

/**
 * These tests exercise the LibroConnectionFactory class.
 *
 * @version $Id: LibroConnectionFactoryTest.java,v 1.1 2005/01/09 11:10:43 atownley Exp $
 * @author <a href="mailto:atownley@users.sourceforge.net">Andrew S. Townley</a>
 */

public final class LibroConnectionFactoryTest extends TestCase
{
	public void setUp() throws Exception
	{
		// make sure this is called!!!
		Libro.init();
	}

	public void tearDown() throws Exception
	{
		Libro.shutdown();
	}

	public void testInvaldSchemaVersion() throws Exception
	{
		final String nversion = "0.0.0 (Build 57)";
		final String sql = "update version_info set version = ? where what = ?";

		System.setProperty("te-common.version.developer-override", "false");

		Connection conn = Libro.getConnectionFactory().getConnection();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, nversion);
		ps.setString(2, Version.PROJECT);
		assertEquals(1, ps.executeUpdate());

		// shut things down
		Libro.shutdown();

		try
		{
			Libro.init();
			Libro.getConnectionFactory().getConnection();
			fail("Version check didn't fail");
		}
		catch(RuntimeException re)
		{
			PersistenceConfig pc = new PersistenceConfig(
						Libro.getConfig());
			conn = DriverManager.getConnection(
					pc.getConnectionURL(),
					pc.getUser(),
					pc.getPassword());

			ps = conn.prepareStatement(sql);
			ps.setString(1, Version.getFullVersion());
			ps.setString(2, Version.PROJECT);
			assertEquals(1, ps.executeUpdate());
		}
	}

	public LibroConnectionFactoryTest(String name)
	{
		super(name);
	}
}
