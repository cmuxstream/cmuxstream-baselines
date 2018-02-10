//James Tan

package stochastic;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.text.DecimalFormat;

public class Datum {
  double [] value;
  int dim;
  char symbol;
  int index = -1;
  DistanceFunction distanceFun = null;
  double ownAlpha = 0.5;
  ArrayList nearestItems = new ArrayList();  
  int outlierFactor = -1;
  
  boolean noise = false;
  double localDensity = 0;
  //BagCluster myCluster;
  int memSize = 30;
  boolean mArray[] = new boolean[memSize];
  int mArrayCount = 0;
  
  int Label = -1;
  double Template = 0;
  double M = 1;
  double Mplus = 0;
  double A = 0;
  double maxSim = 0;
  double meanSim = 0;
  double antAlpha = 0.2;
  double meetCount = 0;

  public void setLabel(Datum aDatum) {
     boolean ruleApplied = false;      
     if ((this.Label == -1) && (aDatum.Label == -1)) {
        //Parameters.globalLabel++;  //first increment becomes zero
        Parameters.globalLabel = Parameters.getNextFreeBin();
        this.setLabel(Parameters.globalLabel);
        aDatum.setLabel(Parameters.globalLabel);
        ruleApplied = true;
     }

     if (!ruleApplied) {
         if ((this.Label == -1) && (aDatum.Label > -1)) {
            this.setLabel(aDatum.Label);
            ruleApplied = true;
         } 
     }

     if (!ruleApplied) {
         if ((this.Label > -1) && (aDatum.Label == -1)) {
            aDatum.setLabel(this.Label);
            ruleApplied = true;
          //  System.out.println("this should not happen");
         } 
     }

     if (!ruleApplied) {
         if ((this.Label != aDatum.Label) && ((this.Label > -1) && (aDatum.Label > -1))) {
             this.decreaseM();
             aDatum.decreaseM();
//             if (this.M < aDatum.M) {
                 this.setLabel(aDatum.Label);
//             } else {
//                 aDatum.setLabel(this.Label);
//             }
             ruleApplied = true;
         }
     }

    if (!ruleApplied) {
         if (((this.Label == aDatum.Label) && ((this.Label > -1) && (aDatum.Label > -1)))) {
             this.increaseM();  this.increaseMplus();
             aDatum.increaseM();  aDatum.increaseMplus();
             ruleApplied = true;
         }
    }
     
  }
  
  
  
  public void setLabel(int newLabel) {
      if (Label > -1) {
          Parameters.binSizes[Label]--; 
      }
      Parameters.binSizes[newLabel]++;
      Label = newLabel;
  }
  
  public void updateMaxSim(double sim) {
      maxSim = Math.max(maxSim, sim);
  }

  public void updateMeanSim(double sim) {
      meetCount++;
      if (meetCount ==0) {
          meanSim = sim;
      } else {
          meanSim = ((meanSim*(meetCount-1)+ sim)/meetCount);
      }
  }
  
  public void updateTemplate() {
//      Template = (meanSim + maxSim)/2.0;
        double weight1 = 0.1;
     // double weight1 = 0.38197;
        Template = weight1*meanSim + (1 -weight1)*maxSim;

  }
  
 public void updateTemplate(double weight1) {
        Template = weight1*meanSim + (1 -weight1)*maxSim;
  }  
  
  public boolean acceptance(Datum item_j) {
      double sim_ij = 1 - this.distanceFrom(item_j);
      double higherTemplate = Math.max(this.Template, item_j.Template);
      boolean accept = false;
      if (sim_ij > higherTemplate) {
    //     System.out.println(" ACCEPT symbol 1:" + this.symbol + " and " + item_j.symbol + " sim = " + sim_ij + ", higherTemplate = " + higherTemplate);
          accept = true;
      } else {
  //       System.out.println(" REJECT symbol 1:" + this.symbol + " and " + item_j.symbol + " sim = " + sim_ij + ", higherTemplate = " + higherTemplate);

          accept = false;
      }
      return accept;
  }
  
  public void increaseM() {
      if (Label == -1) {
          M = 1;
          mArrayCount = 0;
          for(int i=0; i < mArray.length; i++) {
              mArray[i] = false;
          }
      } else {
//          M =  1 + (1 - antAlpha)*(M-1) + antAlpha;
//          if (M < 2){
//              M = M + 0.01;
//          }
          mArray[mArrayCount] = true;
          mArrayCount++;
          if (mArrayCount==memSize){
              mArrayCount = 0;
          }

          if (M < 20){
              M = M + 1;
          }
          
      }
  }
  
  public double getM() {
      return M;
  }

  public double calMArray() {
      int count = 0;
      for(int i=0; i < mArray.length; i++) {
          if(mArray[i]) count++;
      }
      
      return count;
  }
  
  
  public void decreaseM() {
      if (Label == -1) {
          M = 1;
          mArrayCount = 0;
          for(int i=0; i < mArray.length; i++) {
              mArray[i] = false;
          }
      } else {
//          if (M > 1){
//              M = M - 0.01;
//          }
          mArray[mArrayCount] = false;
          mArrayCount++;
          if (mArrayCount==memSize){
              mArrayCount = 0;
          }

          if (M > 1){
              M = M - 1;
          }
      // omit the following line 25 Nov    
      //    M = 1 + (1 - antAlpha)*(M-1);
      }
  }

  public void increaseMplus() {
     Mplus = (1 - antAlpha)*Mplus + antAlpha;
  }

  public void decreaseMplus() {
     Mplus = (1 - antAlpha)*Mplus;
  }
  
  public void resetNest() {
      if (Label > -1) Parameters.binSizes[Label]--;
      Label = -1;
      M = 1;
      Mplus = 0;
  }
  
  public Datum(double [] value) {
    this.value = new double [value.length];
    System.arraycopy(value, 0, this.value, 0, value.length);
    this.dim = value.length;
  }

  public void setIndex(int i){
      this.index = i;
  }

  public int getIndex(){
      return this.index;
  }

  public void setNearestItems(Datum[] allItems, int neighSize){
        Datum anItem = null;
        Datum anotherItem = null;
        Datum itemToAdd = null;
        Datum itemToRemove = null;
        boolean somethingToReplace = false;
        // fill up the KNN with some values first
        int ctr = 0;
        while(nearestItems.size() < neighSize) {
            if (!this.equals(allItems[ctr])){ // don't add itself to KNN
                nearestItems.add(allItems[ctr]);
            }
            ctr++;
        }
        
        // for neighSize =8, initSize must be = 7
        for(int i=0; i<allItems.length; i++){
            anItem = allItems[i];
            somethingToReplace = false;
            if (this.equals(anItem)) continue;
            for (int j = 0; j < nearestItems.size() ; j++){
                anotherItem = (Datum) nearestItems.get(j);
                if (this.equals(anotherItem)) continue;

                if (!nearestItems.contains(anItem)){
                    if (this.distanceFrom(anotherItem) > this.distanceFrom(anItem)) {
                        itemToAdd = anItem;
                        itemToRemove = anotherItem;
                        somethingToReplace = true;
                    }
                }
            }
            if(somethingToReplace){
                nearestItems.remove(itemToRemove);
                nearestItems.add(itemToAdd);
            }
        }
        
    //    System.out.println(this + "'s nearest are " + nearestItems);
  }
      
  
  public Datum(String dataDetails, double d1, char c1) //written for GENERAL FILE data
  {
    StringTokenizer tokens;
    String Delimiters = "\t";
    String classLabel = "";
    int randXPos = (int)Math.floor(Math.random()*Parameters.maxX);
    int randYPos = (int)Math.floor(Math.random()*Parameters.maxY);

    tokens = new StringTokenizer(dataDetails,Delimiters);
    classLabel = (String) (tokens.nextToken());
    classLabel = String.valueOf((int)Double.parseDouble(classLabel));
    if((Parameters.dataSetName.compareTo("http")==0)||
       (Parameters.dataSetName.compareTo("HttpSmtpContinuous")==0)||
       (Parameters.dataSetName.compareTo("smtp")==0)||
       (Parameters.dataSetName.compareTo("satellite")==0)||
       (Parameters.dataSetName.compareTo("annthyroid")==0)||
       (Parameters.dataSetName.compareTo("MULCROSS_NoACluster")==0)||
       (Parameters.dataSetName.compareTo("Mulcross6000")==0)||
       (Parameters.dataSetName.compareTo("CoverType")==0)||
       (Parameters.dataSetName.compareTo("Arrhythmia")==0)||
       (Parameters.dataSetName.compareTo("Mulcrossfull")==0)||
       (Parameters.dataSetName.compareTo("massVsOthers")==0)||
       (Parameters.dataSetName.compareTo("DensityRatio1")==0)||
       (Parameters.dataSetName.compareTo("DensityRatio2")==0)||
       (Parameters.dataSetName.compareTo("DensityRatio3")==0)||
       (Parameters.dataSetName.compareTo("DensityRatio4")==0)||
       (Parameters.dataSetName.compareTo("DensityRatio5")==0)||
       (Parameters.dataSetName.compareTo("DensityRatio6")==0)||
       (Parameters.dataSetName.compareTo("DensityRatio7")==0)||
       (Parameters.dataSetName.compareTo("DensityRatio8")==0)||
       (Parameters.dataSetName.compareTo("DensityRatio9")==0)||
       (Parameters.dataSetName.compareTo("DensityRatio10")==0)||
       (Parameters.dataSetName.compareTo("VaryDensityRatio")==0)||
       (Parameters.dataSetName.compareTo("MulcrossChange")==0)||
       (Parameters.dataSetName.compareTo("ChangeLevel")==0)||
       (Parameters.dataSetName.compareTo("ChangeLevel_1")==0)||
       (Parameters.dataSetName.compareTo("SEA")==0)||
       (Parameters.dataSetName.compareTo("ChangeVar")==0)||
       (Parameters.dataSetName.compareTo("ChangeEventsDetection")==0)||
       (Parameters.dataSetName.compareTo("DiffAnalysis_Case1")==0)||
       (Parameters.dataSetName.compareTo("DiffAnalysis_Case2")==0)||
       (Parameters.dataSetName.compareTo("DiffAnalysis_Case3")==0)||
       (Parameters.dataSetName.compareTo("DiffAnalysis_Case4")==0)||
       (Parameters.dataSetName.compareTo("DiffAnalysis_Case5")==0)||
       (Parameters.dataSetName.compareTo("DiffAnalysis_Case6")==0)||
       (Parameters.dataSetName.compareTo("smtp_http")==0)||
       (Parameters.dataSetName.compareTo("Elec2")==0)||
       (Parameters.dataSetName.compareTo("ShopFrontChange")==0)||
       (Parameters.dataSetName.compareTo("ionosphere")==0)){
        if (classLabel.compareTo("0") == 0) symbol = '/';
    }
    if (classLabel.compareTo("1") == 0) symbol = '+';
    if (classLabel.compareTo("2") == 0) symbol = 'x';
    if (classLabel.compareTo("3") == 0) symbol = 'o';
    if (classLabel.compareTo("4") == 0) symbol = '*';
    if (classLabel.compareTo("5") == 0) symbol = '-';
    if (classLabel.compareTo("6") == 0) symbol = ':';
    if (classLabel.compareTo("7") == 0) symbol = '#';
    if (classLabel.compareTo("8") == 0) symbol = '=';
    if (classLabel.compareTo("9") == 0) symbol = '%';
    if (classLabel.compareTo("10") == 0) symbol = '$';
    if (classLabel.compareTo("11") == 0) symbol = 'a';
    if (classLabel.compareTo("12") == 0) symbol = 'b';
    if (classLabel.compareTo("13") == 0) symbol = 'c';
    if (classLabel.compareTo("14") == 0) symbol = 'd';
    if (classLabel.compareTo("15") == 0) symbol = 'e';
    if (classLabel.compareTo("16") == 0) symbol = 'f';
    if (classLabel.compareTo("17") == 0) symbol = 'g';
    if (classLabel.compareTo("18") == 0) symbol = 'h';
    if (classLabel.compareTo("19") == 0) symbol = 'i';
    if (classLabel.compareTo("20") == 0) symbol = 'j';
    if (classLabel.compareTo("21") == 0) symbol = 'k';
    if (classLabel.compareTo("22") == 0) symbol = 'l';
    if (classLabel.compareTo("23") == 0) symbol = 'm';
    if (classLabel.compareTo("24") == 0) symbol = 'n';
    if (classLabel.compareTo("25") == 0) symbol = 'o';
    if (classLabel.compareTo("26") == 0) symbol = 'p';
    if (classLabel.compareTo("27") == 0) symbol = 'q';
    if (classLabel.compareTo("28") == 0) symbol = 'r';
    if (classLabel.compareTo("29") == 0) symbol = 's';
    if (classLabel.compareTo("30") == 0) symbol = 't';

    System.out.println("ClassLabel="+classLabel);
    int c = 0;
    if((Parameters.dataSetName.compareTo("http")==0)||
       (Parameters.dataSetName.compareTo("HttpSmtpContinuous")==0)||
       (Parameters.dataSetName.compareTo("smtp")==0)||
       (Parameters.dataSetName.compareTo("satellite")==0)||
       (Parameters.dataSetName.compareTo("annthyroid")==0)||
       (Parameters.dataSetName.compareTo("Mulcross6000")==0)||
       (Parameters.dataSetName.compareTo("Arrhythmia")==0)||
       (Parameters.dataSetName.compareTo("CoverType")==0)||
       (Parameters.dataSetName.compareTo("Mulcrossfull")==0)||
       (Parameters.dataSetName.compareTo("MULCROSS_NoACluster")==0)||
       (Parameters.dataSetName.compareTo("massVsOthers")==0)||
       (Parameters.dataSetName.compareTo("DensityRatio1")==0)||
       (Parameters.dataSetName.compareTo("DensityRatio2")==0)||
       (Parameters.dataSetName.compareTo("DensityRatio3")==0)||
       (Parameters.dataSetName.compareTo("DensityRatio4")==0)||
       (Parameters.dataSetName.compareTo("DensityRatio5")==0)||
       (Parameters.dataSetName.compareTo("DensityRatio6")==0)||
       (Parameters.dataSetName.compareTo("DensityRatio7")==0)||
       (Parameters.dataSetName.compareTo("DensityRatio8")==0)||
       (Parameters.dataSetName.compareTo("DensityRatio9")==0)||
       (Parameters.dataSetName.compareTo("DensityRatio10")==0)||
       (Parameters.dataSetName.compareTo("VaryDensityRatio")==0)||
       (Parameters.dataSetName.compareTo("MulcrossChange")==0)||
       (Parameters.dataSetName.compareTo("ChangeLevel")==0)||
       (Parameters.dataSetName.compareTo("ChangeLevel_1")==0)||
       (Parameters.dataSetName.compareTo("SEA")==0)||
       (Parameters.dataSetName.compareTo("ChangeVar")==0)||
       (Parameters.dataSetName.compareTo("ChangeEventsDetection")==0)||
       (Parameters.dataSetName.compareTo("DiffAnalysis_Case1")==0)||
       (Parameters.dataSetName.compareTo("DiffAnalysis_Case2")==0)||
       (Parameters.dataSetName.compareTo("DiffAnalysis_Case3")==0)||
       (Parameters.dataSetName.compareTo("DiffAnalysis_Case4")==0)||
       (Parameters.dataSetName.compareTo("DiffAnalysis_Case5")==0)||
       (Parameters.dataSetName.compareTo("DiffAnalysis_Case6")==0)||
       (Parameters.dataSetName.compareTo("Extended_Ver2_Case1")==0)||
       (Parameters.dataSetName.compareTo("Extended_Ver2_Case2")==0)||
       (Parameters.dataSetName.compareTo("Extended_Ver2_Case3")==0)||
       (Parameters.dataSetName.compareTo("Extended_Ver2_Case4")==0)||
       (Parameters.dataSetName.compareTo("Extended_Ver2_Case5")==0)||
       (Parameters.dataSetName.compareTo("smtp_http")==0)||
       (Parameters.dataSetName.compareTo("Elec2")==0)||
       (Parameters.dataSetName.compareTo("ShopFrontChange")==0)||
       (Parameters.dataSetName.compareTo("ionosphere")==0)){
        c = (int)Double.parseDouble(classLabel); //only for http
    } else {
        c = Integer.parseInt(classLabel) - 1;
    }
    Parameters.clusterSizes[c]++;
    
    this.dim = Parameters.dimension;
    
    value = new double[dim];
    // extract data from the input string
    for (int i = 0; i < dim; i++) {
        value[i] = Double.parseDouble(tokens.nextToken());
    }
  }
  
  public Datum(String dataDetails) //written for IRIS data
  {
    StringTokenizer tokens;
    String Delimiters = "\t";
    String classLabel = "";
    tokens = new StringTokenizer(dataDetails,Delimiters);

    value = new double[4];
    // extract data from the input string
    for (int i = 0; i < 4; i++) {
      value[i] = Double.parseDouble(tokens.nextToken());
    }
    classLabel = (String) (tokens.nextToken());
    if (classLabel.compareTo("Iris-setosa") == 0) {
      symbol = 'o';
    }
    if (classLabel.compareTo("Iris-versicolor") == 0) {
      symbol = '+';
    }
    if (classLabel.compareTo("Iris-virginica") == 0) {
      symbol = 'x';
    }

//    System.out.println(value[0] + " " + value[1] + " " + value[2] + " " + value[3] + " " + classLabel);
    this.dim = 4;
  }

  public Datum(String dataDetails, int wineDummy) //written for WINE data
  {
    StringTokenizer tokens;
    String Delimiters = "\t";
    int classLabel = 0;
    int randXPos = (int)Math.floor(Math.random()*Parameters.maxX);
    int randYPos = (int)Math.floor(Math.random()*Parameters.maxY);

    tokens = new StringTokenizer(dataDetails,Delimiters);

    classLabel = Integer.parseInt(tokens.nextToken());
    if (classLabel == 1) {
      symbol = '+';
    }
    if (classLabel == 2) {
      symbol = 'x';
    }
    if (classLabel == 3) {
      symbol = 'o';
    }

    value = new double[13];
    // extract data from the input string
    for (int i = 0; i < 13; i++) {
      value[i] = Double.parseDouble(tokens.nextToken());
    }

    for (int z = 0; z<13; z++) {
        System.out.print(value[0] + " ");
    }

    System.out.println(classLabel);
    this.dim = 13;
  }

  public Datum(String dataDetails, int Dummy1, int Dummy2) //written for Wisconsin data
  {
    StringTokenizer tokens;
    String Delimiters = "\t";
    int classLabel = 0;
    this.dim = 9;

    tokens = new StringTokenizer(dataDetails,Delimiters);

    value = new double[this.dim];
    // extract data from the input string
    for (int i = 0; i < this.dim; i++) {
      value[i] = Double.parseDouble(tokens.nextToken());
    }

    for (int z = 0; z<this.dim; z++) {
        System.out.print(value[0] + ", ");
    }

    classLabel = Integer.parseInt(tokens.nextToken());
    if (classLabel == 2) {
      symbol = '+';
    }
    if (classLabel == 4) {
      symbol = 'x';
    }
    
    System.out.println(classLabel);
  }
  
    
  public Datum(String dataDetails, int Dummy1, int Dummy2, int Dummy3, int Dummy4, int Dummy5, int Dummy6, int Dummy7) //written for xCyD data
  {
    StringTokenizer tokens;
    String Delimiters = " ";
    this.dim = Parameters.dimension; 

    tokens = new StringTokenizer(dataDetails,Delimiters);

    value = new double[this.dim];
    
    double totalCummItems = Double.parseDouble(tokens.nextToken());
    double cummItems = Double.parseDouble(tokens.nextToken());

    // extract data from the input string
    for (int i = 0; i < this.dim; i++) {
      value[i] = Double.parseDouble(tokens.nextToken());
    }

    for (int z = 0; z<this.dim; z++) {
        System.out.print(value[z] + ", ");
    }

    double classLabel = Double.parseDouble(tokens.nextToken());
    if (Parameters.numClusters == 4){
        if (classLabel == 0) {symbol = '+';    Parameters.clusterSizes[0]++; }
        if (classLabel == 1) {symbol = 'x';      Parameters.clusterSizes[1]++;}
        if (classLabel == 2) {symbol = 'o';      Parameters.clusterSizes[2]++;}
        if (classLabel == 3) {symbol = '*';      Parameters.clusterSizes[3]++;}
    } else {
        if (Parameters.numClusters == 10){
            if (classLabel == 0) {symbol = '+';   Parameters.clusterSizes[0]++;}
            if (classLabel == 1) {symbol = 'x';   Parameters.clusterSizes[1]++;}
            if (classLabel == 2) {symbol = 'o';   Parameters.clusterSizes[2]++;}
            if (classLabel == 3) {symbol = '*';   Parameters.clusterSizes[3]++;}
            if (classLabel == 4) {symbol = '-';   Parameters.clusterSizes[4]++;}
            if (classLabel == 5) {symbol = ':';   Parameters.clusterSizes[5]++;}
            if (classLabel == 6) {symbol = '#';   Parameters.clusterSizes[6]++;}
            if (classLabel == 7) {symbol = '=';   Parameters.clusterSizes[7]++;}
            if (classLabel == 8) {symbol = '%';   Parameters.clusterSizes[8]++;}
            if (classLabel == 9) {symbol = '$';   Parameters.clusterSizes[9]++;}
        } else {
            System.out.println("ERROR in the number of clusters for xCyD");
        }
     }
        
    System.out.println(classLabel);
  }      
     
   public Datum(double [] value, char symbol) {
    this.value = new double [value.length];
    System.arraycopy(value, 0, this.value, 0, value.length);
    this.symbol = symbol;
    this.dim = value.length;
   }

  public Datum(double [] value, int posX, int posY, char symbol) {
    this.value = new double [value.length];
    System.arraycopy(value, 0, this.value, 0, value.length);

    this.symbol = symbol;
    this.dim = value.length;
  }

  public Datum(double [] value, char symbol, int dim) {
    this.value = new double [value.length];
    System.arraycopy(value, 0, this.value, 0, dim);

    this.symbol = symbol;
    this.dim = dim;
  }

  public Datum(double [] value, int posX, int posY, char symbol, int dim) {
    this.value = new double [value.length];
    System.arraycopy(value, 0, this.value, 0, dim);
//arraycopy(Object source, int sourcePosition, Object destination, int destinationPosition, int numberOfElements)

    this.symbol = symbol;
    this.dim = dim;
  }

  public void init2D(double [] value, int posX, int posY, char symbol, int dim) {
    this.value = new double [value.length];
    System.arraycopy(value, 0, this.value, 0, dim);
//arraycopy(Object source, int sourcePosition, Object destination, int destinationPosition, int numberOfElements)

    this.symbol = symbol;
    this.dim = dim;
  }



  public Datum() {
      this.value = new double [1];
      this.symbol = 'u'; //undefined
      this.dim = 1;
  }

  public void setDatum(Datum d) {
      this.value = d.getValue();
      this.symbol = d.getSymbol();
      this.dim = d.getDim();
  }

  public char getSymbol() {
    return this.symbol;
  }

  public void setSymbol(char s) {
    this.symbol = s;
  }

  
  public int getDim() {
    return this.dim;
  }

  public void setDim(int d) {
    this.dim = d;
  }

  public double[] getValue() {
    return value;
  }

  public void setValue(double [] value) {
    this.value = value;
  }

  public void show() {
      System.out.println(this);
  }

  public void setDistanceMeasure(DistanceFunction df) {
      this.distanceFun = df;
      df.setClient(this);
  }
  
  public DistanceFunction getDistanceMeasure() {
      return this.distanceFun;
  }
  
  public double unnormalisedDistanceFrom(Datum d) {
      double dist = 0;
        dist = this.distanceFun.precompute(d);
        return dist;
  }  
  
  public double distanceFrom(Datum d) {
      double dist = 0;
        dist = this.distanceFun.precompute(d);
        return dist/Parameters.maxDist;

/*      
      if ((d.getIndex() == -1)||(this.getIndex()==-1)){
        dist = this.distanceFun.precompute(d);
        return dist/Parameters.maxDist;
      } else {
        dist = this.distanceFun.compute(d);
        return dist/Parameters.maxDist;
      }
 */
  }

  public double preComputeDistanceFrom(Datum d) {
      double dist = this.distanceFun.precompute(d);
      return dist;
  }
  
  public double preComputeDistanceFromNormalised(Datum d) {
      double dist = this.distanceFun.precompute(d);
      return dist/Parameters.maxDist;
  }
  
   public double unNormalisedDistanceFrom(Datum d) {
      double dist = this.distanceFun.compute(d);
      return dist;
  }
  
   public double distanceFrom_2DNormal(Datum d) {
      double [] dVal = d.getValue();

      double dist = Parameters.euclidean(dVal[0], dVal[1], this.value[0], this.value[1]);
      return dist;
  }
 
   public double precomputed(Datum d) {
      double dist = this.distanceFun.precomputed(d);
      return dist;
  }
    
  public double normDistance(Datum d) {
      double dist = this.distanceFrom(d);
      return dist;
       // max Dist is the maximum distance of all mutual distances
      // Parameters.range: use this when using 2D normal data
  }

  public String toString() {
    String result = new String();
    DecimalFormat fourDigits = new DecimalFormat("0.0000");

    result += this.symbol + "(Value:";
    for (int i = 0; i < dim; i++) result += fourDigits.format(value[i]) + ", ";
    result += ")";
    return result.toString();
  }
   
 }
