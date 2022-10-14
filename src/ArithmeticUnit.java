
/**
 *  Group: Richelin And Ben Trnka;
 * @author written by: Richelin Metellus
 * @version 11/04/2015
 */
public class ArithmeticUnit {
    //******************************Nested Class **************************
    public static class Buffer {

        private boolean busyState;          // will be false then sub can dispatch at middle of cc4
        private int prevTag;                    // at beging of cc4 for my example the unit should tag will be rs0 before loading the sub at middle of cc4
        private Integer prevResult = null;            // not that result change really, this is just a copy in this caase

       
        public Buffer(boolean busyState, int prevTag, Integer prevResult) {
            this.busyState = busyState;
            this.prevTag = prevTag;
            this.prevResult = prevResult;
        }

        public boolean getPrevBusyState() {
            return busyState;
        }

        public int getPrevTag() {
            return prevTag;
        }

        public int getPrevResult() {
            return prevResult;
        }

    }
    //********************************End of Nested Class *********************************************
    public Buffer aluBuffer;
    private boolean unitBusy;       // will check if the unit is busy.
    private int freeAtCycle;         // the cycle the unit will be free at/ the same cycle at which it can start broadcast. 
    private int instrStart;         // the clock cycle at which the instruction has been dispatch for exection
    private int instrEnd;           // the end clock cycle at which the result is available
    private Instruction instr = null;         // instruction i will be using to load the unit with default destReg -1
    private Integer result = null;  // the result coulb be zero. and this is integer unit
    private  int tag = -1;      // the current RS instruction currently executing.
    private boolean broadcastReady; //
    private boolean prevBroadcastReady;         // for special case in which the unit is receiving an instruction(sub in my example) and broacation the instuction in(add
                                                // previous state of unit before new instruction dispach
 
    
    
     // latencies
     private final  int addLatency;
     private final  int subLatency;
     private final  int mulLatency;
     private final  int divLatency;
    
    
    public ArithmeticUnit()
    {
        this.unitBusy = false;
        this.freeAtCycle = 0;
        
        this.addLatency           = 2;                        // 2 cc for add
        this.subLatency           = 2;
        this.mulLatency           = 10;
        this.divLatency           = 40;
        
        aluBuffer = new Buffer(false, -1,null);
    
         
    }
    
    
  
  
    
    public int getFreeAtCycle(){return freeAtCycle;}
    public int getResult(){return result;}
    public int getInstrEnd(){return instrEnd;}
    public boolean isBroacastReady(){return broadcastReady;}
    public int getTag() {return tag;}
    public boolean getPrevBroadcastState(){return prevBroadcastReady;}
    
    
    
    
    private void setInstrEnd(int opCode) {
        switch (opCode) {
            case 0:
                instrEnd = instrStart + addLatency-1;
                break;
            case 1:
                instrEnd = instrStart + subLatency-1;
                break;

            case 2:
                instrEnd = instrStart + mulLatency-1;
                break;
            case 3:
                instrEnd = instrStart + divLatency-1;
                break;
        }
    }
    private void setResult(int result){this.result = result;}
    
    
    public boolean isUnitBusy()
    {
        return unitBusy;
    }
    
    private void setFreeAtCycle(){ this.freeAtCycle = instrEnd + 1;}
    
    public int execute()
    {
        int value = -1;
        if (instr != null) 
        {
            System.out.println("the Instruction to execute" + instr.toString());
            //System.out.println("Instruction Opcode " + instr.getOpcode());

            switch (instr.getOpcode()) {
                case 0:
                    value = instr.getSrc1() + instr.getSrc2();
                    setResult(value);
                    System.out.println("result in case add " +result);
                    break;
                    
                case 1:
                    value = instr.getSrc1() - instr.getSrc2();
                     setResult(value);
                    System.out.println("result in case sub " +result);
                    break;
                case 2:
                    value = instr.getSrc1() * instr.getSrc2();
                    setResult(value);
                    System.out.println("result in case mul " +result);
                    break;
                case 3:
                    try 
                    {
                        value = instr.getSrc1() / instr.getSrc2();
                        setResult(value);
                        System.out.println("result in case div " + result);
                    } catch (Exception e) {
                        System.out.println("Division by zero failed");
                    }
                    break;
            }
        }
        return value;
    }
    
    public void updateUnits(int currentClkcc)
    {
        if(instrStart == currentClkcc)
        {
            prevBroadcastReady = broadcastReady;
        }
        if(currentClkcc == freeAtCycle)
        {
            
            broadcastReady = true;
            unitBusy = false;
            
        }
 
//        //                      issue    ex(instrStart)  End       wb
//        //if add R2, R4, r6       1       2                3        4        
//        if(instrStart >= currentClkcc && currentClkcc <= instrEnd)
//            unitBusy = true;

        if(currentClkcc !=  freeAtCycle)
            broadcastReady = false;
        Buffer newBuffer = new Buffer(broadcastReady,tag,result);
        aluBuffer = newBuffer;
        newBuffer = null;
        
        
    }
    
    
    public void loadALU(int startcc,int tag, int op, int src1, int src2)
    {
        instr = new Instruction(op, tag, src1, src2);
        this.tag = tag;
        instrStart = startcc;
        setInstrEnd(op);
        setFreeAtCycle();           // set when the unit will be free cc4 for my 1st example
        
       //                      issue    ex(instrStart)  End       wb
        //if add R2, R4, r6       1       2                3        4        
        if(instrStart >= startcc && startcc <= instrEnd)
            unitBusy = true;
        
    }
    
    @Override
    public String toString()
    {
        String S ="";
        return S + "Busy: " + isUnitBusy() + "| Free at cc: " + freeAtCycle + "| Start" + instrStart
                + "|End" + instrEnd + "|UnitTag: " + tag + " prev Broadcast ready: "
                + prevBroadcastReady + "  Broadcast Ready  " + broadcastReady + " Result " + result; 
    }
}
