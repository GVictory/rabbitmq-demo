import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: ReceiveLogsDirect
 * @author: 郭木凯
 * @create_time 2020/5/26
 */
public class ReceiveLogsDirect {

    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("192.168.56.101");
        factory.setUsername("gmk");
        factory.setPassword("gmk");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //声明类型为Direct的队列
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        String queueName = channel.queueDeclare().getQueue();
        //定义类型为warning的消息
        String severity = "tip";
        System.out.println("只接收："+severity);
        //只接收为warning的消息
        channel.queueBind(queueName,EXCHANGE_NAME,severity);
        //消息处理方法
        DeliverCallback deliverCallback=(consumerTag,delivery)->{
            String message=new String(delivery.getBody(),"UTF-8");
            System.out.println("接收了消息："+message);
        };
        //监听队列
        channel.basicConsume(queueName,true,deliverCallback,consumerTag->{});
    }
}
