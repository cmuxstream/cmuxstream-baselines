function [yHat] = stream_loda_mod(X,y,winSize,sparsity,histType,maxBins,op_file)
% Streaming version of LODA - adapted from original code.
% It outputs yHat matrix to op_file
% The shape of yHat is [InputSize, 1].
% Paramters:
%   winSize - The window sie.
%   sparsity - the sparsity factor
%   histType - 'continuos'/'two-window'
%   maxBins - The maximum number of bins on which the streaming version of
%   loda should be runing.
%   op_file - The output file, in which yHat should be stored.
    
    disp('Running LODA')
    
    W=+loda_dd(X(1:winSize,:),[],0.01,sparsity,maxBins);
    %yHat_init = score_dd(X(1:winSize,:),W);
    disp('LODA Completed')
    W.W=full(W.W);
    disp('Projecting The FULL Input Data')
    XX=(+X)*W.W;	%project the input data
    disp('Fixing the Delta')
    %fix the delta
	ra=range(XX);
	mask=(ra./W.delta)>maxBins;
	if sum(mask>0)
		W.delta(mask)=ra(mask)/maxBins;
    end
    
    disp('Getting The Bin Ids')
    W.b=min(XX,[],1);	%get new shift such that we can easily acomodate all data
	idxs=bsxfun(@minus,XX,W.b);
	idxs=round(bsxfun(@rdivide,idxs,W.delta));
    
    disp('Initializing Histogram Structure')
	nBins=max(idxs(:))+1;
	nProj=size(W.W,2);
    
    disp('Normalizing Histogram Counts')
    W.N=zeros(1,nProj);		%normalization of histogram counts
	W.H=zeros(2*nBins,nProj); %histogram values
	W.window=zeros(2*winSize,nProj); %history values
    
    disp('Maintaining History')
    W.windowIdx=0;	%index to the history cache
    
    disp('Updating Histograms')
    switch histType
		case 'continuous' 
			[yHat,W.H,W.N,W.window,W.windowIdx]=onHistUpdateL(idxs,W.H,W.N,W.window,W.windowIdx,1,W.delta);
		case 'two-window'
			[yHat,W.H]=twoWinHistL(idxs,W.H,winSize,1,W.delta);
		otherwise 
			error('streamLoda_dd','unknown type of the histgoram, only continuous or two-window are allowed')
    end
    
    %yHat(1:winSize)=yHat_init;
    % just return the histogram now.
    % and save the yHat matrix to op_file
    csvwrite(op_file,[yHat y]);
end


