######################################################################
##
## Copyright (c) 2004-2005, Andrew S. Townley
## All rights reserved.
## 
## Redistribution and use in source and binary forms, with or without
## modification, are permitted provided that the following conditions
## are met:
## 
##     * Redistributions of source code must retain the above
##     copyright notice, this list of conditions and the following
##     disclaimer.
## 
##     * Redistributions in binary form must reproduce the above
##     copyright notice, this list of conditions and the following
##     disclaimer in the documentation and/or other materials provided
##     with the distribution.
## 
##     * Neither the names Andrew Townley or Townley Enterprises,
##     Inc. nor the names of its contributors may be used to endorse
##     or promote products derived from this software without specific
##     prior written permission.  
## 
## THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
## "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
## LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
## FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
## COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
## INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
## (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
## SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
## HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
## STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
## ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
## OF THE POSSIBILITY OF SUCH DAMAGE.
##
## File:	classpath.sh
## Created:	Tue Dec 28 18:03:28 GMT 2004
##
## Description:
##	This script is used to set the classpath under UNIX so that
##	all of the jars necessary to run the program are available.
##
######################################################################

if [ ! -f build.xml ]
then
	echo "error:  you must run this script from the top of the source tree"
	echo "Usage:  ./src/etc/classpath.sh"
else
	if [ ! -d ./src/lib ]
	then
		echo "error:  unable to locate the library directory"
	else
		for f in `pwd`/src/lib/*.jar
		do
			CLASSPATH=$f:$CLASSPATH; export CLASSPATH
		done

		if [ ! -d ./tests/lib ]
		then
			echo "warning:  unable to locate tests library directory"
		else
			for f in `pwd`/tests/lib/*.jar
			do
				CLASSPATH=$f:$CLASSPATH; export CLASSPATH
			done
	fi
fi
