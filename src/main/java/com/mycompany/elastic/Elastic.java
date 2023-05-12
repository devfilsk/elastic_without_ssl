/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.mycompany.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.json.JsonArray;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;

/**
 *
 * @author devfilsk
 */
public class Elastic {

    public static void main(String[] args) throws IOException {

        RestClientBuilder restClient = RestClient.builder(
//            new HttpHost("localhost", 9200)            
            new HttpHost("host_elastic_hospedado_sem", 9200, "https") // The host must be without the https://
        );
        
        restClient.setHttpClientConfigCallback(new HttpClientConfigCallback() {
        @Override
        public HttpAsyncClientBuilder customizeHttpClient(
                HttpAsyncClientBuilder httpClientBuilder) {
                SSLContext sslContext;
                try {
                    sslContext = new SSLContextBuilder()
                        .loadTrustMaterial(null, (chain, authType) -> true)
                        .build();
                    httpClientBuilder
                            .setSSLContext(sslContext)
                            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
                                       
                    return httpClientBuilder;
                } catch (NoSuchAlgorithmException | KeyManagementException e) {
                    throw new RuntimeException("Failed to create SSL context", e);
                } catch (KeyStoreException ex) {
                    Logger.getLogger(Elastic.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("DEU RUIM");
                return null;
                
            }
        });      

        Header[] defaultHeaders
                = new Header[]{new BasicHeader("Authorization",
                            "ApiKey bXBRbTZJY0JrT2FOS1JtSFFhTmE6cHZZMXFqQXdTY1NRMTYxOVItajVSUQ==")};
        restClient.setDefaultHeaders(defaultHeaders);

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
            restClient.build(), new JacksonJsonpMapper());

        // And create the API client
        ElasticsearchClient client = new ElasticsearchClient(transport);
        
        

        // Criando produto para enviar para o Elastic
        Product produto = new Product("Caixa dagua", "123456");
        
           
        try{
             IndexResponse response = client.index(i -> i
                .index("integrador_ecommerce_ferragensthony")
                .id(produto.getSku())
                .document(produto)
            );
            System.out.println("Indexed with version "+ response.version());

        }catch(Exception e) {
            System.out.println("E ===>"+ e.getMessage());
        }
       
    }
    
    
}
