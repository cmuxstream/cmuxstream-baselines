function [O,fScore] = loda_dd(varargin)
% D = loda_dd(X,fracrej,nProj,sparsity,maxBins)
% [O,fScore] = loda_dd(X,D)
 % Implementation of the batch version of the Lightweight online detector of Anomalies from the paper of the same name published in
% http://link.springer.com/article/10.1007/s10994-015-5521-0.
% The detector is a collection of one-dimensional histograms, each operating on a random projection vector.
%
% 
% Input:
% X --- dataset used for training / classification. The classifier can handle missing values, identified by NaNs.
% fracrej --- rejection rate on the testing set (default 0.05)
% nProj --- integer number greater than one specify number of projections
%       --- real between between 0 and 1 is the threshold used in automatic determination of number of projections.
% 					In all experiments in the paper it has been used tau = 0.01, which is the default.
% 					See section 4.1.1 for details.
% sparsity --- 'sparse / dense' determines whether the projection should be sparse with 1/sqrt(d) number of zero elements
% 					or dense. If sparsity is a real number between zero and one, then it determines the fraction of zero elements.
% maxBins --- maximum number of bins in the histogram. (default l/log(l) )
% 
% Ouput
% D --- trained / untrained detector
% O --- classified dataset
% fScore --- matrix with feature's contribution to the anomality of the sample. One row contains features relevances for one sample.
% 
% (c) Tomas Pevny, pevnak@gmail.com
% The function HistOptimal optimizing the number of bins is due to Yves ROZENHOLC, yves.rozenholc@univ-paris5.fr

% Take care of empty/not-defined arguments:
argin = setdefaults(varargin,[],0.05,0.01,'dense',500);

if mapping_task(argin,'definition')
	[a,fracrej,nProj,sparsity] = deal(argin{:});
	O = define_mapping(argin,'untrained',createName(nProj,sparsity));
	return;
end

if mapping_task(argin,'training')
	[a,fracrej,nProj,sparsity,maxBins] = deal(argin{:});
	name=createName(nProj,sparsity);
	%this is to prevent endless cycles...
	if nProj<1;
		tau=nProj;
		nProj=1000;
	else
		tau=0;
	end;


	[l,d]=size(a);
	%set the sparsity as desired
	if ischar(sparsity)
		switch sparsity
		case 'dense'
			sparsity=0;
		case 'sparse'
			sparsity=1-1/sqrt(d);
		end
	end

	if isempty(maxBins)
		maxBins=ceil(l/log(l));
	end

	%initialization of structures;
	W=struct;
	yHat=zeros(l,1);	%this holds the current sum of probabilities on projections
	updates=zeros(l,1);	%this holds, how many times was particular element updated

	previousEst=zeros(l,1); % here is the previous estimates
	W.variance=ones(3,1); %here we store the progress in the W.variance of the estimate
	W.W=zeros(d,nProj);
	W.delta=zeros(1,nProj);
	W.H=zeros(maxBins,nProj);
	vI=1;
	for i=1:nProj
		%generate the current random projection. If desired, make it sparse. The vector is
		%normalized to have unit length
		w=randn(d,1);
		if sparsity>0 && sparsity<1
			mask=randsample(d,round(sparsity*d));
			w(mask)=0;
			w=sparse(w);
		end
		w=w/norm(w);

		%create histograms of projections
		%we need to deal with nans, that can occur due to missing variables
		X=+a*w;	%project the input data
		mask=~isnan(X);	%remove nans

		%if we have samples, we can proceed to the construction of the detector
		if sum(mask)>0
			W.W(:,i)=w;
	  	%create quantization steps and indexes for the histogram 
	  	
	  	if range(X(mask))>0
	  		nBins=size(HistOptimal(X(mask),[],false),2);
	  		if nBins>maxBins
	  			nBins=maxBins;
	  		end
	  		W.delta(i)=range(X(mask))/nBins;
	  	else
	  		W.delta(i)=1;
	  	end

	  	W.b(i)=min(X);
	  	idxs=round((X(mask)-W.b(i))/W.delta(i));
	  	W.H(max(idxs)+2,nProj)=0;	%this is to ensure that histogram will fit

			%create the histogram and pull out answers on the dataset
			oneHist=histc(idxs,0:(size(W.H,1)-1));
			W.H(:,i)=oneHist/(sum(oneHist)*W.delta(i));
			yHat(mask)=yHat(mask)+log(W.H(idxs+1,i));
			updates(mask)=updates(mask)+1;

			iHat=yHat./updates;
			W.variance(vI)=nanmean(abs(previousEst-iHat));
			previousEst=iHat;

			W.variance(2)=W.variance(2)+(W.variance(2)==0);
			%if the variance is below threshold, than stop adding new
			if W.variance(vI)/W.variance(2)<tau;
				break;
			end;
			vI=vI+1;
		end
	end
	yHat=yHat./updates;

	%remove histograms that was impossible to construct
	%note that the appropriate projection has to be excluded as well
	mask=sum(W.H)>0;
	W.H=W.H(:,mask);
	W.W=W.W(:,mask);
	W.delta=W.delta(mask);

	%set default value for samples we have not observed
	W.H(W.H==0)=1e-10;
	W.H(end,:)=1e-10;

	%this is needed for handling nans
	if sparsity>0
		W.W=sparse(W.W);
	end

	%calculate the threshold
	W.fracrej=fracrej;
	W.threshold=quantile(exp(yHat),fracrej);
	O = prmapping(mfilename,'trained',W,char('target','outlier'),size(a,2),2);
	O = setname(O,name);
	fScore=[];
	return;
end

if mapping_task(argin,'trained execution')
	%here, we output only the anomaly scores
	[X,D] =argin{1:2};
	% Unpack the mapping and dataset:
	[l,d]=size(X);
	W=+D;
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

	%if we do not need output about feauter's cotribution to the outlier score
	if nargout < 2
		yHat=zeros(l,1);
		updates=zeros(l,1);	%this holds, how many times was particular element updated
		for i=1:size(idxs,2)
			mask=~isnan(idxs(:,i));	%remove nans
			oY=log(W.H(idxs(mask,i),i));
			yHat(mask)=yHat(mask)+oY;
			updates(mask)=updates(mask)+1;
		end
		yHat=exp(yHat./updates);

	else
		nProj=size(W.W,2);
		yHat=nan(l,nProj);
		for i=1:nProj
	  	mask=~isnan(idxs(:,i));	%remove nans
	  	oY=log(W.H(idxs(mask,i),i));
	  	yHat(mask,i)=oY;
	  end

    %try to determine which features are responsible for the point being anomalous
    %the score of every feature on every sample will be stored in SC
    fScore=nan(l,d);
    for i=1:size(yHat,1)
    	for j=1:size(W.W,1)
    		mask=(W.W(j,:))~=0;
    		active=yHat(i,mask);
    		active=active(~isnan(active));
    		absent=yHat(i,~mask);
    		absent=absent(~isnan(absent));
    		if isempty(active) | isempty(absent)
    			fScore(i,j)=nan;
    			tto(i,j)=nan;
    			mww(i,j)=nan;
    		else
	    		fScore(i,j)=nanmean(active) - nanmean(absent);
				end
    	end
    end
    yHat=exp(nanmean(yHat,2));
  end
  
  newout = [yHat repmat(W.threshold,size(X,1),1)];

	% Fill in the data, keeping all other fields in the dataset intact:
	O = setdat(X,newout,D);
	O = setfeatdom(O,{[-inf inf;-inf inf] [-inf inf;-inf inf]});
end

end


function str=createName(nProj,sparsity)
	if nProj<1
		str=sprintf('loda-tau=%g',nProj);
	else
		str=sprintf('loda-n=%d',nProj);
	end
	if ischar(sparsity)
		str=sprintf('%s-%s',str,sparsity);
	else
		str=sprintf('%s-spars=%.2f',str,sparsity);
	end
end


function [H,LVpen] = HistOptimal(Y, Dmax, withfig)

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% PERFORMS DENSITY ESTIMATION using PENALIZED REGULAR HISTOGRAM, author Yves ROZENHOLC
%
% Compute automaticaly the number of bins of the classical density estimator, 
% the regular histogram, and return description of the obtained histogram(s). The 
% number(s) of bins chose by the algorithm derived from the works of 
%
%%%%%%%%%%%%%%%%%% MODEL and COMMANDE LINE %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% MODEL : 
% Y n-sample of an UNKNOWN density f 
%
% COMMANDE LINE : 
%
% H = HistOptimal(Y, Dmax, withfig)
% [H,LVpen] = HistOptimal(Y, Dmax, withfig)
%
% INPUT
%      Y, the observations - an n sample of an unknown density f
%      Dmax, the maximum number of bins of the histogram
%           Default value [n/log(n)].
%           For very large n, use smaller value to reduce the computation time.
%      withfig, if 1 plot the selected histogram 
%           Default 1.
%
% OUTPUT
%      H, the selected histogram 
%      LVpen, the penalized Log-Likelihood of the selected histogram
%
%%%%%%%%%%%%%%%%%%%%% CONNECTED PAPERS %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% HOW MANY BINS SHOULD BE PUT IN A REGULAR HISTOGRAM.
% by L. BIRG\'E and Y. ROZENHOLC in ESAIM P&S
%
% Other references :
%
% 	A. Barron, L. Birg'e, P.Massart 1995 
%
% 	G. Castellan 1998 
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% for details you can contact
% 	Yves ROZENHOLC
% 	yves.rozenholc@univ-paris5.fr
%
% version du 17/03/00
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

if (nargin<1) exit; end;

if (size(Y, 2)==1) Y=Y'; end;
n = length(Y);
if (nargin<2) Dmax = floor(n/log(n)); end;
if isempty(Dmax) Dmax = floor(n/log(n)); end;
if length(Dmax)>1, D=Dmax; Dmax=max(D); else D=1:Dmax; end;
	% les nbres de cases 
if (nargin<3) withfig=1; end;

pen = (D-1) + log(D).^2.5;

mini=min(Y); maxi=max(Y);
if (mini<0)||(maxi>1)
	Y = (Y - mini)/(maxi - mini); 
else
   mini=0; maxi=1;
end;

LV = zeros(size(D));
   % Le vecteur des max de la log-vraisemblance
i = 0;
for d = D
    i = i+1;
	Nd = BinData(Y,d,1);
    % valeur de la fct de repartition aux bornes
	ind = find(Nd~=0);
    LV(i) = Nd(ind)*log(Nd(ind)'*d/n);
end;

[LVpen,iopt] = max(LV - pen); d = D(iopt);
% d nbre de cases optimal

Pd=BinData(Y,d,1)/n;
% les probas sur chaque intervalle
H=[(0:(d-1))/d;Pd];
% l'histogramme a d cases

if withfig==1,
	PlotHist(H, mini, maxi);
	title(['Optimal histogram - D=', num2str(d)])
end;

end

function B0 = BinData(X,D,in01);
    if nargin<3, in01=0; end;

    if ~in01,	
        a=min(X); b=max(X);
        if (a<0 | b>1), X = (X-a)/(b-a); end;
    end;

    n = length(X);
    B0 = zeros(1,D+1);
    I = floor(D*X)+1;
    for i=1:n, B0(1,I(i)) = B0(1,I(i))+1; end;
    B0(1,D) = B0(1,D)+B0(1,D+1);
    B0 = B0(1,1:D);
end