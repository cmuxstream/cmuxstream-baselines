function [newD,O,execTime] = streamLoda_dd(varargin)
% function [newD,O,execTime] = streamLoda_dd(X,fracrej,winSize,sparsity,histType,maxBins)
% 
% Implementation of the streamed version of the Lightweight online detector of Anomalies from the paper of the same name published in
% http://link.springer.com/article/10.1007/s10994-015-5521-0.
% The detector is a collection of one-dimensional histograms, each operating on a random projection vector.
% The detector operates in the stream mode (updating the internal states and classifying the samples simultaneously) only during training.
% During trained execution the detector operates in the batch mode.
%
% 
% Input:
% X --- dataset used for training / classification. The classifier can handle missing values, identified by NaNs.
% fracrej --- rejection rate on the testing set (default 0.05)
% winSize --- is the size of the window  (default 256)
% sparsity --- 'sparse / dense' determines whether the projection should be sparse with 1/sqrt(d) number of zero elements
%              or dense. If sparsity is a real number between zero and one, then it determines the fraction of zero elements.
% histType --- type of the online histogram (default two-window)
%              'continuous' means that histograms are continuously updated in first in last out fashion
%              'two-window' means that two histograms are used, older for classification while the new one is being constructed
% maxBins --- maximum number of bins used in the online histogram
% 
% 
% Output
% newD --- trained / untrained detector
% O --- classified dataset
% execTime --- time of the execution used to measure
% 
% (c) Tomas Pevny, pevnak@gmail.com
% The function HistOptimal optimizing the number of bins is due to Yves ROZENHOLC, yves.rozenholc@univ-paris5.fr
 
% Take care of empty/not-defined arguments:
argin = setdefaults(varargin,[],0.05,256,'dense','two-window',500);
if mapping_task(argin,'definition')
    [X,fracrej,winSize,sparsity,histType,maxBins] = deal(argin{:});
    newD = define_mapping(argin,'untrained',createName(winSize,sparsity,histType,maxBins));
    O=[];
    return;
end
 
if mapping_task(argin,'training')
    [X,fracrej,winSize,sparsity,histType,maxBins] = deal(argin{:});
 
    %optimize parameters by calling LUDA on the first batch of data
    W=+loda_dd(X(1:winSize,:),[],0.01,sparsity,maxBins);
    W.W=full(W.W);
 
    tic;
    %calculate indexes of the input data
    XX=(+X)*W.W;    %project the input data
    %fix the delta
    ra=range(XX);
    mask=(ra./W.delta)>maxBins;
    if sum(mask>0)
        W.delta(mask)=ra(mask)/maxBins;
    end
    W.b=min(XX,[],1);   %get new shift such that we can easily acomodate all data
    idxs=bsxfun(@minus,XX,W.b);
    idxs=round(bsxfun(@rdivide,idxs,W.delta));
 
    %initialize structures for histograms
    nBins=max(idxs(:))+1;
    nProj=size(W.W,2);
 
    W.N=zeros(1,nProj);     %normalization of histogram counts
    W.H=zeros(2*nBins,nProj); %histogram values
    W.window=zeros(2*winSize,nProj); %history values
    W.windowIdx=0;  %index to the history cache
    switch histType
        case 'continuous'
            [yHat,W.H,W.N,W.window,W.windowIdx]=onHistUpdateL(idxs,W.H,W.N,W.window,W.windowIdx,1,W.delta);
        case 'two-window'
            [yHat,W.H]=twoWinHistL(idxs,W.H,winSize,1,W.delta);
        otherwise 
            error('streamLoda_dd','unknown type of the histgoram, only continuous or two-window are allowed')
    end
    execTime=toc;
    %calculate the threshold
    W.fracrej=fracrej;
    W.threshold=quantile(yHat,fracrej);
 
    newD = prmapping(mfilename,'trained',W,char('target','outlier'),size(X,2),2);
    newD = setname(newD,createName(winSize,sparsity,histType,maxBins));
 
    O = setdat(X,[yHat repmat(W.threshold,size(X,1),1)],newD);
    O = setfeatdom(O,{[-inf inf;-inf inf] [-inf inf;-inf inf]});
    O=O(winSize+1:end,:);
 
    return;
end
 
if mapping_task(argin,'trained execution')
    [X,D] =argin{1:2};
    % Unpack the mapping and dataset:
    W=+D;
    yHat=zeros(size(X,1),1);
    nProj=size(W.W,2);
    nBins=size(W.H,1);
     
    %calculate indexes
    XX=(+X)*W.W;    %project the input data
    idxs=bsxfun(@minus,XX,W.b);
    idxs=round(bsxfun(@rdivide,idxs,W.delta));
    idxs=idxs+1;    %this is to move to matlab notation
 
    %remove huge outliers
    mask=idxs>=nBins;
    idxs(mask)=nBins;
    mask=idxs<1;
    idxs(mask)=1;
     
    for i=1:nProj
        Y=W.H(idxs(:,i),i)/W.N(i);
        yHat=yHat+Y;
    end
    yHat=yHat/nProj;
 
    %calculate the threshold
    O = setdat(X,[yHat repmat(W.threshold,size(X,1),1)],D);
    O = setfeatdom(O,{[-inf inf;-inf inf] [-inf inf;-inf inf]});
 
    newD=O;
end
 
end
 
 
function str=createName(winSize,sparsity,histType,maxBins)
    str=sprintf('streamLoda-winsize=%d-%s-%s',winSize,sparsity,histType);
    str=sprintf('%s-maxBins=%d',str,maxBins);
end
