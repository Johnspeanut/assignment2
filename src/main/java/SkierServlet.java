import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "SkierServlet", value = "/SkierServlet")
public class SkierServlet extends HttpServlet {

  private String msg;
  private final static String QUEUE_NAME = "threadExQ";
  private final static int NUM_MESSAGES_PER_THREAD =10;
  ConnectionFactory factory = new ConnectionFactory();
  {

    factory.setHost("localhost");
  }

  public void inti() throws ServletException {
    //Initialization
    msg = "Hello World";
  }

  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
//    res.setContentType("text/plain");
    String urlPath = req.getPathInfo();

    res.setContentType("text/html");

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    PrintWriter out = res.getWriter();
    res.getWriter().write("It works!");
    out.println("<h1>" + msg + "<h1>");

//    // check we have a URL!
//    if (urlPath == null || urlPath.isEmpty()) {
//      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
//      res.getWriter().write("missing paramterers");
//      return;
//    }
//
//    String[] urlParts = urlPath.split("/");
//    // and now validate url path and return the response status code
//    // (and maybe also some value if input is valid)
//
//    if (!isUrlValid(urlParts)) {
//      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
//    } else {
//      res.setStatus(HttpServletResponse.SC_OK);
//      // do any sophisticated processing with urlParts which contains all the url params
//      // TODO: process url params in `urlParts`
//      res.getWriter().write("It works!");
//    }
  }

  private boolean isUrlValid(String[] urlPath) {
    // TODO: validate the request url path according to the API spec
    // urlPath  = "/1/seasons/2019/day/1/skier/123"
    // urlParts = [, 1, seasons, 2019, day, 1, skier, 123]
    return true;
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      final Connection conn = factory.newConnection();
      Runnable runnable = new Runnable() {
        @Override
        public void run() {
          try {
            // channel per thread
            Channel channel = conn.createChannel();
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            for (int i = 0; i < NUM_MESSAGES_PER_THREAD; i++) {
              String message = "Here's a message " + Integer.toString(i) + response.toString();
              channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            }
            channel.close();
            System.out.println(" [All Messages  Sent '");
          } catch (IOException | TimeoutException ex) {
            Logger.getLogger(SendMT.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      };
      // start threads and wait for completion
      Thread t1 = new Thread(runnable);
      Thread t2 = new Thread(runnable);
      t1.start();
      t2.start();
      t1.join();
      t2.join();
      // close connection
      conn.close();

    }catch (TimeoutException | InterruptedException e) {
      e.printStackTrace();
    }

  }
}
