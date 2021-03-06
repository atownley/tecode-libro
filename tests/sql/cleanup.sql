----------------------------------------------------------------------
--
-- Copyright (c) 2004-2005, Andrew S. Townley
-- All rights reserved.
-- 
-- Redistribution and use in source and binary forms, with or without
-- modification, are permitted provided that the following conditions
-- are met:
-- 
--     * Redistributions of source code must retain the above
--     copyright notice, this list of conditions and the following
--     disclaimer.
-- 
--     * Redistributions in binary form must reproduce the above
--     copyright notice, this list of conditions and the following
--     disclaimer in the documentation and/or other materials provided
--     with the distribution.
-- 
--     * Neither the names Andrew Townley or Townley Enterprises,
--     Inc. nor the names of its contributors may be used to endorse
--     or promote products derived from this software without specific
--     prior written permission.  
-- 
-- THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
-- 'AS IS' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
-- LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
-- FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
-- COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
-- INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
-- (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
-- SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
-- HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
-- STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
-- ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
-- OF THE POSSIBILITY OF SUCH DAMAGE.
--
-- File:		cleanup.sql
-- Created:		Sun Jan  2 17:59:26 GMT 2005
--
-- Description:
-- 	This file is used to undo the effects of the unit test's
-- 	bootstrap.sql file.  Care should be taken to ensure that all
-- 	of the data gets deleted.
--
-- $Id: cleanup.sql,v 1.1 2005/01/02 22:43:28 atownley Exp $
--
----------------------------------------------------------------------

-- support the counter unit tests
delete from counter where what = 'counter_test';
