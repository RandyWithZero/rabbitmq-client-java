package com.example.rabbitmq.demo;

import com.example.rabbitmq.demo.mq.BuildMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 发送RabbitMq消息
 * @author wangshaoqi
 */
@SpringBootApplication
@Slf4j
public class DemoApplication {
    private static Map<String, String> params = new HashMap<>();
    private static String[] paramKeys = {"help", "host", "username", "password", "port", "data", "queue", "msg_prefix"};

    public static void main(String[] args) {
        String help = "--" + paramKeys[0];
        if (args.length == 0 || args[0].equals(help)) {
            printParamDesc();
            System.exit(0);
        }
        for (int i = 0; i < args.length; i++) {
            if (i % 2 == 0 && args[i].startsWith("--")) {
                String value=null;
                if(i+1<args.length){
                    value=args[i+1];
                }
                params.put(args[i].replace("--", ""),value );
            }
        }
        System.out.println(params);
        if (params.get(paramKeys[5]) == null) {
            log.error("{}【数据目录】参数未设置", paramKeys[5]);
            System.exit(1);
        }
        if (params.get(paramKeys[6]) == null) {
            log.error("{}【消息队列名】参数未设置", paramKeys[6]);
            System.exit(1);
        }
        try {
            SpringApplication.run(DemoApplication.class, args);
        } catch (Exception e) {
            System.exit(1);
        }
        System.exit(0);
    }

    @Bean
    public Queue queue() {
        return new Queue(params.get(paramKeys[6]), true);
    }

    @Bean
    public BuildMsg buildMsg(AmqpTemplate amqpTemplate) {
        BuildMsg buildMsg = new BuildMsg(amqpTemplate, params.get(paramKeys[6]),params.get(paramKeys[7]));
            buildMsg.getAllFiles(new File(params.get("data")), null);
        return buildMsg;
    }

    @Bean
    @Primary
    public RabbitProperties rabbitProperties() {
        RabbitProperties rabbitProperties = new RabbitProperties();
        rabbitProperties.setHost(params.get(paramKeys[1]) == null ? "127.0.0.1" : params.get(paramKeys[1]));
        rabbitProperties.setPassword(params.get(paramKeys[3]) == null ? "guest" : params.get(paramKeys[3]));
        rabbitProperties.setPort(Integer.parseInt(params.get(paramKeys[4]) == null ? "5672" : params.get(paramKeys[4])));
        rabbitProperties.setUsername(params.get(paramKeys[2]) == null ? "guest" : params.get(paramKeys[2]));
        return rabbitProperties;
    }

    private static void printParamDesc() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("##############参数说明##################\n")
                .append("参数：--host:MQ服务地址,未设置默认：127.0.0.1\n")
                .append("参数：--username:MQ服务用户名，未设置默认：guest\n")
                .append("参数：--password:MQ服务密码，未设置默认：guest\n")
                .append("参数：--port:MQ服务端口，未设置默认：5672\n")
                .append("参数：--data:MQ服务数据目录，必须\n")
                .append("参数：--queue:MQ服务队列名，必须\n")
                .append("参数：--msg_prefix:消息前缀,未设置是按照数据目录的第一级子目录为消息前缀\n")
                .append("#########################################");
        System.out.println(stringBuilder);

    }

}
