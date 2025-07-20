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

CREATE TYPE IF NOT EXISTS time_of_day_t AS ENUM (
  'AM Peak'
  'AM Early'
  'PM Peak'
  'PM Evening'
  'PM Late'
  'Midday'
  'Other'
);

-- TODO: add foreign key from route_id when routes table is added
CREATE TABLE IF NOT EXISTS trips (
  block_id         TEXT         NOT NULL,
  direction_id     BOOLEAN      NOT NULL,
  end_time         TEXT         NOT NULL,
  end_time_24_hr   TIME         NOT NULL,
  first_stop       INTEGER      NOT NULL REFERENCES stops(stop_id),
  last_stop        INTEGER      NOT NULL REFERENCES stops(stop_id),
  max_avg_load     INTEGER,
  route_id         TEXT         NOT NULL,
  service_id       service_id_t NOT NULL,
  service_id_mod   TEXT,
  shape_id         INTEGER      NOT NULL REFERENCES shapes(shape_id),
  start_time       TEXT         NOT NULL,
  start_time_24_hr TIME         NOT NULL,
  sum_boardings    INTEGER,
  time_of_day      time_of_day_t,
  total_stops      INTEGER      NOT NULL,
  trip_headsign    TEXT         NOT NULL,
  trip_id          INTEGER      NOT NULL,
  trip_time        TIME         NOT NULL,
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

COMMENT ON COLUMN trips.start_time_24_hr IS
  'Like start_time but guaranteed to be compliant with 00:00:00-23:59:59 format in cases where
  trip spans two days.';

COMMENT ON COLUMN trips.end_time_24_hr IS
  'Like end_time but guaranteed to be compliant with 00:00:00-23:59:59 format in cases where
  trip spans two days.';

COMMENT ON TABLE trips IS
  'time_of_day, sum_boardings, max_avg_load, and service_id_mod are all always
  either NULL or not NULL as a group (i.e. for any given row either
  time_of_day IS NULL AND sum_boardings IS NULL AND max_avg_load IS NULL AND
  service_id_mod IS NULL
  or
  time_of_day IS NOT NULL AND sum_boardings IS NOT NULL AND
  max_avg_load IS NOT NULL AND service_id_mod IS NOT NULL).';
