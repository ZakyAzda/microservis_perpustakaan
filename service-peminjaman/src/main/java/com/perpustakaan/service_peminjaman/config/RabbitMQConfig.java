package com.perpustakaan.service_peminjaman.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // --- TAMBAHAN: Konstanta Static agar bisa dipanggil PeminjamanService ---
    public static final String EXCHANGE = "peminjaman_exchange";
    public static final String ROUTING_KEY = "peminjaman_routing_key";
    public static final String QUEUE = "peminjaman_queue";
    // ------------------------------------------------------------------------

    @Value("${perpustakaan.rabbitmq.queue}")
    private String queueName;

    @Value("${perpustakaan.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${perpustakaan.rabbitmq.routing-key}")
    private String routingKey;

    @Bean
    public Queue queue() {
        // Menggunakan value dari properties, atau fallback ke konstanta jika kosong
        return new Queue(queueName != null ? queueName : QUEUE, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName != null ? exchangeName : EXCHANGE);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey != null ? routingKey : ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}