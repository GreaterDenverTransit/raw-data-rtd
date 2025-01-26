# -*- coding: utf-8 -*-
"""
Created on Tue Nov 26 10:09:47 2024

@author: Joe
"""

import os
import pandas as pd

def find_and_process_files(directory, column_order, output_file):
    """
    Find files ending with 'calendar.txt', reorder columns, and concatenate them.
    
    Parameters:
    - directory: Path to the folder to search for files.
    - column_order: List specifying the desired order of columns.
    - output_file: Path for the output concatenated file.
    """
    all_dataframes = []
    
    # Walk through the directory and its subfolders
    for root, _, files in os.walk(directory):
        for file in files:
            if file.endswith("calendar.txt"):
                file_path = os.path.join(root, file)
                print(f"Processing file: {file_path}")
                
                # Read the CSV file
                df = pd.read_csv(file_path)
                
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
fieldnames = ['service_id','start_date','end_date','monday','tuesday','wednesday','thursday','friday','saturday','sunday']
os.chdir(os.path.dirname(path)) # set working directory up one level from path
outfile = 'Combined_gtfs\gtfs_all_calendar.csv'

find_and_process_files(path, fieldnames, outfile)
