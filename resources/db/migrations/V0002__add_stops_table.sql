CREATE TYPE IF NOT EXISTS stop_direction_t AS ENUM (
  'Vehicles Travelling East'
  'Vehicles Travelling North'
  'Vehicles Travelling Northeast'
  'Vehicles Travelling Northwest'
  'Vehicles Travelling South'
  'Vehicles Travelling Southeast'
  'Vehicles Travelling Southwest'
  'Vehicles Travelling West'
  'Vehicles Travelling'
)

CREATE TABLE IF NOT EXISTS stops (
  location_type       BOOLEAN        NOT NULL,
  parent_station      TEXT,
  stop_code           INTEGER        NOT NULL,
  stop_desc           stop_direction NOT NULL,
  stop_id             INTEGER        NOT NULL,
  stop_lat            REAL           NOT NULL,
  stop_lon            REAL           NOT NULL,
  stop_name           TEXT           NOT NULL,
  stop_timezone       BOOLEAN,
  stop_url            TEXT           NOT NULL,
  wheelchair_boarding BOOLEAN        NOT NULL,
  zone_id             TEXT,
  PRIMARY KEY(stop_id)
);

COMMENT ON stops.stop_timezone IS
  'This field is always NULL but included by RTD to conform to a standard,
  boolean used to waste minimal space';

COMMENT ON stops.stop_url IS
  'URL to RTD''s webpage on stop';
