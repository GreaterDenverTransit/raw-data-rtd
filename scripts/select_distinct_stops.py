# -*- coding: utf-8 -*-
"""
Created on Tue Nov 12 17:34:40 2024

@author: Joe
"""

import pandas as pd

df = pd.read_csv("C:/Users/thejo/OneDrive - Re Build/Documents/Personal/Transit/RTD_Ridership/Combined_gtfs/gtfs_all_stops.csv", sep=',',dtype={'stop_id':int}).drop_duplicates(subset=['stop_id'],keep='first')
file_name = "C:/Users/thejo/OneDrive - Re Build/Documents/Personal/Transit/RTD_Ridership/Combined_gtfs/gtfs_all_stops_distinct.csv"
df.to_csv(file_name, index=False)
