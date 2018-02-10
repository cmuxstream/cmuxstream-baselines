totCols =  3231962;

disp('Reading:../../Data/mod_url_svmlight/Day0.mat')
day_refX = load('../../Data/mod_url_svmlight/Day0.mat');
day_refY = load('../../Data/mod_url_svmlight/Day0_Labels.mat');

day_refX = day_refX.vect;
day_refY = day_refY.labels;

refDims = size(day_refX,2);
diff = totCols - refDims;
day_refX(:,refDims+1:refDims+diff) = zeros([size(day_refX,1),diff]);

for i=0:119
    if i+1==45
        disp('Scoring on:Day45_46.mat')
        day_latestX=load('../../Data/mod_url_svmlight/Day45_46.mat');
        day_latesty=load('../../Data/mod_url_svmlight/Day45_46_Labels.mat');
    else
        disp(strcat('Scoring on:','Day',num2str(i+1),'.mat'))
        day_latestX = load(strcat('../../Data/mod_url_svmlight/Day',num2str(i+1),'.mat'));
        day_latesty = load(strcat('../../Data/mod_url_svmlight/Day',num2str(i+1),'_Labels.mat'));
    end
    day_latestX = day_latestX.vect;
    day_latesty = day_latesty.labels;
    
    latDims = size(day_latestX,2);
    diff = totCols - latDims;
    day_latestX(:,latDims+1:latDims+diff) = zeros([size(day_latestX,1),diff]);
    
    %Computing Scores for day_latest on day_ref:
    
    [yHat] = day_loda(day_refX,day_latestX,day_latesty,sparsity,maxBins);
    csvwrite(strcat('../../Data/mod_url_svmlight/Scores_',num2str(i+1),'.csv'), yHat);
    %Switching over to the next window
    day_refX = day_latestX;
    day_refy = day_latesty;
end