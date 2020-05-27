import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: ReceiveLogs
 * @author: 郭木凯
 * @create_time 2020/5/26
 */
public class ReceiveLogs {

    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.56.101");
        factory.setUsername("gmk");
        factory.setPassword("gmk");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //声明交换机名称和类型为扇形（对所有的绑定队列都推送消息）
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        //随机创建一个队列
        String queue = channel.queueDeclare().getQueue();
        //将队列绑定到交换机中
        channel.queueBind(queue, EXCHANGE_NAME, "");
        //消息接收后的处理
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String receiverMessage = new String(delivery.getBody(), "UTF-8");
            System.out.println("接受到了消息：" + receiverMessage);
        };
        //对队列进行监听
        channel.basicConsume(queue, true, deliverCallback, consumerTag -> {
        });
    }
}
