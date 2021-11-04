import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "SkierServlet", value = "/SkierServlet")
public class SkierServlet extends HttpServlet {

//  private String msg;
//  private final static String QUEUE_NAME = "threadExQ";
//  private final static int NUM_MESSAGES_PER_THREAD =10;
//  ConnectionFactory factory = new ConnectionFactory();
//  {
//
//    factory.setHost("localhost");//replace with ec2 ip and set port.
//  }
//
//  public void inti() throws ServletException {
//    //Initialization
//    msg = "Hello World";
//  }

  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    res.setContentType("text/plain");
    String urlPath = req.getPathInfo();


    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    res.getWriter().write("It works!");

  }

//  private boolean isUrlValid(String[] urlPath) {
//    // TODO: validate the request url path according to the API spec
//    // urlPath  = "/1/seasons/2019/day/1/skier/123"
//    // urlParts = [, 1, seasons, 2019, day, 1, skier, 123]
//    return true;
//  }

//  protected void doPost(HttpServletRequest request, HttpServletResponse response)
//      throws ServletException, IOException {
//    try {
//      final Connection conn = factory.newConnection();
//      Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//          try {
//            // channel per thread
//            Channel channel = conn.createChannel();
//            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
//            for (int i = 0; i < NUM_MESSAGES_PER_THREAD; i++) {
//              String message = "Here's a message " + Integer.toString(i) + response.toString();
//              channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
//              // more optiomns
//            }
//            channel.close();
//            System.out.println(" [All Messages  Sent '");
//          } catch (IOException | TimeoutException ex) {
//            Logger.getLogger(SendMT.class.getName()).log(Level.SEVERE, null, ex);
//          }
//        }
//      };
//      // start threads and wait for completion
//      Thread t1 = new Thread(runnable);
//      Thread t2 = new Thread(runnable);
//      t1.start();
//      t2.start();
//      t1.join();
//      t2.join();
//      // close connection
//      conn.close();
//
//    }catch (TimeoutException | InterruptedException e) {
//      e.printStackTrace();
//    }
//
//  }








  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    BufferedReader reader = request.getReader();
    String jsonString = "";
    try{
      for(String line;(line = reader.readLine()) != null; jsonString += line);
    }catch (IOException e){
      e.printStackTrace();
    }

    response.setContentType("application/json");

    String urlPath = request.getPathInfo();
    if(!isBodyValid(jsonString)){
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write("{\"message\": \"invalid input\"}");
    }else if (!isUrlValid(urlPath)) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("{\"message\": \"not found\"}");
    } else {
      response.setStatus(HttpServletResponse.SC_CREATED);
      response.getWriter().write("{\"message\": \"ok\"}");
    }

  }









  private boolean isUrlValid(String urlPath) {
    // urlPath  = "/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}"
    // urlPath  = "/{skierID}/vertical"
//    Pattern skierApiPattern = Pattern.compile("^/([\\d]+)/seasons/([\\d]+)/days/([\\d]+)/skiers/([\\d]+)$");
//    Pattern verticalApiPattern = Pattern.compile("^/([\\d]+)/vertical$");
//
//    Matcher skierApiMatches = skierApiPattern.matcher(urlPath);
//    Matcher verticalApiMatches = verticalApiPattern.matcher(urlPath);
//    boolean skierApiFind = skierApiMatches.find();
//    boolean verticalApiFind = verticalApiMatches.find();
//    if (!skierApiFind && !verticalApiFind) {
//      return false;
//    }
//    if (skierApiFind) {
//      int day = Integer.parseInt(skierApiMatches.group(3));
//      if (day > 366 || day < 1) {
//        return false;
//      }
//    }
    return true;
  }

  private boolean isBodyValid(String jsonStr) {
//    if (jsonStr.isEmpty()) {
//      return false;
//    }
//    try {
//      Gson gson = new Gson();
//      LiftRide liftRide = gson.fromJson(jsonStr, LiftRide.class);
//      if (liftRide == null) {
//        return false;
//      }
//    }
//    catch(Exception e){
//      return false;
//    }

    return true;
  }
}
