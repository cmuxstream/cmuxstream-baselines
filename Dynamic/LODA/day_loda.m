function [yHat] = day_loda(day1_X,day2_X,day2_y,sparsity,maxBins)
    %columnsWithAllZeros = all(day1_X == 0)
    %day1_X = full(day1_X(:,~columnswithAllZeros));
    %day2_X = full(day2_X(:,~columnswithAllZeros));
    D = loda_dd_modified(day1_X,[],0.01,sparsity,maxBins);
    disp('LODA FIT. Computing Score....')
    [yHat] = score_dd(day2_X,D);
    disp('Score Obtained')
end
