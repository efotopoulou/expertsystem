/*
 *  Copyright 2016 Arcadia Framework, http://www.arcadia-framework.eu/
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eu.arcadia.expertsystem.config;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

/**
 *
 * @author Christos Paraskeva <ch.paraskeva at gmail dot com>
 */
@Configuration
@EnableSolrRepositories(basePackages = {"eu.arcadia.repository.solr.dao"}, multicoreSupport = true)
public class SolrContext {
    
    @Resource
    private Environment environment;
    
    @Bean(name = "solrServer")
    public SolrServer getSolrServerInstance() {        
        String solrUrl = getSolrUrl();
        Logger.getLogger(SolrContext.class.getName()).log(Level.INFO, "Solr url is set to: {0}", solrUrl);
        return new HttpSolrServer(solrUrl);
    }
    
    private String getSolrUrl() {
        if (null == environment.getProperty("solr.url") || environment.getProperty("solr.url").isEmpty()) {
            return environment.getProperty("solr.protocol")
                    .concat("://")
                    .concat(environment.getProperty("solr.host"))
                    .concat(":")
                    .concat(environment.getProperty("solr.port"))
                    .concat("/solr/")
                    .concat(environment.getProperty("solr.core"));
        }
        return environment.getProperty("solr.url");
    }
}
