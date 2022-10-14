
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;



/**
 * Group: Ben Trnka And Richelin
 * @author Richelin Metellus
 */
public class TomasuloClient {

    public static void main(String[] args) throws FileNotFoundException {
        Processor tomasulo = new Processor();
        LinkedQueue<Instruction> IQ = null;
         boolean valid = false;
        
        Scanner scan = new Scanner(System.in);
        String folderPath = null;
        String fileName = null;

       File f = new File("C:\\");

//        while (!valid) {
//            System.out.print("Enter the folder  Path: ");
//            folderPath = scan.nextLine();
//
//            System.out.print("Enter the file Name to read (e.g : filename.txt): ");
//            fileName = scan.nextLine();
//       
//            f = new File(folderPath,fileName);
//            if (f.exists()) {
//                valid = true;
//            } 
//            else {
//                System.out.println("Invalid file path or name. Enter a valid path");
//            }
//        }
         folderPath ="C:\\Users\\ricky\\Google Drive\\NDSU file\\Junior Year\\Fall 2017\\Computer Architecture\\Project\\Project1";
         fileName = "tomasulo3.txt";
        f = new File(folderPath, fileName);
        if (f.exists()) {
            tomasulo.readFile(f);
            IQ = tomasulo.getIQ();

            tomasulo.runProcessor();

        } else {
            System.out.println("Invalid file");
        }

    }
}
