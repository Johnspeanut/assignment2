import com.rabbitmq.client.Channel;

public interface ChannelPool {
  void init();

  Channel take() throws InterruptedException;

  boolean add(Channel channel);
}