import csv
import psycopg2
import os

# Map the database connection parameters to the environment variables
conn = psycopg2.connect(
    host=os.getenv("PG_HOST", "postgres"),
    port=int(os.getenv("PG_LOCAL_PORT", "5432")),
    database=os.getenv("POSTGRES_DB"),
    user=os.getenv("POSTGRES_USER"),
    password=os.getenv("POSTGRES_PASSWORD")
)

conn.autocommit = False

# Get spreadsheets directory (default to current dir for local testing)
SPREADSHEET_DIR = os.getenv("SPREADSHEET_DIR", "/spreadsheets")

# Populate country table
with open(f'{SPREADSHEET_DIR}/air_quality_country_avg.csv', 'r') as f:
    reader = csv.DictReader(f)
    for row in reader:
        country = row['country']  # Assuming 'country' is the column name
        # Get the country code from public_transport.csv
        country_code = None
        with open(f'{SPREADSHEET_DIR}/public_transport.csv', 'r') as f2:
            reader2 = csv.DictReader(f2)
            for row2 in reader2:
                if row2['Geopolitical entity (reporting)'] == country:
                    country_code = row2['geo']
                    break

        with conn.cursor() as cur:
            cur.execute(
                "INSERT INTO locations (name, code) VALUES (%s, %s)",
                (country, country_code)
            )    



#Add life expectancy to the metrics table
conn.cursor().execute(
    "INSERT INTO metrics (name, label, unit) " \
    "VALUES ('life_expectancy', 'Life Expectancy at Birth', 'years')")

#add entries to location_metrics table
with open(f'{SPREADSHEET_DIR}/life_expectancy.csv', 'r') as f:
    reader = csv.DictReader(f)
    for row in reader:
        country = row['geo']
        life_expectancy = row['OBS_VALUE'].strip()
        year = row['TIME_PERIOD']
        if not life_expectancy:
            continue
        with conn.cursor() as cur:
            cur.execute(
                "SELECT id FROM locations WHERE code = %s",
                (country,)
            )
            location_row = cur.fetchone()
            if location_row is not None:
                location_id = location_row[0]
                cur.execute(
                    "SELECT id FROM metrics WHERE name = 'life_expectancy'",
                    ()
                )
                metric_row = cur.fetchone()
                if metric_row is not None:
                    metric_id = metric_row[0]
                    cur.execute(
                        "INSERT INTO location_metrics (location_id, metric_id, metric_value, year) VALUES (%s, %s, %s, %s)",
                        (location_id, metric_id, float(life_expectancy), int(year))
                    )
    


conn.cursor().execute(
    "INSERT INTO metrics (name, label, unit) " \
    "VALUES ('health', 'Physicians per hundred thousand people', 'physicians/100,000 people')")

with open(f'{SPREADSHEET_DIR}/physicians_country.csv', 'r') as f:
    reader = csv.DictReader(f)
    for row in reader:
        country = row['geo']
        physicians_per_1000 = row['OBS_VALUE'].strip()
        year = row['TIME_PERIOD']
        if not physicians_per_1000:
            continue
        with conn.cursor() as cur:
            cur.execute(
                "SELECT id FROM locations WHERE code = %s",
                (country,)
            )
            location_row = cur.fetchone()
            if location_row is not None:
                location_id = location_row[0]
                cur.execute(
                    "SELECT id FROM metrics WHERE name = 'health'",
                    ()
                )
                metric_row = cur.fetchone()
                if metric_row is not None:
                    metric_id = metric_row[0]
                    cur.execute(
                        "INSERT INTO location_metrics (location_id, metric_id, metric_value, year) VALUES (%s, %s, %s, %s)",
                        (location_id, metric_id, float(physicians_per_1000), int(year))
                    )



conn.cursor().execute(
    "INSERT INTO metrics (name, label, unit) " \
    "VALUES ('working_hours', 'Hours Worked per Week', 'hours per week')")

with open(f'{SPREADSHEET_DIR}/hours_worked_weekly.csv', 'r') as f:
    reader = csv.DictReader(f)
    for row in reader:
        country = row['geo']
        hours_worked_per_week = row['OBS_VALUE'].strip()
        year = row['TIME_PERIOD']
        if not hours_worked_per_week:
            continue
        with conn.cursor() as cur:
            cur.execute(
                "SELECT id FROM locations WHERE code = %s",
                (country,)
            )
            location_row = cur.fetchone()
            if location_row is not None:
                location_id = location_row[0]
                cur.execute(
                    "SELECT id FROM metrics WHERE name = 'working_hours'",
                    ()
                )
                metric_row = cur.fetchone()
                if metric_row is not None:
                    metric_id = metric_row[0]
                    cur.execute(
                        "INSERT INTO location_metrics (location_id, metric_id, metric_value, year) VALUES (%s, %s, %s, %s)",
                        (location_id, metric_id, float(hours_worked_per_week), int(year))
                    )



conn.cursor().execute(
    "INSERT INTO metrics (name, label, unit) " \
    "VALUES ('unemployment', 'Foreign Unemployment Rate', '%')")

with open(f'{SPREADSHEET_DIR}/foreign_unemployment.csv', 'r') as f:
    reader = csv.DictReader(f)
    for row in reader:
        country = row['geo']
        foreign_unemployment_rate = row['OBS_VALUE'].strip()
        year = row['TIME_PERIOD']
        if not foreign_unemployment_rate:
            continue
        with conn.cursor() as cur:
            cur.execute(
                "SELECT id FROM locations WHERE code = %s",
                (country,)
            )
            location_row = cur.fetchone()
            if location_row is not None:
                location_id = location_row[0]
                cur.execute(
                    "SELECT id FROM metrics WHERE name = 'unemployment'",
                    ()
                )
                metric_row = cur.fetchone()
                if metric_row is not None:
                    metric_id = metric_row[0]
                    cur.execute(
                        "INSERT INTO location_metrics (location_id, metric_id, metric_value, year) VALUES (%s, %s, %s, %s)",
                        (location_id, metric_id, float(foreign_unemployment_rate), int(year))
                    )


conn.cursor().execute(
    "INSERT INTO metrics (name, label, unit) " \
    "VALUES ('crime', 'Homicide Rate', 'per 100,000 population')")

with open(f'{SPREADSHEET_DIR}/crime_homicide_rate.csv', 'r') as f:
    reader = csv.DictReader(f)
    for row in reader:
        country = row['geo']
        homicide_rate = row['OBS_VALUE'].strip()
        year = row['TIME_PERIOD']
        if not homicide_rate:
            continue
        with conn.cursor() as cur:
            cur.execute(
                "SELECT id FROM locations WHERE code = %s",
                (country,)
            )
            location_row = cur.fetchone()
            if location_row is not None:
                location_id = location_row[0]
                cur.execute(
                    "SELECT id FROM metrics WHERE name = 'crime'",
                    ()
                )
                metric_row = cur.fetchone()
                if metric_row is not None:
                    metric_id = metric_row[0]
                    cur.execute(
                        "INSERT INTO location_metrics (location_id, metric_id, metric_value, year) VALUES (%s, %s, %s, %s)",
                        (location_id, metric_id, float(homicide_rate), int(year))
                    )


conn.cursor().execute(
    "INSERT INTO metrics (name, label, unit) " \
    "VALUES ('hpi', 'Housing Price Index', 'index')")

with open(f'{SPREADSHEET_DIR}/housing_price_index.csv', 'r') as f:
    reader = csv.DictReader(f)
    for row in reader:
        country = row['geo']
        housing_price_index = row['OBS_VALUE'].strip()
        year = row['TIME_PERIOD']
        if not housing_price_index:
            continue
        with conn.cursor() as cur:
            cur.execute(
                "SELECT id FROM locations WHERE code = %s",
                (country,)
            )
            location_row = cur.fetchone()
            if location_row is not None:
                location_id = location_row[0]
                cur.execute(
                    "SELECT id FROM metrics WHERE name = 'hpi'",
                    ()
                )
                metric_row = cur.fetchone()
                if metric_row is not None:
                    metric_id = metric_row[0]
                    cur.execute(
                        "INSERT INTO location_metrics (location_id, metric_id, metric_value, year) VALUES (%s, %s, %s, %s)",
                        (location_id, metric_id, float(housing_price_index), int(year))
                    )



with conn.cursor() as cur:
    cur.execute(
        "INSERT INTO metrics (name, label, unit) "
        "VALUES ('pli', 'Price Level Index (EU27_2020=100)', 'index')"
    )
 
with open(f'{SPREADSHEET_DIR}/price_level_idx.csv', 'r') as f:
    reader = csv.DictReader(f)
    for row in reader:
        country = row['geo']
        value   = row['OBS_VALUE'].strip()
        year    = row['TIME_PERIOD']
        if not value:
            continue
        with conn.cursor() as cur:
            cur.execute("SELECT id FROM locations WHERE code = %s", (country,))
            location_row = cur.fetchone()
            if location_row is None:
                continue
            location_id = location_row[0]
 
            cur.execute("SELECT id FROM metrics WHERE name = 'pli'")
            metric_id = cur.fetchone()[0]
 
            cur.execute(
                "INSERT INTO location_metrics (location_id, metric_id, metric_value, year) "
                "VALUES (%s, %s, %s, %s)",
                (location_id, metric_id, float(value), int(year))
            )

with conn.cursor() as cur:
    cur.execute(
        "INSERT INTO metrics (name, label, unit) "
        "VALUES ('transport', 'Public Transport Modal Split', '%')"
    )
 
with open(f'{SPREADSHEET_DIR}/public_transport.csv', newline='') as f:
    reader = csv.DictReader(f)
    for row in reader:
        country = row['geo']
        value   = row['OBS_VALUE'].strip()
        year    = row['TIME_PERIOD']
        if not value:
            continue
        with conn.cursor() as cur:
            cur.execute("SELECT id FROM locations WHERE code = %s", (country,))
            location_row = cur.fetchone()
            if location_row is None:
                continue
            location_id = location_row[0]
 
            cur.execute("SELECT id FROM metrics WHERE name = 'transport'")
            metric_id = cur.fetchone()[0]
 
            cur.execute(
                "INSERT INTO location_metrics (location_id, metric_id, metric_value, year) "
                "VALUES (%s, %s, %s, %s)",
                (location_id, metric_id, float(value), int(year))
            )

with conn.cursor() as cur:
    cur.execute(
        "INSERT INTO metrics (name, label, unit) "
        "VALUES ('air_quality', 'Fine Particulate Matter Average (PM2.5)', 'µg/m³')"
    )
 
with open(f'{SPREADSHEET_DIR}/air_quality_country_avg.csv', 'r') as f:
    reader = csv.DictReader(f)
    for row in reader:
        country = row['country']
        value   = row['fpm_avg'].strip()
        if not value:
            continue
        with conn.cursor() as cur:
            cur.execute("SELECT id FROM locations WHERE name = %s", (country,))
            location_row = cur.fetchone()
            if location_row is None:
                continue
            location_id = location_row[0]
 
            cur.execute("SELECT id FROM metrics WHERE name = 'air_quality'")
            metric_id = cur.fetchone()[0]
 
            cur.execute(
                "INSERT INTO location_metrics (location_id, metric_id, metric_value, year) "
                "VALUES (%s, %s, %s, %s)",
                (location_id, metric_id, float(value), 2024)
            )

conn.cursor().execute(
    "INSERT INTO users (username, email, name) VALUES (%s, %s, %s)",
    ("matt", "matt@example.com", "Matt Sample")
)


conn.commit()

conn.close()

