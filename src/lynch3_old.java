
import java.util.LinkedList;
import java.util.Random; 
import java.util.StringTokenizer;  
//Program:	clynch1
//Course:	COSC470
//Description:	The purpose of this program is to search a graph and come up with
//              two solutions, one for the shortest path and another for the most
//              reward.  Every node has a "treasure" value and every arck/path
//              has a length value.  This program promts the user to be able to
//              decide what percent of each option to use, and then finds the 
//              pathe with those peramiters.
//Author:	Connor Lynch
//Revised:	2/14/17
//Language:	Java
//IDE:		NetBeans 8.2
//Notes:	
//******************************************************************************
//******************************************************************************
public class lynch3_old {
    
    public static               TextFileClass textFromFile = new TextFileClass();               //loads the main project perams
    public static               KeyboardInputClass keyboardInput = new KeyboardInputClass();    //takes keyboard input
    public static double        learningRate;                                                   //this is the learning rate
    public static int           numberOfTargetOutput;                                           //this is the number of target output 
    public static int[][]       inputImages;                                                    //this holds all of the inputImages 
    public static int[][]       targetImages;                                                   //this holds all of the outputImages
    public static double[][]    weights;                                                        //this contains all of the wights
//    public static LinkedList<Pixel[][]>     inputImages = new LinkedList<Pixel[][]>();                        //this contains all of the input images
//    public static LinkedList<Pixel[][]>     outputImages = new LinkedList<Pixel[][]>();                       //this contains all of the output images 
    
    
    
    //******************************************************************************
    //Method:           main
    //Description:      This method runs the program.  It has the user select a 
    //                  text file and puts the contents into global variables.
    //                  It then calls depthFirstSearch to find all the paths
    //                  and set the shortest.  The shortest path and all of its
    //                  attributes are then printed out.  The user can run the 
    //                  program as many times as desired.
    //Parameters:       none
    //Returns:          userSelection 									
    //Throws:           None
    //Calls:            keyboardInput class, TextFileClass, stringTokenizer,
    //                  depthFirstSearch, distanceCalulator, treasureCalculator    
    public static void main(String[] args) {
        int userInputInt = keyboardInput.getInteger(true, 0, 0, 3, "Please make a selection\n"
                                                                + "0: Train \n"
                                                                + "1: Recal\n"
                                                                + "2: Exit\n");
        switch(userInputInt){
            case 0: { //train
                textFromFile.getFileName("Enter Name of Text File Containing The Mapping Information:");
                textFromFile.getFileContents();
                
                learningRate = keyboardInput.getDouble(true, .05, -10.0, 10.0, "Please enter the learning rate");
                
                int timesToTrain = keyboardInput.getInteger(true, 20, 1, 100000, "Please enter the number of training itterations");
                
                numberOfTargetOutput = Integer.parseInt(textFromFile.text[0]);
                inputImages = new int[numberOfTargetOutput][121];
                targetImages = new int[numberOfTargetOutput][121];
                weights = new double[121][121];
                buildImages();
                setRandomWeight();
                train(timesToTrain);
                break;
            }//end of case 0
            case 1:{//recal
                textFromFile.getFileName("Enter Name of Image File:");
                textFromFile.getFileContents();
                
                break;
            }//end of case 1
            case 2:{//exit
                break;
            }//end of case 2      
        }//end of switch
    }//end of main
    
    public static int[] buildSingleImageArray(int imageType, int location){
        int[] newArray = new int[121];
        switch(imageType){
            case 0:{                //inputImage
                for(int i = 0; i < 121; i++){
                    newArray[i] = inputImages[location][i];
                }//end of for
                break;
            }//end of case 0
            case 1:{                //target Image   
                for(int i = 0; i < 121; i++){
                    newArray[i] = targetImages[location][i];                    
                }//end of for
                break;
            }//end of case 1
        }//end of switch
        return newArray;
    }//end of buildSingleImageArray
    
    public static void train(int timesToTrain){
        for(int x = 0; x < timesToTrain; x++){
            for(int i = 0; i < numberOfTargetOutput; i++){
                int[] currentInputImage = new int[121];
                int[] currentTargetImage = new int[121];
                double[] currentOutputImage = new double[121];

                currentInputImage = buildSingleImageArray(0, i);
                currentTargetImage = buildSingleImageArray(1, i);
                currentOutputImage = getActivation(currentInputImage);

                printImage(currentInputImage, currentTargetImage, currentOutputImage);

                delta(currentInputImage, currentTargetImage, currentOutputImage);
            }//end of for
        }//end of for
    }//end of train 
    
    public static void delta(int[] inputImage, int[] targetImage, double[] outputImage){
        double [][] newWeights = new double[121][121];
        for(int i = 0; i < 121; i++){
            for(int j = 0; j < 121; j++){
                double oldWeight = weights[i][j];
                double aValue = inputImage[j];
                double adjustment = (targetImage[j] - outputImage[j]);
//                                                                                System.out.println(adjustment);
                double newWeight = (oldWeight + learningRate * aValue * adjustment);
                if(learningRate == 0){
                    System.out.println("learning rate");
                }
                if(aValue == 0){
                    System.out.println("aValue");
                }
//                if(adjustment == 0){
//                    System.out.println(oldWeight + " "+ newWeight);
                    
//                }
//                                                                                System.out.println(newWeight);
                if(oldWeight == newWeight){
                    System.out.println("same");
                }//end of if
                newWeights[i][j] = newWeight;
//                weights[i][j] = newWeight;
            }//end of for
        }//end of for
        weights = deepCopy(newWeights);
    }//end of delta 
    public static double[][] deepCopy(double[][] tableToBeCoppied){
        double[][] newTable;
        newTable = new double [121][121];
        for(int x = 0; x < 121; x++){
            for(int y = 0; y < 121; y++){
                double valueFromTableToBeCoppied = tableToBeCoppied[x][y];
                newTable[x][y] = valueFromTableToBeCoppied;
            }//end of for
        }//end of for 
        return newTable;
    }//end of deepCopy
    public static double[] getActivation(int[]inputImage){
        double[] activationArray = new double[121];
        
        for(int i = 0; i < 121; i++){
            double totalNodeActivation = 0;
            for(int j = 0; j < 121; j++){
                double currentA = inputImage[j];
                double currentWeight = weights[i][j];
                double currentNodeActivation = currentA * currentWeight;
                totalNodeActivation = totalNodeActivation + currentNodeActivation;
            }//end of for
            activationArray[i] = totalNodeActivation;
        }//end of for
        return activationArray;
    }//end of get activation
    
    public static void buildImages(){
        int numberInInput = 0;
        int numberInTarget = 0;
        for (int i = 1; i < numberOfTargetOutput * 2 + 1; i++) {
            String fileName = textFromFile.text[i];
            TextFileClass newTextFromFile = new TextFileClass();
            newTextFromFile.fileName = fileName;
            newTextFromFile.getFileContents();
            StringTokenizer st = new StringTokenizer(newTextFromFile.text[0]); 
            if(i % 2 == 0){                     //target Image                   
                for(int j = 0; j < 121; j++){
                    targetImages[numberInTarget][j] = Integer.parseInt(st.nextToken(" "));
                }//end of for    
                 numberInTarget ++;
            }//end of if
            else{                               //input Image
                for(int j = 0; j < 121; j++){
                    inputImages[numberInInput][j] = Integer.parseInt(st.nextToken(" "));
                }//end of for   
                numberInInput ++;
            }//end of else
        }//end of for
    }//end of buildImages
            
    public static void setRandomWeight(){
        for(int i = 0; i < 121; i++){
            for(int j = 0; j < 121; j++){
            double randomWeight;
            double randomNumber = Math.random();
            while(randomNumber > .5){
                randomNumber = Math.random();
            }//end of while
            double sign = Math.random();
            if(sign > .5){
                randomWeight = randomNumber * -1;
            }//end of if
            else{
                randomWeight = randomNumber;
            }//end of else
//                                                                                System.out.println(randomWeight);
                weights[i][j] = randomWeight;
            }//end of for
        }//end of for
    }//end of setRandomWeight
        
//    public static void print(int[] passedOutputImages, int location){
    public static void printImage(int[] inputImage, int[] targetImage, double[] outputImage){
        System.out.println("");
        int locationInInput = 0;
        int locationInTarget = 0;
        int locationInOutput = 0;
        
        for(int i = 0; i < 11; i++){
        //Print Input
            for(int j = 0; j < 11; j++){
                switch(inputImage[locationInInput]){
                    case 1:{
//                        System.out.print("\u9608");
                        System.out.print("X");
                        break;
                    }//end of case 1
                    case -1:{
                        System.out.print("_");
                        break;
                    }//end of case -1
                }//end of switch
                locationInInput++;
            }//end of for
        //Print Target
            System.out.print("    ");
            for(int j = 0; j < 11; j++){
                switch(targetImage[locationInTarget]){
                    case 1:{
//                        System.out.print("\u9608");
                        System.out.print("X");
                        break;
                    }//end of case 1
                    case -1:{
                        System.out.print("_");
                        break;
                    }//end of case -1
                }//end of switch
                locationInTarget++;
            }//end of for
        //Print Output
            System.out.print("    ");                                           
            for(int j = 0; j < 11; j++){
                double valueToPrint = Math.round(outputImage[locationInOutput]);
                if(valueToPrint < 0){
//                    System.out.print(valueToPrint);
                    System.out.print("_");
                }//end of if
                if(valueToPrint >= 0){
//                    System.out.print(valueToPrint);
//                    System.out.print("\u9608");
                    System.out.print("X");
                }//end of if

                locationInOutput++;
            }//end of for
            System.out.println("");
        }//end of for
    }//printImage
}//end of lynch3