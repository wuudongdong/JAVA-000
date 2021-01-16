## 学习笔记

- 1、（必做）搭建ActiveMQ服务，基于JMS，写代码分别实现对于queue和topic的消息的生产和消费，代码提交到github
思路：
基于SpringBoot集成ActiveMQ，需要引入如下依赖：
```xml
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-activemq</artifactId>
    </dependency>
```

Producer代码实现如下：
```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;
import javax.jms.Message;

@RestController
public class MessageProducer {

    @Autowired
    JmsTemplate jmsTemplate;

    @GetMapping("send")
    public String send() throws JMSException {
        jmsTemplate.send("test.queue", session -> session.createTextMessage("hello"));
        return "ok";
    }

}
```

Consumer代码实现如下：
```java
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    @JmsListener(destination = "test.queue")
    public void processMessage(String content) {
        System.out.println("receive message: " + content);
    }
}
```

- 2、（必做）搭建一个3节点Kafka集群，测试功能和性能；实现spring kafka下对kafka集群的操作，将代码提交到github 
思路：
  基于SpringBoot集成Kafka，需要引入如下依赖：
```xml
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
    </dependency>
```

Producer代码实现如下：
```java
import com.example.kafkademo.common.Foo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Kafka producer dome.
 *
 * @author wuudongdong
 * @date 2020/01/14
 */
@RestController
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<Object, Object> template;

    @PostMapping("send/foo/{what}")
    public String sendFoo(@PathVariable String what) {
        template.send("test.topic", new Foo(what));
        return "ok";
    }
}
```

Consumer代码实现如下：
```java
import com.example.kafkademo.common.Foo;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @KafkaListener(id = "fooGroup", topics = "test.topic")
    public void listen(Foo foo) {
        System.out.println("Received: " + foo);
    }
}
```
