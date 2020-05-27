import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

/**
 * @description: RPCClient
 * @author: 郭木凯
 * @create_time 2020/5/27
 */
public class RPCClient implements AutoCloseable {

    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";

    public RPCClient() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.56.101");
        factory.setUsername("gmk");
        factory.setPassword("gmk");
        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    public static void main(String[] argv) {
        try (RPCClient fibonacciRpc = new RPCClient()) {
            for (int i = 0; i < 32; i++) {
                String i_str = Integer.toString(i);
                System.out.println(" [x] Requesting fib(" + i_str + ")");
                String response = fibonacciRpc.call(i_str);
                System.out.println(" [.] Got '" + response + "'");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String call(String message) throws IOException, InterruptedException {
        //corrId为唯一标识，用来判断应答的是哪个请求
        final String corrId = UUID.randomUUID().toString();
        //随机创建一个队列
        String replyQueueName = channel.queueDeclare().getQueue();
        //设置消息属性
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();
        //推送消息
        channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));

        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

        //接收应答的消息
        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            //判断应答的请求是否为当前设置的，是则处理，否则丢弃
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response.offer(new String(delivery.getBody(), "UTF-8"));
            }
        }, consumerTag -> {
        });

        String result = response.take();
        //手动取消订阅，防止队列同时被多个订阅而分发过多被丢弃的消息
        channel.basicCancel(ctag);
        return result;
    }

    public void close() throws Exception {
        connection.close();
    }
}
