function [yHat] = score_dd(X,D)
    [l,d]=size(X);
    W=+D;
    %W=D;
	yHat=zeros(size(X,1),1);
	nProj=size(W.W,2);
	nBins=size(W.H,1);
    
    %calculate indexes
	XX=(+X)*W.W;	%project the input data
	idxs=bsxfun(@minus,XX,W.b);
	idxs=round(bsxfun(@rdivide,idxs,W.delta))+1;
    
    %remove huge outliers
	idxs(idxs>=nBins)=nBins;
	idxs(idxs<1)=nBins;
    
    yHat=zeros(l,1);
    updates=zeros(l,1);	%this holds, how many times was particular element updated
	for i=1:size(idxs,2)
        mask=~isnan(idxs(:,i));	%remove nans
		oY=log(W.H(idxs(mask,i),i));
		yHat(mask)=yHat(mask)+oY;
		updates(mask)=updates(mask)+1;
    end
    %yHat = yHat./size(idxs,2);
    %yHat = log(yHat./updates);
    yHat=exp(yHat./updates);
end

