import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.time.Duration;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

public class Client {

  private static final HttpClient httpClient = HttpClient.newBuilder()
      .version(HttpClient.Version.HTTP_2)
      .connectTimeout(Duration.ofSeconds(10))
      .build();
  final static private int NUMTHREADS = 50;
  private int count = 0;

  synchronized public void inc(){
    count++;
  }

  public int getVal(){
    return this.count;
  }

  public static void main(String[] args) throws Exception {

    final Client counter = new Client();
    CountDownLatch completed = new CountDownLatch(NUMTHREADS);
    long start = System.currentTimeMillis();
    // 1. Get
//    for(int i = 0; i < NUMTHREADS; i++){
//      String url = "http://localhost:8080/assignment2_war_exploded/skiers/" + i;
//      Runnable thread = ()->{
//        HttpRequest request = HttpRequest.newBuilder()
//            .GET()
//            .uri(URI.create(url))
//            .setHeader("User-Agent", "Java 11 HttpClient Bot")
//            .build();
//        CompletableFuture<HttpResponse<String>> response =
//            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
//        String result = null;
//        try {
//          result = response.thenApply(HttpResponse::body).get(10, TimeUnit.SECONDS);
////          result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
//          System.out.println(result);
//          System.out.println("Current thread id:"+Thread.currentThread().getId());
//        } catch (InterruptedException e) {
//          e.printStackTrace();
//        } catch (ExecutionException e) {
//          e.printStackTrace();
//        } catch (TimeoutException e) {
//          e.printStackTrace();
//        }
//        completed.countDown();
//      };
//      new Thread(thread).start();
//    }

    //2.Post
    System.out.println("***********************************************************************");
    for(int i = 0; i < NUMTHREADS; i++){
      String url = "http://localhost:8080/assignment2_war_exploded/skiers/" + i;
      Runnable thread = ()->{

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .setHeader("Content-Type", "application/json")
            .POST(BodyPublishers.ofString("john"))
            .build();
        CompletableFuture<HttpResponse<String>> response =
            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        String result = null;
        try {
          result = response.thenApply(HttpResponse::body).get(10, TimeUnit.SECONDS);
//          result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
          System.out.println(result);
          System.out.println("Current thread id:"+Thread.currentThread().getId());
        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (ExecutionException e) {
          e.printStackTrace();
        } catch (TimeoutException e) {
          e.printStackTrace();
        }
        completed.countDown();
      };
      new Thread(thread).start();
    }

    completed.await();
    long spendTime = System.currentTimeMillis() - start;
    System.out.println("The time is " + spendTime);
    System.out.println("Value should be equal to " + NUMTHREADS + " It is: " + counter.getVal());


//    HttpRequest request = HttpRequest.newBuilder()
//        .GET()
//        .uri(URI.create("http://localhost:8080/assignment2_war_exploded/skiers/1"))
//        .setHeader("User-Agent", "Java 11 HttpClient Bot")
//        .build();
//
//    CompletableFuture<HttpResponse<String>> response =
//        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
//
//    String result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
//
//    System.out.println(result);

  }

}