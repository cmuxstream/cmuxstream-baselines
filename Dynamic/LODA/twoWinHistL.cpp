/*This function implements on-line learning of approximate one-class nu support vector machine.
 * input:
 * X --- vector with samples
 * b --- points, in which histogram is evaluated
 * m --- count in points in b
 * free --- the first free (unused) element in b and m
 *
 * To compile on mac:
 * mex -lblas -I/System/Library/Frameworks/Accelerate.framework/Versions/Current/Frameworks/vecLib.framework/Versions/Current/Headers approxOCSVM.c
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
    double *IDXS,*H,*DELTA;
    int nSamples,nProj;
    double val,minCount;
    int winSize; 
    
    if (nrhs<5) {
        mexPrintf("\nusage: idx=fastFind(X,b)\n");
        mexPrintf("IDXS --- a matrix with examples. \n");
        mexPrintf("H --- matrix with the histogram counts == 0\n");
        mexPrintf("winSize --- size of the window\n");
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

    if (getDouble(prhs[2],&val)==-1){
        mexErrMsgTxt("Failed to extract index of the first free position in the history buffer\n");
    };
    winSize=val;

    if (getDouble(prhs[3],&val)==-1){
        mexErrMsgTxt("Failed to extract index of the first free position in the history buffer\n");
    };
    minCount=val;

    /* get the quantization steps */
    DELTA=mxGetPr(prhs[4]);
    if (mxGetN(prhs[4])!=nProj){
        mexErrMsgTxt("Quantization steps has to be the row vector of length nProj.\n");
    }

    /*alocate space for results */
    mxArray *outVal = mxCreateDoubleMatrix(nSamples,1,mxREAL);
    double *O=mxGetPr(outVal);

    double *readingWindow=mxGetPr(histH);
    mxArray *histH2=mxCreateDoubleMatrix(nBins,nProj,mxREAL);
    double *updatingWindow=mxGetPr(histH2);
    memset(updatingWindow,0,sizeof(double)*nBins*nProj);

    /*precalculate step */
    double *updateStep=(double*)mxCalloc(nProj,sizeof(double));
    for (int j=0;j<nProj;j++){
        updateStep[j]=1.0/(winSize*DELTA[j]);
    }
    
    /*iterate over all samples and do not forget to take advantage of column-major matrix; */
    for (int i=0;i<nSamples;i++){
        O[i]=0;
        double updated=0;
    
        for (int j=0;j<nProj;j++){
            int idx=IDXS[j*nSamples+i];
            if (idx>=nBins){
                continue;
            }

           /*pull out the output of the histogram from this given point */
           /*update the final output */
            int histIdx=j*nBins+idx;
            O[i]+=(readingWindow[histIdx]>0)?log(readingWindow[histIdx]):log(1e-99);

            /*update the current histogram by C */
            updatingWindow[histIdx]+=updateStep[j];
           }
        O[i]=O[i]/nProj;

        /*if we need to switch windows*/
        if ((i+1)%winSize==0){
            /* update  the pointers to windows */
            double *tmp=readingWindow;
            readingWindow=updatingWindow;
            updatingWindow=tmp;
            memset(updatingWindow,0,sizeof(double)*nBins*nProj);
        }
    }
    mxFree(updateStep);

    if (nlhs>0) plhs[0]=outVal;
    if (nlhs>1){
        if (readingWindow==mxGetPr(histH)){
            plhs[1]=histH;
            mxDestroyArray(histH2);
        } else {
            plhs[1]=histH2;
            mxDestroyArray(histH);
        }
    } 
    return;
}