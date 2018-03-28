-- Copyright 2018 Anthony F. Stuart - All rights reserved.
--
-- This program and the accompanying materials are made available
-- under the terms of the GNU General Public License. For other license
-- options please contact the copyright owner.
--
-- This program is made available on an "as is" basis, without
-- warranties or conditions of any kind, either express or implied.

select * from notable order by song, tick, channel fetch first 10 rows only;

select * from notable where song = 2 order by song, tick, channel fetch first 10 rows only;

values (append(-1, 60, 200, 64, 0, 0)), (append(-1, 64, 200, 64, 0, 0)), (append(-1, 67, 200, 64, 0, 0));

values (append(0, 60, 1024, 64, 0, 0)), (append(0, 64, 1024, 64, 0, 0)), (append(0, 67, 1024, 64, 0, 0));

-- generated rownum column: row_number() over() as rownum

call tempo(50);
call tempo(150);
call tempo(100);

select distinct
  song,
  program,
  getProgramName(program) as instrument,
  channel,
  min(tick) as first_tick,
  count(note) as count
from notable
group by song, program, channel
order by 1, 2, 3;


select
  song,
  channel,
  program,
  getProgramName(program) as program,
  avg(occupancy) as occupancy,
  avg(concurrency) as concurrency,
  count(note) as total_note_count,
  count(distinct note) as distinct_note_count
from notable
group by
  song,
  channel,
  program
order by
  song,
  channel;

select song, copy(min(id), max(id)) from notable where song = 4 group by song;

call reset();
select song, copy(min(id), max(id)) from notable where song = 4 and measure > 16 and measure < 32 group by song;
select song, copy(min(id), max(id)) from notable where song = 3 and measure > 16 and measure < 32 group by song;
select song, copy(min(id), max(id)) from notable where song = 2 and measure > 16 and measure < 32 group by song;
select song, copy(min(id), max(id)) from notable where song = 1 and measure > 16 and measure < 32 group by song;
select song, copy(min(id), max(id)) from notable where song = 4 and measure > 16 and measure < 32 group by song;
call play();

select append(tick, note, duration, velocity, program, channel) from notable where measure = 10 order by tick;

select append(tick, note, duration, velocity, program, channel) from notable where line = 2 order by tick;

select append(tick, note, 1024, velocity, 0, channel) from notable where song = 5 and id < 500 order by tick;

select song, min(id), max(id) from notable group by song order by song fetch first 20 rows only;

select append(tick, note, 1024, velocity, 0, channel) from notable where id >= 8948 and id <= 12900 order by id;

select song, line, min(id), max(id) from notable group by song, line order by song;

select distinct song, stanza, line from notable order by song;

select measure, id, stanza, line from notable where song = 2 and line = 1;

select append(tick, note, duration, velocity, 0, channel) from notable where song = 2 and line = 1 order by tick;

select append(tick, note, duration, velocity, 0, channel) from notable where song = 2 and stanza = 1 order by tick;

select bpm, count(distinct song) from notable group by bpm order by bpm;

select distinct bpm, name from notable natural join name order by bpm;

select distinct avg(bpm), name from notable natural join name group by name order by avg(bpm);

select * from name;

select * from notable;

select *
from notable
natural join name;

select song, name, avg(bpm) as "AVG(BPM)"
from notable
natural join name
group by song, name
order by avg(bpm);

select song, name, stanza, line, avg(bpm)
from notable
natural join name
where song < 5
group by song, name, stanza, line
order by name, stanza, line;

call reset();
select song, copy(min(id), max(id)) from notable where song = 394 and measure < 32 group by song;
call play();

call tempo(150);
call tempo(100);
call tempo(50);

call reset();
select append(tick, note, duration, velocity, 0, channel)
from notable
where song < 5
and measure < 16
order by tick;

call reset();
select append(tick, note, duration, velocity, 0, channel) from notable where song = 4 and measure > 8 and measure <= 17;
select append(tick, note, duration, velocity, 0, channel) from notable where song = 3 and measure > 8 and measure <= 17;
select append(tick, note, duration, velocity, 0, channel) from notable where song = 2 and measure > 8 and measure <= 17;
select append(tick, note, duration, velocity, 0, channel) from notable where song = 1 and measure > 8 and measure <= 17;
select append(tick, note, duration, velocity, 0, channel) from notable where song = 4 and measure > 8 and measure <= 17;
call play();

select distinct measure from notable where song in (1, 2, 3, 4);

-- First measure (0) of song 4 has meta events
-- Second measure (1) is drum intro
-- Third measure (2) is first note

call reset();
select append(tick, note, duration, velocity, 0, channel) from notable where song = 4 and measure >= 1 and measure < 2;
call play();

select distinct song, name, stanza, avg(bpm) as bpm, count(note) as notes
from notable
natural join name
group by notable.song, name, stanza
order by bpm, song, stanza;

call reset();
select append(tick, note, duration, velocity, 0, channel)
from notable
where
(song = 0
or song = 1)
and stanza = 1
order by song, tick;
call play();

-- NB: program is 0-based, but most GM descriptions are 1-based
-- so program index less than 8 means pianos 1 through 8

call reset();
select append
(
  tick,
  note,
  duration,
  velocity,
  case
    when program < 8 then 0 -- acoustic grand piano
    when program < 16 then 13 -- xylophone
    when program < 24 then 19 -- church organ
    when program < 32 then 25 -- acoustic guitar (steel)
    when program < 40 then 32 -- acoustic bass
    when program < 47 then 40 -- violin
    when program = 47 then 47 -- timpani
    when program < 56 then 48 -- string ensemble 1
    when program < 64 then 56 -- trumpet
    when program < 72 then 65 -- alto sax
    when program < 80 then 74 -- recorder
    when program < 88 then 82 -- synth lead 3 (calliope)
    when program < 96 then 88 -- synth pad 1 (new age)
    else 0 -- acoustic grand piano
    end,
  channel
)
from notable
where measure >= 8 and measure <= 16
order by bpm, song;
call play();

call reset();
select append(tick, note, duration, velocity, program, channel)
from notable
where measure >= 8 and measure <= 16
order by bpm, song;
call play();

call reset();
select append
(
  tick,
  note,
  duration,
  velocity,
  case
    when program < 8 then 0 -- acoustic grand piano
    when program < 16 then 13 -- xylophone
    when program < 24 then 19 -- church organ
    when program < 32 then 25 -- acoustic guitar (steel)
    when program < 40 then 32 -- acoustic bass
    when program < 47 then 40 -- violin
    when program = 47 then 47 -- timpani
    when program < 56 then 48 -- string ensemble 1
    when program < 64 then 56 -- trumpet
    when program < 72 then 65 -- alto sax
    when program < 80 then 74 -- recorder
    when program < 88 then 82 -- synth lead 3 (calliope)
    when program < 96 then 88 -- synth pad 1 (new age)
    else 0 -- acoustic grand piano
    end,
  channel
)
from notable
where measure >= 8 and measure <= 16
and song in (
  select song
  from notable
  group by song
  having avg(bpm) > 100
  and avg(bpm) < 150
  order by avg(bpm)
)
order by song, tick;
call play();

drop view notable_bpm;
create view notable_bpm as
  select song, avg(bpm) as avg_bpm
  from notable
  group by song
  order by avg(bpm);

call reset();
select append
(
  n1.tick,
  n1.note,
  n1.duration,
  n1.velocity,
  case
    when n1.program < 8 then 0 -- acoustic grand piano
    when n1.program < 16 then 13 -- xylophone
    when n1.program < 24 then 19 -- church organ
    when n1.program < 32 then 25 -- acoustic guitar (steel)
    when n1.program < 40 then 32 -- acoustic bass
    when n1.program < 47 then 40 -- violin
    when n1.program = 47 then 47 -- timpani
    when n1.program < 56 then 48 -- string ensemble 1
    when n1.program < 64 then 56 -- trumpet
    when n1.program < 72 then 65 -- alto sax
    when n1.program < 80 then 74 -- recorder
    when n1.program < 88 then 82 -- synth lead 3 (calliope)
    when n1.program < 96 then 88 -- synth pad 1 (new age)
    else 0 -- acoustic grand piano
    end,
  n1.channel
)
from notable as n1, notable_bpm as n2
where n1.song = n2.song
and n2.avg_bpm >= 100
and n2.avg_bpm <= 150
and n1.measure >= 8
and n1.measure <= 12
order by n2.avg_bpm, n1.tick;
call play();

call stop();

select song,
  measure,
  bpm
from notable
where measure >= 8 and measure <= 16
order by bpm, song;

select song, avg(bpm) as avg_bpm
from notable
group by song
order by avg_bpm;

select distinct song, name, max(measure) as max_measure
from notable
natural join name
group by song, name
order by max_measure, song;

select distinct song, name
from notable
natural join name
where program >= 56
and program <= 63;

drop view notable_brass;
create view notable_brass as
  select distinct song
  from notable
  where program >= 56
  and program <= 63;

select * from notable_brass;

call reset();
select append
(
  n1.tick,
  n1.note,
  n1.duration,
  n1.velocity,
  case
    when n1.program < 8 then 0 -- acoustic grand piano
    when n1.program < 16 then 13 -- xylophone
    when n1.program < 24 then 19 -- church organ
    when n1.program < 32 then 25 -- acoustic guitar (steel)
    when n1.program < 40 then 32 -- acoustic bass
    when n1.program < 47 then 40 -- violin
    when n1.program = 47 then 47 -- timpani
    when n1.program < 56 then 48 -- string ensemble 1
    when n1.program < 64 then 56 -- trumpet
    when n1.program < 72 then 65 -- alto sax
    when n1.program < 80 then 74 -- recorder
    when n1.program < 88 then 82 -- synth lead 3 (calliope)
    when n1.program < 96 then 88 -- synth pad 1 (new age)
    else 0 -- acoustic grand piano
    end,
  n1.channel
)
from notable as n1, notable_bpm as n2, notable_brass as n3
where n1.song = n2.song
and n2.song = n3.song
and n2.avg_bpm >= 100
and n2.avg_bpm <= 150
and n1.measure >= 8
and n1.measure <= 12
order by n2.avg_bpm, n1.tick;
call play();

call stop();