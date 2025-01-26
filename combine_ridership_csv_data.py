# -*- coding: utf-8 -*-
import shutil
import glob
import os

path = r'C:\Users\thejo\OneDrive - Re Build\Documents\Personal\Transit\RTD_Ridership\csv' # folder location for all .csv ridership data
os.chdir(os.path.dirname(path)) # set working directory to directory one level up from path

allFiles = glob.glob(path + "/*.csv") # find all files with .csv extension in the path folder
allFiles.sort() # sort by ?

with open('Combined_Ridership_Data.csv','wb') as outfile: # create single file to combine all the individual csvs into
        for i, fname in enumerate(allFiles):
            with open(fname, 'rb') as infile:
                if i != 0:
                    infile.readline() # ignore header row on all files except for first one
                shutil.copyfileobj(infile, outfile)
                print(fname + " has been imported")
