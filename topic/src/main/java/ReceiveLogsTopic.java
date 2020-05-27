import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: ReceiveLogsTopic
 * @author: 郭木凯
 * @create_time 2020/5/27
 */
public class ReceiveLogsTopic {

    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("192.168.56.101");
        factory.setUsername("gmk");
        factory.setPassword("gmk");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //声明交换机类型为Topic
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        String queueName = channel.queueDeclare().getQueue();
        String condition="jack.critical";
        System.out.println("当前匹配的条件："+condition);
        //topic下的绑定
        channel.queueBind(queueName,EXCHANGE_NAME,condition);
        //消息处理
        DeliverCallback deliverCallback=(consumeTag,delivery)->{
            String message=new String(delivery.getBody(),"UTF-8");
            System.out.println("接收到了消息："+message);
        };
        channel.basicConsume(queueName,true,deliverCallback,consumerTag->{});
    }
}
