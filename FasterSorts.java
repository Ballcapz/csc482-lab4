import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.invoke.MethodType;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class FasterSorts {
    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    /* define constants */
    static long MAXVALUE =  2000000000;
    static long MINVALUE = -2000000000;
    private static int numberOfTrials = 1;
    private static int MAXINPUTSIZE  = (int) Math.pow(2, 26);
    private static int MININPUTSIZE  =  1;
    private static int SIZEINCREMENT = 2;

    static Random rand = new Random();

    // static int SIZEINCREMENT =  10000000; // not using this since we are doubling the size each time
    private static String ResultsFolderPath = "/home/zach/Results/lab4/"; // pathname to results folder
    private static FileWriter resultsFile;
    private static PrintWriter resultsWriter;



    public static void main(String args[])
    {
//        checkSortCorrectness();
        runFullExperiment("QuickSort-1-TRASH.txt");
        runFullExperiment("QuickSort-2.txt");
        runFullExperiment("QuickSort-3.txt");
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

        quickSort(testList1);
        quickSort(testList2);
        quickSort(testShortList);
        quickSort(testShortList2);

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


                quickSort(testList);


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


    static void verifyMergeSort() {

        long[] testList1 = createRandomIntegerList(10);
        long[] testList2 = createRandomIntegerList(10);

        System.out.println("Verifying Merge Sort-------------");
        printArray(testList1);
        printArray(testList2);

        mergeSortRunner(testList1);
        mergeSortRunner(testList2);

        printArray(testList1);
        printArray(testList2);

        if (verifySorted(testList1) && verifySorted(testList2)) {
            System.out.println("Merge sort results verified correct!!!");
        } else {
            System.out.println("Merge sort results NOT correct...");
        }


    }

    private static void merge(long arr[], int l, int m, int r) {
        // Find sizes of two subarrays to be merged
        int n1 = m - l + 1;
        int n2 = r - m;

        /* Create temp arrays */
        long L[] = new long [n1];
        long R[] = new long [n2];

        /*Copy data to temp arrays*/
        for (int i=0; i<n1; ++i)
            L[i] = arr[l + i];
        for (int j=0; j<n2; ++j)
            R[j] = arr[m + 1+ j];

        /* Merge the temp arrays */

        // Initial indexes of first and second subarrays
        int i = 0, j = 0;

        // Initial index of merged subarry array
        int k = l;
        while (i < n1 && j < n2)
        {
            if (L[i] <= R[j])
            {
                arr[k] = L[i];
                i++;
            }
            else
            {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        /* Copy remaining elements of L[] if any */
        while (i < n1)
        {
            arr[k] = L[i];
            i++;
            k++;
        }

        /* Copy remaining elements of R[] if any */
        while (j < n2)
        {
            arr[k] = R[j];
            j++;
            k++;
        }
    }
    // Main function that sorts arr[l..r] using
    // merge()
    private static void sort(long arr[], int l, int r) {
        if (l < r)
        {
            // Find the middle point
            int m = (l+r)/2;

            // Sort first and second halves
            sort(arr, l, m);
            sort(arr , m+1, r);

            // Merge the sorted halves
            merge(arr, l, m, r);
        }
    }

    private  static void mergeSortRunner(long arr[]) {
        sort(arr, 0, arr.length-1);
    }



    private static void verifySlowQuickSort(){
        long[] testList1 = createRandomIntegerList(10);
        long[] testList2 = createRandomIntegerList(10);

        slowQuickSort(testList1);
        slowQuickSort(testList2);


        if (verifySorted(testList1) && verifySorted(testList2)) {
            System.out.println("Slow Quick sort results verified correct!!!");
        } else {
            System.out.println("Slow Quick sort results NOT correct...");
        }
    }

    private static void slowQuickSort(long arr[]) {
        slowQuickSortWorker(arr, 0, arr.length -1);
    }

    private static void slowQuickSortWorker(long arr[], int lo, int hi) {
        if (lo < hi) {
            long pivot = arr[lo];
            int i = lo;
            int j = hi;

            while (i < j) {
                i+=1;

                while (i <= hi && arr[i] < pivot) {
                    i += 1;
                }
                while (j >= lo && arr[j] > pivot) {
                    j -= 1;
                }
                if (i <= hi && i < j) {
                    // swap
                    long tmp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = tmp;
                }
            }
            // swap
            long tmp = arr[lo];
            arr[lo] = arr[j];
            arr[j] = tmp;

            slowQuickSortWorker(arr, lo, j-1);
            slowQuickSortWorker(arr, j+1, hi);
        }
    }




    private static void verifyQuickSort() {
        long[] testList1 = createRandomIntegerList(10);
        long[] testList2 = createRandomIntegerList(10);

        quickSort(testList1);
        quickSort(testList2);


        if (verifySorted(testList1) && verifySorted(testList2)) {
            System.out.println("Quick sort results verified correct!!!");
        } else {
            System.out.println("Quick sort results NOT correct...");
        }
    }

    private static void quickSort(long arr[]) {
        quickSortWorker(arr, 0, arr.length-1);
    }

    private static void quickSortWorker(long arr[], int lo, int hi) {
        int pivotIndex = partition(arr, lo, hi);
        if (lo < pivotIndex -1)
            quickSortWorker(arr, lo, pivotIndex - 1);
        if (pivotIndex < hi)
            quickSortWorker(arr, pivotIndex, hi);
    }

    public static int partition(long arr[], int lo, int hi) {
        int i = lo, j = hi;
        long tmp;
        long pivot;
        if ((hi - lo) < 1) {
            pivot = arr[hi];
        } else {
            pivot = arr[lo + rand.nextInt(hi - lo)];
        }

        while (i <= j) {
            while (arr[i] < pivot)
                i++;
            while (arr[j] > pivot)
                j--;
            if (i <= j) {
                tmp = arr[i];
                arr[i] = arr[j];
                arr[j] = tmp;
                i++;
                j--;
            }
        };

        return i;
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
