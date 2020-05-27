import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: Worker
 * @author: 郭木凯
 * @create_time 2020/5/26
 */
public class Worker {

    private static  final String QUEUE_NAME="Task";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory=new ConnectionFactory();
        factory.setUsername("gmk");
        factory.setPassword("gmk");
        factory.setHost("192.168.56.101");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //开启持久化，将消息保存到磁盘
        boolean durable = true;
        //先声明队列，防止发送者晚于接收者启动而未创建队列发生错误
        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
        //告诉MQ只同时处理1个消息，未结束前不要分发消息到此处
        channel.basicQos(1);
        //消息接收实现
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("接收到了消息：" + message);
            //模拟耗时操作
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                System.out.println("处理了消息：" + message);
                //消息处理回馈
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
            }
        };
        //ack接收自动反馈机制，true为自动确认，false为手动确认
        boolean autoAck=false;
        //消费消息
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> {
        });
    }
}
