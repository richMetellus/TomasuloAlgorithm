
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Group: Richelin and Ben Trnka;
 * 
 * This class simulate the Tomasulo Algorithm supporting only add, sub, mul, div
 * of tomasulo Algorithm.
 * @author Richelin Metellus 
 *         contributor: Ben Trnka
 * @version 11/05/2017
 */
public class Processor {
    
    private int simulationCC;                           // how many cycle you want to simulate.
    private int addUnit;                            // how many executing unit for add
    private final int mulDivUnit;                   // how many executing unit for div/multipication
    private int mulRsSize, addRsSize;               // no of reservation station for each instruction type
    private final boolean issueDispSameCC;                // issue + dipatch same cycle yes or no
    private boolean captureDisSameCC;               // can the processor capture result and dispatch same instructin in same cycle 
    private final boolean issueBroadcastSameCC;           // can we issue an instr and broadcast in same cc. WAW type of situation. usually yes. should broadcsat 1st then issue.
    private final boolean rsFreeRsAllocSameCC;           // can the proc alloc an instruction into a newly free RS station in same cylce
    
    private ArrayList<ResStation> resStations;
 
    
//     private ArrayList<MulDivUnit> mulDivUnitList;          // hold MulDivUnits.. will need to check the unit whose available. getAvailableUnit()
//      private ArrayList<AddSubUnit> addSubUnitList;
    ArithmeticUnit addSubUnit;
    ArithmeticUnit mulDivU;
    private Clock clock = new Clock();
    private Integer rat[];                                    // big type Integer obj. If null check the regFile
    
    private int units[];

    
    LinkedQueue<Instruction> instrQueue;
    public int regFile[];
    
   
    
   // default constructor according to the processor specification teacher gives
    @SuppressWarnings("empty-statement")
    public Processor()
    {
        this.addUnit              = 1;
        this.mulDivUnit           = 1;
        this.addRsSize            = 3;
        this.mulRsSize            = 2;
        this.issueDispSameCC      = false;
        this.captureDisSameCC     = false;          // once instr get the operands it can't dispatch in the same cycle
        this.issueBroadcastSameCC = true;
        this.rsFreeRsAllocSameCC  = false;     

        regFile = new int[8];                                // only 8 registers
        rat     = new Integer[8];                            // 8 rat entry. 1 to 1 relatiship with regFile
        units = new int[2];
        units[0] = addUnit;
        units[1] = mulDivUnit;
        instrQueue = new LinkedQueue();
        
       this.resStations = new ArrayList<>(addRsSize+mulRsSize);
       
        
        this.addSubUnit = new ArithmeticUnit();           
        this.mulDivU = new ArithmeticUnit();
        
        intializeRsStations();
    }
    
    
    public int getSimuationCC(){return simulationCC;}
    public int getAddUnit(){ return addUnit;}
    public int getMulDivUnit(){return mulDivUnit;}
    public int getAddRsSize(){return addRsSize;}
    public int getMulRsSize(){return mulRsSize;}

    
    
    //getter for processor rules
    public boolean getIssueDispSameCC(){return issueDispSameCC;}
    public boolean getCaptureDisSameCC(){return captureDisSameCC;}
    public boolean getIssueBroadCastSameCC(){return issueBroadcastSameCC;}
    public boolean getRsFreeRsAllocSameCC(){return rsFreeRsAllocSameCC;}
    
    public LinkedQueue<Instruction> getIQ(){return instrQueue;};
    
    
     public void setAddUnit( int addUnit)
    {
        this.addUnit = addUnit;
        
    }
    public void setSimulationCC(int cycle)
    {
        this.simulationCC = cycle;
    }
    
    
    
    /**
     * This method read the file and load the initial state of the processor.
     * i. e the instruction queue and the Register Values
     * @param f the file to read
     * @throws FileNotFoundException 
     */
    
    public void readFile(File f) throws FileNotFoundException
    {
        int instrQt = 0;            // number of instruction in the file;
        int simCycle;           // number of cycles of simulation
        int lineCtr = 0;
        Scanner scanFile = new Scanner(f);
       
        String line = null;
        String instrLineArr[] = null;
        while(scanFile.hasNextLine())
        {
            lineCtr++;                  // line counter to indicate which line we are currenly reading.
            if(lineCtr == 1)            // 1st loop it execute this
            {
                line = scanFile.nextLine();
                instrQt = Integer.parseInt(line);
                instrLineArr = new String[instrQt];
                System.out.println(lineCtr);
                lineCtr++;
                System.out.println("Number of Instruction " + instrQt);
            }
            
            if(lineCtr == 2)       // second while loop iter it execute this
            {
                line = scanFile.nextLine();
                simCycle = Integer.parseInt(line);
                setSimulationCC(simCycle);
                lineCtr++;
                System.out.println("Number of Cycle to Simulate: "+ simCycle);
                System.out.println("After getting Simualtion Cycle Linectr point to line # :" + lineCtr);
            }
              
            // set the instruction into an array as a string(as is)
            if(lineCtr == 3)                    // 3rd while iteration it execute this. lineCtr = 3;   
            {
                int instrEnd = lineCtr + (instrQt-1);           // the line where the instruction ends
               
                System.out.println("LineCtr before Entering the whileloop " + lineCtr);
                while(lineCtr <= instrEnd)
                {
                    System.out.println("Line Counter point at Line # : " + lineCtr);
                    line = scanFile.nextLine();
                    instrQueue.enqueue(stringToInstr(line));
                    System.out.println("Done Enqueuing Instruction at Line " + lineCtr);
                    lineCtr++;
                    
                }
                System.out.println("Here is the queue");
                System.out.println(instrQueue.toString());
     
            }
            
            // now load the rest of the file into the regFile;
            //LineCtr = 5 now if we simulate this with the project instruction on blacboard
            System.out.println("Here is lineCtr after instruction are loaded to queue: " + lineCtr);
            System.out.println("Ready to set the initial State of the Register File");
            // at this point the scanfile is ready to read the 5th line value 3 for F0;
            for(int k = 0; k < regFile.length && scanFile.hasNextLine();k++, lineCtr++)
            {
              
                line = scanFile.nextLine();
                regFile[k] = Integer.parseInt(line);
            }
            System.out.println("The Register File");
            for(int l = 0; l < regFile.length;l++)
            {
                System.out.println("reg[ " +l +"] :" +regFile[l]);
            }
            System.out.println("lineCtr in now at Line# " + lineCtr);
          
                
            
        }
          System.out.println("End of File\n\n");
    
    }
   
    
    public void runProcessor()
    {
        for(int i = 1; i <= simulationCC; i++)
        {
            System.out.println("\n\nCycle Number: " + i);
            // keeps updating the units busy state evry clk cc
            addSubUnit.updateUnits(i);
            mulDivU.updateUnits(i);
            System.out.println("State of add unit at first\n" +addSubUnit.toString());
            System.out.println("State of mul div unit at first\n" +mulDivU.toString());
            
            boolean hasIssued;          // returned if the processor has issued an instruction
            hasIssued = issue();
            System.out.println("Has the processor issue in this cycle" + i +":"+ hasIssued);
            ArrayList<Integer> rsIndxtoDisp;     // an int arraylist of the indexes of instruction available for dispatch
            rsIndxtoDisp = findIToDisp(resStations);  // find all the instruction whose operand are ready can dispatch execpt the newly issue ones. // this will be emplty arraylist on 1st call. 
            // now if arraylist is not empty then check for available unit.
            
            if(!rsIndxtoDisp.isEmpty())
            {
                System.out.println("now will dispach if possible");
                dispatch(i,rsIndxtoDisp);       // this will check for available unit and dispatch if posible
            }
            if(i == addSubUnit.getInstrEnd())
            {
                addSubUnit.execute();
                System.out.println("AddSub Unit done executing\n"+ addSubUnit.toString() );
                
            }
            if(i== mulDivU.getInstrEnd())
            {
                mulDivU.execute();
                System.out.println("MulDiv Unit Done exectuting\n" + mulDivU.toString());
            }
                        // true if broadcast ready = true at the begining of the cycle.
            if(addSubUnit.aluBuffer.getPrevBusyState() && !mulDivU.aluBuffer.getPrevBusyState())
            {
                System.out.println("Add Unit will broacast result now");
                broadcast(addSubUnit.aluBuffer.getPrevTag(), addSubUnit.getResult());
            }
                                                               
             if(mulDivU.aluBuffer.getPrevBusyState())
            {
                System.out.println("mul/div Unit will broacast result now");
                broadcast(mulDivU.getTag(), mulDivU.getResult());
            }
  
            System.out.println("before updating Reservations\n" + resStations.toString());
            updateRs(resStations);
            System.out.println("after updating Reservations\n" + resStations.toString());
        
    }
        String blkSpc = "    ";
        System.out.println("\n\n-----------------------------------------------------------------------------------------------------------------\n\n");
        System.out.printf("%20s Final State %20s \n", blkSpc, blkSpc);
        System.out.println("Printing reservation Station\n" + resStations.toString());
        System.out.println("\n\n Here the Rat \n");
        printRat();
        System.out.println("State of the RegFile");
        printArray(regFile);
        System.out.println("The Final State of the Queue \n" + instrQueue.toString());


 }
    
    /**
     * 
     * @return true if  an instruction has been issue.
     */
    public boolean issue()
    {
        boolean hasIssued =  false;
        Instruction newInstr = null;
        int rsOp ;
        int src1Index ;                 // what index to look for the first source operand
        int src2Index ;
        int ratTagSrc1 ;                // the index in the Rat where the source operant is mapped to.
        int ratTagSrc2 ;
        int freeRsIndex;                // the index of the condensed rsStation that is free for addi it's 0-2, 3-4 for mul/div  
        if(!instrQueue.isEmpty())
        {
            
           
             newInstr = instrQueue.first();
             rsOp = newInstr.getOpcode();
            
             src1Index = newInstr.getSrc1();                 //Vj
             src2Index = newInstr.getSrc2();
             ratTagSrc1  = ratLookup(rat, src1Index);        // the tag value of the rat for source operand 1
             ratTagSrc2= ratLookup(rat, src2Index);
             
        

            // get the instruction type and add it to the appropriate Rs station
            switch(newInstr.getOpcode())
            {
                case 0:
                case 1:
                    // if this type of rs has free station, not full then add it there.
                    freeRsIndex = hasFreeAddRs(resStations);
                    System.out.println("free rs indice for add/sub" + freeRsIndex );
                    if(freeRsIndex != -1)
                    {
                        newInstr = instrQueue.dequeue();           // only need to deque if there an available station.
                        hasIssued = true;
                        // create a minimal instance of reservation fields
                        ResStation newRs = new ResStation(1,rsOp,1);  // since the instruction is add or sub then they type of rs is 1 and the station is now busy;
                        resStations.set(freeRsIndex,newRs);        // |RS0||---|-----| replace dummy rs
                        System.out.println(" add ratTagSrc1: "+ ratTagSrc1 );
                        if(ratTagSrc1 != -1)              // if true get value from RAT.
                        {
                            System.out.println("inside  add/sub rat tagSrc1 =! -1");
                            newRs.setBusyState(2);
                            newRs.setVj(null);            // operand not ready
                            newRs.setQj(ratTagSrc1);      //  waiting on operand value
                            System.out.println("Done Setting State" + newRs.getBusy());
                            
                        }
                        System.out.println("add ratTagSrc2: "+ ratTagSrc2 );
                        if(ratTagSrc2 != -1 )
                        {
                            System.out.println("inside  add/sub rat tagSrc2 =! -1");
                            newRs.setQk(ratTagSrc2);      // contains the index of the reservation it's waiting on.
                            newRs.setVk(null);
                            newRs.setBusyState(2);
                            System.out.println("Done Setting State" + newRs.getBusy());
                        }
                        
                        if(ratTagSrc2 == -1 && ratTagSrc1 == -1)                               // take the values form the register. operand ready
                        {   
                            newRs.setVj(regFile[src1Index]);
                            newRs.setVk(regFile[src2Index]);
                            newRs.setBusyState(1);          //  instruction just issued to dispatch for next cycle assuming alu unit is nto busy
                            System.out.println("Done Setting State" + newRs.getBusy());
                            if(issueDispSameCC)
                            {
                                newRs.setBusyState(3);      // if allow to dispatch right away this will return as readdy for disp on 
                            }
                        }
                        if(ratTagSrc1 == -1 && ratTagSrc2 != -1)
                            newRs.setVj(src1Index);
                        else
                            newRs.setVk(regFile[src2Index]);
                        
                       
                        
                    }
                    if(freeRsIndex != -1)
                    {
                        rat[newInstr.getDstReg()] = freeRsIndex;
                        System.out.println("Now Let's see how rat after adding instruction in Rs");
                        printRat();
                        System.out.println("RS look so far");
                        System.out.println(resStations.toString());
                        if (isAddRsFull()) {
                            System.out.println("The add/sub reservation station is Full. wont'issue next unless clear this cycle ");
                            System.out.println(resStations.toString());
                            System.out.println("reservation station size: " + resStations.size());

                        }
                    }
                     
                    break;
                case 2:
                case 3:
                    freeRsIndex = hasFreeMulRs(resStations);
                    System.out.println("free rs index for mul/div rs" + freeRsIndex );
                    if(freeRsIndex != -1)
                    {
                        newInstr = instrQueue.dequeue();
                        hasIssued = true;
                        // create a new instance of reservation fields
                        ResStation newRs = new ResStation(1,rsOp,3);  // since the instruction is add or sub then they type of rs is 3
                        resStations.set(freeRsIndex,newRs);        // |RS4||---|
                        // now need to check a way to get the righ entry for setting vj, vk by looking at the RAT
                        System.out.println(" mul ratTagSrc1: "+ ratTagSrc1 );   
                        if(ratTagSrc1 != -1)              // if true get value from RAT.
                        {
                               System.out.println("inside mul/div rat tagSrc1");
                                newRs.setQj(ratTagSrc1);      //  waiting on operand value
                               newRs.setVj(null);            // operand not ready
                               newRs.setBusyState(2);
                             
                               System.out.println("Done Setting State" + newRs.getBusy());
                            
                        }
                        System.out.println(" mul ratTagSrc2: "+ ratTagSrc2 );
                        if(ratTagSrc2 != -1)
                        {
                            System.out.println("inside  mul/div rattagSrc2 !=-1");
                            newRs.setQk(ratTagSrc2);      // contains the index of the reservation it's waiting on.
                            newRs.setVk(null);
                            newRs.setBusyState(2);
                        }
                        
                        if(ratTagSrc2 == -1 && ratTagSrc1 == -1)                              // take the values form the gerister
                        {   
                            System.out.println("src1 index from regFile: " + src1Index);
                            newRs.setVj(regFile[src1Index]);
                            System.out.println("src1 index from reFile: " + src2Index);
                            newRs.setVk(regFile[src2Index]);
                            System.out.printf("newRs Vj %d newRs Vk: %d\n", newRs.getVj(),newRs.getVk());
                            newRs.setBusyState(1);          //  instruction newly issued waiting to dispatch
                            if(issueDispSameCC)
                            {
                                newRs.setBusyState(3);      // if allow to dispatch right away this will return as readdy for disp on the same cycle
                            }
                        }
                        
                        if(ratTagSrc1 == -1 && ratTagSrc2 != -1)
                            newRs.setVj(src1Index);
                        else
                            newRs.setVk(regFile[src2Index]);
                    
                    }
                   
                    //  tag the Rat for mult: value 3-4 representing RS3 and RS4
                    
                    if(freeRsIndex != -1)
                    {
                        rat[newInstr.getDstReg()] = freeRsIndex;
                        System.out.println("Now Let's ee how rat after adding instr");
                        printRat();
                        System.out.println("RS look so far");
                        System.out.println(resStations.toString());
                        if (isMulRsFull()) {
                            System.out.println("The mul/Div reservation station is Full. won't  Issue next intr unless a mul spot clear in this cycle  ");
                            System.out.println(resStations.toString());
                            System.out.println("reservation station size: " + resStations.size());

                        }
                    }
                     
                     break;
                default:
                    System.out.println("Not a valid instruction type");
                
                    
            }
            
        }
        else
        {
            System.out.println(" Queue is empty. No more Instruction To issue  ");
            
        }
      return  hasIssued;
    } 
    /**
     * 
     * @param curClkcc  the current clock cycle to start dispaching
     * @param dipachableIndex  array list of the reservation station index that are ready to dispach
     */
    
    public void dispatch(int curClkcc, ArrayList<Integer> dipachableIndex )
    {
        //check for the type of rs for each index in the arraylist
                for(int j = 0; j < dipachableIndex.size(); j++)
                {
                    int resTag   =  dipachableIndex.get(j); // the index of the reservation; for my example add r2, r4, r6; index = 0
                    int disOp    = resStations.get(resTag).getrsOpcode();               // rs0.opcode 
                    int src1     = resStations.get(resTag).getVj();
                    int src2     = resStations.get(resTag).getVk();
                    if(resTag >= 0 && resTag < addRsSize ) // between [0-2] meaning it's an add reservation station.
                    {
                        if(!addSubUnit.isUnitBusy())        // check for available unit of this type of rs
                        {
                            addSubUnit.loadALU(curClkcc, resTag,disOp ,src1, src2);       //if unit free, load the alu i.e by calling loadALU(startcc,int tag(rs0), int op,src1,src2)_which int turn call the the ALU updateUnit
                            resStations.get(resTag).setBusyState(4);           // instruction currently dispatching
                            System.out.println("rs"+ resTag + " is currently dispatching");
                            System.out.println("State of add unit at dispatch \n" +addSubUnit.toString());
            
                            
                            
                        }
                        else
                            System.out.println("add/sub unit is busy");
                    }
                    else           // rest of index 3 or 4 meaning div unit need
                    {
                        if(!mulDivU.isUnitBusy())        // check for available unit of this type of rs
                        {
                            mulDivU.loadALU(curClkcc, resTag,disOp ,src1, src2);
                            resStations.get(resTag).setBusyState(4);           // instruction currently dispatching
                            System.out.println("rs"+ resTag + " is currently dispatching");
                            System.out.println("State of mul div unit at dispatch\n" +mulDivU.toString());
                        }
                        else
                            System.out.println("Mul Div unit is busy");
                        
                    }

               }
    }
    
    /**
     * 
     * @param tag the rs currently broadcasting, rs0 for example
     * @param result the result to capture
     * @param list  list of only the rs index waiting on a result
     */
    private void capture(int tag, int result, ArrayList<Integer> list) 
    {
         int resTag; 
         int qj;
         int qk;
        for(int i = 0; i < list.size(); i++)
        {
                                    
           resTag = list.get(i);       // index of rs where to capture the result;
           qj    = resStations.get(resTag).getQj();
           qk    = resStations.get(resTag).getQk();
           if(qj == tag && qj != -1)
           {
               resStations.get(resTag).setVj(result);
               resStations.get(resTag).setQj(-1);
               System.out.println("Done  capturing vj,qj result for rs" + resTag);
           }
           if(qk == tag && qk != -1)
           {
                resStations.get(resTag).setVk(result);
               resStations.get(resTag).setQk(-1);
               System.out.println("Done  capturing vk,qj result for rs" + resTag);
           }
            
        }

    }
    /**
     * 
     * @param tag the res index to look for
     * @return the index of the rat if in the rat or -1 if no match found;
     */
    
    private int searchRat(int tag)
    {
        int flag = -1;
        for(int i = 0; i < rat.length; i++)
        {
            if(rat[i]== null)
                continue;
            if(rat[i]== tag)
                flag = i;
        }
        System.out.println("Rat index at:" + flag);
        return flag;
    }
    
    private void broadcast(int tag, int result)
    {
        ArrayList<Integer> rsIndxtoCapt;     // an int arraylist of the indexes of instruction awaiting values
        rsIndxtoCapt= insWaitingOnOp(resStations);
        System.out.println("rs  wating on Operand :\n" + rsIndxtoCapt.toString() );
        if(!rsIndxtoCapt.isEmpty())
        {
            System.out.println("now will try to caputre if possible");
            capture(tag, result, rsIndxtoCapt);
        }
        
        System.out.println("Checking for matching rat tag");
        int ratIndex = searchRat(tag);          // rat index or rf index     
        if(ratIndex != -1)          // match found at Rat
        {
           rat[ratIndex] = null;
           regFile[ratIndex] = result;      // release Rat entry
            System.out.println("finish updating Rat and RegFile");
            System.out.println("Here is the Rat");
            printRat();
            System.out.println("Here is the RegFile");
            printArray(regFile);
            
        }
        resStations.get(tag).clearRsEntry();
            
        
        
    }
      
    
    //************************ Utility Method *********************************
    
    /**
     * 
     * @param S  the String line as in the file (with the space) representing the Instruction 
     * @return a MIPS representation of the instruction. Note if you use the toString()
     * of the instruction class it will return the exact arithmetic instruction as in
     * MIPS
     */
     
    private static Instruction stringToInstr(String S)
    {
        Scanner lineScan = new Scanner(S);
        lineScan.useDelimiter(" ");
        int instrArr[] = new int[4];            // array initialize to get the 4 field
        Instruction instr;
        // take the space-delimitted string then put each field in an array slot.
        for(int i =0; lineScan.hasNextInt(); i++)
            instrArr[i] =lineScan.nextInt();
        
      return  instr = new Instruction(instrArr[0],instrArr[1],instrArr[2],instrArr[3]);
        
    }
    
    
    
    private void intializeRsStations()
    {
        int rsSize =  addRsSize + mulRsSize;
        ResStation rsStation;
        for(int i = 0; i < rsSize; i++)
        {
            if( i < addRsSize)
            {
                rsStation =  new ResStation(1); // 1 tells it's an add/sub type of res
                resStations.add(rsStation);
            }
            else
                resStations.add(new ResStation(3)); // 3 indictate it's an mult/div type of reservation
        }
    }
    /**
     * 
     * @param rat the rat array to look into
     * @param location the location of the src operand;
     * if value is 3, or 4 it means search the muld[0]div[1] Reservation unit
     * @return an int the index of the RS or -1 if the value to look should come
     * from the register file. the return value goes from 0 to 4. RS0-RS2 are add/sub RS units
     */
    
    
    private static int ratLookup(Integer[] rat, int location)
    {
        if(rat[location] != null)
            return rat[location];
        return -1;
    }
    

    
 
 
    /**
     * 
     * @param list an arraylist of reservation station.
     * @return  the index of the first found free reservation station, -1 otherwise
     */
      private int hasFreeAddRs(ArrayList<ResStation> list) 
      {
          int flag = -1;
          for(int i =0; i < addRsSize; i++)
          {
              if(list.get(i).getBusy() == 0){
                  System.out.println("FreeIndex:" +i);
                  return i; 
              }
                 
          }
          
          return flag;
      }
       private boolean isAddRsFull() {
        for (int i = 0; i < addRsSize; i++) {
            if (resStations.get(i).getBusy() == 0) {
                return false;
            }
        }

        return true;
    }
      
    private int hasFreeMulRs(ArrayList<ResStation> list) {
        int flag = -1;
        for (int i = addRsSize; i < list.size(); i++) {
            if (list.get(i).getBusy() == 0) {
                System.out.println("FreeIndex:" + i);
                return i;
            }

        }

        return flag;
    }
    
    private boolean isMulRsFull() {
        for (int i = addRsSize; i < resStations.size(); i++) {
            if (resStations.get(i).getBusy() == 0) {
                return false;
            }
        }

        return true;
    }

 

    
  
    
    public void printRat()
    {
        for(int i = 0; i < rat.length; i++)
            System.out.println("rat["+ i +"] " + rat[i]);
    }
    public void printArray(int[] array)
    {
         for(int i = 0; i < array.length; i++)
            System.out.println("r["+ i +"] " + array[i]);
    }
    /**
     * This method will search if there exist a dipachable reservation station in the condesened reservation Stations.
     * @param list
     * @return 
     */
    private boolean hasDipachableRs(ArrayList<ResStation> list)
    {
        boolean flag = false;
        if(!list.isEmpty())
        {
            
            for(ResStation res: list)
            {
                if(res.canRsDispach())
                {
                     flag = true;
                     return flag;           // return right away we find 1 readyInstruction.
                }
            }
        }
        return flag; // flag will be false if reach there.
    }
   /**
    *  This method will check for thee index of any instruction that is ready for dispatch.
    * @param list the arraylist of the reservation station to pass. //add/sub or mul/div RS
    * @return an array of the indexes of the  instruction ready for dispatch.  since one add or mult unit for this assignment
    * and RS0 has higher priority than RS1 to dispatch then no need to save the index of all the instruction that are ready to dispatch
    *  use that arrray to compare which instruction can go base on the status of the ALU.
    * --------if there is not an instruction to dispatch the list will be empty.----***---
    * 
    */
      private ArrayList<Integer> findIToDisp(ArrayList<ResStation> list) 
      {
          ArrayList<Integer> instrReadIndex = new ArrayList(list.size());        // arraylist that will hold only the index of the instruction that can dispatch
          for(int i =0; i < list.size(); i++)
          {
              if(list.get(i).getBusy() == 3){
                    instrReadIndex.add(i);
              }
 
          }
          System.out.println("index(es) of resevation ready to dispatch " + instrReadIndex.toString());
          return instrReadIndex;
      }
      /**
       * this will search for any reservation station waiting on operands.
       * @param list
       * @return an Arraylist containing the index of the instructions waiting on 
       * operand. this way I don't have to search the entire reservation station. 
       */
      private ArrayList<Integer> insWaitingOnOp(ArrayList<ResStation> list)
      {
          ArrayList<Integer> instrReadIndex = new ArrayList(list.size());        // arraylist that will hold only the index of the instruction that can dispatch
          for(int i =0; i < list.size(); i++)
          {
              if(list.get(i).getBusy() == 2){
                    instrReadIndex.add(i);
              }
 
          }
          System.out.println("index(es) of resevation waiting on operand " + instrReadIndex.toString());
          return instrReadIndex;
      
      }
      
     
      public void updateRs(ArrayList<ResStation> list)
      {
          for(ResStation res : list)
              res.updateRsEntry();
      }
      
//    private int getAvailableUnit(ArrayList<ArithmeticUnit> list) 
//    {
//        for(int i = 0; i< list.size(); i++)
//        {
//            if(!list.get(i).isUnitBusy())
//                return i;
//        }
//        
//        return -1;
//
//    }
}
