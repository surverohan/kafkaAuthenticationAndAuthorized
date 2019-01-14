package com.app.kafkaAuthenticationAndAuthorized;

import java.util.Properties;
import java.util.Random;
import java.util.stream.IntStream;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.producer.ProducerRecord;

@Slf4j

public class KakfaProducerUsingOauthBearerAclWithNoSSL {
	public static void main(String[] args) {
		log.info("KakfaProducer started...");
        Random rnd = new Random();
        Properties props = new Properties();
        
        // kafka bootstrap server
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,  "127.0.0.1:9092");
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        // producer acks
        props.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        props.setProperty(ProducerConfig.RETRIES_CONFIG, "3");
        props.setProperty(ProducerConfig.LINGER_MS_CONFIG, "1");

        //kafka oauth configuration
        props.setProperty("sasl.jaas.config", "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required ;");
        props.setProperty("security.protocol", "SASL_PLAINTEXT");
        props.setProperty("sasl.mechanism", "OAUTHBEARER");
        props.setProperty("sasl.login.callback.handler.class", "com.app.kafka_oauthbearer_connect.handler.KafkaOauthbearerLoginCallbackHandler");

        props.put("client.id", "ProducerExample");
       
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);

        IntStream.range(0, 10).forEach(
				rec ->  {             String ip = "192.168.2." + rnd.nextInt(255);
            String msg =  rnd.nextInt(1000) + ",kakfaOuath test" + ip;
            try {
                
                //sync
        		log.info("KakfaProducer sending message..."+msg);

                producer.send(new ProducerRecord<String, String>("authtest", ip, msg)).get();
            } catch (Exception ex) {
        		log.error("Error occured KakfaProducer sending message..."+ex.getMessage());

                ex.printStackTrace();
            }
        });
		log.info("KakfaProducer ended...");

        producer.close();
    }
}
