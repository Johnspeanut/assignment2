import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;


public class ClientEntrance {

  public static void main(String[] args) {
    int numSkiLifts = 40;
    int numRuns = 10;
    String hostname = "localhost";
    int port = 8080;
    int numSkiers = 100000;

   // Part 1
    int[] threads = new int[]{32,64,128,256};
    for(int numThread:threads){
      PhaseSkiersClient phaseSkiersClient = new PhaseSkiersClient(numThread,numSkiLifts,
          numRuns, hostname, port, numSkiers);
      long begTimeStamp = System.currentTimeMillis();
        phaseSkiersClient.start();
      long endTimeStamp = System.currentTimeMillis();
      System.out.println("Wall time: " + (float) (endTimeStamp - begTimeStamp) / 1000 + " seconds");
      System.out.println("Num Success Requests: " + HttpCounter.getSuccessRequests());
      System.out.println("Num Failed Requests: " + HttpCounter.getFailureRequests());
    }

  }


}
