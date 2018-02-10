import os
import sys
import numpy as np
import pandas as pd
from sklearn.metrics import average_precision_score, roc_auc_score
from sklearn.preprocessing import MinMaxScaler, scale
from scipy.io import loadmat
import pickle
import time

def compute_statistics(scores, labels):
    avg_precision = average_precision_score(labels, scores)
    auc = roc_auc_score(labels, scores)
    return auc, avg_precision

class RSHash(object):
    
    def __init__(self,
                 data,
                 labels,
                 num_components=100,
                 sampling_points=1000,
                 num_hash_fns=1,
                 random_state=None,
                 ):
        self.m = num_components
        self.w = num_hash_fns
        self.s = min(sampling_points,data.shape[0])
        self.X = data
        self.labels = labels
        self.scores = []
        
    def multi_runs(self):
        for i in range(self.m):
            self.scores.append(self.single_run())
    
    def single_run(self):
        minimum = self.X.min(axis=0)
        maximum = self.X.max(axis=0)
        
        hash_functions=[]
        for i in range(self.w):
            hash_functions.append({})
        
        # Select the locality parameter
        f = np.random.uniform(low=1.0/np.sqrt(self.s), high = 1-1.0/np.sqrt(self.s))
        
        # Generate a d-dimensional random vectors
        alpha = np.zeros(self.X.shape[1],)
        for i in range(self.X.shape[1]):
            alpha[i] = np.random.uniform(low=0, high = f)
        
        # Select integer r (dimensions) to be extracted from dataset
        low = 1+0.5 * (np.log(self.s)/np.log(max(2,1.0/f)))
        high = np.log(self.s)/np.log(max(2,1.0/f))
        r = int(np.random.uniform(low,high))
        if(r>self.X.shape[1]):
            r = self.X.shape[1]
        
        # Select r dimensions from the dataset.
        V = np.random.choice(range(self.X.shape[1]),r,replace=False)
        filtered_V = V[np.where(minimum[V]!=maximum[V])]
        
        # Randomly sample dataset S of s points.
        selected_indexes = np.random.choice(range(self.X.shape[0]), self.s, replace=False)
        S = self.X[selected_indexes,:]
        
        #Normalize S
        norm_S =  (S -  minimum)/(maximum -  minimum)
        #Setting range(S) norm value to 0
        norm_S[np.abs(norm_S) == np.inf] = 0
        
        # Shift and Set Y
        Y = -1 * np.ones([S.shape[0], S.shape[1]])
        
        for j in range(Y.shape[1]):
            if j in filtered_V:
                Y[:,j] = np.floor((norm_S[:,j]+alpha[j])/float(f))
        
        # Apply w different hash functions
        for vec in Y:
            vec = tuple(vec.astype(np.int))
            for i in range(self.w):
                if (vec in hash_functions[i].keys()):
                    hash_functions[i][vec]+=1
                else:
                    hash_functions[i][vec]=1
                    
        # SCORING THE SAMPLE
        # Transform each point
        norm_X =  (self.X -  minimum)/(maximum -  minimum)
        norm_X[np.abs(norm_X) == np.inf] =0
        score_Y = -1 * np.ones([self.X.shape[0], self.X.shape[1]])
            
        for j in range(score_Y.shape[1]):
            if j in filtered_V:
                score_Y[:,j] = np.floor((norm_X[:,j]+alpha[j])/float(f))
        
        score_arr=[]
        for index in range(score_Y.shape[0]):
            vec = score_Y[index]            
            vec = tuple(vec.astype(np.int))
            c = [0]*self.w
            for i in range(self.w):
                if (vec in hash_functions[i].keys()):
                    c[i] = hash_functions[i][vec]
                else:
                    c[i] = 0
            
            if index in selected_indexes:
                score_arr.append(np.log2(min(c)))
            else:
                score_arr.append(np.log2(min(c)+1))
        
        return np.array(score_arr)
    
def read_dataset(filename):
    data = np.loadtxt(filename, delimiter=',')
    n,m = data.shape
    X = data[:,0:m-1]
    y = data[:,m-1]
    print n,m, X.shape, y.shape
    
    return X,y

def run_RSHash(X, labels, params):
    rs_obj = RSHash(X,labels, params['num_components'], params['sampling_points'])
    rs_obj.multi_runs()
    anomaly_scores = np.mean(rs_obj.scores,axis=0)
    auc,ap = compute_statistics(-anomaly_scores, labels)
    return auc, ap, anomaly_scores

def run_for_dataset(in_file, out_file, num_runs, params):
    start_time = time.time()
    fw=open(out_file,'w')
    out_file2=out_file+"_Scores.pkl"
    print "Doing for:"+str(in_file)
    X, labels = read_dataset(in_file)
    auc_arr = []
    ap_arr = []
    score_arr = []
    for i in range(num_runs):
        auc, ap, scores = run_RSHash(X, labels, params)
        auc_arr.append(auc)
        ap_arr.append(ap)
        score_arr.append(scores)
        fw.write(str(i)+"\t"+str(auc)+"\t"+str(ap)+"\n")
    fw.write(str(np.mean(auc_arr))+","+str(np.std(auc_arr))+","+str(np.mean(ap_arr))+","+str(np.std(ap_arr))+"\n")
    fw.close()
    pickle.dump(score_arr, open(out_file2,"w"))
    print "Time Taken="+str(time.time() - start_time)+ " for:"+in_file
    
print "Running RSHash"
in_file = sys.argv[1]
out_file = sys.argv[2]
num_runs = int(sys.argv[3])

params = {}
params['num_components'] = int(sys.argv[4])
params['sampling_points'] = int(sys.argv[5])

run_for_dataset(in_file, out_file, num_runs, params)
