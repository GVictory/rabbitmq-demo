import com.rabbitmq.client.*;


/**
 * @description: RPCServer
 * @author: 郭木凯
 * @create_time 2020/5/27
 */
public class RPCServer {

    private static final String RPC_QUEUE_NAME = "rpc_queue";

    //业务处理逻辑
    private static int fib(int n) {
        if (n == 0) return 0;
        if (n == 1) return 1;
        return fib(n - 1) + fib(n - 2);
    }

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.56.101");
        factory.setUsername("gmk");
        factory.setPassword("gmk");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            //声明队列
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);

            //清空队列内容
            channel.queuePurge(RPC_QUEUE_NAME);

            //同时只处理一个消息
            channel.basicQos(1);

            System.out.println("等待接收请求。。。");

            Object monitor = new Object();
            //消息处理
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                //定义消息属性，用于响应
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                String response = "";

                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    int n = Integer.parseInt(message);
                    System.out.println(" [.] fib(" + message + ")");
                    response += fib(n);
                } catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                } finally {
                    //返回响应信息
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    //手动反馈处理成功
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    // 防止线程直接结束
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };

            //消费消息并开启手动确认反馈
            channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));
            // 防止线程直接结束
            while (true) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
