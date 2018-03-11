-- Copyright 2018 Anthony F. Stuart - All rights reserved.
--
-- This program and the accompanying materials are made available
-- under the terms of the GNU General Public License. For other license
-- options please contact the copyright owner.
--
-- This program is made available on an "as is" basis, without
-- warranties or conditions of any kind, either express or implied.

connect 'jdbc:derby://localhost:1527/Music;create=true;';

drop function append;
drop function copy;
drop function program;
drop function round;
drop function transpose;

create function append
(
  tick integer,
  note integer,
  duration integer,
  velocity integer,
  program integer,
  channel integer
)
returns integer
language java
deterministic
external name 'com.example.afs.frankenmusic.db.Derby.append'
parameter style java
no sql
returns null on null input;

create function copy
(
  first_id integer,
  last_id integer
)
returns integer
language java
deterministic
external name 'com.example.afs.frankenmusic.db.Derby.copy'
parameter style java
reads sql data
returns null on null input;

create function program
(
  program integer
)
returns varchar(32)
language java
deterministic
external name 'com.example.afs.frankenmusic.db.Derby.program'
parameter style java
no sql
returns null on null input;

create function round
(
  source integer,
  toNearest integer
)
returns integer
language java
deterministic
external name 'com.example.afs.frankenmusic.db.Derby.round'
parameter style java
no sql
returns null on null input;

create function transpose
(
  song integer,
  amount integer
)
returns table
(
  id integer,
  note integer
)
language java
parameter style derby_jdbc_result_set
reads sql data
external name 'com.example.afs.frankenmusic.db.Derby.transpose';

