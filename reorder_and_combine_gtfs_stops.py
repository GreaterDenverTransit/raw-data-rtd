# -*- coding: utf-8 -*-
"""
Created on Tue Nov 26 10:09:47 2024

@author: Joe
"""

import os
import pandas as pd

def find_and_process_files(directory, column_order, output_file):
    """
    Find files ending with 'stops.txt', reorder columns, and concatenate them.
    
    Parameters:
    - directory: Path to the folder to search for files.
    - column_order: List specifying the desired order of columns.
    - output_file: Path for the output concatenated file.
    """
    all_dataframes = []
    
    # Walk through the directory and its subfolders
    for root, _, files in os.walk(directory):
        for file in files:
            if file.endswith("stops.txt"):
                file_path = os.path.join(root, file)
                print(f"Processing file: {file_path}")
                
                # Read the CSV file
                df = pd.read_csv(file_path)
                df['stop_code'] = df.get('stop_code',df['stop_id']) # add stop_code column if missing, populate with stop_id value
                df['stop_name'] = df.get('stop_name','')
                df['stop_desc'] = df.get('stop_desc','')
                df['zone_id'] = df.get('zone_id','')
                df['stop_url'] = df.get('stop_url','')
                df['location_type'] = df.get('location_type','')
                df['parent_station'] = df.get('parent_station','')
                df['stop_timezone'] = df.get('stop_timezone','')
                df['wheelchair_boarding'] = df.get('wheelchair_boarding','')
                df['stop_code'] = df['stop_code'].astype(str) # convert trip_id column to string (currently column could be mixed string and integer datatypes)
                df['stop_code'] = df['stop_code'].str.replace(r'_.*', '', regex=True) # remove any instances of underscored text following the trip_id
                df['stop_code'] = df['stop_code'].astype(int) # convert back to integer
                df['stop_id'] = df['stop_id'].astype(str)
                df['stop_id'] = df['stop_id'].str.replace(r'_.*', '', regex=True)
                df['stop_id'] = df['stop_id'].astype(int)
                df['zone_id'] = df['zone_id'].astype(str)
                df['zone_id'] = df['zone_id'].str.replace(r'_.*', '', regex=True)
                # Reorder columns
                if not set(column_order).issubset(df.columns):
                    print(f"Warning: Some specified columns not in {file_path}")
                    continue  # Skip files that don't match the column_order
                
                df = df[column_order]  # Reorder columns
                all_dataframes.append(df)
    
    # Concatenate all dataframes
    if all_dataframes:
        combined_df = pd.concat(all_dataframes, ignore_index=True)
        
        # Save the combined dataframe to the output file
        combined_df.to_csv(output_file, index=False)
        print(f"Combined file saved to {output_file}")
    else:
        print("No matching files found or no valid data to concatenate.")

# User input
path = r'C:\Users\thejo\OneDrive - Re Build\Documents\Personal\Transit\RTD_Ridership\gtfs' # folder location for all .csv ridership data
fieldnames = ['stop_id','stop_code','stop_name','stop_desc','stop_lat','stop_lon','zone_id','stop_url','location_type','parent_station','stop_timezone','wheelchair_boarding']
os.chdir(os.path.dirname(path)) # set working directory up one level from path
outfile = 'Combined_gtfs\gtfs_all_stops.csv'

find_and_process_files(path, fieldnames, outfile)
