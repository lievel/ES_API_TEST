package com.elasticsearch.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * Created by GGBX08 on 2017-07-11.
 */
public class ESConnector {
    public static ESConnector instance;

    public TransportClient getClient() {
        return client;
    }

    private static TransportClient client;
    private ESConnector(){}
    
    public static final String ES_SERVER_IP = "10.250.46.160";
    public static final int ES_SERVER_PORT = 9300;
    public static final String INDEX_NAME = "secu_rt_alm_160";
    public static final String TYPE_NAME = "alarm_data";
    public static final String CLUSTER_NAME = "elasticsearch_watzeye";


    public static synchronized ESConnector getInstance() throws UnknownHostException {
        if(instance == null){
            instance = new ESConnector();

            Settings settings = Settings.builder()
                    .put("cluster.name", CLUSTER_NAME)
                    .build();

            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ES_SERVER_IP), ES_SERVER_PORT));  
        }
        return  instance;
    }

}
