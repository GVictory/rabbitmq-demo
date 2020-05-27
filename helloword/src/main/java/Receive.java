import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: Receive
 * @author: 郭木凯
 * @create_time 2020/5/26
 */
public class Receive {

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        //设置远程访问下的认证
        factory.setUsername("gmk");
        factory.setPassword("gmk");
        factory.setHost("192.168.56.101");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //先声明队列，防止发送者晚于接收者启动而发生错误
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //消息接收实现
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("接收到了消息：" + message);
        };
        //消费消息
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });
    }
}
