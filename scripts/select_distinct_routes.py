# -*- coding: utf-8 -*-
"""
Created on Tue Dec 10 11:18:37 2024

@author: Joe
"""
import pandas as pd

df = pd.read_csv("C:/Users/thejo/OneDrive - Re Build/Documents/Personal/Transit/RTD_Ridership/Combined_gtfs/gtfs_all_routes.csv", sep=',',dtype={'route_id':str}).drop_duplicates(subset=['route_id','route_short_name','route_long_name'],keep='first')
file_name = "C:/Users/thejo/OneDrive - Re Build/Documents/Personal/Transit/RTD_Ridership/Combined_gtfs/gtfs_all_routes_distinct.csv"
df.to_csv(file_name, index=False)
