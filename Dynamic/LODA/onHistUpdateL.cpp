/*This function implements on-line learning of approximate one-class nu support vector machine.
 * input:
 * X --- vector with samples
 * b --- points, in which histogram is evaluated
 * m --- count in points in b
 * free --- the first free (unused) element in b and m
 *
 * To compile on mac:
 * mex  onHistUpdateL.cpp
 */

#include "mex.h"
#include "matrix.h"
#include <math.h>
#include <string.h>
#include <algorithm>
#include <limits>

/* Translate Matlab array to C array. It is a column major notation.
 * data(i,j)=data[j*rows + i]  */
int getDoubleArray(const mxArray *arg, double** x, int* rows, int* colls) {
    if (!arg) return -1;
    *rows = (int) mxGetM(arg);
    *colls = (int) mxGetN(arg);
    if (!mxIsNumeric(arg) || mxIsComplex(arg) || mxIsSparse(arg)  || !mxIsDouble(arg) ) {
        mexPrintf("Type of array should be double");
        *x= NULL;
        return -1;
    }
    *x = mxGetPr(arg);
    return 1;
}

int getDouble(const mxArray *arg, double *x) {
    int m, n;
    
    if (!arg) return -1;
    m = mxGetM(arg);
    n = mxGetN(arg);
    if (!mxIsNumeric(arg) || mxIsComplex(arg) || mxIsSparse(arg)  || !mxIsDouble(arg) ||
            (m != 1) || (n != 1)) {
        *x = 0.0;
        return -1;
    }
    *x = mxGetScalar(arg);
    return 1;
}

/*creates new array of the same size as A and copy A to it*/
mxArray* initAndCopy(const mxArray* A){
    int m = mxGetM(A);
    int n = mxGetN(A);
    mxArray *newA=mxCreateDoubleMatrix(m,n,mxREAL);
    double *PN=mxGetPr(newA);
    double *PA=mxGetPr(A);
    std::copy(PA,PA+m*n,PN);
    return(newA);
}

void printMatrix(const mxArray *A){
    int m = mxGetM(A);
    int n = mxGetN(A);
    double *PA=mxGetPr(A);
    for (int i=0;i<m;i++){
        for (int j=0;j<n;j++){
            mexPrintf("%g ",PA[j*m+i]);
        }
        mexPrintf("\n");
    }
}


/*Main function */
void mexFunction(int nlhs,mxArray *plhs[],int nrhs,const mxArray *prhs[]){
    /*some stuff for quantization */
    double *IDXS,*H,*N, *WINDOW,*DELTA;
    int nSamples,nProj;
    double val,minCount;
    int windowIdx; 
    
    if (nrhs<7) {
        mexPrintf("\nusage: idx=fastFind(X,b)\n");
        mexPrintf("IDXS --- a matrix with examples. \n");
        mexPrintf("H --- matrix with the histogram counts == 0\n");
        mexPrintf("N --- Normalization constants for every histogram, such that counts == 0\n");
        mexPrintf("window --- history window where we store informations to keep histogram correct\n");
        mexPrintf("windowIdx --- index into the history window\n");
        mexPrintf("minCount --- minimal number of samples in the bin fro which value is returned\n");
        mexPrintf("DELTA --- quantization steps of individual projections\n");
        return;
    };

    /*get the referrence to indexes with data samples */
    if (getDoubleArray(prhs[0],&IDXS,&nSamples,&nProj)==-1){
        mexErrMsgTxt("Failed to extract vector with indexes\n");
    };

    /* copy the histogram counts */
    mxArray *histH =initAndCopy(prhs[1]);
    int nBins=mxGetM(histH);
    if (mxGetN(histH)!=nProj){
        mexErrMsgTxt("Matrix storing histogram has to have the nProj columns\n");
    }

    /* copy normalization constants */
    mxArray *histN =initAndCopy(prhs[2]);
    if (mxGetN(histN)*mxGetM(histN)!=nProj){
        mexErrMsgTxt("Vector with normalization should be a row vector of size nProj\n");
    }

    /* copy the sliding window */
    mxArray *histWin =initAndCopy(prhs[3]);
    int windowLength=mxGetM(histWin);
    if (mxGetN(histWin)!=nProj){
        mexErrMsgTxt("Window has to be a matrix with nProj number of columns size nProj\n");
    }
    
    if (getDouble(prhs[4],&val)==-1){
        mexErrMsgTxt("Failed to extract index of the first free position in the history buffer\n");
    };
    windowIdx=val;

    if (getDouble(prhs[5],&val)==-1){
        mexErrMsgTxt("Failed to extract index of the first free position in the history buffer\n");
    };
    minCount=val;

    /* get the quantization steps */
    DELTA=mxGetPr(prhs[6]);
    if (mxGetN(prhs[6])!=nProj){
        mexErrMsgTxt("Quantization steps has to be the row vector of length nProj.\n");
    }

    /*alocate space for results */
    mxArray *outVal = mxCreateDoubleMatrix(nSamples,1,mxREAL);
    double *O=mxGetPr(outVal);

    H=mxGetPr(histH);
    N=mxGetPr(histN);
    WINDOW=mxGetPr(histWin);

    /*iterate over all samples and do not forget to take advantage of column-mjor matrix; */
    int sampleIdx=0;
    for (int i=0;i<nSamples;i++){
        O[i]=0;
        double updated=0;
    
        for (int j=0;j<nProj;j++){
            int idx=IDXS[j*nSamples+i];

           /*pull out the output of the histogram from this given point */
           /*update the final output */
            O[i]+=(H[j*nBins+idx]/(N[j]*DELTA[j])>0)?log(H[j*nBins+idx]/(N[j]*DELTA[j])):log(1e-10);
            updated+=+11;

            /*update the current histogram by C */
            H[j*nBins+idx]+=1;
            N[j]+=1;

            // mexPrintf("windowIdx =  %d \n",windowIdx);
            // mexPrintf("adding to %d a value %f ",idx,C);

            /*remove the contribution of the oldest point in the buffer*/
            int idxFromWin=WINDOW[j*windowLength+windowIdx];
            int cFromWin=WINDOW[j*windowLength+windowIdx+1];
            H[j*nBins+idxFromWin]-=cFromWin;
            N[j]-=cFromWin;
            // mexPrintf("subtracting from %d a value %f\n ",idxFromWin,cFromWin);

            /*finally, update the buffer */
            WINDOW[j*windowLength+windowIdx]=idx;
            WINDOW[j*windowLength+windowIdx+1]=1;
           }
        // mexPrintf("\n");
        O[i]=(updated>0)?O[i]/updated:std::numeric_limits<double>::quiet_NaN();

        /*update the buffer index */
        windowIdx+=2;
        windowIdx=(windowIdx>=windowLength)?0:windowIdx;
    }

    if (nlhs>0) plhs[0]=outVal;
    if (nlhs>1) plhs[1]=histH;
    if (nlhs>2) plhs[2]=histN;
    if (nlhs>3) plhs[3]=histWin;
    if (nlhs>4) plhs[4]=mxCreateDoubleScalar(windowIdx);
    return;
}