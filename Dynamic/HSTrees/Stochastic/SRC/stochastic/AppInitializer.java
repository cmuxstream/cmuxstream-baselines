/*
 * AppInitializer.java
 *
 * Created on 26 August 2005, 13:22
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package stochastic;

import javax.swing.*;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;




/**
 *
 * @author jamestan
 */
public class AppInitializer {
    
    /** Creates a new instance of AppInitializer */
    public AppInitializer() {
        Parameters.maxX = 100;
        Parameters.maxY = 100;
        Parameters.numIteration = 1000;
        Parameters.alpha = 0.5;
        Parameters.numDropTrials = 2;
        Parameters.Kd = 0.1;
        Parameters.Kp = 0.1;
    }

   public void init_xCyD_datasets(int dim, int nClusters, String afilename) {
        FileIO filehandle = new FileIO(afilename, 'R');
        String dataDetails;
        int size = 0;
        while (!filehandle.eof()) {
          dataDetails = filehandle.readString();
          size++;
        }
        filehandle.close('R');
       
        Parameters.dataSize = size-1;
        Parameters.numClusters = nClusters;
        Parameters.distType = 2; // 1 for cosine measure, 2 for euclidean
        if (nClusters == 4){
            Parameters.dataSet = 14; // 14 for xCyD datasets 4 clusters
            Parameters.clusterSizes = new int[4];
            for(int i=0; i< Parameters.clusterSizes.length; i++) Parameters.clusterSizes[i]=0;
        } else {
            Parameters.dataSet = 15; // 15 for xCyD datasets 10 clusters
            Parameters.clusterSizes = new int[10];
            for(int i=0; i< Parameters.clusterSizes.length; i++) Parameters.clusterSizes[i]=0;            
        }
        Parameters.filename = afilename;
        Parameters.dimension = dim; 

        Parameters.maxX = (int) Math.floor(Math.sqrt(Parameters.dataSize*10));
        Parameters.maxY = (int) Math.floor(Math.sqrt(Parameters.dataSize*10));
        
        Parameters.distType = 2; // 1 for cosine measure, 2 for euclidean

        Parameters.numIteration = 1000;
        Parameters.alpha = 0.5;
        Parameters.numDropTrials = 2;
        Parameters.Kd = 0.1;
        Parameters.Kp = 0.1;
   }    
    
    
    
public void initDataFromFile(String filename) { // data set from consensus clustering
	FileIO filehandle = new FileIO(filename, 'R');

	StringTokenizer tokens;
	String Delimiters = ",\t ";
	String aToken = "";

	String dataDetails;

        System.out.println("reading input data...");

	dataDetails = filehandle.readString(); // read the first line it is dummy
        System.out.println(dataDetails);

	dataDetails = filehandle.readString();
        System.out.println(dataDetails);
	tokens = new StringTokenizer(dataDetails,Delimiters);

	aToken = (String) (tokens.nextToken());
        if(aToken.contains("dataSize:")){
           	aToken = (String) (tokens.nextToken());
                Parameters.dataSize = Integer.parseInt(aToken);
        } else {
            System.out.println("Problem parsing data size.");
        }
        
	dataDetails = filehandle.readString();
        System.out.println(dataDetails);
	tokens = new StringTokenizer(dataDetails,Delimiters);
        aToken = (String) (tokens.nextToken());
        
        if(aToken.contains("numClusters:")){
           	aToken = (String) (tokens.nextToken());
                Parameters.numClusters = Integer.parseInt(aToken);
        } else {
            System.out.println("Problem parsing num clusters. If you do not know the number of clusters, simply put 1.");
        }

	dataDetails = filehandle.readString();
        System.out.println(dataDetails);
	tokens = new StringTokenizer(dataDetails,Delimiters);
        aToken = (String) (tokens.nextToken());
        if(aToken.contains("distType:")){
           	aToken = (String) (tokens.nextToken());
                Parameters.distType = Integer.parseInt(aToken);
        } else {
            System.out.println("Problem parsing distance type (1 for Cosine measure, 2 for Euclidean.");
        }

      	dataDetails = filehandle.readString();
        System.out.println(dataDetails);
	tokens = new StringTokenizer(dataDetails,Delimiters);
        aToken = (String) (tokens.nextToken());
        
        if(aToken.contains("dimension:")){
           	aToken = (String) (tokens.nextToken());
                Parameters.dimension = Integer.parseInt(aToken);
        } else {
            System.out.println("Problem parsing dimension");
        }
        
        Parameters.dataSet = 26; // 26 for general data set
        
        Parameters.clusterSizes = new int[Parameters.numClusters];
        for(int i=0; i< Parameters.clusterSizes.length; i++) Parameters.clusterSizes[i]=0;
        Parameters.filename = filename;
        
        Parameters.useMinVar = false;
        Parameters.possibleKstart = 4;
        Parameters.possibleKend = 2;
    }   

    
    
    
    public void initIris() {
        Parameters.dataSize = 150;
        Parameters.numClusters = 3; 
        Parameters.distType = 1; // 1 for cosine measure, 2 for euclidean
        Parameters.dataSet = 2; // 2 for Iris
        Parameters.useMinVar = false;
        
        Parameters.possibleKstart = 4;
        Parameters.possibleKend = 2;
    }

   
    public void initWine() {
        Parameters.dataSize = 178;
        Parameters.numClusters = 3; 
        Parameters.distType = 1; // 1 for cosine measure, 2 for euclidean
        Parameters.dataSet = 1; // 1 for wine
        Parameters.useMinVar = true;

        Parameters.possibleKstart = 4;
        Parameters.possibleKend = 2;
    }

    public void initWisconsin() {
        Parameters.dataSize = 683; // was 699, 683 is after removing missing value
        Parameters.numClusters = 2; 
        Parameters.distType = 1; // 1 for cosine measure, 2 for euclidean
        Parameters.dataSet = 4; // 4 for Wisconsin
        Parameters.useMinVar = false;

        Parameters.possibleKstart = 3;
        Parameters.possibleKend = 2;
    }

   public void initDermatology() {
        Parameters.dataSize = 358; // was 699, 683 is after removing missing value
        Parameters.numClusters = 6; 
        Parameters.distType = 1; // 1 for cosine measure, 2 for euclidean
        Parameters.dataSet = 5; // 5 for Dermatology

        Parameters.possibleKstart = 7;
        Parameters.possibleKend = 3;
    }

    public void initYeast() {
        Parameters.dataSize = 1484; // no missing value
        Parameters.numClusters = 10; 
        Parameters.distType = 1; // 1 for cosine measure, 2 for euclidean
        Parameters.dataSet = 11; // 11 for Yeast

        Parameters.possibleKstart = 15; // was 12
        Parameters.possibleKend = 3;
    }   
   
    public void initDigits() {
        Parameters.dataSize = 3498; // no missing value
        Parameters.numClusters = 10; 
        Parameters.distType = 1; // 1 for cosine measure, 2 for euclidean
        Parameters.dataSet = 13; // 13 for Digits

        Parameters.possibleKstart = 8;
        Parameters.possibleKend = 3;
    }   
    
    
   public void initZoo() {
        Parameters.dataSize = 101; // no missing value
        Parameters.numClusters = 7; 
        Parameters.distType = 1; // 1 for cosine measure, 2 for euclidean
        Parameters.dataSet = 12; // 12 for Zoo

        Parameters.possibleKstart = 8;
        Parameters.possibleKend = 3;
    }   
    
    public void initNewRuns(int iter) {
        Parameters.alpha = 0.5;
        Parameters.numDropTrials = 2;
        Parameters.numIteration = iter;
    }
    
    public void initRuns(int iter) {
        Parameters.numIteration = iter;
    }
    
    public void initSize(int sizeCode) {
        Parameters.dataSize = 1000;
        Parameters.numClusters = 4;
        Parameters.distType = 2; // 1 for cosine measure, 2 for euclidean
        Parameters.dataSet = sizeCode; // 6..10 for size1..5

        Parameters.possibleKstart = 8;
        Parameters.possibleKend = 2;
    }
    
    public void initSquare() {
        Parameters.dataSize = 1000;
        Parameters.numClusters = 4;
        Parameters.distType = 2; // 1 for cosine measure, 2 for euclidean
        Parameters.dataSet = 3; // 3 for square

        Parameters.possibleKstart = 8;
        Parameters.possibleKend = 2;
    }

    public void initSquareSize(int s) {
        Parameters.dataSize = s;
    }

    public void resetCounter(String afilename){
        FileIO filehandle1 = new FileIO(afilename, 'W');
        filehandle1.writeString("0");
        //filehandle1.writeNewLine();
        filehandle1.close('W');
    }    
    
    public int getCounter(String afilename){
        FileIO filehandle = new FileIO(afilename, 'R');
        String dataDetails=null;
//        while (!filehandle.eof()) {
          dataDetails = filehandle.readString();
//        }
        filehandle.close('R');
        int counter = Integer.parseInt(dataDetails);
        FileIO filehandle1 = new FileIO(afilename, 'W');
        filehandle1.writeString(counter+1+"");
        filehandle1.close('W');
        return counter;
    }
    

   public void init_xCyD_datasets_BK(int dim, int nClusters, String afilename) {
        FileIO filehandle = new FileIO(afilename, 'R');
        String dataDetails;
        int size = 0;
        while (!filehandle.eof()) {
          dataDetails = filehandle.readString();
          size++;
        }
        filehandle.close('R');
       
        Parameters.dataSize = size-1;
        Parameters.numClusters = nClusters;
        Parameters.distType = 2; // 1 for cosine measure, 2 for euclidean
        if (nClusters == 4){
            Parameters.possibleKstart = 6;
            Parameters.possibleKend = 2;
            Parameters.dataSet = 14; // 14 for xCyD datasets 4 clusters
            Parameters.clusterSizes = new int[4];
            for(int i=0; i< Parameters.clusterSizes.length; i++) Parameters.clusterSizes[i]=0;
        } else {
            Parameters.possibleKstart = 12;
            Parameters.possibleKend = 8;
            Parameters.dataSet = 15; // 15 for xCyD datasets 10 clusters
            Parameters.clusterSizes = new int[10];
            for(int i=0; i< Parameters.clusterSizes.length; i++) Parameters.clusterSizes[i]=0;            
        }
        Parameters.filename = afilename;
        Parameters.dimension = dim; 
   }
    
    public void initLine() {
        Parameters.dataSize = 1000; 
        Parameters.numClusters = 4; 
        Parameters.distType = 2; // 1 for cosine measure, 2 for euclidean
    }

    /***** to delete
    public void initGUI(NewJFrame before, CustomCanvas beforeCanvas) {
        int frameOffsetX = 30;
        int frameOffsetY = 40 + 50;

        before.setBackground(Color.BLUE);
        before.setSize(Parameters.maxX*5 + frameOffsetX, Parameters.maxY*5 + frameOffsetY);
        before.setTitle("Before Cluster");

        before.getContentPane().add(beforeCanvas);
        before.setVisible(true);
    }
***/
    
}
