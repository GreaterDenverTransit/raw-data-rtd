CREATE TABLE IF NOT EXISTS "Combined_Ridership_Data" (
        "SCHEDULE_NAME"    TEXT COLLATE NOCASE,
        "ROUTE"            INTEGER,
        "BRANCH"           TEXT COLLATE NOCASE,
        "SERVICE_TYPE"     TEXT COLLATE NOCASE,
        "SERVICE_MODE"     TEXT COLLATE NOCASE,
        "DIRECTION_NUMBER" INTEGER,
        "DIRECTION_NAME"   TEXT COLLATE NOCASE,
        "TRIP_ID"          INTEGER,
        "TIME_PERIOD"      TEXT COLLATE NOCASE,
        "SORT_ORDER"       INTEGER,
        "STOP_ID"          INTEGER,
        "BOARDINGS"        INTEGER,
        "ALIGHTINGS"       INTEGER,
        "LOAD"             INTEGER,
        "Schedule_Year"    INTEGER,
        "Schedule_Month"   INTEGER
, schedule_days, service_id);
