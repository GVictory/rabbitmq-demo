import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: NewTask
 * @author: 郭木凯
 * @create_time 2020/5/26
 */
public class NewTask {

    private final static String QUEUE_NAME="Task";

    public static void main(String[] args) {
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("192.168.56.101");
        factory.setUsername("gmk");
        factory.setPassword("gmk");
        Connection connection = null;
        try {
            connection = factory.newConnection();
            Channel channel = connection.createChannel();
            //开启持久化，将消息保存到磁盘
            boolean durable = true;
            channel.queueDeclare(QUEUE_NAME,durable,false,false,null);
            String message="需要处理的消息：";
            for (int i=0;i<10;i++){
                String publish=message+(i+1);
                //MessageProperties.PERSISTENT_TEXT_PLAIN表示持久化消息
                channel.basicPublish("",QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN,publish.getBytes());
                System.out.println("发送了消息："+publish);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
