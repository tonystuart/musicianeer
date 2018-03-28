-- Copyright 2018 Anthony F. Stuart - All rights reserved.
--
-- This program and the accompanying materials are made available
-- under the terms of the GNU General Public License. For other license
-- options please contact the copyright owner.
--
-- This program is made available on an "as is" basis, without
-- warranties or conditions of any kind, either express or implied.

connect 'jdbc:derby://localhost:1527/Music;create=true;';

drop table notable;
drop table name;

create table notable
(
	accidentals integer,
	beats integer,
	bpm integer,
	channel integer,
	concurrency integer,
	duration integer,
	id integer,
	line integer,
	major integer,
	measure integer,
	melody integer,
	note integer,
	occupancy integer,
	parts integer,
	program integer,
	seconds integer,
	song integer,
	stanza integer,
	start integer,
	stop integer,
	thirds integer,
	tick integer,
	tonic integer,
	transpose integer,
	triads integer,
	unit integer,
	velocity integer
);

create table name
(
  song integer,
  name varchar(255)
);
