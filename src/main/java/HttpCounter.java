public class HttpCounter {
  private static int successRequests = 0;
  private static int failureRequests = 0;

  public static synchronized void successInc(){
    HttpCounter.successRequests++;
  }

  public static synchronized void failInc(){
    HttpCounter.failureRequests++;
  }

  public static int getSuccessRequests() {
    return successRequests;
  }

  public static int getFailureRequests() {
    return failureRequests;
  }

  public static  synchronized void reset(){
    HttpCounter.successRequests = 0;
    HttpCounter.failureRequests = 0;
  }
}
