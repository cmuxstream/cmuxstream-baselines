/*
 * DataGenerator.java
 *
 * Created on 5 August 2005, 15:33
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package stochastic;

/**
 *
 * @author jamestan
 */
import java.text.DecimalFormat;
import java.util.*;

public class DataGenerator {
    Datum [] data;
    double [][] matrix;  // triangular similarity/dissimilarity matrix
    
    /** Creates a new instance of DataGenerator */
    public DataGenerator(Datum [] data) {
        this.data = data;
    }

    public void initIndex() {
        for (int i =0; i < data.length; i++) {
            data[i].setIndex(i);
        }
    }

    public void randomise() {
        for (int i =0; i < data.length; i++) {
            int tmp = (int)Parameters.generator.nextInt(data.length);
            Datum tmpDatum = data[tmp];
            data[tmp] = data[i];
            data[i] = tmpDatum;
        }
    }


    public void load_xDyC_Data(){
        FileIO filehandle = openFileToRead(Parameters.filename);
        read_xCyD_Data(filehandle);
        closeReader(filehandle);
    }  

    private void read_xCyD_Data(FileIO dataFile) {
        String dataDetails;
        for (int i = 0; i <  Parameters.dataSize; i++) {
          dataDetails = dataFile.readString();
          data[i]= new Datum(dataDetails, 1, 1, 1, 1, 1, 1, 1); // second to eighth parameter is just a dummy 
        }
     } // end of reading xCyD

    
        
    
/**
 * public void createBackup() {
        for (int i =0; i < data.length; i++) {
            backupData[i] = new Datum(data[i].getValue(), data[i].getSymbol());
            Parameters.setDistanceMeasure(backupData[i]);
        }
    }
*/
    
    /**
    public void randomiseDataPosn() {
        int randXPos, randYPos;
        
        for (int i =0; i < data.length; i++) {
            randXPos = (int)Math.floor(Math.random()*Parameters.maxX);
            randYPos = (int)Math.floor(Math.random()*Parameters.maxY);
            this.data[i].setPos(randXPos, randYPos); 
            createBackup();
        }
    }
***/
    
    public void setDistanceMeasure(int distType) {
        for (int i =0; i < data.length; i++) {
            if (Parameters.distType == 1) { // 1 for cosine measure
                data[i].setDistanceMeasure(new Cosine());
            }
            if (Parameters.distType == 2) { // 2 for euclidean
                data[i].setDistanceMeasure(new Euclidean());
            }
            if (Parameters.distType == 3) { // 3 for euclidean
                data[i].setDistanceMeasure(new OneNorm());
            }

        }
    }
    
    
    public void setDistanceMeasure() {
        if (Parameters.distType == 1) {
            System.out.println("Cosine distance was used");
        } else {
            System.out.println("Euclidean distance was used");
        }
        
        for (int i =0; i < data.length; i++) {
            if (Parameters.distType == 1) { // 1 for cosine measure
                data[i].setDistanceMeasure(new Cosine());
//                backupData[i].setDistanceMeasure(new Cosine());
            } else {  // must be 2 for euclidean
                data[i].setDistanceMeasure(new Euclidean());
//                backupData[i].setDistanceMeasure(new Euclidean());
            }
        }
    }    
    
    
    public void generateLine() {
        generateSytheticData(1,    50,   0, 49, 'o');
        generateSytheticData(100, 150, 50, 499, '+');
        generateSytheticData(200, 250, 500, 749, 'x');
        generateSytheticData(300, 350, 750, 999, '*');
    }
    
    public void loadFileData(String filename){
        FileIO filehandle = openFileToRead(filename);
        readFileData(filehandle);
        closeReader(filehandle);
    }
  
    private void readFileData(FileIO dataFile)
  {
    String dataDetails;
    dataDetails = dataFile.readString();
    while (!dataDetails.contains("*** Data ***")){
        dataDetails = dataFile.readString();
        System.out.println(dataFile);
    }
    System.out.println(Parameters.dataSize);
    for (int i = 0; i < Parameters.dataSize; i++) {
      if(i%1000 == 0){
          System.out.println("Line#"+i);
      }
      dataDetails = dataFile.readString();
      data[i]= new Datum(dataDetails, 1.0, 'a');
    }
  } 
    
    //UNIFORM distribution of data from startRange to endRange
    public void generateSytheticData(int startRange, int endRange, int startIndex, int endIndex, char symbol){
        int range = endRange - startRange;
        double randVal;
        int randXPos, randYPos;

        for (int i = startIndex; i <= endIndex; i++){
            double [] value = new double[1];
            value[0] = (startRange + Math.floor(Math.random()*range));
            randXPos = (int)Math.floor(Math.random()*Parameters.maxX);
            randYPos = (int)Math.floor(Math.random()*Parameters.maxY);
            data[i] = new Datum(value, randXPos, randYPos,  symbol, 1);
        }
    }

    
    
     public void loadIRISData(){
        FileIO filehandle = openFileToRead("C:\\Documents and Settings\\jamestan\\My Documents\\DATA\\IRIS.txt");
        readIrisData(filehandle);
        closeReader(filehandle);
    }

    public void loadWINEData(){
        FileIO filehandle = openFileToRead("C:\\Documents and Settings\\jamestan\\My Documents\\DATA\\Wine.txt");
        readWineData(filehandle);
        closeReader(filehandle);
    }

    public void loadWisconsinData(){
        FileIO filehandle = openFileToRead("C:\\Documents and Settings\\jamestan\\My Documents\\DATA\\wisconsin.txt");
        readWisconsinData(filehandle);
        closeReader(filehandle);
    }

    
    public void load_xDyC_Data_BK(){
        FileIO filehandle = openFileToRead(Parameters.filename);
        read_xCyD_Data(filehandle);
        closeReader(filehandle);
    }  
    
    
  private void readIrisData(FileIO dataFile)
  {
    String dataDetails;
    for (int i = 0; i < Parameters.dataSize; i++) {
      dataDetails = dataFile.readString();
      data[i]= new Datum(dataDetails);
    }
  } // end of reading IRIS data

  private void readWineData(FileIO dataFile)
  {
    String dataDetails;
    for (int i = 0; i <  Parameters.dataSize; i++) {
      dataDetails = dataFile.readString();
      data[i]= new Datum(dataDetails, 1); // second parameter is just a dummy
    }
  } // end of reading WINE data

 private void readWisconsinData(FileIO dataFile)
  {
    String dataDetails;
    for (int i = 0; i <  Parameters.dataSize; i++) {
      dataDetails = dataFile.readString();
      data[i]= new Datum(dataDetails, 1, 1); // second and thrid parameter is just a dummy
    }
  } // end of reading Wisconsin data

  
 private void read_xCyD_Data_BK(FileIO dataFile)
  {
    String dataDetails;
    for (int i = 0; i <  Parameters.dataSize; i++) {
      dataDetails = dataFile.readString();
      data[i]= new Datum(dataDetails, 1, 1, 1, 1, 1, 1, 1); // second to eighth parameter is just a dummy 
    }
  } // end of reading Digits data
 
  // open file to read data from
  public FileIO openFileToRead(String filename)
  {
    FileIO jobsfile = new FileIO(filename, 'R');
    return jobsfile;
  } // end of openFileToRead()

  // close reading file
  public void closeReader(FileIO jobsfilehandle)
  {
    jobsfilehandle.close('R');
  } // end of closeReader()

  
  
  public void initMatrix() {
    int N = data.length - 1;
    Datum m = null;

    // create the triangular array structure
    matrix = new double[N][];
    for (int i=0; i<N; i++) {
      matrix[i] = new double[i+1];
    }
    
    int i, j;
    int di, dj;
    for (i=1; i<=N; i++) {
      m = data[i];
      di = data[i].getIndex();
      for (j=0; j<i; j++) {
        dj = data[j].getIndex();
//        System.out.println("di-1="+(di-1) +", dj=" + dj);
        matrix[di-1][dj] = (double)m.preComputeDistanceFrom(data[j]);
      }
    }
  }
  
  public void initMatrix_backup() {
    int N = data.length - 1;
    Datum m = null;

    // create the triangular array structure
    matrix = new double[N][];
    for (int i=0; i<N; i++) {
      matrix[i] = new double[i+1];
    }
    
    int i, j;
    for (i=1; i<=N; i++) {
      m = data[i];
      for (j=0; j<i; j++) {
//        matrix[i-1][j] = (double)m.distanceFrom(data[j]);
        matrix[i-1][j] = (double)m.preComputeDistanceFrom(data[j]);
      }
    }
  }
  
  
  public double[][] getMatrix(){
      return this.matrix;
  }
  
  
  public void normaliseMatrix() {
    int N = data.length - 1;
    int i, j;

    for (i=1; i<=N; i++) {
      for (j=0; j<i; j++) {
//        matrix[i-1][j] = matrix[i-1][j]/getMaxElementMatrix();
          matrix[i-1][j] = matrix[i-1][j]/Parameters.maxDist;
      }
    }
  }

  public double getMaxElementMatrix() {
    int N = data.length - 1;
    int i, j;
    double max = -100000;
    double distance;
    Datum m = null;
    
    for (i=1; i<=N; i++) {
        m = data[i];
        for (j=0; j<i; j++) {
          distance = (double)m.distanceFrom(data[j]);
          max = Math.max(max, distance);
      }
    }
    return max;
  }

  public void printMatrix() {
    int N = data.length - 1;
    DecimalFormat twoDigits = new DecimalFormat("0.00");
    DecimalFormat fourDigits = new DecimalFormat("000000");

    int z, i, j;
    System.out.println("Triangular Dissimilarity Matrix");
    for (z=1; z<=N; z++) System.out.print(fourDigits.format(z));
    System.out.println("");

    for (i=1; i<=N; i++) {
      for (j=0; j<i; j++) {
        System.out.print((i-1)+":"+j+"="+twoDigits.format(matrix[i-1][j]) + ", ");
      }
      System.out.println("");
    }
  }
  
  public double getScaleFactor() {
    int N = data.length - 1;
    Datum m = null;
    double scaleFactor;
    double totalDistance = 0;
    
    for (int i=1; i<=N; i++) {
      m = data[i];
      for (int j=0; j<i; j++) {
        //totalDistance += matrix[i-1][j];
          totalDistance += (double)m.distanceFrom(data[j]);
      }
    }
    // this is more used in 2D normal distribution
    // compute the scale factor (average over all inter-document distances)
    scaleFactor = totalDistance/(0.5*(float)(N*(N-1))); 
    return scaleFactor;
  }

  public void setParametersMaxDist() {
    int N = data.length - 1;
    Datum m = null;
    double Distance = 0;
    Parameters.maxDist = -1;
  
    for (int i=1; i<=N; i++) {
      m = data[i];
      for (int j=0; j<i; j++) {
          Distance = (double)m.unnormalisedDistanceFrom(data[j]);
          Parameters.maxDist = Math.max(Distance, Parameters.maxDist);
      }
    }
//      Parameters.maxDist = 1;
//      Parameters.maxDist = getMaxElementMatrix();
      System.out.println("Parameters.maxDist = " + Parameters.maxDist);
  }

}
