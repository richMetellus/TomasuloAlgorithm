




/**
 * Group: Richelin and Ben Trnka
 * @version 11/02/2017
 * @author Ben Trnka
 * 
 *          
 */
public class ResStation {
    private int rsOpcode =-1;
    private Integer Vj= null;   // changed by Rich because the operand can be zero
    private Integer Vk = null;  // changed by Rich because the operand can be zero
    private int Qj =-1, Qk = -1;
    private int busyStatus=0;
    private int resType=0; 
    private int rstag;       // integer to hold the fixed value of the rs 0-4.
    private Integer result;
    private int cyclecomplete; // check if cyle you're on matches cycleComplete
   
	//busyStatus key: 0 = open, 1 = newly active, 2 = waiting on operand
	// 3 = ready for dispatch 4 = currently dispatching 
	//resType corresponds to either add/sub units (resType = 1) or multi/divide (resType =3)
	//Constructor for a resStation, only requires the resType to be updated on creation, the
	//rest defaults to initial values of 0.
    public ResStation(int resType){this.resType = resType;}
    public ResStation(int busyStatus,int rsOpcode,int resType)
    {
        this.busyStatus = busyStatus;
        this.rsOpcode = rsOpcode;
        this.resType = resType;
    }

//    public ResStation(int rsOpcode, Integer Vj, Integer Vk, int busyStatus, int resType)
//    {
//        this.rsOpcode = rsOpcode;
//        this.Vj = Vj;
//        this.Vk = Vk;
//        this.busyStatus = busyStatus;
//        this.resType = resType;
//    }
    public int getrsOpcode(){return rsOpcode;}
    
    public int getVj(){return Vj;}
    public int getVk(){return Vk;}
    public int getQj(){return Qj;}
    public int getQk(){return Qk;}
    public int getresType(){return resType;}
    public int getBusy(){return busyStatus;}
    
    public void setrsOpcode(int rsOpcode){this.rsOpcode = rsOpcode;}
    public void setVj(Integer Vj){this.Vj = Vj;}
    public void setVk(Integer Vk){this.Vk = Vk;}
    public void setQj(int Qj){this.Qj = Qj;}
    public void setQk(int Qk){this.Qk = Qk;}
    public void setResType(int resType){this.resType = resType;}
    public void setBusyState(int busy){this.busyStatus = busy;}
    public void setResult(Integer result){this.result = result;}
    
    
    public String toString()
    {
        String sInstr = ":::::";
        return sInstr + strBusyStatus() + " " + getBusy() + "| " + Instruction.translateOp(rsOpcode) + " | "  +Vj + " | " + Vk
               + " | "  + Qj  +" | " + Qk;
    }
    
    
    /**
     * 
     *(Rich) method needed to check if should change the state of the res station if true.
     * @return true if the operand data are available and ready for dispatch
     */
    public boolean isOperandReady()
    {
        // operand are not tagged and right values were set from previous broadcast cycle.
        return( Vj != null && Vk != null);
    }
   
    public void clearRsEntry()
    {
        setBusyState(0);         // if not busy the then i can overwrite the value
        setrsOpcode(-1);
        setVk(null);
        setQk(-1);
        setVj(null);
        setQj(-1);
        setResult(null);
        
    }
    
    public boolean canRsDispach(){ return busyStatus == 3;}
    
    public boolean strBusyStatus(){ return busyStatus != 0 ;}
    
    public void updateRsEntry()
    {
        if(isOperandReady() && busyStatus != 4) // don't update if the instruction currently dispaching
            setBusyState(3); 
    }
    
    
}
