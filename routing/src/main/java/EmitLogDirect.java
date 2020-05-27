import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: EmitLogDirect
 * @author: 郭木凯
 * @create_time 2020/5/26
 */
public class EmitLogDirect {

    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.56.101");
        factory.setUsername("gmk");
        factory.setPassword("gmk");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //声明类型为Direct的路由
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //类型为warning
        String[] severities={"warning","danger","tip"};
        String message = "一条日志消息：";
        //循环发布各种类型的消息
        for (int i=0;i<severities.length;i++){
            channel.basicPublish(EXCHANGE_NAME, severities[i], null, (message+severities[i]).getBytes("UTF-8"));
            System.out.println("发送了消息：" + message+severities[i]);
        }
    }
}
