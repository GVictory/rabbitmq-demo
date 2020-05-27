import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: EmitLogTopic
 * @author: 郭木凯
 * @create_time 2020/5/27
 */
public class EmitLogTopic {

    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("192.168.56.101");
        factory.setUsername("gmk");
        factory.setPassword("gmk");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //声明类型为Topic的交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        String message="发送的消息：";
        String[] list={"#","kern.*","*.critical","jack.*","jack.critical","kern.json","jack.json"};
        //循环发送消息
        for (String simple:list){
            channel.basicPublish(EXCHANGE_NAME,simple,null,(message+simple).getBytes("UTF-8"));
        }
    }
}
