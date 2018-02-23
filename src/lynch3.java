import java.util.StringTokenizer;  
//Program:	clynch3
//Course:	COSC470
//Description:  HW3: This is a Java program that simulate a two-layer neural network 
//              which can perform basic pattern association tasks. Input to the 
//              network is a vector of binary values (1/-1) representing the pixels 
//              in a two-tone 11x11 image. Output is another vector, also representing 
//              an image of the same size. The input and output vectors are stored in 
//              text files (one pattern per file) and are read by this program. The 
//              network is trained to learn several heteroassociative input/output pair 
//              relationships such that it can reproduce the proper output pattern when 
//              the corresponding input pattern is given. This network will operate in 
//              two modes: training and recall. It uses the delta rule (Widrow-Hoff) 
//              for training and permits any number of input/output mappings to be 
//              learned. Since it typically takes a number of training passes to learn 
//              several relationships. Training is then done for each relationship until 
//              all have been processed and the routine is repeated some specified 
//              number of times. 
//Author:	Connor Lynch
//Revised:	4/4/17
//Language:	Java
//IDE:		NetBeans 8.2
//Notes:	
//******************************************************************************
//******************************************************************************
public class lynch3 {
    public static               TextFileClass textFromFile = new TextFileClass();               //loads the main project perams
    public static               KeyboardInputClass keyboardInput = new KeyboardInputClass();    //takes keyboard input
    public static double        learningRate;                                                   //this is the learning rate
    public static int           numberOfTargetOutput;                                           //this is the number of target output 
    public static int[][]       inputImages;                                                    //this holds all of the inputImages 
    public static int[][]       targetImages;                                                   //this holds all of the outputImages
    public static double[][]    weights;                                                        //this contains all of the wights
    public static boolean       recalCheck;                                                     //this makes sure the program does not crass if recall is called without training
  
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
        System.out.println("Connor Lynch - AI_HW3");
        System.out.println("This program plays uses a neural network to recall \n"
                            + "images that are passed and trained to the network.\n");
        recalCheck = false;
        boolean firstTime = true;
        boolean loopCheck = true;
        while(loopCheck){
            int userInputInt = keyboardInput.getInteger(true, 0, 0, 3, "Please make a selection\n"
                                                                    + "0: Train (Default)\n"
                                                                    + "1: Recall\n"
                                                                    + "2: Exit\n");
            switch(userInputInt){
                case 0: { //train
                    if(firstTime){
                        textFromFile.getFileName("Enter Name of Text File Containing The Mapping Information:");
                        textFromFile.getFileContents();
                    }//end of if
                    learningRate = keyboardInput.getDouble(true, .01, -10.0, 10.0, "Please enter the learning rate. (Default .01)");
                    int timesToTrain = keyboardInput.getInteger(true, 20, 1, 100000, "Please enter the number of training itterations (Default 20)");                   
                    if(firstTime){
                        numberOfTargetOutput = Integer.parseInt(textFromFile.text[0]);
                        inputImages = new int[numberOfTargetOutput][121];
                        targetImages = new int[numberOfTargetOutput][121];
                        weights = new double[121][121];
                        buildImages();
                        setRandomWeight();
                        recalCheck = true;
                    }//end of if
                    train(timesToTrain);
                    firstTime = false;
                    break;
                }//end of case 0
                case 1:{//recal
                    recal();
                    break;
                }//end of case 1
                case 2:{//exit
                    loopCheck = false;
                    break;
                }//end of case 2      
            }//end of switch
        }//end of while
    }//end of main
    //******************************************************************************
    //Method:           train
    //Description:      This method executes the training algorothy for the program.
    //Parameters:       int timesToTrain - this is the number of training itterations 
    //                      to run.
    //Returns:          Nothing 									
    //Throws:           None
    //Calls:            getActivation, printImage, newWeights
    public static void train(int timesToTrain){
        for(int x = 0; x < timesToTrain; x++){
            for(int y = 0; y < numberOfTargetOutput; y++){
                double[] activationImage = new double[121];
                activationImage = getActivation(y);
                printImage(y, y, activationImage);                     
                newWeights(y, y, activationImage); 
            }//end of for
        }//end of for
    }//end of train 
    //******************************************************************************
    //Method:           newWeights
    //Description:      This method trains the weights using the delta rule.
    //Parameters:       int inputImaheLocation - this is the location where the values 
    //                      for the current image are stored in the 2d array.
    //                  int targetImageLocation - this is the location where he values 
    //                      for the current target image are store in the 2d array.
    //                  doubnle[] activationImage - this is the array containing the 
    //                      activations for all of the output nodes
    //Returns:          Nothing 									
    //Throws:           None
    //Calls:            Nothing
    public static void newWeights(int inputImageLocation, int targetImageLocation, double[] activationImage){
        for(int i = 0; i < 121; i++){
            for(int j = 0; j < 121; j++){
                double oldWeight = weights[i][j];
                double delta = targetImages[targetImageLocation][j] - activationImage[j];
                weights[i][j] = oldWeight + (learningRate * inputImages[inputImageLocation][i] * delta);
            }//end of for
        }//end of for
    }//end of newWeights
    //******************************************************************************
    //Method:           getActivation 
    //Description:      This method calculates the activation on the output and then
    //                      places it into a new array and returns that array.
    //Parameters:       int inputImageLocation - this is the location where the values 
    //                      for the current image are stored in the 2d array.
    //Returns:          double[] activationImage - this is the array containing all of
    //                      all of the activation values.
    //Throws:           None
    //Calls:            Nothing
    public static double[] getActivation(int inputImageLocation){
        double[] activationImage = new double[121];
        for(int i = 0; i < 121; i++){
            double activationTotal = 0;
            for(int j = 0; j < 121; j++){
                double activation = inputImages[inputImageLocation][j] * weights[j][i];
                activationTotal = activationTotal + activation;
            }//end of for
            activationImage[i] = activationTotal;
        }//end of for
        return activationImage;
    }//end of get activation
    //******************************************************************************
    //Method:           buildImages
    //Description:      This methods reads the index file from the main method 
    //                      and then inports all of the input and target images.
    //Parameters:       None
    //Returns:          Nothing 
    //Throws:           None
    //Calls:            textFromFile, StringTokenizer
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
    //******************************************************************************
    //Method:           setRandomWeight
    //Description:      This method randomly assigns weights to the weights.  It 
    //                      randomly calculates a number between .0 and .05.  It then
    //                      randomly decides if it that number will be egative or posative.
    //Parameters:       None
    //Returns:          Nothing
    //Throws:           None
    //Calls:            Math.random()     
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
                weights[i][j] = randomWeight;
            }//end of for
        }//end of for
    }//end of setRandomWeight
    //******************************************************************************
    //Method:           printImage 
    //Description:      This method prints out the input, target, and ooutpu image.
    //Parameters:       int inputImaheLocation - this is the location where the values 
    //                      for the current image are stored in the 2d array.
    //                  int targetImageLocation - this is the location where he values 
    //                      for the current target image are store in the 2d array.
    //                  doubnle[] activationImage - this is the array containing the 
    //                      activations for all of the output nodes
    //Returns:          Nothing 
    //Throws:           None
    //Calls:            Nothing
    public static void printImage(int inputImageLocation, int targetImageLocaion, double[] outputImage){
        System.out.println("");
        int locationInInput = 0, locationInTarget = 0, locationInOutput = 0;        
        for(int i = 0; i < 11; i++){
        //Print Input
            for(int j = 0; j < 11; j++){
                switch(inputImages[inputImageLocation][locationInInput]){
                    case 1:{
                        System.out.print((char)9608);
                        break;
                    }//end of case 1
                    case -1:{
                        System.out.print("X");
                        break;
                    }//end of case -1
                }//end of switch
                locationInInput++;
            }//end of for
        //Print Target
            System.out.print("    ");
            for(int j = 0; j < 11; j++){
                switch(targetImages[targetImageLocaion][locationInTarget]){
                    case 1:{
                        System.out.print((char)9608);
                        break;
                    }//end of case 1
                    case -1:{
                        System.out.print("X");
                        break;
                    }//end of case -1
                }//end of switch
                locationInTarget++;
            }//end of for
        //Print Output
            System.out.print("    ");                                           
            for(int j = 0; j < 11; j++){
                double valueToPrint = Math.round(outputImage[locationInOutput]);
                if(valueToPrint > 0){
                    System.out.print((char)9608);
                }//end of if
                if(valueToPrint <= 0){
                    System.out.print("X");
                }//end of if
                locationInOutput++;
            }//end of for
            System.out.println("");
        }//end of for
    }//printImage
    //******************************************************************************
    //Method:           recal 
    //Description:      This method performs the recall action.  It does this by geting 
    //                      the name of a file and then reading it into the memory.  
    //                      It calculates the activation and then prints out the output.
    //Parameters:       None
    //Returns:          Nothing 
    //Throws:           None
    //Calls:            textFromFile, StringTokenizer
    public static void recal(){
        if(recalCheck){
            textFromFile.getFileName("Enter Name of Text File Containing The Pattern Information:");
            textFromFile.getFileContents();
            int[] inputImage = new int[121];
            StringTokenizer st = new StringTokenizer(textFromFile.text[0]); 
            for(int j = 0; j < 121; j++){
                inputImage[j] = Integer.parseInt(st.nextToken(" "));
            }//end of for  
            double[] activationImage = new double[121];
            for(int i = 0; i < 121; i++){
                double activationTotal = 0;
                for(int j = 0; j < 121; j++){
                    double activation = inputImage[j] * weights[j][i];
                    activationTotal = activationTotal + activation;
                }//end of for
                activationImage[i] = activationTotal;
            }//end of for
            int locationInInput = 0;
            int locationInOutput = 0;
            for(int i = 0; i < 11; i++){
            //Print Input
                for(int j = 0; j < 11; j++){
                    switch(inputImage[locationInInput]){
                        case 1:{
                            System.out.print((char)9608);
                            break;
                        }//end of case 1
                        case -1:{
                            System.out.print("X");
                            break;
                        }//end of case -1
                    }//end of switch
                    locationInInput++;
                }//end of for
            //Print Output
                System.out.print("    ");                                           
                for(int j = 0; j < 11; j++){
                    double valueToPrint = Math.round(activationImage[locationInOutput]);
                    if(valueToPrint > 0){
                        System.out.print((char)9608);
                    }//end of if
                    if(valueToPrint <= 0){
                        System.out.print("X");
                    }//end of if
                    locationInOutput++;
                }//end of for
                System.out.println("");
            }//end of for
        }//end of if
        else{
            System.out.println("Please train before trying to recall.\n");
        }//end of else
    }//end of recal
}//end of lynch3