-- Copyright 2018 Anthony F. Stuart - All rights reserved.
--
-- This program and the accompanying materials are made available
-- under the terms of the GNU General Public License. For other license
-- options please contact the copyright owner.
--
-- This program is made available on an "as is" basis, without
-- warranties or conditions of any kind, either express or implied.

connect 'jdbc:derby://localhost:1527/Music;create=true;';

drop index notable_accidentals_index;
drop index notable_beats_index;
drop index notable_bpm_index;
drop index notable_channel_index;
drop index notable_concurrency_index;
drop index notable_duration_index;
drop index notable_id_index;
drop index notable_line_index;
drop index notable_major_index;
drop index notable_measure_index;
drop index notable_melody_index;
drop index notable_note_index;
drop index notable_occupancy_index;
drop index notable_parts_index;
drop index notable_program_index;
drop index notable_seconds_index;
drop index notable_song_index;
drop index notable_stanza_index;
drop index notable_start_index;
drop index notable_stop_index;
drop index notable_thirds_index;
drop index notable_tick_index;
drop index notable_tonic_index;
drop index notable_transpose_index;
drop index notable_triads_index;
drop index notable_unit_index;
drop index notable_velocity_index;
drop index name_song_index;

create index notable_accidentals_index on notable(accidentals);
create index notable_beats_index on notable(beats);
create index notable_bpm_index on notable(bpm);
create index notable_channel_index on notable(channel);
create index notable_concurrency_index on notable(concurrency);
create index notable_duration_index on notable(duration);
create index notable_id_index on notable(id);
create index notable_line_index on notable(line);
create index notable_major_index on notable(major);
create index notable_measure_index on notable(measure);
create index notable_melody_index on notable(melody);
create index notable_note_index on notable(note);
create index notable_occupancy_index on notable(occupancy);
create index notable_parts_index on notable(parts);
create index notable_program_index on notable(program);
create index notable_seconds_index on notable(seconds);
create index notable_song_index on notable(song);
create index notable_stanza_index on notable(stanza);
create index notable_start_index on notable(start);
create index notable_stop_index on notable(stop);
create index notable_thirds_index on notable(thirds);
create index notable_tick_index on notable(tick);
create index notable_tonic_index on notable(tonic);
create index notable_transpose_index on notable(transpose);
create index notable_triads_index on notable(triads);
create index notable_unit_index on notable(unit);
create index notable_velocity_index on notable(velocity);
create index name_song_index on name(song);

