import java.util.concurrent.CyclicBarrier;

public class PhaseRunner extends Thread {

  private int numThreads;
  private int numSkiLifts;
  private String hostname;
  private int port;
  private int startTime;
  private int endTime;
  private CyclicBarrier barrier;
  private int numRuns;
  private int numSkiers;

  public PhaseRunner(int numThreads, int numSkiLifts, String hostname, int port, int startTime,
      int endTime, CyclicBarrier barrier, int numRuns, int numSkiers) {
    this.numThreads = numThreads;
    this.numSkiLifts = numSkiLifts;
    this.hostname = hostname;
    this.port = port;
    this.startTime = startTime;
    this.endTime = endTime;
    this.barrier = barrier;
    this.numRuns = numRuns;
    this.numSkiers = numSkiers;
  }

  // Override run method
  @Override
  public void run(){
    for(int i = 0; i < numThreads; i++){
      int idStart = i * (numSkiers/numThreads) + 1;
      int idEnd = (i+1) * (numSkiers/numThreads);
      new SkierThread(hostname, port, idStart, idEnd, startTime, endTime, numSkiers,numSkiLifts, numRuns, barrier).start();

    }
  }
}
