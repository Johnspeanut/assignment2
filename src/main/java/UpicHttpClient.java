import edu.emory.mathcs.backport.java.util.Arrays;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import com.google.gson.Gson;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.List;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;

// HttpClient for UPic Ski
public class UpicHttpClient {
  private static HttpClient httpClientInstance;

  // gson converts pojo to json string
  private static Gson gson = new Gson();

  //HttpClient uses to a singleton pattern
  public static HttpClient getInstance(){
    if(httpClientInstance == null){
      httpClientInstance = HttpClient.newBuilder().build();
    }
    return httpClientInstance;
  }

  public static void postLiftRide(String url, int time, int liftID)
      throws IOException, InterruptedException {
    // create lift ride pojo and convert json string
    LiftRide liftRide = new LiftRide(time, liftID);
    String body = gson.toJson(liftRide);

    //construct request, and it, and get response code
    HttpRequest postRequest = HttpRequest.newBuilder().uri(URI.create(url)).POST(BodyPublishers.ofString(body)).build();
    HttpResponse<String> response = UpicHttpClient.getInstance().send(postRequest, HttpResponse.BodyHandlers.ofString());
    int responseCode = response.statusCode();

    //check response code and log any 4xx or 5xx errors
    if(responseCode == 201 || responseCode == 200){
      HttpCounter.successInc();
    }else if(responseCode == 404){
      HttpCounter.failInc();
    }

  }

}
