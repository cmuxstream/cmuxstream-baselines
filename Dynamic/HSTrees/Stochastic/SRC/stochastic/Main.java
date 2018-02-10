/*
 * Main.java
 *
 * Created on 3 August 2005, 11:54
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
/** Stochastic sampling of data on grid */
package stochastic;

import java.util.*;
import javax.swing.*;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.JOptionPane;
import sizeof.agent.SizeOfAgent;

/**
 *
 * @author jamestan
 */
public class Main {

    /** Creates a new instance of Main */
    public Main() {
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("ENTERING MAIN");
       AppInitializer app = new AppInitializer();
        
       Parameters.evaluationCount = 0; 
       double readAlpha = 0.0;
       
       System.out.println("Parsing arguments");

//       String dataPath = "C:\\Documents and Settings\\jamestan\\My Documents\\RPC_ExternalData\\";
//        String resultPath ="";
       String dataPath = "G:\\myMonashComputerBackup\\C_drive\\My Documents\\RPC_ExternalData\\";
       String resultPath = "G:\\myMonashComputerBackup\\D_drive\\iForest\\HSTreeLevelresults\\";

       //default setting
       Parameters.maxTreeHeight = 15; //was 12 for jForest
       Parameters.maxExpansionLevel = 20;
       Parameters.maxLeafCount = 25; // was 40; but 20 was better
       Parameters.numTrees = 25; // 50 was no good
       
       Parameters.threshold = -1000; // -1000: always retrain
       //Parameters.transcientThreshold = 1;

       //Parameters.threshold = 1000; // 1000: no retrain
       //Parameters.transcientThreshold = 1;

       //Parameters.threshold = 2; // 4:selective retrain
       Parameters.transcientThreshold = 1; //was 6

       Parameters.windowSize = 256; // default

       if(null == args || args.length < 1) {
           Parameters.evaluationCount = 0;
       } else {
           if (args.length ==1) {
                 Parameters.evaluationCount = Integer.parseInt(args[0]);
           } else{
               if (args.length ==2) {
                     Parameters.evaluationCount = Integer.parseInt(args[0]);
                     Parameters.seed = Integer.parseInt(args[1]);
               } else {
                     if (args.length ==3) {
                         Parameters.evaluationCount = Integer.parseInt(args[0]);
                         Parameters.seed = Integer.parseInt(args[1]);
                         resultPath = args[2];
                     } else {
                         if (args.length ==4) {
                             Parameters.evaluationCount = Integer.parseInt(args[0]);
                             Parameters.seed = Integer.parseInt(args[1]);
                             resultPath = args[2];
                             Parameters.maxTreeHeight = Integer.parseInt(args[3]);
                         } else {
                             if (args.length ==5) {
                                 Parameters.evaluationCount = Integer.parseInt(args[0]);
                                 Parameters.seed = Integer.parseInt(args[1]);
                                 resultPath = args[2];
                                 Parameters.maxTreeHeight = Integer.parseInt(args[3]);
                                 Parameters.maxExpansionLevel = Integer.parseInt(args[4]);
                             } else {
                                 if (args.length ==6) {
                                     Parameters.evaluationCount = Integer.parseInt(args[0]);
                                     Parameters.seed = Integer.parseInt(args[1]);
                                     resultPath = args[2];
                                     Parameters.maxTreeHeight = Integer.parseInt(args[3]);
                                     Parameters.maxExpansionLevel = Integer.parseInt(args[4]);
                                     Parameters.maxLeafCount = Integer.parseInt(args[5]);
                                 } else {
                                     if (args.length ==7) {
                                         Parameters.evaluationCount = Integer.parseInt(args[0]);
                                         Parameters.seed = Integer.parseInt(args[1]);
                                         resultPath = args[2];
                                         Parameters.maxTreeHeight = Integer.parseInt(args[3]);
                                         Parameters.maxExpansionLevel = Integer.parseInt(args[4]);
                                         Parameters.maxLeafCount = Integer.parseInt(args[5]);
                                         Parameters.numTrees = Integer.parseInt(args[6]);
                                     } else {
                                         if (args.length ==8) {
                                             Parameters.evaluationCount = Integer.parseInt(args[0]);
                                             Parameters.seed = Integer.parseInt(args[1]);
                                             resultPath = args[2];
                                             Parameters.maxTreeHeight = Integer.parseInt(args[3]);
                                             Parameters.maxExpansionLevel = Integer.parseInt(args[4]);
                                             Parameters.maxLeafCount = Integer.parseInt(args[5]);
                                             Parameters.numTrees = Integer.parseInt(args[6]);
                                             dataPath = args[7];
                                         } else {
                                             if (args.length == 9) {
                                                 Parameters.evaluationCount = Integer.parseInt(args[0]);
                                                 Parameters.seed = Integer.parseInt(args[1]);
                                                 resultPath = args[2];
                                                 Parameters.maxTreeHeight = Integer.parseInt(args[3]);
                                                 Parameters.maxExpansionLevel = Integer.parseInt(args[4]);
                                                 Parameters.maxLeafCount = Integer.parseInt(args[5]);
                                                 Parameters.numTrees = Integer.parseInt(args[6]);
                                                 dataPath = args[7];
                                                 Parameters.threshold = Double.parseDouble(args[8]);
                                             } else {
                                                 if (args.length == 10) {
                                                     Parameters.evaluationCount = Integer.parseInt(args[0]);
                                                     Parameters.seed = Integer.parseInt(args[1]);
                                                     resultPath = args[2];
                                                     Parameters.maxTreeHeight = Integer.parseInt(args[3]);
                                                     Parameters.maxExpansionLevel = Integer.parseInt(args[4]);
                                                     Parameters.maxLeafCount = Integer.parseInt(args[5]);
                                                     Parameters.numTrees = Integer.parseInt(args[6]);
                                                     dataPath = args[7];
                                                     Parameters.threshold = Double.parseDouble(args[8]);
                                                     Parameters.transcientThreshold = Integer.parseInt(args[9]);
                                                 } else {
                                                     if (args.length == 11) {
                                                         Parameters.evaluationCount = Integer.parseInt(args[0]);
                                                         Parameters.seed = Integer.parseInt(args[1]);
                                                         resultPath = args[2];
                                                         Parameters.maxTreeHeight = Integer.parseInt(args[3]);
                                                         Parameters.maxExpansionLevel = Integer.parseInt(args[4]);
                                                         Parameters.maxLeafCount = Integer.parseInt(args[5]);
                                                         Parameters.numTrees = Integer.parseInt(args[6]);
                                                         dataPath = args[7];
                                                         Parameters.threshold = Double.parseDouble(args[8]);
                                                         Parameters.transcientThreshold = Integer.parseInt(args[9]);
                                                         Parameters.windowSize = Integer.parseInt(args[10]);
                                                     } else{
                                                         System.out.println("Found " + args.length + " arguments; Require max 11 arguments!");
                                                         System.exit(0);
                                                     }
                                                 }
                                             }
                                         }
                                     }
                                 }
                             }
                         }
                     }

               }
           }
       }     
      System.out.println("============================================================================"); 
      System.out.println("first argument is evaluation count: " + Parameters.evaluationCount); 
      System.out.println("second argument is dataPath: " + dataPath);
      System.out.println("third argument is resultPath: " + resultPath);
      System.out.println("============================================================================"); 

      Parameters.generator = new Random(Parameters.seed*Parameters.evaluationCount);
      Parameters.evaluationCount += 0; // used with batch file to test the bioinfomatic data set
      //Parameters.evaluationCount = 5000; //1011 wisconsin; 5013 for pima; 5000 for selected dataset shuttle_omitclass4
      
      // must avoid clash if other files also go into 1000 range
      String dataFileName = "";
      
      if (Parameters.isInRange(Parameters.evaluationCount, 2001, 2001)) {
          dataFileName = dataPath + "parametersNdata_HttpSmtpContinous.txt";
          Parameters.dataSetName = "HttpSmtpContinuous";
          Parameters.streamSegmentSize = 150000;
      }
      if (Parameters.isInRange(Parameters.evaluationCount, 2002, 2002)) {
            dataFileName = dataPath + "parametersNdata_HttpSmtpContinous_Shuffled.txt";
            Parameters.dataSetName = "HttpSmtpContinuous";
            Parameters.streamSegmentSize = 150000;
      }
      if (Parameters.isInRange(Parameters.evaluationCount, 2003, 2003)) {
            dataFileName = dataPath + "parametersNdata_SpamSmsCounts.txt";
            Parameters.dataSetName = "HttpSmtpContinuous";
            Parameters.streamSegmentSize = 150000;
      }
      if (Parameters.isInRange(Parameters.evaluationCount, 4003, 4003)) {
          dataFileName = dataPath + "parametersNdata_http.txt";
          Parameters.dataSetName = "http";
          Parameters.streamSegmentSize = 120000;
      }

      if (Parameters.isInRange(Parameters.evaluationCount, 5000, 5000)) {
          app.initDataFromFile(dataFileName); 
      }
      
        boolean fileprint = true;

        if (!Parameters.isInRange(Parameters.evaluationCount, 20001, 20300)) {
              app.initDataFromFile(dataFileName);
         }
        
        
        Datum data[] = new Datum[Parameters.dataSize];
        DataGenerator dg = new DataGenerator(data);
        
         if (Parameters.isInRange(Parameters.evaluationCount, 20001, 20300)) {
              dg.load_xDyC_Data(); Parameters.standardise_range(data);
         } else {
             System.out.println("Loading File...");
             dg.loadFileData(dataFileName);
             System.out.println("File Loaded.");
         }

       Parameters.tScoreThreshold = 70;
       String resultFileName = "";
       
        dg.initIndex();

        final int cosineMeasure = 1;
        final int euclidenMeasure = 2;
        final int oneNorm = 3;
        
        //Parameters.distType = 1;
        //dg.setDistanceMeasure(oneNorm);
        //dg.setDistanceMeasure(cosineMeasure);
        // dg.setDistanceMeasure(euclidenMeasure);
        dg.setDistanceMeasure();
        
        Datum baseItem = null;
        double max[] = new double[data[0].dim];
        double min[] = new double[data[0].dim];
        double range[] = new double[data[0].dim];
        for(int j=0; j<data[0].dim; j++){
            max[j] = -100000;
            min[j] = 1000000;
            range[j] = max[j]-min[j];
        }

        double startTime = System.nanoTime();

        int windowSize = Parameters.windowSize;//was 250
        int transcientThreshold = Parameters.transcientThreshold;

        Datum subSample[] = new Datum[windowSize];
        int maxNItems = Math.min(data.length, windowSize);

        int randomIndex = 0;

        // create multiple trees
        iNode iTrees[] = new iNode[Parameters.numTrees];
        //jNode jTrees[] = new jNode[Parameters.numTrees];
        for(int k=0; k < iTrees.length; k++) {
            // take a random sample of 256 elements
            ArrayList aList = new ArrayList();
            for(int i =0; i < subSample.length; i++) {
                randomIndex = Parameters.generator.nextInt(maxNItems);
                while(aList.contains((Integer)randomIndex)){
                    randomIndex = Parameters.generator.nextInt(maxNItems);
                }
                aList.add((Integer)randomIndex);
                subSample[i] = data[randomIndex];
            }
        //((Integer)randomIndex).intValue();
        // THIS IS LIKE A RESET
            for(int j=0; j<data[0].dim; j++){
                max[j] = -100000;
                min[j] = 1000000;
                range[j] = max[j]-min[j];
            }

            double pThreshold10percent = 0.1;
            for(int i=0; i<subSample.length; i++){
                baseItem = subSample[i];
                for(int j=0; j<data[0].dim; j++){
                    max[j] = Math.max(baseItem.value[j], max[j]);
                    min[j] = Math.min(baseItem.value[j], min[j]);
                     range[j] = (max[j]-min[j]);
                  //   System.out.println("tree# " + k + ",Dim# " + j + ",min="+min[j] + ", max = " + max[j]);
                }
            }

            double tempSplit = 0;
            double tempRange = 0;
            for(int j=0; j<data[0].dim; j++){
                tempSplit = min[j] + Parameters.generator.nextDouble()*range[j];
                tempRange = Math.max((tempSplit-min[j]), max[j]-tempSplit)*2;
                max[j] = tempSplit + tempRange;
                min[j] = tempSplit - tempRange;
            }


            System.out.println("Build tree# " + k);
            
            iTrees[k] = new iNode(max, min, 0); // mass1 is true initially
            Parameters.globalMass1 = true;

            iTrees[k].clearArray();
            System.out.println("Train tree# " + k);
            for(int i=0; i<subSample.length; i++){
                //EDITED
                iTrees[k].assignSize(subSample[i]);
                //iTrees[k].assign(subSample[i]);
                //if (Parameters.checkType(subSample[i]) == 0) {
                //     iTrees[k].assignSize(subSample[i]); // training the first batch
                //}
            }
        }

        String AnomalyScoreFile = "AnormalyScore_" + Parameters.dataSetName + "_" + Parameters.seed +".csv";
        FileIO asfile = new FileIO(resultPath + AnomalyScoreFile, 'W');

        boolean writeTo_asfile = true;

//        System.out.println("========================");

        double score = 0;
        int anomalyType = 0;
        int window = 1;
        double MAD[] = new double[iTrees.length];
        for(int i=0; i < iTrees.length; i++){
            MAD[i] = 0.0;
            //iTrees[i].clearSize2();
            iTrees[i].clearALLSize2();
        }
        double totalMAD = 0;

        double batchScores[] = new double[windowSize*transcientThreshold];
        for(int i=0; i<windowSize*transcientThreshold; i++) batchScores[i] = 0.0;

        double batchAnomalyType[] = new double[windowSize*transcientThreshold];
        for(int i=0; i<windowSize*transcientThreshold; i++) batchAnomalyType[i] = 0;

        int batchCounter = 0;

        double oldChangeLevel = 0.01;
        boolean changeOccured = false;
        boolean firstChange = true;
        double changeDeviation = 0;
        double previousChangeLevel = 0;
        double estimatedLevel = 0;
        double alwaysEstimatedLevel = 0;
        double runningMean = 0;
        double runningMeanTotal = 0;
        double runningMeanCtr = 0;

        long oldTime = System.nanoTime();
        int changeCtr = 0;
        int NegChangeCtr = 0;
        int lastStopItemNum = 0;
        int numAdaptation = 0;

        System.out.println("ready=, set , go = ,"+ System.currentTimeMillis());
        ArrayList<Integer> anomalyTypeArray = new ArrayList<Integer>();
        ArrayList<Double> anormalyScoresArray = new ArrayList<Double>();
        
        double oldEntropy = 0;

        int trainCtr = 0;

        for(int j=0; j < data.length; j++){
            score = 0;
            double secondHalfPercentageChange = 0;

            ArrayList a1 = new ArrayList();
            ArrayList a2 = new ArrayList();
            for(int i=0; i < iTrees.length; i++) {
                //score += iTrees[i].score(data[j]);
                //score += iTrees[i].scoreDist(data[j]);
                //score += iTrees[i].scoreKthDist(data[j]);
                //score += iTrees[i].scoreMassPathLen(data[j]);
                //score += iTrees[i].scoreMassPathLen1(data[j]);
                score += iTrees[i].scoreMassPathLen2(data[j]);
             
                // EDITED
                iTrees[i].assignSize2(data[j]);
               // if (Parameters.generator.nextDouble() > 0.8){
                    // if (Parameters.checkType(data[j]) == 0) {
                       // iTrees[i].assignSize2(data[j]);

                   // }
               //         trainCtr++;
              //  }
             }

           // changeOccured = false;

            /////////////////////////////////////////
          //  if ((trainCtr > 1)&&(trainCtr%windowSize == 0)) {
             if ((j > 1)&&(j%windowSize == 0)) {
                  batchCounter++;

                  boolean retrain = false;

                  if (Parameters.threshold == 1000){ //No adaptation (NoA)
                      retrain = false;
                  } 
                  if (Parameters.threshold == -1000){ //Always Adaptation
                          retrain = true;
                  }
               changeOccured = false;

               if (retrain){
                        changeOccured = true;
                        changeCtr++;
                }
               if(changeCtr >= transcientThreshold){ // was 6
                         for(int i=0; i < iTrees.length; i++) {
                             iTrees[i].updateSize(); // modified to speed up
                         }
                        numAdaptation++;
                        batchCounter=0;
      /*output*/        System.out.println(",Retrain,");
                 } else {
      /*output*/        System.out.println(",No Retrain,");
                 }

                        alwaysEstimatedLevel =  0.5*alwaysEstimatedLevel + 0.5*secondHalfPercentageChange;

                     if (changeCtr >= transcientThreshold){ // was > 6
                         changeCtr = 0;
                      }

                     for(int i=0; i < iTrees.length; i++) {
                         iTrees[i].clearSize2();
                     }
                     batchCounter = 0;
              //   }
            }

            //if (j > 0 && j%100000 == 0) {
            if (j > 0 && j%50000 == 0) {
                System.out.println("time at j=," + j + ", = ,"+  System.nanoTime() + ", time elapsed=," + (System.nanoTime() - oldTime)/1000000);
                oldTime = System.nanoTime();
            }

            if ((j > 1)&&(j%windowSize == 0)) {
     /*output*/         System.out.println(); 
            }
            anomalyType = Parameters.checkType(data[j]);

            if ((j >= 0)) {
                int lastIdx = j;
                lastStopItemNum = lastIdx;
          //      System.out.println("write to lastIdx = " + lastIdx);
                double anomalyScore = 0;
                for(int i=0; i < iTrees.length; i++) {
                    anomalyScore += iTrees[i].scoreMassPathLen2(data[lastIdx]);
                    //anomalyScore += iTrees[i].scoreMassPathLen1(data[lastIdx]);
                }
                anomalyType = Parameters.checkType(data[lastIdx]);
                anomalyTypeArray.add((int)anomalyType);
                anormalyScoresArray.add((double)anomalyScore);
                if (writeTo_asfile) {
                    asfile.appendString(anomalyType + ", " +anomalyScore);
                    asfile.appendNewLine();
                }
            }

            batchScores[j%(windowSize*transcientThreshold)] = score;  // was *7
            batchAnomalyType[j%(windowSize*transcientThreshold)] = anomalyType;   // was *7

        }
        int remainder = Parameters.dataSize%(windowSize*transcientThreshold);
        //int remainder = Parameters.dataSize%(windowSize);
               System.out.println("======");
               System.out.println("writing to file from lastStopItemNum " + (lastStopItemNum+1) + " to the rest of the items");
        for(int i = (lastStopItemNum+1); i< data.length; i++){
           //     System.out.println("remaining write to lastIdx = " + i);
                double anomalyScore = 0;
                for(int ii=0; ii < iTrees.length; ii++) {
                    anomalyScore += iTrees[ii].scoreMassPathLen2(data[i]);
                    //anomalyScore += iTrees[ii].scoreMassPathLen1(data[i]);
                }

                anomalyType = Parameters.checkType(data[i]);
                anomalyTypeArray.add((int)anomalyType);
                anormalyScoresArray.add((double)anomalyScore);

                System.out.println(anomalyScore);
                if (writeTo_asfile) {
                        asfile.appendString(anomalyType + ", " +anomalyScore);
                        asfile.appendNewLine();
                 }
        }
        long endTime = System.nanoTime();

        System.out.println("The end");
        asfile.close('A');
        System.exit(1); // status =1 means normal exit
    }
}
