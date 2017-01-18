#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Fri Nov  4 20:08:06 2016

@author: aj
"""




import sys
import numpy as np




#Reading data from input file
#ip_file = open('hdfs:/user/hduser/gutenberg/cho.txt', 'r')




import mr_km_mapper




#Receive input from stdin
same_cl_list = []
for line in sys.stdin:
       split_line = line.split('\t')
       split_line = split_line[1].split('[')
       split_line = split_line[1].split(']')
       split_line = split_line[0].split(', ')
       for i in split_line:
              same_cl_list.append(int(i))




#Recompute cluster centroids
sum_g = 0
for i in same_cl_list:
       sum_g = sum_g + mr_km_mapper.ip_data[i-1]
new_cl = sum_g/len(same_cl_list)
print new_cl