
/**
 *
 * @author Richelin Metellus
 */
public class Clock {
    int clockCycleTime;
    static Clock clockPtr = null;
    
    public Clock()
    {
        this.clockCycleTime = 0;
    }
    
    public int getClockCycleTime(){return clockCycleTime;}
    public void incrementClock(){
        clockCycleTime++;
    }
    
    //This is the algorythm for 1 cycle (each cycle)
    public void runCycle(){
        /*
        issueInstr{}
        update units
            if ready, broadcast(a)
                send calculated values to rs tags
                update RAT
        clockCycleTime++;
        */
    }
}
