import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class PhaseSkiersClient {
  private int numThreads;
  private int numSkiers;
  private int numSkiLifts;
  private int numRuns;
  private final String hostname;
  private final int port;
  static CyclicBarrier phaseOneBarrier;
  static CyclicBarrier phaseTwoBarrier;
  static CyclicBarrier phaseThreeBarrier;

  public PhaseSkiersClient(int numThreads,int numSkiLifts,
      int numRuns, String hostname, int port, int numSkiers) {
    this.numThreads = numThreads;
    this.numSkiers = numSkiers;
    this.numSkiLifts = numSkiLifts;
    this.numRuns = numRuns;
    this.hostname = hostname;
    this.port = port;
    PhaseSkiersClient.phaseOneBarrier = new CyclicBarrier(numThreads/4 + 1);
    PhaseSkiersClient.phaseTwoBarrier = new CyclicBarrier(numThreads + 1);
    PhaseSkiersClient.phaseThreeBarrier = new CyclicBarrier(numThreads/4 + 1);
  }

  public void start() {
    // launch phase 1: 10% of runs/skier; 1/4 of total threads;start at min 1 and end at min 90.
    launchPhase((float) 0.1, 4, 1, 90, PhaseSkiersClient.phaseOneBarrier);
    // wait for phase one to be 10% completed
    while (phaseOneBarrier.getNumberWaiting() < Math.round((float) (numThreads / 4)) * 0.1) {
    }

    // launch phase 2: 80% of runs/skier; max number threads; start at 91 and end at min 360
    launchPhase((float) 0.8, 1, 91, 360, PhaseSkiersClient.phaseTwoBarrier);

    // wait for phase two to be 10% completed
    while (phaseTwoBarrier.getNumberWaiting() < Math.round((float) numThreads * 0.1)) {
    }

    // launch phase 3:10% of runs/skier; 1/4 number threads;start at min 361 and end at min 420;
    launchPhase((float) 0.1, 4, 91, 360, PhaseSkiersClient.phaseThreeBarrier);

    try {
      phaseOneBarrier.await();
    } catch (BrokenBarrierException | InterruptedException e) {
      e.printStackTrace();
    }

    try {
      phaseTwoBarrier.await();
    } catch (BrokenBarrierException | InterruptedException e) {
      e.printStackTrace();
    }

    try {
      phaseThreeBarrier.await();
    } catch (BrokenBarrierException | InterruptedException e) {
      e.printStackTrace();
    }
  }



  private void launchPhase(float runScaleFactor, int threadScaleFactor, int startTime, int endTime, CyclicBarrier barrier){
    int phaseNumRuns = (int) Math.round(runScaleFactor * numRuns);
    int phaseNumThreads = numThreads / threadScaleFactor;
    PhaseRunner phaseRunner = new PhaseRunner(phaseNumThreads,  numSkiLifts, hostname,port,startTime,endTime,barrier,phaseNumRuns,numSkiers);
    phaseRunner.start();


  }
}
