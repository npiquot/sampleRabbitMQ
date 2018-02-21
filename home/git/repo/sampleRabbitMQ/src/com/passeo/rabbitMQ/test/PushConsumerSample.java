package com.passeo.rabbitMQ.test;

import com.rabbitmq.client.ConnectionFactory;

public class PushConsumerSample extends com.rabbitmq.client.DefaultConsumer {
	private static String userName;
	private static String password;
	private static String virtualHost;
	private static String hostName;
	private static int portNumber;
	private static com.rabbitmq.client.Channel channel;
	private static String queueName;

	public PushConsumerSample(com.rabbitmq.client.Channel channel) {
		super(channel);
	}

	public static void main(String[] args) {
		boolean autoAck = false;

		userName = "guest";
		password = "guest";
		virtualHost = "/";
		hostName = "192.168.99.101";
		portNumber = 5672;
		queueName = "createVM";

		com.rabbitmq.client.Connection conn = null;

		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setUsername(userName);
			factory.setPassword(password);
			factory.setVirtualHost(virtualHost);
			factory.setHost(hostName);
			factory.setPort(portNumber);

			conn = factory.newConnection();

			channel = conn.createChannel();

			System.out.println("waiting for messages");

			channel.basicConsume(queueName, autoAck, "myConsumerTag", new com.rabbitmq.client.DefaultConsumer(channel) {

				public void handleDelivery(String consumerTag, com.rabbitmq.client.Envelope envelope,
						com.rabbitmq.client.AMQP.BasicProperties properties, byte[] body) throws java.io.IOException {

					String routingKey = envelope.getRoutingKey();
					String contentType = properties.getContentType();
					long deliveryTag = envelope.getDeliveryTag();

					PushConsumerSample.channel.basicAck(deliveryTag, false);
					System.out.println("received a new message");
					System.out.println("correldID " + properties.getCorrelationId());

					System.out.println(routingKey + ".." + contentType);
					System.out.println("message " + new String(body));
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}