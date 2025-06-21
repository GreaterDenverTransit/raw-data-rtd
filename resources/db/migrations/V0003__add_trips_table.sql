CREATE TYPE IF NOT EXISTS service_id_t AS ENUM (
 'SA'
 'SU'
 'WK'
 'FR'
 'MT'
 'DPS'
 'BVSD'
 'DPSWK'
);

-- TODO: add foreign key from route_id when routes table is added
CREATE TABLE IF NOT EXISTS trips (
  block_id     TEXT NOT NULL,
  direction_id BOOLEAN NOT NULL,
  end_time     TIME NOT NULL,
  first_stop   INTEGER NOT NULL REFERENCES stops(stop_id),
  last_stop    INTEGER NOT NULL REFERENCES stops(stop_id),
  max_avg_load INTEGER,
  route_id     TEXT NOT NULL,
  service_id   NOT NULL,
  service_id_mod,
  shape_id NOT NULL,
  start_time   TIME NOT NULL,
  sum_boardings,
  time_of_day,
  total_stops NOT NULL,
  trip_headsign NOT NULL,
  trip_id NOT NULL,
  trip_time NOT NULL,
);

-- TODO consider standardizing to
-- EB/NB/IB/CW = false and WB/SB/OB/CCW = true
COMMENT ON COLUMN trips.direction_id IS
  'This field is used to distinguish one direction of travel from another but is
  not easily mapped to a direction (e.g. east/west/north/south/inbound/outbound).
  Its primary utility comes from grouping/aggregating/filtering one direction
  from another where identifying which direction (e.g. northbound versus southbound)
  in results isn''t necessary. trip_headsign provides better information on this
  but is bespoke to a given route rather than standardized to a cardinal direction.';

COMMENT ON TABLE trips IS
  'time_of_day, sum_boardings, max_avg_load, and service_id_mod are all always
  either NULL or not NULL as a group (i.e. for any given row either
  time_of_day IS NULL AND sum_boardings IS NULL AND max_avg_load IS NULL AND
  service_id_mod IS NULL
  or
  time_of_day IS NOT NULL AND sum_boardings IS NOT NULL AND
  max_avg_load IS NOT NULL AND service_id_mod IS NOT NULL).';

SELECT COUNT(*)
  FROM trips
 WHERE time_of_day IS NULL AND
       sum_boardings IS NULL AND
       max_avg_load IS NULL AND
       service_id_mod IS NULL;

  
SELECT SUM(CAST((trip_id IS NULL) AS int)) AS trip_id_null_tally,
       SUM(CAST((route_id IS NULL) AS int)) AS route_id_null_tally,
       SUM(CAST((service_id IS NULL) AS int)) AS service_id_null_tally,
       SUM(CAST((trip_headsign IS NULL) AS int)) AS trip_headsign_null_tally,
       SUM(CAST((direction_id IS NULL) AS int)) AS direction_id_null_tally,
       SUM(CAST((block_id IS NULL) AS int)) AS block_id_null_tally,
       SUM(CAST((shape_id IS NULL) AS int)) AS shape_id_null_tally,
       SUM(CAST((first_stop IS NULL) AS int)) AS first_stop_null_tally,
       SUM(CAST((last_stop IS NULL) AS int)) AS last_stop_null_tally,
       SUM(CAST((start_time IS NULL) AS int)) AS start_time_null_tally,
       SUM(CAST((end_time IS NULL) AS int)) AS end_time_null_tally,
       SUM(CAST((trip_time IS NULL) AS int)) AS trip_time_null_tally,
       SUM(CAST((time_of_day IS NULL) AS int)) AS time_of_day_null_tally,
       SUM(CAST((sum_boardings IS NULL) AS int)) AS sum_boardings_null_tally,
       SUM(CAST((max_avg_load IS NULL) AS int)) AS max_avg_load_null_tally,
       SUM(CAST((total_stops IS NULL) AS int)) AS total_stops_null_tally,
       SUM(CAST((service_id_mod IS NULL) AS int)) AS service_id_mod_null_tally
FROM trips;
