/*
 * Parameters.java
 *
 * Created on 4 August 2005, 18:29
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package stochastic;

import java.util.*;
import java.text.DecimalFormat;
import sizeof.agent.SizeOfAgent;

class iNode {
    public int maxHeight = Parameters.maxTreeHeight;;
    public boolean isRoot = false;
    public double splitPoint = 0;
    public iNode parentNode = null;
    public iNode leftChildNode = null;
    public iNode rightChildNode = null;
    public boolean isLeaf = false;
    public int leafCounter = -1;
    public int myLevel = 0;
    public int dimIndex = 0;
    public int mySize = 0;
    public int mySize2 = 0;
    public ArrayList myList = null;
    public boolean computedScore = false;
    public double distScore = 0;
    public boolean marked = false;
    public boolean mass1 = true;

    public double myMax[];
    public double myMin[];

    iNode(double max[], double min[], int ml){
    //    myMax = new double[max.length];
    //    myMin = new double[min.length];
    //    for(int i=0; i < max.length; i++){
   //         myMax[i] = max[i];
     //       myMin[i] = min[i];
    //    }
        this.myLevel = ml;

   //     myList = new ArrayList();
       
        if (myLevel == maxHeight){
            isLeaf = true;
            leafCounter = 1;

         //   System.out.println("I am a leaf");
        } else { // continue to expand the child nodes

            dimIndex = Parameters.generator.nextInt(min.length);
            while(min[dimIndex] == max[dimIndex]){
                dimIndex = Parameters.generator.nextInt(min.length);
//                System.out.print(".");
            }

            /*
            ArrayList randomList = new ArrayList();
            for(int i=0; i<max.length; i++) {
                randomList.add(i);
            }
            int idx = (int) Parameters.generator.nextInt(randomList.size());
            dimIndex = ((Integer)randomList.get(idx)).intValue();
            while((min[dimIndex] == max[dimIndex])&&(randomList.size() > 0)){
                if (randomList.size()==1){
                    dimIndex = ((Integer)randomList.get(0)).intValue();
                    randomList.remove(0);
                }else {
                    randomList.remove(idx);
                    idx = (int) Parameters.generator.nextInt(randomList.size());
                    dimIndex = ((Integer)randomList.get(idx)).intValue();
                }
            }
            if(randomList.size() == 0){
                dimIndex = (int) Parameters.generator.nextInt(max.length);
            }
*/

         // half split seems better
         //   this.splitPoint = min[dimIndex] + Math.random()*(max[dimIndex]-min[dimIndex]);
            this.splitPoint = (min[dimIndex] + max[dimIndex])/2;
       //     System.out.println("my level " + ml + " I am a node with split point = " + splitPoint + ", dimIndex = " + dimIndex);
        //    System.out.println("min[dimIndex], max[dimIndex]" + min[dimIndex] +"," +max[dimIndex]);


           double newMax[] = new double[max.length];
           double newMin[] = new double[min.length];
            for(int i=0; i < max.length; i++){
                newMax[i] = max[i];
                newMin[i] = min[i];
            }
            newMax[dimIndex]=splitPoint;
            newMin[dimIndex]=splitPoint;
            leftChildNode = new iNode(max, newMin, ml+1);
            rightChildNode = new iNode(newMax, min, ml+1);

            /*
            double oldMax = max[dimIndex];
            double oldMin = min[dimIndex];
            min[dimIndex] = splitPoint;
            leftChildNode = new iNode(max, min, ml+1);

            min[dimIndex] = oldMin;
            max[dimIndex] = splitPoint;
            rightChildNode = new iNode(max, min, ml+1);
*/
        }
    }

    public void clearArray() {
        myMax = null;
        myMin = null;
        if (myLevel < maxHeight){
            leftChildNode.clearArray();
            rightChildNode.clearArray();
        }
    }

    
   public void assign(Datum d){
        myList.add(d);
        if (isLeaf){
            leafCounter++;
        } else {
            if (d.value[dimIndex] > splitPoint) {
                leftChildNode.assign(d);
            } else {
                rightChildNode.assign(d);
            }
        }
    }


   public void assignSize(Datum d){
    //        if (Parameters.checkType(d) == 0) {
                this.mySize++;
    //        }
            if (isLeaf){
                leafCounter++;
            } else {
                if (d.value[dimIndex] > splitPoint) {
                    leftChildNode.assignSize(d);
                } else {
                    rightChildNode.assignSize(d);
                }
            }
    }

   public void assignSize2(Datum d){
      //  if (Parameters.checkType(d) == 0) {
           this.mySize2++;
     //   }
        if (!isLeaf){
            if (d.value[dimIndex] > splitPoint) {
                leftChildNode.assignSize2(d);
            } else {
                rightChildNode.assignSize2(d);
            }
        }
    }

   public void printMass1Mass2(){
//       if (isLeaf){
    if (this.myLevel==10){
           if(mySize!=0 || mySize2!=0)
            System.out.println("mySize:," + mySize + ", mySize2:," + mySize2);
        } else {
            leftChildNode.printMass1Mass2();
            rightChildNode.printMass1Mass2();
        }
    }

    public double log2(double n){
        return Math.log(n)/Math.log(2);
    }

    public void printAugmentedMass1Mass2_Ver1(){
        if (isLeaf){
                if (mySize ==0)mySize = 1;
                if (mySize2 ==0)mySize2 = 1;
               // System.out.println("my level:, " + myLevel + ", mySizePathLen:," + (Math.log(mySize) + (double)myLevel) + ", mySize2_PathLen:," + (Math.log(mySize2) + (double)myLevel));
                 System.out.println("my level:, " + myLevel + ", mySizePathLen:," + (log2(mySize) + (double)myLevel) + ", mySize2_PathLen:," +  (log2(mySize2) + (double)myLevel));
         } else {
//                if ((leftChildNode.mySize > 40)||(leftChildNode.mySize2 > 40)) {
                if (leftChildNode.mySize > 20) {
                    leftChildNode.printAugmentedMass1Mass2_Ver1();
                } else {
                    if (leftChildNode.mySize ==0)leftChildNode.mySize = 1;
                    if (leftChildNode.mySize2 ==0) leftChildNode.mySize2 = 1;
                    System.out.println("my level:, " + leftChildNode.myLevel + ", mySizePathLen:," + (log2(leftChildNode.mySize) + (double)leftChildNode.myLevel) + ", mySize2_PathLen:," + (log2(leftChildNode.mySize2) + (double)leftChildNode.myLevel));
                }
//                if ((rightChildNode.mySize > 40)||(rightChildNode.mySize2 > 40)) {
                if (rightChildNode.mySize > 20) {
                    rightChildNode.printAugmentedMass1Mass2_Ver1();
                } else {
                    if (rightChildNode.mySize ==0)rightChildNode.mySize = 1;
                    if (rightChildNode.mySize2 ==0) rightChildNode.mySize2 = 1;
                    System.out.println("my level:, " + rightChildNode.myLevel + ", mySizePathLen:," + (log2(rightChildNode.mySize) + (double)rightChildNode.myLevel) + ", mySize2_PathLen:," + (log2(rightChildNode.mySize2) + (double)rightChildNode.myLevel));
                }
          }
    }


    public ArrayList getAugmentedMass1Mass2_Ver1(ArrayList alist1){

        if (isLeaf){
                if (mySize ==0)mySize = 1;
                if (mySize2 ==0)mySize2 = 1;
               // System.out.println("my level:, " + myLevel + ", mySizePathLen:," + (Math.log(mySize) + (double)myLevel) + ", mySize2_PathLen:," + (Math.log(mySize2) + (double)myLevel));
               // System.out.println("my level:, " + myLevel + ", mySizePathLen:," + (log2(mySize) + (double)myLevel) + ", mySize2_PathLen:," +  (log2(mySize2) + (double)myLevel));
                double val2[] = new double[2];
                val2[0] = (log2(mySize) + (double)myLevel);
                val2[1] = (log2(mySize2) + (double)myLevel);
                alist1.add(val2);
         } else {
//                if ((leftChildNode.mySize > 40)||(leftChildNode.mySize2 > 40)) {
                if (leftChildNode.mySize > 10) {
                    alist1 = leftChildNode.getAugmentedMass1Mass2_Ver1(alist1);
                } else {
                    if (leftChildNode.mySize ==0)leftChildNode.mySize = 1;
                    if (leftChildNode.mySize2 ==0) leftChildNode.mySize2 = 1;
                 //   System.out.println("my level:, " + leftChildNode.myLevel + ", mySizePathLen:," + (log2(leftChildNode.mySize) + (double)leftChildNode.myLevel) + ", mySize2_PathLen:," + (log2(leftChildNode.mySize2) + (double)leftChildNode.myLevel));
                    double val2[] = new double[2];
                    val2[0] = (log2(leftChildNode.mySize) + (double)leftChildNode.myLevel);
                    val2[1] = (log2(leftChildNode.mySize2) + (double)leftChildNode.myLevel);
                    alist1.add(val2);
                }
//                if ((rightChildNode.mySize > 40)||(rightChildNode.mySize2 > 40)) {
                if (rightChildNode.mySize > 10) {
                    alist1 = rightChildNode.getAugmentedMass1Mass2_Ver1(alist1);
                } else {
                    if (rightChildNode.mySize ==0)rightChildNode.mySize = 1;
                    if (rightChildNode.mySize2 ==0) rightChildNode.mySize2 = 1;
                 //   System.out.println("my level:, " + rightChildNode.myLevel + ", mySizePathLen:," + (log2(rightChildNode.mySize) + (double)rightChildNode.myLevel) + ", mySize2_PathLen:," + (log2(rightChildNode.mySize2) + (double)rightChildNode.myLevel));
                    double val2[] = new double[2];
                    val2[0] = (log2(rightChildNode.mySize) + (double)rightChildNode.myLevel);
                    val2[1] = (log2(rightChildNode.mySize2) + (double)rightChildNode.myLevel);
                    alist1.add(val2);
                }
          }
        return alist1;
    }

    public ArrayList getAugmentedMass1Mass2_Ver2(ArrayList alist1){
        double tempMySize = mySize;
        double tempMySize2 = mySize2;

        if (isLeaf){
                tempMySize = Math.max(1, mySize);
                tempMySize2 = Math.max(1, mySize2);
               // System.out.println("my level:, " + myLevel + ", mySizePathLen:," + (Math.log(mySize) + (double)myLevel) + ", mySize2_PathLen:," + (Math.log(mySize2) + (double)myLevel));
               // System.out.println("my level:, " + myLevel + ", mySizePathLen:," + (log2(mySize) + (double)myLevel) + ", mySize2_PathLen:," +  (log2(mySize2) + (double)myLevel));
                double val2[] = new double[3];
                val2[0] = (log2(tempMySize) + (double)myLevel);
                val2[1] = (log2(tempMySize2) + (double)myLevel);
                val2[2] = Math.pow(2, -(double)myLevel);
                alist1.add(val2);
         } else {

                //if (leftChildNode.mySize > 10) {
                if (leftChildNode.mySize > Parameters.maxLeafCount) {
                    alist1 = leftChildNode.getAugmentedMass1Mass2_Ver2(alist1);
                } else {
                    tempMySize = Math.max(1, leftChildNode.mySize);
                    tempMySize2 = Math.max(1, leftChildNode.mySize2);
                 //   System.out.println("my level:, " + leftChildNode.myLevel + ", mySizePathLen:," + (log2(leftChildNode.mySize) + (double)leftChildNode.myLevel) + ", mySize2_PathLen:," + (log2(leftChildNode.mySize2) + (double)leftChildNode.myLevel));
                    double val2[] = new double[3];
                    val2[0] = (log2(tempMySize) + (double)leftChildNode.myLevel);
                    val2[1] = (log2(tempMySize2) + (double)leftChildNode.myLevel);
                    val2[2] = Math.pow(2, -(double)leftChildNode.myLevel);
                    alist1.add(val2);
                }
//                if ((rightChildNode.mySize > 40)||(rightChildNode.mySize2 > 40)) {
                //if (rightChildNode.mySize > 10) {
                if (rightChildNode.mySize > Parameters.maxLeafCount) {
                    alist1 = rightChildNode.getAugmentedMass1Mass2_Ver2(alist1);
                } else {
                    tempMySize = Math.max(1, rightChildNode.mySize);
                    tempMySize2 = Math.max(1, rightChildNode.mySize2);
                 //   System.out.println("my level:, " + rightChildNode.myLevel + ", mySizePathLen:," + (log2(rightChildNode.mySize) + (double)rightChildNode.myLevel) + ", mySize2_PathLen:," + (log2(rightChildNode.mySize2) + (double)rightChildNode.myLevel));
                    double val2[] = new double[3];
                    val2[0] = (log2(tempMySize) + (double)rightChildNode.myLevel);
                    val2[1] = (log2(tempMySize2) + (double)rightChildNode.myLevel);
                    val2[2] = Math.pow(2, -(double)rightChildNode.myLevel);
                    alist1.add(val2);
                }
          }
        return alist1;
    }

    public ArrayList getAugmentedMass1Mass2_Ver3(ArrayList alist1){
        double tempMySize = mySize;
        double tempMySize2 = mySize2;

        if (isLeaf){
                tempMySize = Math.max(1, mySize);
                tempMySize2 = Math.max(1, mySize2);
               // System.out.println("my level:, " + myLevel + ", mySizePathLen:," + (Math.log(mySize) + (double)myLevel) + ", mySize2_PathLen:," + (Math.log(mySize2) + (double)myLevel));
               // System.out.println("my level:, " + myLevel + ", mySizePathLen:," + (log2(mySize) + (double)myLevel) + ", mySize2_PathLen:," +  (log2(mySize2) + (double)myLevel));
                double val2[] = new double[3];
                val2[0] = (log2(tempMySize) + (double)myLevel);
                val2[1] = (log2(tempMySize2) + (double)myLevel);
                val2[2] = Math.pow(2, -(double)myLevel);

                alist1.add(val2);
         } else {

                //if (leftChildNode.mySize > 20) {
                if (leftChildNode.mySize > Parameters.maxLeafCount) {
                    alist1 = leftChildNode.getAugmentedMass1Mass2_Ver2(alist1);
                } else {
                    tempMySize = Math.max(1, leftChildNode.mySize);
                    tempMySize2 = Math.max(1, leftChildNode.mySize2);
                 //   System.out.println("my level:, " + leftChildNode.myLevel + ", mySizePathLen:," + (log2(leftChildNode.mySize) + (double)leftChildNode.myLevel) + ", mySize2_PathLen:," + (log2(leftChildNode.mySize2) + (double)leftChildNode.myLevel));
                    double val2[] = new double[3];
                    val2[0] = (log2(tempMySize) + (double)leftChildNode.myLevel);
                    val2[1] = (log2(tempMySize2) + (double)leftChildNode.myLevel);
                    val2[2] = Math.pow(2, -(double)leftChildNode.myLevel);
                    alist1.add(val2);
                }
//                if ((rightChildNode.mySize > 40)||(rightChildNode.mySize2 > 40)) {
                //if (rightChildNode.mySize > 20) {
                if (rightChildNode.mySize > Parameters.maxLeafCount) {
                    alist1 = rightChildNode.getAugmentedMass1Mass2_Ver2(alist1);
                } else {
                    tempMySize = Math.max(1, rightChildNode.mySize);
                    tempMySize2 = Math.max(1, rightChildNode.mySize2);
                 //   System.out.println("my level:, " + rightChildNode.myLevel + ", mySizePathLen:," + (log2(rightChildNode.mySize) + (double)rightChildNode.myLevel) + ", mySize2_PathLen:," + (log2(rightChildNode.mySize2) + (double)rightChildNode.myLevel));
                    double val2[] = new double[3];
                    val2[0] = (log2(tempMySize) + (double)rightChildNode.myLevel);
                    val2[1] = (log2(tempMySize2) + (double)rightChildNode.myLevel);
                    val2[2] = Math.pow(2, -(double)rightChildNode.myLevel);
                    alist1.add(val2);
                }
          }
        return alist1;
    }

public ArrayList getAugmentedMass1Mass2_Ver4(ArrayList alist1){
        double tempMySize = mySize;
        double tempMySize2 = mySize2;

        if (isLeaf){
                tempMySize = Math.max(1, mySize);
                tempMySize2 = Math.max(1, mySize2);
               // System.out.println("my level:, " + myLevel + ", mySizePathLen:," + (Math.log(mySize) + (double)myLevel) + ", mySize2_PathLen:," + (Math.log(mySize2) + (double)myLevel));
               // System.out.println("my level:, " + myLevel + ", mySizePathLen:," + (log2(mySize) + (double)myLevel) + ", mySize2_PathLen:," +  (log2(mySize2) + (double)myLevel));
                double val2[] = new double[2];
                val2[0] = tempMySize * Math.pow(2,(double)myLevel);
                val2[1] = tempMySize2* Math.pow(2,(double)myLevel);
                alist1.add(val2);
         } else {

                if (leftChildNode.mySize > 20) {
                    alist1 = leftChildNode.getAugmentedMass1Mass2_Ver2(alist1);
                } else {
                    tempMySize = Math.max(1, leftChildNode.mySize);
                    tempMySize2 = Math.max(1, leftChildNode.mySize2);
                 //   System.out.println("my level:, " + leftChildNode.myLevel + ", mySizePathLen:," + (log2(leftChildNode.mySize) + (double)leftChildNode.myLevel) + ", mySize2_PathLen:," + (log2(leftChildNode.mySize2) + (double)leftChildNode.myLevel));
                    double val2[] = new double[2];
                    val2[0] = tempMySize * Math.pow(2,(double)leftChildNode.myLevel);
                    val2[1] = tempMySize2* Math.pow(2,(double)leftChildNode.myLevel);

                    alist1.add(val2);
                }
//                if ((rightChildNode.mySize > 40)||(rightChildNode.mySize2 > 40)) {
                if (rightChildNode.mySize > 20) {
                    alist1 = rightChildNode.getAugmentedMass1Mass2_Ver2(alist1);
                } else {
                    tempMySize = Math.max(1, rightChildNode.mySize);
                    tempMySize2 = Math.max(1, rightChildNode.mySize2);
                 //   System.out.println("my level:, " + rightChildNode.myLevel + ", mySizePathLen:," + (log2(rightChildNode.mySize) + (double)rightChildNode.myLevel) + ", mySize2_PathLen:," + (log2(rightChildNode.mySize2) + (double)rightChildNode.myLevel));
                    double val2[] = new double[2];
                    val2[0] = tempMySize * Math.pow(2,(double)rightChildNode.myLevel);
                    val2[1] = tempMySize2* Math.pow(2,(double)rightChildNode.myLevel);
                    alist1.add(val2);
                }
          }
        return alist1;
    }

   public void printAugmentedMass1Mass2(){
        if (marked){
            if (mySize ==0)mySize = 1;
            if (mySize2 ==0)mySize2 = 1;
            System.out.println("my level:, " + myLevel + ", mySizePathLen:," + (Math.log(mySize) + (double)myLevel) + ", mySize2_PathLen:," + (Math.log(mySize2) + (double)myLevel));
        } 
        if (!isLeaf){
            leftChildNode.printAugmentedMass1Mass2();
            rightChildNode.printAugmentedMass1Mass2();
        }
    }


   public double computerSizeDiff(){
       double myDiff = 0.0;
        if (isLeaf){
            return (Math.abs(mySize2 - mySize));
        } else {
            myDiff += leftChildNode.computerSizeDiff();
            myDiff += rightChildNode.computerSizeDiff();
        }

       return myDiff;
    }

   public double computerSizeDiffVer1(){
       double sum = 0.0;
       double myDiff = Math.abs(mySize2 - mySize);
       //sum += myDiff * Math.log10(myDiff+1);
       sum += myDiff;
        if (!isLeaf){
            myDiff = leftChildNode.computerSizeDiff();
       //     sum += myDiff * Math.log10(myDiff+1);
            sum += myDiff;

            myDiff = rightChildNode.computerSizeDiff();
            //sum += myDiff * Math.log10(myDiff+1);
            sum += myDiff;
        }
       return sum;
    }


    public void updateSize(){
//        this.mySize = this.mySize2; // moved to last line
        //if(this.mySize>0){ //added on 23 Sep to speed up
        if((this.mySize>0)||(this.mySize2>0)){
            if (!isLeaf){
                leftChildNode.updateSize();
                rightChildNode.updateSize();
            }
        } //added on 23 Sep to speed up
        this.mySize = this.mySize2;
    }

    public void clearALLSize2(){
        //if (this.mySize2 > 0){
            this.mySize2 = 0;
            if (!isLeaf){
                leftChildNode.clearSize2();
                rightChildNode.clearSize2();
            }
        //}
    }

    public long computeTreeSize(long treeSize){
        treeSize += (long)SizeOfAgent.sizeOf(this);
        if (!isLeaf){
            treeSize += leftChildNode.computeTreeSize(treeSize);
            treeSize += rightChildNode.computeTreeSize(treeSize);
        }
        return treeSize;
    }


    public void clearSize2(){
        if (this.mySize2 > 0){
            this.mySize2 = 0;
            if (!isLeaf){
                leftChildNode.clearSize2();
                rightChildNode.clearSize2();
            }
        }
    }

    public int score(Datum d){
        if (isLeaf){
            return leafCounter;
        } else {
            if (d.value[dimIndex] > splitPoint) {
                return leftChildNode.score(d);
            } else {
                return rightChildNode.score(d);
            }
        }
    }

    public double scoreDist(Datum d){
        if (this.computedScore){
            return this.distScore;
        }
        if (isLeaf){
            return this.computeScore(d);
           // return leafCounter;
        } else {
            if (d.value[dimIndex] > splitPoint) {
                if (leftChildNode.myList.size() < 2){
                    return this.computeScore(d);
                } else {
                    return leftChildNode.scoreDist(d);
                }
            } else {
                if (rightChildNode.myList.size() < 2){
                    return this.computeScore(d);
                } else {
                    return rightChildNode.scoreDist(d);
                }
            }
        }
    }

    public double computeScore(Datum d) {
           double dist = 0;
           double counter = 0;
           double temp = 0;
           if (myList.size() <= 1) {
                //compute max dist within this subspace
                for(int i=0; i < d.dim; i++) {
                    temp = myMax[i] - myMin[i];
                    this.distScore += (temp*temp);
                }
                this.distScore = Math.sqrt(distScore);
                this.computedScore = true;
                return this.distScore;
            }
            if (myList.size() == 2) {
                Datum d1 = (Datum) myList.get(0);
                Datum d2 = (Datum) myList.get(1);
                this.distScore = d1.distanceFrom(d2);
                this.computedScore = true;
                return distScore;
            }
            for(int i=0; i< (myList.size()-1); i++) {
                Datum d1 = (Datum) myList.get(i);
                for(int j=(i+1); j < myList.size(); j++){
                    Datum d2 = (Datum) myList.get(j);
                    dist += d1.distanceFrom(d2);
                    counter++;
                }
            }
            this.distScore = (dist/counter);
            this.computedScore = true;
            return distScore;
    }

    public double scoreKthDist(Datum d){
        if (this.computedScore){
            return this.distScore;
        }
        if (isLeaf){
            return this.computeKthDist(d);
           // return leafCounter;
        } else {
            if (d.value[dimIndex] > splitPoint) {
                if (leftChildNode.myList.size() < 5){  // 5 is k
                    return this.computeKthDist(d);
                } else {
                    return leftChildNode.scoreKthDist(d);
                }
            } else {
                if (rightChildNode.myList.size() < 5){ // 5 is k
                    return this.computeKthDist(d);
                } else {
                    return rightChildNode.scoreKthDist(d);
                }
            }
        }
    }


    public double computeKthDist(Datum d) {
           double dist = 0;
           double counter = 0;
           double temp = 0;

           if (myList.size() <= 1) {
                //compute max dist within this subspace
                for(int i=0; i < d.dim; i++) {
                    temp = myMax[i] - myMin[i];
                    this.distScore += (temp*temp);
                }
                this.distScore = Math.sqrt(distScore);
//                this.computedScore = true;
                return this.distScore;
            }
            if (myList.size() == 2) {
                Datum d1 = (Datum) myList.get(0);
                Datum d2 = (Datum) myList.get(1);
                this.distScore = d1.distanceFrom(d2);
  //              this.computedScore = true;
                return distScore;
            }

           Datum KNNeigh[] = new Datum[5];
           Double KNNDist[] = new Double[5];
           double maxDist = -10000;
           int maxIdx = 0;
           for(int i=0; i < 5; i++) {
                Datum d1 = (Datum) myList.get(i);
                KNNeigh[i] = d1;
                KNNDist[i] = d.distanceFrom(d1);
                if (KNNDist[i]>maxDist){
                    maxDist = d.distanceFrom(d1);
                    maxIdx = i;
                }
           }
           double tempDist = 1000;
           for(int i=5; i< myList.size(); i++) {
                Datum d1 = (Datum) myList.get(i);
                tempDist = d1.distanceFrom(d);
                if (tempDist < maxDist){
                    KNNeigh[maxIdx] = d1;
                    KNNDist[maxIdx] = tempDist;
                    //find the new maxIdx
                    maxDist = tempDist;
                    for(int j=0; j < KNNDist.length; j++){
                        if (KNNDist[j]>maxDist){
                            maxDist = KNNDist[j];
                            maxIdx = j;
                        }
                    }
                }
           }

//           for(int i=0; i < KNNeigh.length; i++){
//                 dist += d.distanceFrom(KNNeigh[i]);
//           }
            distScore = KNNDist[maxIdx];
    //        this.computedScore = true;
            return distScore;
    }

    public double scoreMassPathLen(Datum d){
        if (this.computedScore){
            return this.distScore;
        }
        if (isLeaf){
            return this.computeMassPathLen(d);
           // return leafCounter;
        } else {
            if (d.value[dimIndex] > splitPoint) {
                if (leftChildNode.myList.size() < 2){
                    return this.computeMassPathLen(d);
                } else {
                    return leftChildNode.scoreMassPathLen(d);
                }
            } else {
                if (rightChildNode.myList.size() < 2){
                    return this.computeMassPathLen(d);
                } else {
                    return rightChildNode.scoreMassPathLen(d);
                }
            }
        }
    }

    public double computeMassPathLen(Datum d) {
           double size = (double)myList.size();
           this.distScore = Math.log(size) + (double)this.myLevel;
           //this.computedScore = true;
           return distScore;
    }

   public double scoreMassPathLen1(Datum d){

        if (this.computedScore){
            return this.distScore;
        }
        if (isLeaf){
            return this.computeMassPathLen1(d);
           // return leafCounter;
        } else {
            if (d.value[dimIndex] > splitPoint) {
                if (leftChildNode.mySize < 20){
                    return this.computeMassPathLen1(d);
                } else {
                    return leftChildNode.scoreMassPathLen1(d);
                }
            } else {
                if (rightChildNode.mySize < 20){
                    return this.computeMassPathLen1(d);
                } else {
                    return rightChildNode.scoreMassPathLen1(d);
                }
            }
        }
    }

   public double scoreMassPathLen2(Datum d){
        if (this.computedScore){
            return this.distScore;
        }
        if (isLeaf){
            return this.computeMassPathLen1(d);
           // return leafCounter;
        } else {
            if (d.value[dimIndex] > splitPoint) {
                //if (leftChildNode.mySize < 20){
                if (leftChildNode.mySize < Parameters.maxLeafCount){
                    return leftChildNode.computeMassPathLen1(d);
                     //   return this.computeMassPathLen1(d);
                } else {
                    return leftChildNode.scoreMassPathLen2(d);
                }
            } else {
                //if (rightChildNode.mySize < 20){
                if (rightChildNode.mySize < Parameters.maxLeafCount){
                      return rightChildNode.computeMassPathLen1(d);
//                    return this.computeMassPathLen1(d);
                } else {
                    return rightChildNode.scoreMassPathLen2(d);
                }
            }
        }
    }


    //public double computeMassPathLen1_logMass_plus_L(Datum d) {
   public double computeMassPathLen1(Datum d) {
           this.marked = true;
           if (this.mySize < 2) return 1.0;
         //  this.distScore = Math.log(this.mySize) + (double)this.myLevel;
           //this.distScore = Math.log(this.mySize)/Math.log(2) + (double)this.myLevel;

        //   this.distScore = this.mySize * Math.pow(2,this.myLevel);
           this.distScore = Math.log(this.mySize * Math.pow(2,this.myLevel));

           //this.computedScore = true;
           return (double) distScore;
           //return distScore;
    }


    public double computeMassPathLen1_mass_2L(Datum d) {
   // public double computeMassPathLen1(Datum d) {
           this.marked = true;
           if (this.mySize < 2) return 1.0;
           this.distScore = this.mySize * Math.pow(2,this.myLevel);
           //this.computedScore = true;
           return (double) distScore;
           //return distScore;
    }




}

class jNode {
    public int maxHeight = Parameters.maxTreeHeight;
    public int maxLeafCount = Parameters.maxLeafCount;
    public boolean isRoot = false;
    public int size = 0;
    public double splitPoint = 0;
    public jNode parentNode = null;
    public jNode leftChildNode = null;
    public jNode rightChildNode = null;
    public boolean isLeaf = false;
    public int leafCounter = -1;
    public int myLevel = 0;
    public int dimIndex = 0;
    //public Datum myData[] = new Datum[maxLeafCount];
    public ArrayList myData = new ArrayList();
    public double newMax[];
    public double newMin[];
    public double myMax[];
    public double myMin[];
    
    jNode(double max[], double min[], int ml){
    //    System.out.println("New node ...My current level is " + ml);

        this.myLevel = ml;
        dimIndex = Parameters.generator.nextInt(min.length);
        while(min[dimIndex] == max[dimIndex]){
            dimIndex = Parameters.generator.nextInt(min.length);
        }
         // half split seems better
         // this.splitPoint = min[dimIndex] + Math.random()*(max[dimIndex]-min[dimIndex]);
        this.splitPoint = (min[dimIndex] + max[dimIndex])/2;
    //   System.out.println("min[dimIndex] max[dimIndex] " + min[dimIndex] + "," + max[dimIndex]);
         System.out.println("I am a node with split point = " + splitPoint + ", dimIndex = " + dimIndex);
        myMax = new double[max.length];
        myMin = new double[min.length];
        newMax = new double[max.length];
        newMin = new double[min.length];
        for(int i=0; i < max.length; i++){
            myMax[i] = max[i];
            myMin[i] = min[i];
            newMax[i] = max[i];
            newMin[i] = min[i];
        }
        newMax[dimIndex]=splitPoint;
        newMin[dimIndex]=splitPoint;

        if (myLevel >= maxHeight){
            isLeaf = true;
            leafCounter = 0;
           // System.out.println("I am a leaf");
        } else { // continue to expand the child nodes
           // if (Math.random() > 0.5) { dimIndex = 0;} else { dimIndex = 1; }
           // leftChildNode = new iNode(newMax, min, ml+1);
           // rightChildNode = new iNode(max, newMin, ml+1);
            leftChildNode = new jNode(max, newMin, ml+1);
            rightChildNode = new jNode(newMax, min, ml+1);            
        }
    }
    
    public void assign(Datum d){
        size++;
        if (isLeaf){
     //       System.out.println("This is a leaf with " + leafCounter + " items");
            //myData[leafCounter]=d;
            myData.add(d);
            leafCounter++;
            if((leafCounter>=Parameters.maxLeafCount)&&(myLevel < Parameters.maxExpansionLevel)){
                //System.out.println("This is a leaf is too big, expand it");

                isLeaf = false; // too big change it to a node
                leftChildNode = new jNode(myMax, newMin, myLevel+1);
                leftChildNode.isLeaf = true;
                leftChildNode.leafCounter = 0;
                
                
                rightChildNode = new jNode(newMax, myMin, myLevel+1);            
                rightChildNode.isLeaf = true;
                rightChildNode.leafCounter = 0;
                
                //transfer data to leaf nodes
                for(int i=0; i < myData.size(); i++){
                    if (((Datum)myData.get(i)).value[dimIndex] > splitPoint) {
                        leftChildNode.assign((Datum)myData.get(i));
                    } else {
                        rightChildNode.assign((Datum)myData.get(i));
                    }
                }
                myData.clear();
            }
        } else {
//            System.out.println("This is a Node, pass the datum on to one of its leaf");
            if (d.value[dimIndex] > splitPoint) {
                leftChildNode.assign(d);
            } else {
                rightChildNode.assign(d);
            }
        }
    }

    //public int score(Datum d){
    public double score(Datum d){
        if (isLeaf){
            double hyperVolume = 1;
            for(int i=0; i < myMin.length; i++){
                //System.out.print("min max = " + myMax[i] +", " + myMin[i]);
                if (myMax[i] == myMin[i]) continue;
                hyperVolume = hyperVolume*(myMax[i]-myMin[i]);
            }
            //System.out.println("");
            if(hyperVolume == 0){
                System.out.println("Zero Volume!!!!");
                System.exit(0);
            }
            //return ((double)leafCounter)/hyperVolume;

     //       System.out.println("LeafCounter = " + leafCounter + ", My level " + this.myLevel);
     //       System.out.println("path length score = " + ((double)this.myLevel + 2*(Math.log((double)leafCounter-1.0)+0.5772156649) - (2*((double)leafCounter-1.0)/((double)leafCounter))));

            if (leafCounter <2) {
                return (double)this.myLevel;
            } else {
                return ((double)this.myLevel + 2*(Math.log((double)leafCounter-1.0)+0.5772156649) - (2*((double)leafCounter-1.0)/((double)leafCounter)));
            }
            
            //return leafCounter;
        } else {
            if (d.value[dimIndex] > splitPoint) {
                return leftChildNode.score(d);
            } else {
                return rightChildNode.score(d);
            }
        }
    }
    
    
}




class Proposal {
    int bag1;
    int bag2;
    double distance;
    Proposal(int b1, int b2, double d){
        this.bag1 = b1;
        this.bag2 = b2;
        this.distance = d;
    }
}

public class Parameters {
    public static int maxX = 50;
    public static int maxY = 50;

    public static int distType = 1; // 1 for cosine, 2 for euclidean
    public static double maxDist = 1;

    public static boolean clusterDetectionProblem = false;
   
    public static int dataSet = 4; // 1 for Wine, 2 for Iris, 3 for Square, 4 for Wisconsin
    
    public static double range = 350.0;    // max - min
    public static double Kp = 0.1;
    public static double Kd = 0.3;
     public static double alpha = 0.2; // this value is very sensitive
    public static int dataSize = 178; //1000
    public static int minBinSize = 10;
    
    public static int globalLabel = -1;
    
    public static int binSizes[];
    
    public static Datum pickedDatum = new Datum();
    public static double pickedSimilarity = 0;
    public static Datum dropedDatum = new Datum();
    public static double dropedSimilarity = 0;
    public static int failCount = 0;
    
    public static double nfScalingFactor = 0;
        
    public static ArrayList ALL_VALUES = new ArrayList();
    
    public static int totalStage;

    public static Random generator = null;
    //public static Random generator = new Random(12345);
    //public static Random generator = new Random(12346);
    
    public static boolean useMinVar = true;
    public static double tScoreThreshold = 100;
    
    public static int clusterSizes[];
    public static String filename = "";
    
    public static String dataSetName = "Not Stated";
    public static int streamSegmentSize = 100000000;

    
    public static int possibleKstart = 10;
    public static int possibleKend = 2;
    
    public static int dimension = -1;
    
    public static int numClusters = -1; // this value must be initilaised in app initializer

    public static int evaluationCount = 0;
    public static int seed = 12345;
    
    public static int maxTreeHeight = 3;    
    public static int maxExpansionLevel = 20;    
    public static int maxLeafCount = 20;    
    public static int numTrees = 50;

    public static boolean globalMass1 = true;
    
    public static int s;
    public static double [][] n = new double[26][]; // 26 data sets
    public static String [] dermatologySymbols = {"+", "x", "o", "*", "-", ":", "#", "=", "%", "$"};

    public static  ArrayList xAxis;
    public static  ArrayList Dunns;
    public static  ArrayList Variances;
    public static  ArrayList MergeDistance;
    public static  ArrayList silhouette;
    public static  ArrayList daviesDoublin;
    public static  ArrayList grpAvgDistance;
    
    public static int numBAGClusters = 6;

    public static double [][] clusterMatrix;
    public static double [][] matrix;
    
    public static boolean debugLog = true;

    public static int imageOffset = 10;

    public static double nsize = 24.0;
    
    public static int numIteration = 200;
    
    public static int numDropTrials = 300;
    public static double bestAlpha = 0.1;

    public static double threshold = 4.5;

    public static int transcientThreshold = 8;

    public static int windowSize = 250;

    public static int globalIterationCounter = 0;
    
    public static int actualIterationsExecuted = 0;
    
    public static int numUnsuccessfulPick = 0;
  
    //public static CustomCanvas beforeCanvas;
    
    /** Creates a new instance of Parameters */
    public Parameters() {
    }


  public static double computeAUROC(ArrayList anomalyTypeArray, ArrayList anormalyScoresArray) {
        ArrayList<Double> normalScores = new ArrayList<Double>();
        ArrayList<Double> adnormalScores = new ArrayList<Double>();

        for(int i = 0 ; i < anomalyTypeArray.size(); i++) {
            if (((Integer)anomalyTypeArray.get(i)).intValue() == 0){
                normalScores.add((Double)anormalyScoresArray.get(i));
            } else {
                adnormalScores.add((Double)anormalyScoresArray.get(i));
            }
        }

        //Calculate AUC
        double n0 =  normalScores.size();
        double n1 =  adnormalScores.size();
        Double D1 = 0.0;
        double counter = 0;

        for(int i=0; i < adnormalScores.size(); i++){
            counter = 0;
            for(int j=0; j < normalScores.size(); j++) {
                if (adnormalScores.get(i) < normalScores.get(j)){
                   // System.out.println("adnormal score = " + adnormalScores.get(i));
                   // System.out.println("normal score = " + normalScores.get(i));
                    counter++;
                }
            }
            D1 += counter;
        }
        double AUROC = D1/(n0*n1);
        return AUROC;
  }


   public static int checkType(Datum d){
       int anomalyType = 0;
                    if((Parameters.dataSetName.compareTo("http")==0)||
                       (Parameters.dataSetName.compareTo("HttpSmtpContinuous")==0)||
                       (Parameters.dataSetName.compareTo("smtp")==0)||
                       (Parameters.dataSetName.compareTo("satellite")==0)||
                       (Parameters.dataSetName.compareTo("annthyroid")==0)||
                       (Parameters.dataSetName.compareTo("CoverType")==0)||
                       (Parameters.dataSetName.compareTo("Mulcross6000")==0)||
                       (Parameters.dataSetName.compareTo("Arrhythmia")==0)||
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
                       (Parameters.dataSetName.compareTo("ShopFrontChange")==0)||
                       (Parameters.dataSetName.compareTo("ChangeVar")==0)||
                       (Parameters.dataSetName.compareTo("SEA")==0)||
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
                       (Parameters.dataSetName.compareTo("Wisconsin")==0)|| //Wisconsin: 1 is anomaly, 2 is normal
                       (Parameters.dataSetName.compareTo("Pima")==0)||  //pima: 1 is anomaly, 2 is normal
                       (Parameters.dataSetName.compareTo("ionosphere")==0)){
                        if (d.symbol != '+'){ // use != '+' for smtp/htt/pima/wisconsin/ionosphere/satellite/annthyroid
                            anomalyType = 0;
                        } else {
                            anomalyType = 1;
                        }
                    } else {
                        if (d.symbol == '+'){ //shuttle_omiteclass4 should be here
                            anomalyType = 0;
                        } else {
                            anomalyType = 1;
                        }
                    }
                    return anomalyType;
   }


    public static void initBinSizes() {
      //  binSizes = new int[dataSize+1000000];
        binSizes = new int[dataSize];
        for(int i=0; i < dataSize; i++) {
            binSizes[i] = 0;
        }
    }
    
    public static int getNextFreeBin() {
        for(int i=0; i < dataSize; i++) {
            if (binSizes[i] == 0) {
                return i;
            }
        }
        return -1; // error
    }
    
    
    /** Modified from Julia's work
    * Compute the eucclidean distance between two map positions A and B
    * @param i1 y coordinate for position A
    * @param j1 x coordinate for position A
    * @param i2 y coordinate for position B
    * @param j2 x coordinate for position B
    * @return return the Euclidean distance
    */
    public static double euclidean(int i1, int j1, int i2, int j2) {
        double temp1 = (i1-i2);
        temp1 *= temp1;
        double temp2 = (j1-j2);
        temp2 *= temp2;
        return Math.sqrt(temp1+temp2);
    }

    /** Modified from Julia's work
    * Compute the eucclidean distance between two map positions A and B
    * @param i1 y coordinate for position A
    * @param j1 x coordinate for position A
    * @param i2 y coordinate for position B
    * @param j2 x coordinate for position B
    * @return return the Euclidean distance
    */
    public static double euclidean(double i1, double j1, double i2, double j2) {
        double temp1 = (i1-i2);
        temp1 *= temp1;
        double temp2 = (j1-j2);
        temp2 *= temp2;
        return Math.sqrt(temp1+temp2);
    }

    public static void setDistanceMeasure(Datum d) {
            if (Parameters.distType == 1){
                d.setDistanceMeasure(new Cosine(d));            
            }
            if (Parameters.distType == 2){
                d.setDistanceMeasure(new Euclidean(d));  
            }
            if (Parameters.distType == 3){
                d.setDistanceMeasure(new OneNorm(d));  
            }
    }
    
    
    public static double generalEuclidean(double [] data1, double [] data2) {
        double num;
        double total = 0;

        for (int i = 0; i < data1.length; i++) {
          num = data1[i] - data2[i]; // assume no missing value
          total += num*num;
        }
        return Math.sqrt(total);
    }

    public static double generalCosine(double [] data1, double [] data2) {
        double num;
        double div = 0.0;
        double sum_ikjk = 0.0, sum_ikik = 0.0, sum_jkjk = 0.0;

        for (int i = 0; i < data1.length; i++) {
          sum_ikjk += (data1[i] * data2[i]); // assume no missing value
          sum_ikik += (data1[i] * data1[i]);
          sum_jkjk += (data2[i] * data2[i]);
        }
//        div = sum_ikjk/(Math.sqrt(sum_ikik) * Math.sqrt(sum_jkjk));  same as below mathmthematically
          div = sum_ikjk/(Math.sqrt(sum_ikik*sum_jkjk));
//        div = sum_ikjk/((sum_ikik) * (sum_jkjk));
        return div;
//        return (1.0 - 0.5*(1.0+div));
    }

 //range standardization
  public static void standardise_range(Datum [] data) {
        int dim = data[0].getDim(); // better to have error check
        System.out.println("dim = " + dim);

        double [] max = new double[dim];
        double [] min = new double[dim];
        double [] range = new double[dim];
        double [] vector;
        vector = new double[dim];
        int d;

        for (d = 0; d < dim; d++) {
            max[d] = 0.0; // init the max & min
            min[d] = 10000.0;
        }
        System.out.println("data.length = " + data.length);
        // get the individual max and min for each attribute
        for (int i = 0; i < data.length; i++) {
            vector = data[i].getValue();
            for (d = 0; d < dim; d++){
                if (max[d] < vector[d]) {
                    max[d] = vector[d];
                }
                if (min[d] > vector[d]) {
                    min[d] = vector[d];
                }
            }
        }
        // compute ranges
        for (d = 0; d < dim; d++){
            range[d] = max[d] - min[d];
        }

        //standardise the data based on range value
        for (int j = 0; j < data.length; j++) {
            vector = data[j].getValue();
            for (d = 0; d < dim; d++){
               //vector[d] = (vector[d] - min[d])/range[d];
                if (max[d] == min[d]) {
                   vector[d] = 0;
                } else {
                   vector[d] = (vector[d] - min[d])/max[d];
                }
            }
       //     System.out.println("Normalised Values: " + data[j]);
        }
  }

  public static boolean isInRange(int k, int start, int end){
      if ((k >= start)&&( k <= end)){
          return true;
      } else{
          return false;
      }
  }  
  
  
   //Z standardization
  public static void standardise(Datum [] data) {
        int dim = data[0].getDim(); // better to have error check
        double [] mean = new double[dim];
        double [] sumXX = new double[dim];
        double [] stdDev = new double[dim];
        double [] vector;
        vector = new double[dim];
        int d;

        for (d = 0; d < dim; d++) {
            mean[d] = 0.0; // init the max & min
            sumXX[d] = 0.0;
            stdDev[d] = 0.0;
        }

        // get the individual total and sumXX for each attribute
        for (int i = 0; i < data.length; i++) {
            vector = data[i].getValue();
            for (d = 0; d < dim; d++){
                mean[d] += vector[d]; // in progress of computing mean
                sumXX[d] += (vector[d]*vector[d]);
            }
        }
        // compute means and std deviations
        double N = (double)(data.length);
        for (d = 0; d < dim; d++){
            mean[d] = mean[d]/N;
            stdDev[d] = Math.sqrt((sumXX[d] - N*mean[d]*mean[d])/(N - 1));
        }

        //standardise the data based on range value
        for (int j = 0; j < data.length; j++) {
            vector = data[j].getValue();
            for (d = 0; d < dim; d++){
                vector[d] = (vector[d] - mean[d])/stdDev[d];
            }
//            System.out.println("Standardised Values: " + data[j]);
        }
  }



    public static void standardise_backup(Datum [] data) {
        int dim = data[0].getDim(); // better to have error check
        double [] total;
        total = new double[dim];
        double [] vector;
        vector = new double[dim];
        int d;

        for (d = 0; d < dim; d++) {
            total[d] = 0.0; // init the totals
        }

        // get the individual total
        for (int i = 0; i < data.length; i++) {
            vector = data[i].getValue();
            for (d = 0; d < dim; d++){
                total[d] += vector[d];
            }
        }

        //standardise the data
        for (int j = 0; j < data.length; j++) {
            vector = data[j].getValue();
            System.out.print("std value: ");
            for (d = 0; d < dim; d++){
                vector[d] /= total[d];
                System.out.print(vector[d]);
            }
            System.out.println(" ");
        }
    }
 
    public static double to4DecPlace(double num){
        DecimalFormat fourDigits = new DecimalFormat("0.0000");
        return Double.parseDouble(fourDigits.format(num));
    }
    
    public static double stDev(ArrayList var1, double maxDunn){
      double array[] = new double[var1.size()-1];
      int ctr=0;
      double aDunn=0;
      
      for (int i=0; i< var1.size(); i++){
          aDunn = Double.parseDouble(var1.get(i).toString());
          if (aDunn == maxDunn) continue;  // exclude the peak Dunn itself
          
          array[ctr]= aDunn;
          ctr++;
      }
      
      double successiveChange[] = new double[array.length-1];
      for(int i=0; i< successiveChange.length; i++){
          successiveChange[i] = array[i+1] - array[i];
//          System.out.println("Successive change="+successiveChange[i]);
      }
      
      double sumX = 0;
      double sumXsquare=0;
      
      for(int i=0; i<successiveChange.length;i++){
          sumX += successiveChange[i];
          sumXsquare += successiveChange[i]*array[i];
      }
      double N = array.length;
      double stdev = (sumXsquare - (sumX*sumX)/N)/(N-1);
      
      return Math.sqrt(stdev);
    }
    
   
    public static double computeAverage(ArrayList alist) {
      double total = 0;
      
      for (int i=0; i< alist.size(); i++){
          total += Double.parseDouble(alist.get(i).toString());
      }
      
      return total/(double)alist.size();
    }

   public static double computeStdDev(ArrayList alist) {
      double sumX = 0;
      double sumXsquare = 0;
      double X = 0;
      
      for(int i=0; i< alist.size();i++){
          X = Double.parseDouble(alist.get(i).toString());
          sumX += X;
          sumXsquare += X*X;
      }
      double N = alist.size();
      double stdev = (sumXsquare - (sumX*sumX)/N)/(N-1);
      
      return Math.sqrt(stdev);

    }

    public static void showCriteria(boolean showFlag, int stage, ArrayList fmeasure, ArrayList Dunns, ArrayList Variances, ArrayList silhouette, ArrayList daviesDoublin, int bagsLength){
        String result = "\n";
        
        if (showFlag) {
            result += "Stage:"+ stage;
            result += ", oldDunn=," + to4DecPlace(Double.parseDouble(Dunns.get(stage).toString())); 
            result += ", FMeas=," + to4DecPlace(Double.parseDouble(fmeasure.get(stage).toString())); 
            result += ", Intra-Variance=," + to4DecPlace(Double.parseDouble(Variances.get(stage).toString())); 
            result += ", silhouetteCoefficient=," + to4DecPlace(Double.parseDouble(silhouette.get(stage).toString())); 
            result += ", DaviesDouldinIndex=," + to4DecPlace(Double.parseDouble(daviesDoublin.get(stage).toString())); 
            result += ", #cluster=," + bagsLength;
            System.out.println(result);
        }
    }
    
    public static void showArrayList(ArrayList alist){
        for(int i=0; i<(alist.size()); i++){
            System.out.print(i +  ":," + to4DecPlace(Double.parseDouble(alist.get(i).toString()))+", ");
            if ((i > 0) && ((i%9) == 0)) System.out.println();
        }
        System.out.println();
    }

    public static void showDistanceRatio(ArrayList alist){
        double denom = 0;
        for(int i=0; i<(alist.size()-1); i++){
            denom = Double.parseDouble(alist.get(i).toString());
            if (denom > 0) {
                System.out.print((i+1) + " & " + i +  ":" + to4DecPlace(Double.parseDouble(alist.get(i+1).toString())/Double.parseDouble(alist.get(i).toString()))+", ");
            }
            if (i==10) System.out.println();
        }
        System.out.println();
    }
    
   
    public static int findMaxStageWithinKrange(ArrayList grpAvgDistRatio){
        int lastStageIndex = grpAvgDistRatio.size() - 1;
        int startStage = lastStageIndex - Parameters.possibleKstart + 2;
        int endStage = lastStageIndex - Parameters.possibleKend + 2;
        double maxRatio = -100;
        double aRatio;
        int maxStage = -1;
        for(int i=startStage; i <= endStage; i++){
            aRatio = Double.parseDouble(grpAvgDistRatio.get(i).toString());
            if (maxRatio < aRatio) {
                maxStage = i;
                maxRatio = aRatio;
            }
        }
        return maxStage+1; // add 1 to compensate the lost of 1 stage in ratio computation
    }
    
    
    
    public static double getTScoresWeight(ArrayList alist){
      int bestK = scanFirstBigChange(alist); 
        if (bestK == -1) { // no best K, just assign all zero weight ==> no effect
            return 0;
        }
      return 1;
    }
    
    public static void updateWeightage(double[] rankingList, double weight){
        for(int i=0; i<rankingList.length; i++){
            rankingList[i] *= weight;
        }
    }
    
    
    // use change ratio, if can find, use it, if cannot find, use zero weight
    public static int scanFirstBigChange(ArrayList alist) {
        int k =-1;
        double tScore = 0;
        String result = "";
        boolean found = false;
        double threshold = Parameters.tScoreThreshold;
        for(int i=0; i<(alist.size()); i++){
            tScore = Double.parseDouble(alist.get(i).toString());
            result += tScore + ", ";
            if ((tScore > threshold)&&(!found)){ // was 100
                k = i-1; // last one is the optimal
                found = true;
            } 
        }
        if (Parameters.debugLog) System.out.println(result);
        return k;
    }

    // use change ratio, if can find, use it, if cannot find, use zero weight
    public static int scanFirstBigChange_BK(ArrayList alist) {
        int k =-1;
        double tScore = 0;
        String result = "";
        boolean found = false;
        double threshold = 50;
        for(int i=0; i<(alist.size()); i++){
            tScore = Double.parseDouble(alist.get(i).toString());
            result += tScore + ", ";
            if ((tScore > threshold)&&(!found)){ // was 100
                if (i<18){
                    if ((Double.parseDouble(alist.get(i+1).toString()) > 20) && (Double.parseDouble(alist.get(i+2).toString())>20)){
                        k = i-1; // last one is the optimal
                        found = true;
                    } else {
                        threshold = 100;
                    } 
                } else {
                        k = i-1; // last one is the optimal
                        found = true;
                }
            } 
        }
        System.out.println(result);
        return k;
    }
    
    
    // use change ratio, if can find, use it, if cannot find, use zero weight
    public static int scanFirstBigChange_bk(ArrayList alist) {
        int k =-1;
        double oldChange = Double.parseDouble(alist.get(0).toString());
        double newChange = 0;
        String result = "";
        for(int i=1; i<(alist.size()); i++){
            newChange = Double.parseDouble(alist.get(i).toString());
            result += newChange/oldChange + ", ";
            if ((newChange/oldChange) > 500){
                k = i;
                break;
            } 
            oldChange = newChange;
        }
        System.out.println(result);
        return k;
    }
    
    
    // find max
    public static int findMaxStage(ArrayList alist) {
        double max = Double.parseDouble(alist.get(0).toString());
        double value;
        int maxStage = -1;
        for(int i=1; i<(alist.size()); i++){
            value = Double.parseDouble(alist.get(i).toString());
            if (max < value){
                max = value;
                maxStage = i;
            }
            
        }
        return maxStage;
    }    
    
    public static int findMinStage(ArrayList alist) {
        double min = Double.parseDouble(alist.get(0).toString());
        double value;
        int minStage = -1;
        for(int i=1; i<(alist.size()); i++){
            value = Double.parseDouble(alist.get(i).toString());
            if (min > value){
                min = value;
                minStage = i;
            }
            
        }
        return minStage;
    }    

    // check the validity of min variance, i.e. left >> right (at knee point)
    // return the weight of importance
    public static double getMinVarianceWeight(ArrayList alist) {
        int minStage = findMinStage(alist); // get the K for min variance
        //System.out.println("min stage for min variance is " + minStage);
        if(minStage == -1) {
            return 0;
        }
        
        int beforeStages = minStage - 2;
        if (beforeStages<0){
            beforeStages = 0;
        }

        double rightVariance = 0, leftVariance = 0;
        double aVar;
        double ctr = 0;
        for(int i=beforeStages; i < minStage; i++) {
            aVar = Double.parseDouble(alist.get(i+1).toString()) - Double.parseDouble(alist.get(i).toString());
            rightVariance += Math.abs(aVar);
            ctr++;
        }
        rightVariance = rightVariance/ctr;
        
        //               was 19
        if ((minStage == Parameters.totalStage-1)||(minStage == 0)){
            return 0;
        } else {
            leftVariance = (Double.parseDouble(alist.get(minStage+1).toString()) - Double.parseDouble(alist.get(minStage).toString()));
        }
        return leftVariance/(leftVariance+rightVariance);    
    }    
    
    
// rank the variances
    public static double[] rankTScores(ArrayList alist){
        double rank[] = new double[alist.size()];
        int weight = 1;
        
        int bestK = scanFirstBigChange(alist); 
        if (bestK == -1) { // no best K, just assign all zero weight ==> no effect
            for(int i=0; i<rank.length; i++){
                rank[i] = 0;
            }
        } else {
            rank[bestK] = 0;
            weight = 1;
            for(int i = (bestK-1); i >= 0; i--){ // go reverse
                rank[i]=weight;
                weight++;
            }
            weight = 1;
            for(int i = (bestK+1); i < alist.size(); i++){ // go forward
                rank[i]=weight;
                weight++;
            }
        }
        if (Parameters.debugLog){
            System.out.println("T Scores:");
            System.out.println(Arrays.toString(rank));
            System.out.println(alist.toString());
        }
        
        return rank;
    }

    // compute the successive differences only
    public static ArrayList updateDiff(ArrayList alist){
        ArrayList diffList = new ArrayList();
        double diff=0;
        
        diffList.add(0.0);
        for(int i=0; i<(alist.size()-1); i++){
            diff = Double.parseDouble(alist.get(i+1).toString()) - Double.parseDouble(alist.get(i).toString());
            //diffList.add(Math.abs(diff));
            diffList.add(diff);
        }
        diffList.set(0,  Double.parseDouble(diffList.get(1).toString()));
        return diffList;
    }
    
    public static ArrayList updateTscore(ArrayList alist){
        ArrayList tScoreList = new ArrayList();
        ArrayList cumList = new ArrayList();
        double diff=0;
        double tScore=0, mean=0, value =0, stdErr=0;
        
        int initN = alist.size() - Parameters.totalStage; // last 20 stages
        if (initN < 0) {
            for(int i=0; i<Math.abs(initN); i++){ // make the t scores contains the same # as total stage
                tScoreList.add(0);
            }
            initN=3;
            for(int i=0; i<initN; i++){ // gather all previous experiences
                cumList.add(alist.get(i)); // at the same time update cummuative list
                tScoreList.add(0);
            }            
        } else {
            for(int i=0; i<initN; i++){ // gather all previous experiences
                cumList.add(alist.get(i)); // at the same time update cummuative list
            }
        }
        
        for(int i=initN; i<alist.size(); i++){ // first 5 elements set with zero t scores
            value = Double.parseDouble(alist.get(i).toString());
            mean = computeAverage(cumList);
            stdErr = computeStdDev(cumList);//Math.sqrt(cumList.size());
            tScore = (value-mean)/stdErr;
            tScoreList.add(tScore);
            cumList.add(value);
        }
        return tScoreList;
    }

    

    // update change ratio = newChange/oldChange
    public static void updateChangeRatio(ArrayList alist) {
        int k =-1;
        double oldChange = Double.parseDouble(alist.get(0).toString());
        double newChange = 0;
        for(int i=1; i<(alist.size()); i++){
            newChange = Double.parseDouble(alist.get(i).toString());
            alist.set(i, (newChange*newChange)/oldChange);
            oldChange = newChange;
        }
    }

    public static ArrayList updateRatio(ArrayList alist) {
        ArrayList ratioList = new ArrayList();
        double oldChange = Double.parseDouble(alist.get(0).toString());
        double newChange = 0;
        for(int i=1; i<(alist.size()); i++){
            newChange = Double.parseDouble(alist.get(i).toString());
            ratioList.add(newChange/oldChange);
            oldChange = newChange;
        }
        return ratioList;
    }
    
    
    
   // update change ratio = newChange/oldChange
    public static void normliseChangeRatio(ArrayList alist) {
        int k =-1;
        double maxRatio = Double.parseDouble(alist.get(findMaxStage(alist)).toString());
        double aRatio;
        for(int i=1; i<(alist.size()); i++){
            aRatio = Double.parseDouble(alist.get(i).toString());
            alist.set(i, aRatio/maxRatio);
        }
    }
    
    public static ArrayList updateRatio_normalise_seem_problem_still(ArrayList alist){
        ArrayList VarianceRatios = new ArrayList();
        double var1 = Double.parseDouble(alist.get(0).toString());
        double var2 = Double.parseDouble(alist.get(1).toString());
        double oldDiff = var2- var1;
        double newDiff;
        VarianceRatios.add(-10000.0);
        double maxDiff = oldDiff;

        for(int i=1; i<(alist.size()-1); i++){
            var1 = Double.parseDouble(alist.get(i).toString());
            var2 = Double.parseDouble(alist.get(i+1).toString());
            newDiff = var2-var1;
            VarianceRatios.add(Math.abs(newDiff/oldDiff));
            maxDiff = Math.max(maxDiff, newDiff);
            oldDiff = newDiff;
        }
        for(int i=1; i<(alist.size()-1); i++){
            VarianceRatios.set(i, Double.parseDouble(VarianceRatios.get(i).toString())/maxDiff);
        }
        VarianceRatios.add(-10000.0);
        
        
        
//        VarianceRatios.add(-10000.0);

        return VarianceRatios;
    }

    
    public static ArrayList updateRatio_Problem(ArrayList alist){
        ArrayList VarianceRatios = new ArrayList();
        double var1 = Double.parseDouble(alist.get(0).toString());
        double var2 = Double.parseDouble(alist.get(1).toString());
        double oldDiff = var2- var1;
        double newDiff;
//        VarianceRatios.add(-10000.0);

        for(int i=1; i<(alist.size()-1); i++){
            var1 = Double.parseDouble(alist.get(i).toString());
            var2 = Double.parseDouble(alist.get(i+1).toString());
            newDiff = var2-var1;
            VarianceRatios.add(Math.abs(newDiff/oldDiff));
            oldDiff = newDiff;
        }
        VarianceRatios.add(-10000.0);
        VarianceRatios.add(-10000.0);

        return VarianceRatios;
    }
    

    
    public static double[] setBestResult(double[] bestResults, int bestStage, ArrayList fmeasure, ArrayList Dunns, ArrayList Variances, ArrayList silhouette, ArrayList daviesDoublin) {
        
        if (bestStage == 1){
            System.out.println("best stage=" + bestStage);
        }
        bestResults[0] = to4DecPlace(Double.parseDouble(Dunns.get(bestStage).toString()));
        bestResults[1] =  to4DecPlace(Double.parseDouble(fmeasure.get(bestStage).toString())); 
//        bestResults[2] = (double)(20-bestStage); // number of clusters
        bestResults[2] = (double)(Parameters.totalStage-bestStage); // number of clusters
        bestResults[3] = to4DecPlace(Double.parseDouble(silhouette.get(bestStage).toString())); 
        bestResults[4] = to4DecPlace(Double.parseDouble(daviesDoublin.get(bestStage).toString())); 
        return bestResults;
    }
    
    // best stage is defined as the stage with min weight
    public static int getBestStage(double [] rank){
        double min = 1000000;
        int bestStage = -1; 
        
        for(int i=0; i < rank.length; i++){
            if (rank[i] >= 0){
                if (min > rank[i]){
                    bestStage = i;
                    min = rank[i];
                }
            }
        }
        return bestStage;
    }
    
    public static double[] getTotalRank(double [][] ranking){
        double total[] = new double[ranking[0].length];
        for(int i=0; i<total.length; i++){
            total[i] = 0.0;
            for(int j=0; j < ranking.length; j++){
                total[i] += ranking[j][i];
            }
        }
        return total;
    }
    
    public static void showRankingResult(double [][] ranking, double [] total){
        System.out.println("========== RANKING RESULT===============================");
        for(int i=0; i < ranking.length; i++){
            System.out.println(Arrays.toString(ranking[i]));
        }
        System.out.println();
        System.out.println(Arrays.toString(total));
    }    
    
    public static double[][] updateRankTable(double[][] rankTable, int ref, double rank[]){
        for(int i=0; i<rank.length; i++){
            rankTable[ref][i] = rank[i];
        }
        return rankTable;
    }
    
    public static ArrayList getLast20(ArrayList alist){
        ArrayList list20 = new ArrayList();

        int init = alist.size() - Parameters.totalStage;
        if (init < 0){
            for(int i=0; i<Math.abs(init);i++){
                list20.add(0);
            }
            init = 0;            
        }
        for(int i=init; i<alist.size();i++){
            list20.add(Double.parseDouble(alist.get(i).toString()));
        }
        return list20;
    }

    public static ArrayList getLast20_fill(ArrayList alist){
        ArrayList list20 = new ArrayList();

        int init = alist.size() - Parameters.totalStage;
        if (init < 0){
            for(int i=0; i<Math.abs(init);i++){
                list20.add(Double.parseDouble(alist.get(0).toString()));
            }
            init = 0;            
        }
        for(int i=init; i<alist.size();i++){
            list20.add(Double.parseDouble(alist.get(i).toString()));
        }
        return list20;
    }

    
    public static double[] rank(ArrayList alist, boolean minMax){
        double rank[] = new double[alist.size()];
        double sortedList[] = new double[alist.size()];
        double origList[] = new double[alist.size()];

        for(int i=0; i<alist.size(); i++){
            sortedList[i] = Double.parseDouble(alist.get(i).toString());
            origList[i] = sortedList[i];
            rank[i]=-10000;
        }

        Arrays.sort(sortedList);

        if (!minMax) { // means max to minimum
            double temp;
            int ctr = sortedList.length-1;
            for(int i=0; i < sortedList.length/2; i++){
                temp = sortedList[i];
                sortedList[i] = sortedList[ctr];
                sortedList[ctr] = temp;
                ctr--;
            }
        }
        
        
        // assign the rank from min to max
        for(int i=0; i < alist.size(); i++){
            for(int j=0; j < alist.size(); j++){
                if (sortedList[i] == origList[j]){
                    if (rank[j] > -1){
                  //      System.out.println("Error!");
                    }
                    rank[j] = i; //store the rank at the associated position
                    break;
                }
            }
        }
//        System.out.println(Arrays.toString(rank));
//        System.out.println(Arrays.toString(origList));
        
        return rank;
    }
    
   
    public static boolean detectUpSlope(double newDunn, double oldDunn){
        boolean result = false;
        if (newDunn > oldDunn) {
            result = true;
        }
        return result;
    }

    public static void silhouette_test(double [][] allClusters) {

        if (allClusters.length==1){
            System.out.print("silhouette undefined");
            System.exit(1);
        }
        
        double s=0;
        double avgS=0;
        double num=0;
        for (int i=0; i< allClusters.length; i++){
            
            for(int j=0; j< allClusters[i].length; j++){
                // compute a_i
                double a_i = averageDissimilarity(allClusters[i][j], allClusters[i]);

                // compute b_i
                double b_i = 100;
                double d_i_C = 0;
                for(int p=0; p<allClusters.length; p++){
                    if (p==i) continue;
                    
                    d_i_C = averageDissimilarity(allClusters[i][j], allClusters[p]);
                    b_i = Math.min(b_i, d_i_C);
                }
                
                s = (b_i - a_i)/(double)(Math.max(b_i, a_i));
                System.out.println("item " + i + "," + j + "s="+s);
                avgS += s;
                num++;
            }
         }
        
        System.out.print("SC="+avgS/num);
        
    }
    
    public static double averageDissimilarity(double d, double [] aCluster) {
        double totalDismilarity = 0;
        double avgDissim=0;
        double dist=0;
        
        // compute average dissimilarity;
        for (int i=0; i<aCluster.length;i++){
            if(d==aCluster[i]) continue;
            dist = d - aCluster[i];
            avgDissim += Math.sqrt(dist * dist);
        }
        return avgDissim/(double)(aCluster.length-1);
    }    
    
    public static boolean detectDownSlope(double newDunn, double oldDunn){
        boolean result = false;
        if (newDunn < oldDunn) {
            result = true;
        }
        return result;
    }


    public static double randindex(Vector bags[]) {
        double a = 0;
        double b = 0;
        double c = 0;
        double d = 0;
        char p1, q1, p2, q2;
        int i;
        int j = 0;
        s = Parameters.dataSet;

  //      char [] cluster = new char[goldStd.length];

        double n_i = 0; double sum_n_i = 0;
        double n_j = 0; double sum_n_j = 0;
        double n_ij = 0; double sum_n_ij = 0;
        String refSymbol = "";
        double n_temp = 0;

        for (i=0; i< n[s-1].length; i++ ) {
            n_i = (double) n[s-1][i];; // get the actual number in class i
            sum_n_i += nC2(n_i);
        }

        for (j=0; j<bags.length; j++){
            Vector tempV = bags[j];
            n_j = (double) tempV.size();
            sum_n_j += nC2(n_j);
        }

        for (i=0; i< n[s-1].length; i++ ) {
            refSymbol = dermatologySymbols[i];
            for (j=0; j<bags.length; j++){
                Vector tempV = bags[j];
//                System.out.println("----- with Cluster "+(j+1)+"------");
                n_ij = 0;
                Iterator iter = tempV.iterator();
                while(iter.hasNext()){ // for each item in the cluster
                    Datum dpTemp = (Datum)iter.next();
      //              System.out.println("j = " + j + ", symbol = " + dpTemp.getSymbol());
                    if ((dpTemp.getSymbol()+"").compareTo(refSymbol) == 0) n_ij++;
                }
                sum_n_ij += nC2(n_ij);
            }
        }

        double n_2 = nC2((double)dataSize);
        a = sum_n_ij;
        b = sum_n_i - a;
        c = sum_n_j - a;
        d = n_2 - a - b - c;
        System.out.println("a = " + a + ", b = " + b + ", c = " + c + ", d = " + d);

        double r = (double)(a+d)/(double)(a+b+c+d);
        return r;
  }


   public static double nC2(double n) {
       if (n < 2) {
           return 0;
       } else {
           return (n*(n-1))/2;
       }
   }

    public static double adjustedRandindex(Vector bags[]) {
        double a = 0;
        double b = 0;
        double c = 0;
        double d = 0;
        char p1, q1, p2, q2;
        int i;
        int j = 0;

//        char [] cluster = new char[goldStd.length];

        double n_i = 0; double sum_n_i = 0;
        double n_j = 0; double sum_n_j = 0;
        double n_ij = 0; double sum_n_ij = 0;
        String refSymbol = "";
        double n_temp = 0;

        for (i=0; i< n[s-1].length; i++ ) {
            n_i = (double) n[s-1][i];; // get the actual number in class i
            sum_n_i += nC2(n_i);
        }

        for (j=0; j<bags.length; j++){
            Vector tempV = bags[j];
            n_j = (double) tempV.size();
            sum_n_j += nC2(n_j);
        }

        for (i=0; i< n[s-1].length; i++ ) {
            refSymbol = dermatologySymbols[i];
            for (j=0; j<bags.length; j++){
                Vector tempV = bags[j];
//                System.out.println("----- with Cluster "+(j+1)+"------");
                n_ij = 0;
                Iterator iter = tempV.iterator();
                while(iter.hasNext()){ // for each item in the cluster
                    Datum dpTemp = (Datum)iter.next();
      //              System.out.println("j = " + j + ", symbol = " + dpTemp.getSymbol());
                    if ((dpTemp.getSymbol()+"").compareTo(refSymbol) == 0) n_ij++;
                }
                sum_n_ij += nC2(n_ij);
            }
        }

        double n_2 = nC2((double)dataSize);
        a = sum_n_ij;
        b = sum_n_i - a;
        c = sum_n_j - a;
        d = n_2 - a - b - c;
        System.out.println("a = " + a + ", b = " + b + ", c = " + c + ", d = " + d);

        double common = ((a+b)*(a+c))/n_2;
        double adjRandTop = a - common;
        double adjRandBottom = (a+b+a+c)/2.0 - common;

        return adjRandTop/adjRandBottom;

  }

    // open file to read data from
    public static FileIO openFileToWrite(String filename)
    {
            FileIO jobsfile = new FileIO(filename, 'W');
            return jobsfile;
    } // end of openFileToWrite()

     // close writing file
     public static void closeWriter(FileIO jobsfilehandle)
    {
            jobsfilehandle.close('W');
    } // end of closeWriter()


    public static void delay(long milli) {
        try {
            Thread.sleep(milli);
        } catch (Exception e) {
            System.out.println("Bo Chiap");
        }
    }
}
