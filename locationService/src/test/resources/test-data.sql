INSERT INTO locations (id, name, code) VALUES
  (1, 'Portugal', 'PT'),
  (2, 'Spain',    'ES'),
  (3, 'France',   'FR');

INSERT INTO metrics (id, name, label, unit) VALUES
  (1, 'pli',   'Price Level Index', 'index'),
  (2, 'crime', 'Crime Rate',        'index');

INSERT INTO location_metrics (id, location_id, metric_id, metric_value, year) VALUES
  (1, 1, 1, 72.5, 2023),
  (2, 2, 1, 85.3, 2023),
  (3, 3, 1, 95.1, 2023),
  (4, 1, 2, 30.0, 2023);
