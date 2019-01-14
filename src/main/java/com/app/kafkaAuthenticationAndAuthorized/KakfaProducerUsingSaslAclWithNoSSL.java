package com.app.kafkaAuthenticationAndAuthorized;

import java.util.Properties;
import java.util.Random;
import java.util.stream.IntStream;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import lombok.extern.slf4j.Slf4j;
/**
 * The KakfaProducerUsingSaslAclWithNoSSL program provide SASL PLAIN PlainLoginModule
 * with ACL approval from Zookeeper ACL Lists
 *
 * @author Rohan Surve
 * @version 1.0
 * @since 2018-11-24
 */

@Slf4j
public class KakfaProducerUsingSaslAclWithNoSSL {
	public static void main(String[] args) {
		log.info("KakfaProducer started...");
		Random rnd = new Random();
		Properties props = new Properties();

		// kafka bootstrap server
		props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		// producer acks
		props.setProperty(ProducerConfig.ACKS_CONFIG, "all");
		props.setProperty(ProducerConfig.RETRIES_CONFIG, "3");
		props.setProperty(ProducerConfig.LINGER_MS_CONFIG, "1");

		// SASL PLAIN with ACL using custom Principal builder
		String jaasTemplate = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";";
		String jaasCfg = String.format(jaasTemplate, "test", "test-secret");
		props.setProperty("sasl.jaas.config", jaasCfg);
		props.setProperty("security.protocol", "SASL_PLAINTEXT");
		props.setProperty("sasl.mechanism", "PLAIN");

		props.put("client.id", "ProducerExample");

		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
		IntStream.range(0, 10).forEach(
				rec ->  { 
					String ip = "192.168.2." + rnd.nextInt(255);
					String msg = rnd.nextInt(1000) + ",kakfaSASL Plain ACl test" + ip;
					try {
						// sync
						log.info("KakfaProducer sending message..." + msg);

						producer.send(new ProducerRecord<String, String>("acltest", ip, msg)).get();
					} catch (Exception ex) {
						log.error("Error occured KakfaProducer sending message..." + ex.getMessage());
					}
				}
		);
		
		log.info("KakfaProducer ended successfully...");

		producer.close();
	}
}
