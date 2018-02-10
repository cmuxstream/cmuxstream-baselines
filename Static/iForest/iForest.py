import os
import sys
import numpy as np
import pandas as pd
from sklearn.ensemble import IsolationForest
from sklearn.metrics import average_precision_score, roc_auc_score
from sklearn.metrics.ranking import auc
from sklearn.preprocessing import MinMaxScaler, scale
from scipy.io import loadmat
import pickle
import time

def read_dataset(filename):
    data = np.loadtxt(filename, delimiter=',')
    n,m = data.shape
    X = data[:,0:m-1]
    y = data[:,m-1]
    print n,m, X.shape, y.shape
    return X,y

def compute_statistics(scores, labels):
    avg_precision = average_precision_score(labels, scores)
    auc = roc_auc_score(labels, scores)
    return auc, avg_precision
    
def run_IForest(X, labels, params):
    clf = IsolationForest(n_estimators = params['n_estimators'])
    clf.fit(X, labels)
    scores = clf.decision_function(X)
    auc, ap = compute_statistics(-scores, labels)
    return auc, ap, scores
    
def run_for_dataset(in_file, out_file, num_runs, params):
    fw=open(out_file,'w')
    out_file2=out_file+"_Scores.pkl"
    print "Doing for:"+str(in_file)
    X, labels = read_dataset(in_file)
    auc_arr = []
    ap_arr = []
    score_arr = []
    for i in range(num_runs):
        auc, ap, scores = run_IForest(X, labels, params)
        print auc, ap
        auc_arr.append(auc)
        ap_arr.append(ap)
        score_arr.append(scores)
        fw.write(str(i)+"\t"+str(auc)+"\t"+str(ap)+"\n")
    fw.write(str(np.mean(auc_arr))+","+str(np.std(auc_arr))+","+str(np.mean(ap_arr))+","+str(np.std(ap_arr))+"\n")
    fw.close()
    pickle.dump(score_arr, open(out_file2,"w"))

in_file = sys.argv[1]
out_file = sys.argv[2]
num_runs = int(sys.argv[3])
params={}
params['n_estimators'] = int(sys.argv[4])
start_time = time.time()
run_for_dataset(in_file, out_file, num_runs, params)
    
