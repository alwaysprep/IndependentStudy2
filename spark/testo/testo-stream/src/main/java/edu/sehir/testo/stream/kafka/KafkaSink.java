package edu.sehir.testo.stream.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import java.io.Serializable;
import java.util.Properties;

public class KafkaSink implements Serializable {
    private static KafkaProducer<String, String> producer = null;

    public KafkaProducer<String, String> getInstance(final Properties properties) {
        if(producer == null) {
            producer = new KafkaProducer<>(properties);
        }
        return producer;
    }

    public void close() {
        producer.close();
    }
}