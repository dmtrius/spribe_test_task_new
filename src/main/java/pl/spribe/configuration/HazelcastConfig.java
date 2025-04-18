package pl.spribe.configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@EnableCaching
@Configuration
public class HazelcastConfig {
    @Primary
    @Bean("mainHazel")
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.setInstanceName("hazelcast-instance");

        // Network configuration
        config.getNetworkConfig().setPort(5701);
        config.getNetworkConfig().setPortAutoIncrement(true);
        config.getNetworkConfig().setPortCount(100);
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().setPublicAddress("0.0.0.0");
        config.getNetworkConfig().getJoin().getTcpIpConfig().addMember("127.0.0.1:5701");
        config.getJetConfig().setEnabled(false);
    

        // Map configuration
        MapConfig mapConfig = new MapConfig("unitAvailabilityCache");
        mapConfig.setTimeToLiveSeconds(3600);
        mapConfig.setMaxIdleSeconds(1800);
        config.addMapConfig(mapConfig);

        MapConfig jobRecordsMap = new MapConfig("__jet.jobs");
        jobRecordsMap.setBackupCount(1);
        config.addMapConfig(jobRecordsMap);

        return Hazelcast.newHazelcastInstance(config);
    }

    @Bean(destroyMethod = "shutdown")
    public HazelcastInstance hazelcastInstanceShutdown(@Qualifier("mainHazel") HazelcastInstance hazelcastInstance) {
        return hazelcastInstance;
    }
}
