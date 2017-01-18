# -*- coding: utf-8 -*-
"""
Created on Sat Oct 22 20:24:55 2016

@author: AJ
"""




import collections
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.colors as colors
from sklearn.decomposition import PCA







####################---------- Specify files ----------####################
ip_file_g = open('cho.txt', 'r')
ip_file_c = open('res_cho_hier.txt', 'r')




####################---------- P matrix calculation ----------####################

#Read data from input file and create a list of assigned clusters
cluster_list = []
       
for line in ip_file_g:
    split_line = line.split('\t')
    cluster_list.append(int(split_line[1]))


#Create P matrix and fill it with all zeros
p = collections.defaultdict(list)

for row in range(0,len(cluster_list)):
    p_row_list = [0]*len(cluster_list)
    p[row+1] = p_row_list


#Enumerate over the cluster list and find out the unique clusters
cluster_enumerate = list(enumerate(cluster_list, start=1))

cluster_set = set(cluster_list)


#Find items in same cluster and for items in same cluster assign entry in P matrix as 1
for cluster_id in cluster_set:
    same_cluster = []
    for pair in cluster_enumerate:
        if pair[1] == cluster_id:
            same_cluster.append(pair[0])
    for row in same_cluster:
        for col in same_cluster:
            p[row][col-1] = 1

print '\nP matrix calculated!'








####################---------- C matrix calculation ----------####################
#Read data from input file and create a list of assigned clusters
cluster_list = []

for line in ip_file_c:
    split_line = line.split('\t')
    cluster_list.append(int(split_line[1]))


#Create C matrix and fill it with all zeros
c = collections.defaultdict(list)

for row in range(0,len(cluster_list)):
    c_row_list = [0]*len(cluster_list)
    c[row+1] = c_row_list


#Enumerate over the cluster list and find out the unique clusters
cluster_enumerate = list(enumerate(cluster_list, start=1))

cluster_set = set(cluster_list)


#Find items in same cluster and for items in same cluster assign entry in C matrix as 1
for cluster_id in cluster_set:
    same_cluster = []
    for pair in cluster_enumerate:
        if pair[1] == cluster_id:
            same_cluster.append(pair[0])
    for row in same_cluster:
        for col in same_cluster:
            c[row][col-1] = 1

print '\nC matrix calculated!'








####################---------- M value calculations ----------####################
m11 = 0
m00 = 0
m10 = 0
m01 = 0

for row in range(0,len(p)):
    for col in range(0,len(p)):
        if c[row+1][col] == 1:
            if p[row+1][col] == 1:
                m11 += 1
            elif p[row+1][col] == 0:
                m10 += 1
        if c[row+1][col] == 0:
            if p[row+1][col] == 0:
                m00 += 1
            elif p[row+1][col] == 1:
                m01 += 1

print '\nM11 =',m11
print 'M00 =',m00
print 'M10 =',m10
print 'M01 =',m01








####################---------- Rand and Jaccard calculations ----------####################
rand = (m11+m00)/float((m11+m00+m10+m01))
print '\nRand index =',rand

jac = (m11)/float((m11+m10+m01))
print '\nJaccard coefficient =',jac








####################---------- PCA ----------####################
print '\n\n##########----- PCA -----##########\n\n'

####################---------- Specify files ----------####################
ip_file_g = open('cho.txt', 'r')
ip_file_c = open('res_cho_hier.txt', 'r')




ip_data_list = []
ip_label_list = []

for line in ip_file_g:
    split_line = line.split('\t')
    temp_row_list = []
    for col_iter in range(0,len(split_line)-2):
        temp_row_list.append(float(split_line[col_iter+2]))
    ip_data_list.append(temp_row_list)
    ip_label_list.append(float(split_line[1]))

X = np.array(ip_data_list) #Input data
Y_g = np.array(ip_label_list) #Ground truth labels
Y_g_u = np.unique(Y_g) #Unique ground truth labels




ip_label_list = []

for line in ip_file_c:
    split_line = line.split('\t')
    ip_label_list.append(float(split_line[1]))

Y_c = np.array(ip_label_list)
Y_c_u = np.unique(Y_c)




pca_data = PCA(n_components=2).fit_transform(X)
x_min, x_max = pca_data[:, 0].min() - 1, pca_data[:, 0].max() + 1
y_min, y_max = pca_data[:, 1].min() - 1, pca_data[:, 1].max() + 1
col_list_10 = ['r', 'g', 'b', 'y', 'm', 'c', 'b', '#ff8000', '#40ff00', '#bf00ff']
col_list = []
for name, hexval in colors.cnames.iteritems():
    col_list.append(hexval)




plt.figure(1, figsize=(8,6))
plt.clf()
plt.title('Clustering based on the ground truth')
plt.xlim(x_min,x_max)
if Y_g_u.shape[0]<10:
    for i in Y_g_u:
        if i != -1:
            plt.scatter(pca_data[np.where(Y_g == i)[0],0],pca_data[np.where(Y_g == i)[0],1],c=col_list_10[int(i)-1],marker='o')
else:
    for i in Y_g_u:
        if i != -1:
            plt.scatter(pca_data[np.where(Y_g == i)[0],0],pca_data[np.where(Y_g == i)[0],1],c=col_list[int(i)-1],marker='o')
plt.scatter(pca_data[np.where(Y_g == -1)[0],0],pca_data[np.where(Y_g == -1)[0],1],c='k',marker='x')
plt.savefig('1_hier.png', dpi=600)




plt.figure(2, figsize=(8,6))
plt.clf()
plt.title('Clustering based on our algorithm')
plt.xlim(x_min,x_max)
if Y_c_u.shape[0]<=10:
    for i in Y_c_u:
        if i != -1:
            plt.scatter(pca_data[np.where(Y_c == i)[0],0],pca_data[np.where(Y_c == i)[0],1],c=col_list_10[int(i)-1],marker='o')
else:
    for i in Y_c_u:
        if i != -1:
            plt.scatter(pca_data[np.where(Y_c == i)[0],0],pca_data[np.where(Y_c == i)[0],1],c=col_list[int(i)-1],marker='o')
plt.scatter(pca_data[np.where(Y_c == -1)[0],0],pca_data[np.where(Y_c == -1)[0],1],c='k',marker='x')
plt.savefig('2_hier.png', dpi=600)
