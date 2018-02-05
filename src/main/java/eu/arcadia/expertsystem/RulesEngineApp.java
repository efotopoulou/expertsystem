package eu.arcadia.expertsystem;

import eu.arcadia.expertsystem.config.DroolsConfig;
import eu.arcadia.expertsystem.config.Neo4jConfiguration;

import java.util.Arrays;
import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import org.apache.activemq.command.ActiveMQTopic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;

/**
 * The main class, which Spring Boot uses to bootstrap the application.
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
@ComponentScan({
    //Contains all the security configuration regarding the Arcadia Framework 
    "eu.arcadia.app.config",
    "eu.arcadia.app.security.auth",
    "eu.arcadia.api.repository",
    //"eu.arcadia.repository.solr",
    "eu.arcadia.repository.mongo.service",
    "eu.arcadia.repository.mongo.transferobjects",
    "eu.arcadia.expertsystem",
    "eu.arcadia.expertsystem.rules",
    "eu.arcadia.repository.neo4j.service",
    "eu.arcadia.repository.neo4j.dao",
    "rules"
}
)
//Import component specific configurations
@Import({DroolsConfig.class, Neo4jConfiguration.class})
@EnableMongoRepositories("eu.arcadia.repository.mongo.dao")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableJms
public class RulesEngineApp {

    private static Logger log = LoggerFactory.getLogger(RulesEngineApp.class);

    public static final String POLICY_ENFORCEMENT_TOPIC = "eu.arcadia.policy.enforcement";
    public static final String RUNTIME_ACTIONS_TOPIC = "eu.arcadia.runtime.actions";

    static {
        System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES", "eu.arcadia.expertsystem,java.util,java.lang");
    }

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(RulesEngineApp.class, args);

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);

//        StringBuilder sb = new StringBuilder("Application beans:\n");
//        for (String beanName : beanNames) {
//            sb.append(beanName + "\n");
//        }
//        log.info(sb.toString());

    }

    @Bean // Strictly speaking this bean is not necessary as boot creates a default
    JmsListenerContainerFactory<?> myJmsContainerFactory(ConnectionFactory connectionFactory) {
        SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(true);
        return factory;
    }

    @Bean
    public Topic policyEnforcementTopic() {
        return new ActiveMQTopic(POLICY_ENFORCEMENT_TOPIC);
    }

    @Bean
    public Topic runtimeActionsTopic() {
        return new ActiveMQTopic(RUNTIME_ACTIONS_TOPIC);
    }

}
