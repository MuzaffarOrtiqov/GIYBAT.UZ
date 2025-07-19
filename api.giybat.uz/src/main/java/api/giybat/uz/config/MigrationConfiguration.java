package api.giybat.uz.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MigrationConfiguration {
    @Value("jdbc:postgresql://localhost:5432/giybat_db")
    private String dataSourceUrl;
    @Value("giybat_user")
    private String dataSourceUsername;
    @Value("12345")
    private String dataSourcePassword;
    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(dataSourceUrl);
        dataSourceBuilder.username(dataSourceUsername);
        dataSourceBuilder.password(dataSourcePassword);
        return dataSourceBuilder.build();
    }
}
