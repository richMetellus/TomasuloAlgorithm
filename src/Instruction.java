/**
 * Group: Ben and Richelin
 * @author Richelin Metellus
 */
public class Instruction {
   
    private int opcode;
    private int dstReg;             // destination register
    private int src1, src2;         // index into the registerFile for source operand
    
    public Instruction(int opcode, int dstReg, int src1, int src2)
    {
        this.opcode = opcode;
        this.dstReg = dstReg;
        this.src1   = src1;
        this.src2   = src2;
    }
    public int getOpcode(){return opcode;}
    public int getDstReg(){return dstReg;}
    public int getSrc1(){return src1;}
    public int getSrc2(){return src2;}
    
    public void setOpcode(int opcode){this.opcode = opcode;}
    public void setDstReg(int dstReg){this.dstReg = dstReg;}
    public void setSrc1(int src1){this.src1 = src1;}
    public void setSrc2(int src2){this.src2 = src2;}
    
  //************************************************ Utility Method ***************************************  
    public String toString(){
        
        String sInstr = "";
        return sInstr +" " +translateOp(opcode) + " " + "R"+dstReg + "," + "R"+src1 + "," + "R"+src2;
                
    }
    /**
     * 
     * @param opcode the hard coded integer value of the MIPS instruction in the file
     * @return  a string representing the explicit MIPS value, e.g add, sub
     */
    
    
    public static String translateOp(int opcode)
    {
        switch (opcode) {
            case 0:
                return "Add";
            case 1:
                return "Sub";
            case 2:
                return "Mult";
            case 3:
                return "Div";
            default:
                break;
        }
        return "Nothing"; // to prevent compile whining
    }
    
    
}
