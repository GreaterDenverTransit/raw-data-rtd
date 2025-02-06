-- Sample OTP queries on local buses
with bus_routes_2023 as (
  select "Schedule Year" schedule_year,
         cast(sum(replace("On Time Observations",',','')) as real) on_time_observations,
         cast(sum(replace("Late Observations",',','')) as real) late_observations,
         "Line Information" line_information
  from OTP
  where "Schedule Year" = 2023 and "Route Type" = 'Local'
  group by 1, 4
)
select row_number() over (order by (b23.late_observations / (b23.on_time_observations + b23.late_observations)) desc) rank,
       (b23.late_observations / (b23.on_time_observations + b23.late_observations)) percent_late,
       b23.late_observations,
       b23.on_time_observations,
       b23.line_information,
       b23.schedule_year
from bus_routes_2023 b23;

with bus_routes_2024 as (
  select "Schedule Year" schedule_year,
         cast(sum(replace("On Time Observations",',','')) as real) on_time_observations,
         cast(sum(replace("Late Observations",',','')) as real) late_observations,
         "Line Information" line_information
  from OTP
  where "Schedule Year" = 2024 and "Route Type" = 'Local'
  group by 1, 4
)
select row_number() over (order by (b24.late_observations / (b24.on_time_observations + b24.late_observations)) desc) rank,
       (b24.late_observations / (b24.on_time_observations + b24.late_observations)) percent_late,
       b24.late_observations,
       b24.on_time_observations,
       b24.line_information,
       b24.schedule_year
from bus_routes_2024 b24;
