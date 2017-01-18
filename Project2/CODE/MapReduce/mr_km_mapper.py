#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Fri Nov  4 17:10:20 2016

@author: aj
"""




import sys
import numpy as np
from scipy.spatial import distance
import random
from collections import defaultdict




global ip_data




def dist(x,y):
    return distance.euclidean(x,y)








#Reading data from input file
#ip_file = open('hdfs:/user/hduser/gutenberg/cho.txt', 'r')

ip_data_list = []
ip_label_list = []

for line in sys.stdin:
    split_line = line.split('\t')
    temp_row_list = []
    for col_iter in range(0,len(split_line)-2):
        temp_row_list.append(float(split_line[col_iter+2]))
    ip_data_list.append(temp_row_list)
    ip_label_list.append(float(split_line[1]))

ip_data = np.array(ip_data_list) #Input data
ip_label = np.array(ip_label_list) #Ground truth labels

#print '\nInput data read!'








#Calculate the distance matrix
dist_matrix_list = []

for i in ip_data:
    row_dist_list = []
    for j in ip_data:
        row_dist_list.append(dist(i,j))
    dist_matrix_list.append(row_dist_list)

dist_matrix = np.array(dist_matrix_list)

#print '\nDistance matrix calculated!'








#Set the parameters & pick initial cluster centroids
k = 2 #Number of clusters
n = 20 #Number of iterations
ini_cl = random.sample(range(1, ip_data.shape[0]), k)  #Choose random initial clusters
#print '\nPicked initial clusters =',ini_cl

cluster_assignments = defaultdict(list)

for i in range(0,len(dist_matrix)):
       temp_dist_list = []
       for j in ini_cl:
              temp_dist_list.append(dist_matrix[i][j-1])
#       print 'Point',i+1,'will be assigned to cluster',temp_dist_list.index(min(temp_dist_list))+1
       cluster_assignments[temp_dist_list.index(min(temp_dist_list))+1].append(i+1)

#print '\nCluster assignments =',cluster_assignments
#print '\n\n\nCluster assignments:'
for key,val in cluster_assignments.iteritems():
       print key,'\t',[i for i in val]