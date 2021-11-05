import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.netty.channel.pool.ChannelPool;
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
    final  String QUEUE_NAME = "threadExQ";
    final int NUM_THREAD =10;
    response.setContentType("application/json");

    String[] urlPathList = request.getPathInfo().split("/");
    if(!isSkierPostUrlValid(urlPathList)){
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      String responseJSON = new Gson().toJson(new String("The URL path is invalid"));
      response.getWriter().write(responseJSON);
      return;
    }

    try{
      int resortID = Integer.parseInt(urlPathList[1]);
      int season = Integer.parseInt(urlPathList[3]);
      int day = Integer.parseInt(urlPathList[5]);
      int skierID = Integer.parseInt(urlPathList[7]);

      SkierServletPostResponse postResponse = new SkierServletPostResponse(resortID, season, day, skierID);

      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("ec2-100-26-208-245.compute-1.amazonaws.com");


      final Connection conn = factory.newConnection();
      Runnable runnable = new Runnable() {
        @Override
        public void run() {
          try{
            Channel channel = conn.createChannel();
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.basicPublish("", QUEUE_NAME, null, new Gson().toJson(postResponse).getBytes());
            channel.close();
          }catch (IOException | TimeoutException e){
            e.printStackTrace();
          }
        }
      };

      Thread[] threads = new Thread[NUM_THREAD];
      for(int i = 1; i<= NUM_THREAD; i++){
        threads[i] = new Thread(runnable);
      }
      for(Thread thread:threads){
        thread.start();
      }
      for(Thread thread:threads){
        thread.join();
      }
      conn.close();

      // response to the user
      response.setStatus(HttpServletResponse.SC_OK);
//      LiftRide liftRide = new Gson().fromJson(getBodyContent(request), LiftRide.class);
//      response.getWriter().write(new Gson().toJson(liftRide));
    }catch (NumberFormatException e){
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//      String responseJSON = new Gson().toJson(new MessageResponse("The type of the input parameter doesn't match"));
//      response.getWriter().write(responseJSON);
    }catch(IllegalArgumentException e){
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//      String responseJSON = new Gson().toJson(new MessageResponse(e.getMessage()));
//      response.getWriter().write(responseJSON);
    }catch (InterruptedException | TimeoutException e){
      e.printStackTrace();
    }




//    BufferedReader reader = request.getReader();
//    String jsonString = "";
//    try{
//      for(String line;(line = reader.readLine()) != null; jsonString += line);
//    }catch (IOException e){
//      e.printStackTrace();
//    }
//
//    response.setContentType("application/json");
//
//    String urlPath = request.getPathInfo();
//    if(!isBodyValid(jsonString)){
//      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//      response.getWriter().write("{\"message\": \"invalid input\"}");
//    }else if (!isUrlValid(urlPath)) {
//      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//      response.getWriter().write("{\"message\": \"not found\"}");
//    } else {
//      response.setStatus(HttpServletResponse.SC_CREATED);
//      response.getWriter().write("{\"message\": \"ok\"}");
//    }

  }





  private boolean isSkierPostUrlValid(String[] urlPathList) {
    if (urlPathList.length != 8) {
      return false;
    }

    return urlPathList[2].equals("seasons") && urlPathList[4].equals("days")
        && urlPathList[6].equals("skiers");
  }

  private String getBodyContent(HttpServletRequest req) throws IOException {
    BufferedReader reqBodyBuffer = req.getReader();
    StringBuilder reqBody = new StringBuilder();
    String line;
    while ((line = reqBodyBuffer.readLine()) != null) {
      reqBody.append(line);
    }

    return reqBody.toString();
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
