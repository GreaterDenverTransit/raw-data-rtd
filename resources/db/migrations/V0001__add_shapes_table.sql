CREATE TABLE IF NOT EXISTS shapes (
  shape_id            INTEGER NOT NULL,
  shape_pt_lat        REAL NOT NULL,
  shape_pt_lon        REAL NOT NULL,
  shape_pt_sequence   INTEGER NOT NULL,
  shape_dist_traveled TEXT,
  PRIMARY KEY(shape_id, shape_pt_sequence)
);

COMMENT ON TABLE shapes IS
  'Each shape_id corresponds to a trip''s route shape. Taking each row of a particular shape_id
  in order from lowest to highest shape_pt_sequence will show the coordinates
  for the corresponding route trip.

  According to the GTFS spec:
  Shapes describe the path that a vehicle travels along a route alignment, and
  are defined in the file shapes.txt. Shapes are associated with Trips, and
  consist of a sequence of points through which the vehicle passes in order.
  Shapes do not need to intercept the location of Stops exactly, but all Stops on
  a trip should lie within a small distance of the shape for that trip, i.e.
  close to straight line segments connecting the shape points.
  ';
