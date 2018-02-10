/*
 * DistanceFunction.java
 *
 * Created on 24 August 2005, 10:15
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
/**
* The Interface is a general distance function strategy or algorithm
* usable for different distance measure such as Euclidean, Cosine metrics, etc.
* <BR><BR>
* More specific distance measures implements this interface<BR>
* <BR>
* @version 1.0, 24/Aug/2005
* @author  James Tan <A HREF= "mailto:James.Tan@infotech.monash.edu.au">James.Tan@infotech.monash.edu.au</A>
* @since Java 2
*/

package stochastic;

/**
 *
 * @author jamestan
 */

public abstract interface DistanceFunction {

/** Calculate a distance
 * @return a double specifying distance
 */
   public abstract double compute(Datum d);
   public abstract double precompute(Datum d);
   public abstract double precomputed(Datum d);

/** set an object which uses this strategy */
   public abstract void setClient(Object obj);
}

class Euclidean implements DistanceFunction {
  protected Datum myDatum;

  public Euclidean () {
  }

  public Euclidean (Datum d) {
      this.myDatum  = d;
  }
  
  public void setClient(Object l) {
    this.myDatum  =  (Datum) l;
  }

  public double precompute(Datum d) {
        double [] data1 = myDatum.getValue();
        double [] data2 = d.getValue();
        double num;
        double total = 0;

        for (int i = 0; i < data1.length; i++) {
          num = data1[i] - data2[i]; // assume no missing value
          total += num*num;
        }
        return Math.sqrt(total);
 }      

  public double precomputed(Datum d) {
        int index1 = myDatum.index;
        int index2 = d.index;
        double dist = 0;
        if (index1 == index2) {
            //System.out.println("Same same hor, problem lah! index: " + index1 + " and " + myDatum + " and " + d);
            return 0.0;
        }
        if (index1 > index2){
            dist = Parameters.matrix[index1-1][index2];
        } else {
            dist = Parameters.matrix[index2-1][index1];
        }
        return dist;
 }      
  
  
  public double compute(Datum d) {
        int i = myDatum.getIndex();
        int j = d.getIndex();
        if ((i == -1) || (j==-1)){
            System.out.println("Problem!!!!!!!!!!!!!!!!");
        }
        
        if (i > j) {
            return Parameters.matrix[i-1][j];   // must be used in conjuction with Julia neighbourhood function
        } else {
            return Parameters.matrix[j-1][i]; 
        }

   }      
  
  public double compute_bk(Datum d) {
        double [] data1 = myDatum.getValue();
        double [] data2 = d.getValue();
        double num;
        double total = 0;

        for (int i = 0; i < data1.length; i++) {
          num = data1[i] - data2[i]; // assume no missing value
          total += num*num;
        }
        return Math.sqrt(total);
 }      
 
  public String toString() {
      return "I am an Euclidean distance measure";
  }  
  
}


class OneNorm implements DistanceFunction {
  protected Datum myDatum;

  public OneNorm () {
  }

  public OneNorm (Datum d) {
      this.myDatum  = d;
  }
  
  public void setClient(Object l) {
    this.myDatum  =  (Datum) l;
  }

  public double precompute(Datum d) {
        double [] data1 = myDatum.getValue();
        double [] data2 = d.getValue();
        double num;
        double total = 0;

        for (int i = 0; i < data1.length; i++) {
          num = Math.abs(data1[i] - data2[i]); // assume no missing value
          total += num;
        }
        return total/((double)data1.length);
 }      

  public double precomputed(Datum d) {
        double [] data1 = myDatum.getValue();
        double [] data2 = d.getValue();
        double num;
        double total = 0;

        for (int i = 0; i < data1.length; i++) {
          num = Math.abs(data1[i] - data2[i]); // assume no missing value
          total += num;
        }
        return total/((double)data1.length);
 }      
  
  
  public double compute(Datum d) {
        double [] data1 = myDatum.getValue();
        double [] data2 = d.getValue();
        double num;
        double total = 0;

        for (int i = 0; i < data1.length; i++) {
          num = Math.abs(data1[i] - data2[i]); // assume no missing value
          total += num;
        }
        return total/((double)data1.length);
   }      
  
  public String toString() {
      return "I am an one norm distance measure";
  }  
  
}



class Cosine implements DistanceFunction {
  protected Datum myDatum;

  public Cosine () {
  }

  public Cosine (Datum d) {
      this.myDatum  = d;
  }
  
  public void setClient(Object l) {
    this.myDatum  =  (Datum) l;
  }

  public String toString() {
      return "I am a Cosine distance measure";
  }

  public double precompute(Datum d) {
        double [] data1 = myDatum.getValue();
        double [] data2 = d.getValue();
        
        double num;
        double cosineSim = 0.0, MonteSimilarity, JuliaDistance, JuliaSimilarity;
        double sum_ikjk = 0.0, sum_ikik = 0.0, sum_jkjk = 0.0;

        for (int i = 0; i < data1.length; i++) {
          sum_ikjk += (data1[i] * data2[i]); // assume no missing value
          sum_ikik += (data1[i] * data1[i]);
          sum_jkjk += (data2[i] * data2[i]);
        }
        cosineSim = sum_ikjk/(Math.sqrt(sum_ikik*sum_jkjk));  // cosine similarity metric
        
        JuliaDistance = (1.0 - 0.5*(1.0 + cosineSim));  // Julia's method of converting to Dis-similarity
//        JuliaSimilarity = 1 - JuliaDistance/Parameters.alpha;

        return JuliaDistance;   // must be used in conjuction with Julia neighbourhood function

   }      

  public double precomputed(Datum d) {
        int index1 = myDatum.index;
        int index2 = d.index;
        if (index1 == index2) return 0.0;
        
        double dist = 0;
        if (index1 > index2){
            dist = Parameters.matrix[index1-1][index2];
        } else {
            dist = Parameters.matrix[index2-1][index1];
        }
        return dist;
 }      
  
  
  
  public double compute(Datum d) {
        int i = myDatum.getIndex();
        int j = d.getIndex();
        if ((i == -1) || (j==-1)){
            System.out.println("Problem!!!!!!!!!!!!!!!!");
        }
        
        if (i > j) {
            return Parameters.matrix[i-1][j];   // must be used in conjuction with Julia neighbourhood function
        } else {
            return Parameters.matrix[j-1][i]; 
        }

   }      
}
  class General2DEucliden implements DistanceFunction {
      protected Datum myDatum;

      public General2DEucliden () {
      }

      public General2DEucliden (Datum d) {
          this.myDatum  = d;
      }

      public void setClient(Object l) {
        this.myDatum  =  (Datum) l;
      }

      public double precompute(Datum d) {
        double [] dVal = d.getValue();

        double dist = Parameters.euclidean(dVal[0], dVal[1], this.myDatum.value[0], this.myDatum.value[1]);
        return dist;
      }      
      
  public double precomputed(Datum d) {
        int index1 = myDatum.index;
        int index2 = d.index;
        if (index1 == index2) return 0.0;
        
        double dist = 0;
        if (index1 > index2){
            dist = Parameters.matrix[index1-1][index2];
        } else {
            dist = Parameters.matrix[index2-1][index1];
        }
        return dist;
 }            
      
      
      public double compute(Datum d) {
        double [] dVal = d.getValue();

        double dist = Parameters.euclidean(dVal[0], dVal[1], this.myDatum.value[0], this.myDatum.value[1]);
        return dist;
      }      
}


