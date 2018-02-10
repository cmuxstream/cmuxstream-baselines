totCols =  3231962;

disp('Reading:../../Data/LODA_5Day_url_svmlight/Week0.mat')
day_refX = load('../../Data/LODA_5Day_url_svmlight/Week0.mat');
day_refY = load('../../Data/LODA_5Day_url_svmlight/Week0_Labels.mat');

day_refX = day_refX.vect;
day_refY = day_refY.labels;

refDims = size(day_refX,2);
diff = totCols - refDims;
day_refX(:,refDims+1:refDims+diff) = zeros([size(day_refX,1),diff]);

for i=0:23
        disp(strcat('Scoring on:','Week',num2str(i+1),'.mat'))
        day_latestX = load(strcat('../../Data/LODA_5Day_url_svmlight/Week',num2str(i+1),'.mat'));
        day_latesty = load(strcat('../../Data/LODA_5Day_url_svmlight/Week',num2str(i+1),'_Labels.mat'));
    
    day_latestX = day_latestX.vect;
    day_latesty = day_latesty.labels;
    
    latDims = size(day_latestX,2);
    diff = totCols - latDims;
    day_latestX(:,latDims+1:latDims+diff) = zeros([size(day_latestX,1),diff]);
    
    %Computing Scores for day_latest on day_ref:
    
    [yHat] = day_loda(day_refX,day_latestX,day_latesty,sparsity,maxBins);
    csvwrite(strcat('../../Data/LODA_5Day_url_svmlight/Scores_',num2str(i+1),'.csv'), yHat);
    %Switching over to the next window
    day_refX = day_latestX;
    day_refy = day_latesty;
end