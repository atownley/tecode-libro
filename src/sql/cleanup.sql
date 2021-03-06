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
-- Created:		Mon Dec 27 16:15:21 GMT 2004
--
-- $Id: cleanup.sql,v 1.2 2004/12/28 21:58:49 atownley Exp $
--
----------------------------------------------------------------------

drop table book;
drop table publisher;
drop table author;
drop table counter;
drop table version_info;
