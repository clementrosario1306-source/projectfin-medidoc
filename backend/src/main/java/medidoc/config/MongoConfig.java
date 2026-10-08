package medidoc.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Override
    protected String getDatabaseName() {
        return "medidoc_db";
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        String connectionUri = mongoUri;
        if (connectionUri == null || connectionUri.isBlank()) {
            connectionUri = "mongodb+srv://admin:admin@cluster0.ds6tsl7.mongodb.net/medidoc_db?retryWrites=true&w=majority&appName=Cluster0";
        }
        System.out.println("Connecting Spring Mongo Client to: " + connectionUri.replaceAll(":[^/@]+@", ":*****@"));
        
        ConnectionString connectionString = new ConnectionString(connectionUri);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
}
