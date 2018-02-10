import os
import sys
import numpy as np
import pandas as pd
from sklearn.metrics import average_precision_score, roc_auc_score
from sklearn.preprocessing import MinMaxScaler, scale
from loda import *
from loda_support import *
from ensemble_support import *
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

def run_LODA(X, labels):
    lodares = loda(X)
    auc, ap = compute_statistics(lodares.nll, labels)
    print "AUC="+str(auc)+ " & AP="+str(ap)
    return auc, ap, lodares.nll
    
    
def run_for_dataset(in_file, out_file, num_runs):
    start_time = time.time()
    fw=open(out_file,'w')
    out_file2=out_file+"_Scores.pkl"
    print "Doing for:"+str(in_file)
    X, labels = read_dataset(in_file)
    auc_arr = []
    ap_arr = []
    score_arr = []
    for i in range(num_runs):
        auc, ap, scores = run_LODA(X, labels)
        auc_arr.append(auc)
        ap_arr.append(ap)
        score_arr.append(scores)
        fw.write(str(i)+"\t"+str(auc)+"\t"+str(ap)+"\n")
    fw.write(str(np.mean(auc_arr))+","+str(np.std(auc_arr))+","+str(np.mean(ap_arr))+","+str(np.std(ap_arr))+"\n")
    fw.close()
    pickle.dump(score_arr, open(out_file2,"w"))
    print "Time Taken="+str(time.time() - start_time)+ " for:"+str(in_file)

print "Running LODA"
in_file = sys.argv[1]
out_file = sys.argv[2]
num_runs = int(sys.argv[3])
run_for_dataset(in_file, out_file, num_runs)

