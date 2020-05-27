import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: Send
 * @author: 郭木凯
 * @create_time 2020/5/26
 */
public class Send {

    private final static String QUEUE_NAME="hello";

    public static void main(String[] args) {
        ConnectionFactory factory=new ConnectionFactory();
        //设置远程访问下的认证
        factory.setHost("192.168.56.101");
        factory.setPassword("gmk");
        factory.setUsername("gmk");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME,false,false,false,null);
            String sendMessage="Hello World!";
            //循环发送消息
            for (int i=0;i<10;i++){
                Thread.sleep(1000);
                channel.basicPublish("",QUEUE_NAME,null,sendMessage.getBytes());
                System.out.println("发送了消息："+sendMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
