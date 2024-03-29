import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class SimpleSorts {
    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    /* define constants */
    static long MAXVALUE =  2000000000;
    static long MINVALUE = -2000000000;
    private static int numberOfTrials = 1;
    private static int MAXINPUTSIZE  = (int) Math.pow(2, 21);
    private static int MININPUTSIZE  =  1;
    private static int SIZEINCREMENT = 2;

    // static int SIZEINCREMENT =  10000000; // not using this since we are doubling the size each time
    private static String ResultsFolderPath = "/home/zach/Results/lab4/"; // pathname to results folder
    private static FileWriter resultsFile;
    private static PrintWriter resultsWriter;



    public static void main(String args[])
    {
//        checkSortCorrectness();
        runFullExperiment("InsertionSort-1-TRASH.txt");
        runFullExperiment("InsertionSort-2.txt");
        runFullExperiment("InsertionSort-3.txt");
    }

    private static boolean verifySorted(long[] list) {
        if (list.length == 0 || list.length == 1) {
            return true;
        }

        for (int i = 1; i < list.length; i++) {
            // unsorted pair found
            if (list[i-1] > list[i])
                return false;
        }
        // no unsorted pair found
        return true;
    }

    private static void checkSortCorrectness() {
        long[] testList1 = createRandomIntegerList(100);
        long[] testList2 = createRandomIntegerList(100);

        long[] testShortList = {17, 5, 3, 8, 2, 11};
        long[] testShortList2 = {12, 5, 3, 8, 42, 71, 26, 2, 11};

        printArray(testShortList);
        printArray(testShortList2);

        insertionSort(testList1);
        insertionSort(testList2);
        insertionSort(testShortList);
        insertionSort(testShortList2);

        printArray(testShortList);
        printArray(testShortList2);

        if (verifySorted(testList1) && verifySorted(testList2)) {
            System.out.println("sort results verified!!!");
        } else {
            System.out.println("sort results NOT correct...");
        }
    }


    private static void runFullExperiment(String resultsFileName) {
        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);

        } catch(Exception e) {

            System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+resultsFileName);
            return; // not very foolproof... but we do expect to be able to create/open the file...

        }

        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch(); // for timing an individual trial


        resultsWriter.println("#InputSize    AverageTime        DoublingRatio"); // # marks a comment in gnuplot data

        resultsWriter.flush();

        /* for each size of input we want to test: in this case starting small and doubling the size each time */
//        double[] timeRatios;
        double previousTime = 0;

        for(int inputSize=MININPUTSIZE;inputSize<=MAXINPUTSIZE; inputSize*=SIZEINCREMENT) {

            // progress message...

            System.out.println("Running test for input size "+inputSize+" ... ");

            /* repeat for desired number of trials (for a specific size of input)... */

            long batchElapsedTime = 0;

            /* force garbage collection before each batch of trials run so it is not included in the time */

            System.out.println("Collecting the trash...");
            System.gc();

            // instead of timing each individual trial, we will time the entire set of trials (for a given input size)

            // and divide by the number of trials -- this reduces the impact of the amount of time it takes to call the

            // stopwatch methods themselves

            //BatchStopwatch.start(); // comment this line if timing trials individually



            // run the trials
            System.out.println("Timing Each sort individually wo gc every time forced...");
            System.out.print("    Starting trials for input size "+inputSize+" ... ");
            for (long trial = 0; trial < numberOfTrials; trial++) {


                long[] testList = createRandomIntegerList(inputSize);

                /* force garbage collection before each trial run so it is not included in the time */
                //System.gc();



                TrialStopwatch.start(); // *** uncomment this line if timing trials individually

                /* run the function we're testing on the trial input */

                ///////////////////////////////////////////
                /*              DO BIDNESS              */
                /////////////////////////////////////////


                insertionSort(testList);


                ///////////////////////////////////////////
                /*             END DO BIDNESS           */
                /////////////////////////////////////////

                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime(); // *** uncomment this line if timing trials individually

            }

            //batchElapsedTime = BatchStopwatch.elapsedTime(); // *** comment this line if timing trials individually

            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials; // calculate the average time per trial in this batch
            double doublingRatio = 0;
            if (previousTime > 0) {
                doublingRatio = averageTimePerTrialInBatch / previousTime;
            }

            previousTime = averageTimePerTrialInBatch;
            /* print data for this size of input */

            resultsWriter.printf("%12d  %18.2f %18.1f\n",inputSize, averageTimePerTrialInBatch, doublingRatio); // might as well make the columns look nice

            resultsWriter.flush();

            System.out.println(" ....done.");

        }
    }

    private static void bubbleSort(long[] list) {
        for (int i = 0; i < list.length; i++) {
            for (int j = 0; j < list.length - 1; j++) {
                if (list[j] > list[j+1]) {
                    long tmp = list[j];
                    list[j] = list[j+1];
                    list[j+1] = tmp;
                }
            }
        }
    }

    private static void verifyBubbleSort() {
        long[] testList1 = {14, 42, 1, 5, 2, 19, 12, 4};
        long[] testList2 = {98, 13, 77, 79, 84, 107, 33, 26, 52, 44};

        System.out.println("Bubble Sort Presorted ----------------");
        printArray(testList1);
        printArray(testList2);

        bubbleSort(testList1);
        bubbleSort(testList2);

        System.out.println("Bubble Sort After Sort ----------------");
        printArray(testList1);
        printArray(testList2);

        if (verifySorted(testList1) && verifySorted(testList2)) {
            System.out.println("Bubble Sort lists verified sorted!!!!");
        } else {
            System.out.println("Bubble Sort lists NOT SORTED!!!!!");
        }

    }


    private static void insertionSort(long[] list) {
        int n = list.length;
        for (int i = 1; i < n; ++i) {
            long key = list[i];
            int j = i - 1;

            /* Move elements of arr[0..i-1], that are
               greater than key, to one position ahead
               of their current position */
            while (j >= 0 && list[j] > key) {
                list[j + 1] = list[j];
                j = j - 1;
            }
            list[j + 1] = key;
        }
    }

    private static void verifyInsertionSort() {
        long[] testList1 = {14, 42, 1, 5, 2, 19, 12, 4};
        long[] testList2 = {98, 13, 77, 79, 84, 107, 33, 26, 52, 44};

        System.out.println("Insertion Sort Presorted ----------------");
        printArray(testList1);
        printArray(testList2);

        insertionSort(testList1);
        insertionSort(testList2);

        System.out.println("Insertion Sort After Sort ----------------");
        printArray(testList1);
        printArray(testList2);

        if (verifySorted(testList1) && verifySorted(testList2)) {
            System.out.println("Insertion Sort lists verified sorted!!!!");
        } else {
            System.out.println("Insertion Sort lists NOT SORTED!!!!!");
        }

    }

    /* UTILITY FUNCTIONS */
    /* A utility function to print array of size n */
    private static void printArray(long arr[]) {
        int n = arr.length;
        for (long l : arr) System.out.print(l + " ");
        System.out.println();
    }

    private static long[] createRandomIntegerList(int size) {
        long[] newList = new long[size];
        for (int j = 0; j < size; j++) {
            newList[j] = new Random().nextLong();
        }

        return newList;
    }

}
