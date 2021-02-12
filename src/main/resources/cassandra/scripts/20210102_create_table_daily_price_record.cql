CREATE TABLE IF NOT EXISTS daily_price_record (
  symbol text,
  year int,
  month int,
  day int,
  value double,
  previous_value double,
  value_change double,
  percent_change double,
  share_volume int,
  Primary Key ((symbol), year, month, day)
);

CREATE TABLE IF NOT EXISTS company (
    symbol text,
    name text,
    address text,
    Primary Key ((symbol), name)
);