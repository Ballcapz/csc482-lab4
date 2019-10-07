import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
























public class ThreeSum {





















































    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    /* define constants */
    static long MAXVALUE =  2000000000;
    static long MINVALUE = -2000000000;
    private static int numberOfTrials = 1;
    private static int MAXINPUTSIZE  = (int) Math.pow(2, 16);
    private static int MININPUTSIZE  =  1;
    private static int SIZEINCREMENT = 2;

    // static int SIZEINCREMENT =  10000000; // not using this since we are doubling the size each time
    private static String ResultsFolderPath = "/home/zach/Results/"; // pathname to results folder
    private static FileWriter resultsFile;
    private static PrintWriter resultsWriter;



    public static void main(String args[])
    {
//        verifyThreeSum();
//        verifyThreeSumFaster();
//        verifyThreeSumFastest();
        runFullExperiment("ThreeSum-lab3-TRASH.txt");
        runFullExperiment("ThreeSum-lab3-Ex2.txt");
        runFullExperiment("ThreeSum-lab3-Ex3.txt");
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


                int result = ThreeSum(testList);
//                int result = ThreeSumFaster(testList);
//                int result = ThreeSumFastest(testList);




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

    // verification for each of the 3 algorithms, expected results: list 1: 1 sum, list 2: 2 sums
    static void verifyThreeSum() {

        long[] testList1 = {-2, -4, 6};
        long[] testList2 = {-2, -4, 6, 14, 2, -7, 11};
        System.out.println("Verify three sum ********");

        System.out.println("Array 1 : ");
        printArray(testList1);
        System.out.println("Array 2 : ");
        printArray(testList2);

        int sum1 = ThreeSum(testList1);
        int sum2 = ThreeSum(testList2);

        System.out.println("\nSum Array 1: " + sum1);
        System.out.println("Sum Array 2: " + sum2);

    }

    static void verifyThreeSumFaster() {

        long[] testList1 = {-2, -4, 6};
        long[] testList2 = {-2, -4, 6, 14, 2, -7, 11};

        System.out.println("Verify three sum faster ********");

        System.out.println("Array 1 : ");
        printArray(testList1);
        System.out.println("Array 2 : ");
        printArray(testList2);

        int sum1 = ThreeSumFaster(testList1);
        int sum2 = ThreeSumFaster(testList2);

        System.out.println("\nSum Array 1: " + sum1);
        System.out.println("Sum Array 2: " + sum2);

    }

    static void verifyThreeSumFastest() {

        long[] testList1 = {-2, -4, 6};
        long[] testList2 = {-2, -4, 6, 14, 2, -7, 11};
        System.out.println("Verify three sum fastest ********");

        System.out.println("Array 1 : ");
        printArray(testList1);
        System.out.println("Array 2 : ");
        printArray(testList2);

        int sum1 = ThreeSumFastest(testList1);
        int sum2 = ThreeSumFastest(testList2);

        System.out.println("\nSum Array 1: " + sum1);
        System.out.println("Sum Array 2: " + sum2);

    }


    private static int ThreeSum(long arr[]) {
        // iterate over the list 3 times checking each combination of 3 numbers for if they sum up to 0
        int N = arr.length;
        int count = 0;
        for (int i = 0; i < N; i++)
            for (int j = i+1; j < N; j++)
                for (int k = j+1; k < N; k++)
                    // if they sum to 0, add to the count
                    if (arr[i] + arr[j] + arr[k] == 0)
                        count++;
        return count;
    }

    private static int ThreeSumFaster(long arr[]) {
        int N = arr.length;
        int count = 0;
        // sort the array so it can be used for binary search later
        Arrays.sort(arr); // ~ nlogn
        /*
         iterate over the list twice, and binary search for the complement of the sum
         generated from adding nums when they are less than 0 from the first 2 iterations
        */
        for (int i = 0; i < N && arr[i] < 0; i++) {
            for (int j = i + 1; j < N && arr[i] + arr[j] < 0; j++) {
                // if complement of sum is found, add to count of 3sums, only search from current loc in array on up
                int k = Arrays.binarySearch(arr, j+1, N, -arr[i] - arr[j]);
                if (k > j) {
                    count++;
                }
            }
        }
        return count;
    }

    private static int ThreeSumFastest(long arr[]) {
        int N = arr.length;
        int count = 0;
        // add a hash map to check against rather than a third iteration
        HashMap<Long, long[]> hashMap = new HashMap<Long, long[]>();
        // sort the list so we can remove duplicates and easily hash
        Arrays.sort(arr);

        // iterate over the array twice and check for complement in a hash map
        for (int i = 0; i < N - 2 && arr[i] < 0; i++) {
            // empty the hashMap for the next iteration through the list
            hashMap.clear();

            // check the map if i = 0 or if i's value is greater than prv i value
            if (i == 0 || arr[i] > arr[i - 1]) {
                for (int j = i + 1; j < N; j++) {
                    // if the complement of the 2Sum is found in the hashMap add to count
                    if (hashMap.containsKey(arr[j])) {
                        count++;

                        // remove the duplicates from the array that were used
                        while (j < (N - 1) && arr[j] == arr[j + 1]) j++;
                    } else {
                        // create temp arr to insert into hashmap to check against
                        long[] tempArr = new long[2];
                        tempArr[0] = arr[i];
                        tempArr[1] = arr[j];
                        // insert the values to check against
                        hashMap.put(0 - (arr[i] + arr[j]), tempArr);
                    }
                }
            }
        }

        return count;
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
