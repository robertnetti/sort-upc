import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;


class Receipt{
    String grp_id;
    String upc14;
    String upc12;
    String brand;
    String name;

    Receipt(String grp_id, String upc14, String upc12, String brand, String name){
        this.grp_id = grp_id;
        this.upc14 = upc14;
        this.upc12 = upc12;
        this.brand = brand;
        this.name = name;
    }

    public Long getUpc14(){
        return Long.parseLong(this.upc14);
    }

    public Long getUpc12(){
        return Long.parseLong(this.upc12);
    }

    @Override
    public String toString() {
        return grp_id + "," + String.format("%014d", this.getUpc14()) + "," + String.format("%012d", this.getUpc12()) + "," + brand + "," + name + "\n";
    }
}

public class sortUPC {

    public static Long getMaxElement(ArrayList<Receipt> receipts, int n){
        Long maxElement = 0L;
        for(int i = 0; i < n; i++){
            Long temp = receipts.get(i).getUpc14();
            if(temp>maxElement){
                maxElement = temp;
            }
        }
        return maxElement;
    }

    //Swaps Two Indices in an ArrayList
    public static void swap(ArrayList<Receipt> receipts, int index1, int index2){
        Receipt temp = receipts.get(index1);
        receipts.set(index1, receipts.get(index2));
        receipts.set(index2, temp);
    }

    //Returns a Random Pivot Point Between Low and High Bounds Inclusively
    public static int getPivot(int low, int high){
        Random randomPivot = new Random();
        return randomPivot.nextInt((high-low) + 1) + low;
    }

    //Moves Numbers Less than the Pivot to the Left of it
    //and all Numbers Greater than Pivot to the Right of it
    //Returns the New Pivot Value
    public static int partition(ArrayList<Receipt> receipts, int low, int high){
        //Swap Pivot Value into Leftmost Position
        swap(receipts, low, getPivot(low, high));
        //Set the boundary to the Right of the Pivot Position
        int boundary = low + 1;
        //Compare Each item to the right of the Pivot with the Pivot
        for(int i = boundary; i<=high; i++){
            //If the Item is Less than the Pivot Value Swap it with the boundary and move boundary to the right
            if(receipts.get(i).getUpc14() < receipts.get(low).getUpc14()){
                swap(receipts, i, boundary++);
            }
        }
        //Move the Pivot into position, to the left of the boundary
        swap(receipts, low, boundary-1);
        //Return Index of Pivot Position
        return boundary-1;
    }

    public static void QuickSort(ArrayList<Receipt> receipts, int low, int high){
        //If There's More Than One Item to be sorted
        if(low<high){
            //partition the array, and return the index of the new pivot position
            int pivot = partition(receipts, low, high);
            //Recursively Run QuickSort on Left Partition
            QuickSort(receipts, low, pivot-1);
            //Recursively Run QuickSort on Right Partition
            QuickSort(receipts, pivot+1, high);
        }
    }

    public static void HeapSort(ArrayList<Receipt> receipts){
        int n = receipts.size();

        //Build Max Heap (starting from the last non-leaf node, and ending with the root)
        for(int i = n/2-1; i>=0; i--){
            heapify(receipts,n,i);
        }

        //Remove the elements from heap (stating with the root node)
        for(int i=n-1; i>=0; i--){
            //Move root to end
            swap(receipts, 0, i);
            //heapify the reduced heap
            heapify(receipts, i, 0);
        }
    }

    public static void heapify(ArrayList<Receipt> receipts, int n, int i){
        int largest = i; //Largest is Parent node
        int l = 2*i + 1; //Left Child
        int r = 2*i + 2; //Right Child

        //if left child is more than the largest, set largest to the index of left child
        if(l < n && receipts.get(l).getUpc14() > receipts.get(largest).getUpc14()){
            largest = l;
        }

        //if right child is more than the largest, set largest to the index of the right child
        if(r < n && receipts.get(r).getUpc14() > receipts.get(largest).getUpc14()){
            largest = r;
        }

        //if largest is not same as the Parent
        if(largest != i){
            //swap the parent with the new largest node
            swap(receipts, i, largest);

            //reheapify the sub-tree recursively
            heapify(receipts, n, largest);
        }
    }

    public static void RadixSort(ArrayList<Receipt> receipts, int n){
       Long maxElement = getMaxElement(receipts, n);
       //for each placeholder call CountSort on the arraylist
       for(Long place = 1L; (maxElement/place) > 0; place*=10){
           CountSort(receipts, n, place);
       }
    }

    public static void CountSort(ArrayList<Receipt> receipts, int n, long place){

        //initialize a count array of size 10 to count the frequency of the numbers 0-9
        int[] count = new int[10];
        Arrays.fill(count,0);

        Receipt[] temp = new Receipt[n];

        for(int i = 0; i<n; i++) {
            // for each upc14 code increment the count array that is
            // respective to the digit at the current placeholder
            ++count[(int) ((receipts.get(i).getUpc14()/place)%10)];
        }
        for(int i = 1; i<=9; i++) {
            //update count array to reflect the position of each
            //element in the partially sorted array
            count[i] = count[i] + count[i-1];
        }
        for(int i = n-1; i>=0; i--){
            //build partially sorted array from rightmost element to leftmost element
            temp[--count[(int) ((receipts.get(i).getUpc14()/place)%10)]] = receipts.get(i);
        }
        for(int i = 0; i<n; i++){
            //replace unsorted array with partially sorted array
            receipts.set(i, temp[i]);
        }
    }

    public static void main(String[] args) {

        //Prompt User to Enter File Path to CSV file
        Scanner input = new Scanner(System.in);
        //System.out.println("Enter CSV FilePath:");
        //String filePath = input.next();
        String filePath = "Grocery_UPC_Database.csv";

        //Reading Receipt Entries from CSV File into an ArrayList
        ArrayList<Receipt> receipts = readReceiptsFromCSV(filePath);

        //Prompt User to Choose a Sorting Algorithm
        System.out.println("Choose Sorting Algorithm:");
        System.out.println("1) QuickSort");
        System.out.println("2) HeapSort");
        System.out.println("3) RadixSort");
        int option = input.nextInt();

        //Catch Incorrect Input
        while (option < 1 || option > 3) {
            System.out.println("!Invalid Input!");
            System.out.println("Choose Sorting Algorithm:");
            System.out.println("1) QuickSort");
            System.out.println("2) HeapSort");
            System.out.println("3) RadixSort");
            option = input.nextInt();
        }
        input.close();

        long startTime;
        long endTime;
        long timeElapsed;

            switch (option) {
                case 1:
                    startTime = System.nanoTime();
                    QuickSort(receipts, 0, receipts.size() - 1);
                    endTime = System.nanoTime();
                    timeElapsed = endTime - startTime;
                    writeDataToFile(receipts);
                    System.out.println("Execution Time in milliseconds: " + timeElapsed / 1000000);
                    break;
                case 2:
                    startTime = System.nanoTime();
                    HeapSort(receipts);
                    endTime = System.nanoTime();
                    timeElapsed = endTime - startTime;
                    writeDataToFile(receipts);
                    System.out.println("Execution Time in milliseconds: " + timeElapsed / 1000000);
                    break;
                case 3:
                    startTime = System.nanoTime();
                    RadixSort(receipts, receipts.size());
                    endTime = System.nanoTime();
                    timeElapsed = endTime - startTime;
                    writeDataToFile(receipts);
                    System.out.println("Execution Time in milliseconds: " + timeElapsed / 1000000);
                    break;
            }
        }
    private static ArrayList<Receipt> readReceiptsFromCSV(String fileName){
        ArrayList <Receipt> receipts = new ArrayList<>();
        Path filePath = Paths.get(fileName);
        //Scanner input = new Scanner(System.in);

        //System.out.println("Enter Number of Lines to Read: ");
        //int n = input.nextInt();

        try(BufferedReader br = Files.newBufferedReader(filePath)){

            //Read First Line of CSV File
            String line = br.readLine();

            //Loop Through the Rest of the Lines
            //for(int i = 1; i<=n && line != null; i++) {
            while(line != null) {
                //Split the Attributes of Each Line by the Comma Delimiter and Create a Receipt Object
                String[] attributes = line.split(",");
                Receipt receipt = createReceipt(attributes);

                //Add Receipt Object to the ArrayList
                receipts.add(receipt);

                //Read Next Line
                line = br.readLine();
            }

        } catch (IOException e){
            e.printStackTrace();
        }

        return receipts;
    }

    private static Receipt createReceipt(String[] attributes){

        String grp_id = attributes[0];
        String upc14 = attributes[1];
        String upc12 = attributes[2];
        String brand = attributes[3];
        String name = attributes[4];

        //create and return a receipt with csv line data
        return new Receipt(grp_id,upc14,upc12,brand,name);
    }

    public static void writeDataToFile(ArrayList<Receipt> receipts) {

        //Prompt User to Enter Name for Output CSV File
        System.out.println("Enter Name for Output File:");
        Scanner input = new Scanner(System.in);
        String outputFileName = input.next();
        input.close();
        String outputFilePath = "./" + outputFileName + ".csv";

        //Instantiate Output File
        File file = new File(outputFilePath);

        //Append Each Receipt to the Specified Output File
        try{
            FileWriter outputFile = new FileWriter(file);

            for (Receipt r: receipts){
                outputFile.append(r.toString());
            }
            outputFile.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

