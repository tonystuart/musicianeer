-- Copyright 2018 Anthony F. Stuart - All rights reserved.
--
-- This program and the accompanying materials are made available
-- under the terms of the GNU General Public License. For other license
-- options please contact the copyright owner.
--
-- This program is made available on an "as is" basis, without
-- warranties or conditions of any kind, either express or implied.

connect 'jdbc:derby://localhost:1527/Music;create=true;';

drop procedure play;
drop procedure reset;
drop procedure stop;
drop procedure tempo;

create procedure play ( )
language java
not deterministic
external name 'com.example.afs.frankenmusic.db.Derby.play'
parameter style java
no sql
;

create procedure reset ( )
language java
not deterministic
external name 'com.example.afs.frankenmusic.db.Derby.reset'
parameter style java
no sql
;

create procedure stop ( )
language java
not deterministic
external name 'com.example.afs.frankenmusic.db.Derby.stop'
parameter style java
no sql
;

create procedure tempo
(
  tempo integer
)
language java
not deterministic
external name 'com.example.afs.frankenmusic.db.Derby.tempo'
parameter style java
no sql
;
