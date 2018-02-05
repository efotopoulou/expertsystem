package eu.arcadia.expertsystem.config;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * Created by nikos on 15/1/2016 and Edited by cparaskeva (ch.paraskeva at gmail
 * dot com) on 17/01/2017.
 */
@org.springframework.context.annotation.Configuration
public class Neo4jConfiguration {

    @Value("${neo4j.url}")
    private String url;

    @Bean
    public SessionFactory getSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.driverConfiguration().setDriverClassName("org.neo4j.ogm.drivers.http.driver.HttpDriver").setURI(url);
        return new SessionFactory(configuration, "eu.arcadia.repository.neo4j.domain");
    }

    @Bean
    public Session getSession() throws Exception {
        return getSessionFactory().openSession();
    }
}