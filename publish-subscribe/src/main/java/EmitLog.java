import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: EmitLog
 * @author: 郭木凯
 * @create_time 2020/5/26
 */
public class EmitLog {

    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) {
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("192.168.56.101");
        factory.setUsername("gmk");
        factory.setPassword("gmk");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            //设置交换机名称和类型
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
            String message="发送的消息";
            //将消息传给交换机
            channel.basicPublish(EXCHANGE_NAME,"",false,null,message.getBytes("UTF-8"));
            System.out.println("发送了消息："+message);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
