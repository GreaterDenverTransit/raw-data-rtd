CREATE TABLE IF NOT EXISTS "stops" (
        "stop_id"             INTEGER,
        "stop_code"           INTEGER,
        "stop_name"           TEXT,
        "stop_desc"           TEXT,
        "stop_lat"            REAL,
        "stop_lon"            REAL,
        "zone_id"             TEXT,
        "stop_url"            TEXT,
        "location_type"       INTEGER,
        "parent_station"      TEXT,
        "stop_timezone"       TEXT,
        "wheelchair_boarding" TEXT,
        PRIMARY KEY("stop_id")
);
