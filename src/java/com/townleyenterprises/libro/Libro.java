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
// File:	Libro.java
// Created:	Mon Dec 27 20:10:52 GMT 2004
//
//////////////////////////////////////////////////////////////////////

package com.townleyenterprises.libro;

import java.io.IOException;
import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import net.sourceforge.jaxor.api.JaxorTransaction;
import net.sourceforge.jaxor.db.SingleConnectionTransaction;

import com.townleyenterprises.config.AppConfig;
import com.townleyenterprises.config.ConfigRegistry;
import com.townleyenterprises.config.ConfigSupplier;
import com.townleyenterprises.config.PropertiesConfigSupplier;
import com.townleyenterprises.config.WriteCaptureStrategy;
import com.townleyenterprises.persistence.ConnectionFactory;
import com.townleyenterprises.trace.BasicTrace;

import com.townleyenterprises.libro.backend.LibroConnectionFactory;

/**
 * This class effectively provides a global which can be used to
 * ensure that the application is properly initialized and to locate
 * the specific application configuration settings.  The idiom is
 * generally repeated for each application.  It might be possible to
 * provide a skeleton instance, but you would still have to provide a
 * static version, so it probably isn't worth the effort.
 * <p>
 * Another function of this class is to provide centralized logging of
 * application events.  This is different than how the logging APIs
 * are normally used--on a per-class basis.  Personally, I generally
 * use a set of BasicTrace or ErrorTrace instances to handle all of
 * the "debugging and tracing" capabilities because they have a lot
 * more flexibility in the levels used than the logger APIs.  However,
 * I also prefer to have a centralized log file generated for each
 * application.  This logger instance is contained in the central
 * application class with helper methods provided to make it easy to
 * find the appropriate logger.
 * </p>
 *
 * @version $Id: Libro.java,v 1.3 2005/01/02 22:40:57 atownley Exp $
 * @author <a href="mailto:atownley@users.sourceforge.net">Andrew S. Townley</a>
 */

public final class Libro
{
	/**
	 * This method is used to initialize the entire application.
	 */

	public static synchronized void init()
	{
		_trace.methodStart("init");

		try
		{
			if(_initialized)
			{
				_trace.methodReturn();
				return;
			}

			initConfig();
			_initialized = true;

			logInfo("log.fStarted", 
				new Object[] { Version.getFullVersion() });

			_trace.methodReturn();
		}
		finally
		{
			_trace.methodExit();
		}
	}

	/**
	 * This method provides access to the configuration settings
	 * for this application.
	 *
	 * @return the ConfigSupplier for the application
	 */

	public static ConfigSupplier getConfig()
	{
		return ConfigRegistry.getConfig("libro");
	}

	/**
	 * This method is used to return a singleton instance of the
	 * ConnectionFactory to be used by the application.
	 *
	 * @return the connection factory
	 */

	public static ConnectionFactory getConnectionFactory()
	{
		return _cfactory;
	}

	/**
	 * This method is used to return an appropriate instance of
	 * the JaxorTransaction to be used by the JaxorSession.
	 *
	 * @return the JaxorTransaction
	 */

	public static JaxorTransaction getJaxorTransaction()
	{
		// the creation of the new object follows the Jaxor
		// example pattern of providing a new object to each
		// JaxorSession.begin().  The main reason for this
		// that I can think of is that Jaxor maintains it's
		// transaction in a ThreadLocal, so there is a good
		// reason for giving out a new instance each time.
		//
		// Also, there is a good reason that the _cfactory is
		// declared as a LibroConnectionFactory rather than
		// just a ConnectionFactory.  This is because it
		// implements our ConnectionFactory and Jaxor's, but
		// you can't return one of both at the same time
		// unless you make a new interface, which I didn't
		// want to do.  Strongly typing the instance means
		// that I can avoid a cast within this method.

		return new SingleConnectionTransaction(_cfactory);
	}

	/**
	 * This method is used to log an info message.
	 *
	 * @param key the message resource key
	 */

	public static void logInfo(String key)
	{
		logInfo(key, null);
	}

	/**
	 * This method is used to log an info message with format
	 * arguments.
	 *
	 * @param key the message key
	 * @param args the message arguments
	 */

	public static void logInfo(String key, Object[] args)
	{
		_logger.info(getMessage(key, args));
	}

	/**
	 * This method is used to log a warning message.
	 *
	 * @param key the message resource key
	 */

	public static void logWarning(String key)
	{
		logWarning(key, null);
	}

	/**
	 * This method is used to log a warning message with format
	 * arguments.
	 *
	 * @param key the message key
	 * @param args the message args
	 */

	public static void logWarning(String key, Object[] args)
	{
		_logger.warn(getMessage(key, args));
	}

	/**
	 * This method is used to log an error message.
	 *
	 * @param key the message resource key
	 */

	public static void logError(String key)
	{
		logError(key, null);
	}

	/**
	 * This method is used to log an error message with format
	 * arguments.
	 *
	 * @param key the message key
	 * @param args the message args
	 */

	public static void logError(String key, Object[] args)
	{
		_logger.error(getMessage(key, args));
	}

	/**
	 * This method should be called before the JVM exits to ensure
	 * that all resources have been properly released.
	 */

	public static void shutdown()
	{
		_trace.methodStart("shutdown");

		logInfo("log.sAppStopped");

		_trace.methodReturn();
		_trace.methodExit();
	}

	/**
	 * This method is used to configure everything from the loaded
	 * configuration settings.
	 */

	private static void configure()
	{
		_trace.methodStart("configure");

		try
		{
			Properties props = getConfig().getProperties();

			if(_trace.willTrace(10))
			{
				props.list(_trace.getTraceStream());
			}

			configureTracing(props);
			configureLogging(props);

			_trace.methodReturn();
		}
		finally
		{
			_trace.methodExit();
		}

	}

	/**
	 * This method configures the logging components based on the
	 * final set of properties.
	 *
	 * @param props the properties to use
	 */

	private static void configureLogging(Properties props)
	{
		PropertyConfigurator.configure(props);
	}

	/**
	 * This method configures the tracing components based on the
	 * final set of properties.
	 *
	 * @param props the properties to use
	 */

	private static void configureTracing(Properties props)
	{
		_trace.loadSettings(props);
	}

	/**
	 * This is a utility method to get the appropriate log
	 * message.
	 *
	 * @param key the resource key
	 * @param args the method arguments
	 * @return the formatted message
	 */

	private static String getMessage(String key, Object[] args)
	{
		String msg = null;

		if(args == null)
		{
			msg = Strings.get(key);
		}
		else
		{
			msg = Strings.format(key, args);
		}

		return msg;
	}

	/**
	 * This method is used to properly initialize all of the
	 * configuration suppliers.  Typically, there will be at least
	 * two of these in any application.  The first will be
	 * generally maintained relative to this class while the
	 * second will be maintained at the root of the classpath.
	 * This mechanism will allow installation defaults to override
	 * the initial build configuration parameters by including a
	 * reasonable location into the classpath. 
	 * <p>
	 * The configuration settings for this application are
	 * maintained in the file <code>libro.properties</code>.
	 * </p>
	 * <p>
	 * The main reason the ConfigLoader class is not used here is
	 * because it is perfectly reasonable for the override
	 * properties to come from somewhere like a database or LDAP
	 * so that they can be more easily shared amongst the
	 * instances of a distributed application.
	 * </p>
	 */

	private static void initConfig()
	{
		_trace.methodStart("initConfig");

		try
		{
			// NOTE:  remember the order is important here
			// because, by default, the last set of
			// properties loaded overrides all of the ones
			// before it.

			// base configuration is the libro.properties
			// file in this package

			ConfigSupplier baseconfig =
				new PropertiesConfigSupplier("libro",
					Libro.class);
			
			ConfigRegistry.registerSupplier(baseconfig);

			// configure log4j
			PropertyConfigurator.configure(baseconfig.getProperties());

			try
			{
				// override configuration is the
				// libro.properties file in the root of the
				// classpath
			
				ConfigSupplier overrideconfig =
					new PropertiesConfigSupplier("libro",
						Libro.class, "/");
	
				ConfigRegistry.registerSupplier(overrideconfig);
			}
			catch(IOException e)
			{
				// this isn't a showstopper
				logWarning("log.fLoadInstallConfigFailed", new Object[] { e });
			}
			catch(NullPointerException e)
			{
				// in this case, it means that the
				// file doesn't exist in the resource
				// stream... nice, huh?
				
				logWarning("log.fLoadInstallConfigFailed", new Object[] { e });
			}

			initUserConfig();

			configure();

			_trace.methodReturn();
		}
		catch(IOException e)
		{
			logWarning("log.fLoadConfigFailed", new Object[] { e });
		}
		finally
		{
			_trace.methodExit();
		}
	}

	/**
	 * This method is responsible for managing the user
	 * configuration settings.  If they exist, great.  If they
	 * don't, then they are created and then used to capture any
	 * configuration setting changes.
	 *
	 * @exception IOException
	 * 	if there was an error creating the user properties
	 */

	private static void initUserConfig() throws IOException
	{
		_trace.methodStart("initUserConfig");

		try
		{
			ConfigSupplier userconfig = null;

			File uprops = new File(
				System.getProperty("user.home"),
					".librorc");

			try
			{
				// since this application supports
				// user-defined configuration settings, we
				// need to set up something for the user
				userconfig = new PropertiesConfigSupplier("libro",
						uprops.getAbsolutePath());
			}
			catch(IOException e)
			{
				logWarning("log.fLoadUserConfigFailed", new Object[] { e });

				// create the file
				uprops.createNewFile();
				userconfig = new PropertiesConfigSupplier("libro",
					uprops.getAbsolutePath());

				logInfo("log.fCreatedUserConfig", new Object[] { uprops.getAbsolutePath() });
			}
			
			ConfigRegistry.registerSupplier(userconfig);
			AppConfig ac = (AppConfig)getConfig();
			ac.setWriteResolutionStrategy(
				new WriteCaptureStrategy(userconfig));

			_trace.methodReturn();
		}
		finally
		{
			_trace.methodExit();
		}
	}

	/** maintain a sentinal to ensure only are initialized once */
	private static boolean			_initialized = false;

	/** maintain a reference to the connection factory */
	private static LibroConnectionFactory	_cfactory = new LibroConnectionFactory();

	/** create a logger instance */
	private static Logger			_logger = Logger.getLogger("com.townleyenterprises.libro.Libro");

	/** create a trace instance */
	private static BasicTrace		_trace = new BasicTrace("Libro");
}
