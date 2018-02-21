package com.passeo.rabbitMQ.test;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PublishSample {
	private static String userName;
	private static String password;
	private static String virtualHost;
	private static String hostName;
	private static int portNumber;
	private static String exchangeName;
	private static String routingKey;

	public PublishSample() {
	}

	public static void main(String[] args) {
		userName = "apiuser";
		password = "apipwd";
		virtualHost = "/";
		hostName = "192.168.99.101";
		portNumber = 5672;
		exchangeName = "enterpriseNPI";
		routingKey = "getVM";

		Connection conn = null;
		Channel channel = null;

		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setUsername(userName);
			factory.setPassword(password);
			factory.setVirtualHost(virtualHost);
			factory.setHost(hostName);
			factory.setPort(portNumber);

			conn = factory.newConnection();

			channel = conn.createChannel();

			channel.exchangeDeclare(exchangeName, "topic", true);
			String queueName = channel.queueDeclare().getQueue();
			channel.queueBind(queueName, exchangeName, routingKey);

			String l_message = args[0];
			byte[] messageBodyBytes = l_message.getBytes();

			Map<String, Object> headers = new HashMap();
			headers.put("process", Integer.valueOf(154875));
			headers.put("step", "step1");

			UUID aCorrelID = UUID.randomUUID();

			AMQP.BasicProperties l_properties = new AMQP.BasicProperties.Builder().contentType("text/plain")
					.deliveryMode(Integer.valueOf(2)).expiration("60000").priority(Integer.valueOf(1)).userId(userName)
					.headers(headers).correlationId(aCorrelID.toString()).build();

			channel.basicPublish(exchangeName, routingKey, l_properties, messageBodyBytes);

			System.out.println(" [x] Sent '" + l_message + "'");

			channel.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
