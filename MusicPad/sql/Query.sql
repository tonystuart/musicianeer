-- Copyright 2018 Anthony F. Stuart - All rights reserved.
--
-- This program and the accompanying materials are made available
-- under the terms of the GNU General Public License. For other license
-- options please contact the copyright owner.
--
-- This program is made available on an "as is" basis, without
-- warranties or conditions of any kind, either express or implied.

select * from neuron order by song, tick, channel fetch first 10 rows only;

select * from neuron where song = 2 order by song, tick, channel fetch first 10 rows only;

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
from neuron
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
from neuron
group by
  song,
  channel,
  program
order by
  song,
  channel;

select song, copy(min(id), max(id)) from neuron where song = 4 group by song;

call reset();
select song, copy(min(id), max(id)) from neuron where song = 4 and measure = 10 group by song;
select song, copy(min(id), max(id)) from neuron where song = 3 and measure = 11 group by song;
select song, copy(min(id), max(id)) from neuron where song = 2 and measure = 12 group by song;
select song, copy(min(id), max(id)) from neuron where song = 1 and measure = 13 group by song;
select song, copy(min(id), max(id)) from neuron where song = 4 and measure = 11 group by song;
call play();

select append(tick, note, duration, velocity, program, channel) from neuron where measure = 10 order by tick;

select append(tick, note, duration, velocity, program, channel) from neuron where line = 2 order by tick;

select append(tick, note, 1024, velocity, 0, channel) from neuron where song = 5 and id < 500 order by tick;

select song, min(id), max(id) from neuron group by song order by song fetch first 20 rows only;

select append(tick, note, 1024, velocity, 0, channel) from neuron where id >= 8948 and id <= 12900 order by id;

select song, line, min(id), max(id) from neuron group by song, line order by song;

select distinct song, stanza, line from neuron order by song;

select measure, id, stanza, line from neuron where song = 2 and line = 1;

select append(tick, note, duration, velocity, 0, channel) from neuron where song = 2 and line = 1 order by tick;

select append(tick, note, duration, velocity, 0, channel) from neuron where song = 2 and stanza = 1 order by tick;
