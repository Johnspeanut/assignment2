import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;

public class SkierThread  extends Thread{
  private final String hostname;
  private final int port;
  private int startTime;
  private int endTime;
  private int numSkierLifts;
  public CyclicBarrier barrier;
  private int numSkiers;
  private int numRunsForPhase;

  public SkierThread(String hostname, int port, int idStart, int idEnd, int startTime, int endTime, int numSkiers, int numSkierLifts, int numRuns, CyclicBarrier barrier){
    this.hostname = hostname;
    this.port = port;
    this.startTime = startTime;
    this.endTime = endTime;
    this.barrier = barrier;
    this.numSkierLifts = numSkierLifts;
    this.numSkiers = idEnd - idStart + 1;
    this.numRunsForPhase = numRuns * numSkiers;
  }

  @Override
  public void run(){
    for(int i = 1; i <= numRunsForPhase; i++){
      int currentSkierID = ThreadLocalRandom.current().nextInt(numSkiers);
      try{
        String url = "http://" + this.hostname + ":" + this.port + "/assignment2_war_exploded"+"/skier/" + 21 + "/seasons/" + 21 + "/days/"+21+"/skiers/" + currentSkierID;
        // random time from range for phase
        int randomTime = ThreadLocalRandom.current().nextInt(startTime, endTime+1);
        // random lift
        int randomLift = ThreadLocalRandom.current().nextInt(1, numSkiers+1);
        // POST request
        UpicHttpClient.postLiftRide(url, randomTime,randomLift);
      }catch (Exception e){
        e.printStackTrace();
      }

      try{
        barrier.await();
      }catch (InterruptedException e){
        e.printStackTrace();
      }catch (BrokenBarrierException e){
        e.printStackTrace();
      }
    }
  }
}
