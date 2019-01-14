package com.app.kafkaAuthenticationAndAuthorized;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;

public class KafkaConsumerUsingOauthBearerAclWithNoSSL {
	private final KafkaConsumer<String,String> consumer;
	private final String topic;
	private ExecutorService executor;
	private long delay;


	public KafkaConsumerUsingOauthBearerAclWithNoSSL(Properties props, String topic) {
		
		
		consumer = new KafkaConsumer<String,String>(props);
		this.topic = topic;
	}

	public void shutdown() {
		if (consumer != null)
			consumer.close();
		if (executor != null)
			executor.shutdown();
	}

	public void run() {
		
		//Kafka Consumer subscribes list of topics here.
		  consumer.subscribe(Arrays.asList(this.topic));  // replace you topic name

		  //print the topic name

		  java.util.Map<String,java.util.List<PartitionInfo>> listTopics = consumer.listTopics();
		  System.out.println("list of topic size :" + listTopics.size());

		  for(String topic : listTopics.keySet()){
		      System.out.println("topic name :"+topic);
		  }
		  
	//	consumer.subscribe(Collections.singletonList(this.topic));
		while(true) {
			try {
				System.out.println(".....polloing.....");

				ConsumerRecords<String, String> records = consumer.poll(100);
				for (ConsumerRecord<String, String> record : records) {
					System.out.println("..... Received message: (" + record.key() + ", " + record.value() + ") at offset " + record.offset());
				}
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		/*Executors.newSingleThreadExecutor().execute(() -> {
			while(true) {
				try {
					
					ConsumerRecords<String, String> records = consumer.poll(1000);
					for (ConsumerRecord<String, String> record : records) {
						System.out.println("..... Received message: (" + record.key() + ", " + record.value() + ") at offset " + record.offset());
					}
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
*/
	}

	/**
	 * consumer 配置.
	 * 
	 * @param brokers brokers
	 * @param groupId 组名
	 * @return
	 */
	private static Properties createConsumerConfig(String brokers, String groupId) {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);

		//props.put("auto.commit.enable", "false");


        //kafka oauth configuration
        props.setProperty("sasl.jaas.config", "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required ;");
        props.setProperty("security.protocol", "SASL_PLAINTEXT");
        props.setProperty("sasl.mechanism", "OAUTHBEARER");
        props.setProperty("sasl.login.callback.handler.class", "com.app.kafka_oauthbearer_connect.handler.KafkaOauthbearerLoginCallbackHandler");

        
		
		return props;
	}

	public static void main(String[] args) throws InterruptedException {
		String brokers = "127.0.0.1:9092";
		String groupId = "ProducerExample";
		String topic = "authtest";
		Properties props = createConsumerConfig(brokers, groupId);
		KafkaConsumerUsingOauthBearerAclWithNoSSL example = new KafkaConsumerUsingOauthBearerAclWithNoSSL(props, topic);
		example.run();

		Thread.sleep(24*60*60*1000);
		
		example.shutdown();
	}
}
