-- Static reference data for Location and Metric tables
-- This is initialized once when the test database is created

INSERT INTO locations (id, name, code) VALUES
  (1, 'Portugal', 'PT'),
  (2, 'Spain', 'ES'),
  (3, 'France', 'FR');

INSERT INTO metrics (id, name) VALUES
  (1, 'gdp'),
  (2, 'inflation'),
  (3, 'unemployment');
