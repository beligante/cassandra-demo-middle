package almeida.rochapaulo.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;

import almeida.rochapaulo.demo.api.service.query.QueryFactory;
import almeida.rochapaulo.demo.bucket.BucketRepository;
import almeida.rochapaulo.demo.filters.LoginFilter;
import almeida.rochapaulo.demo.service.PhotoService;
import almeida.rochapaulo.demo.service.SessionService;
import almeida.rochapaulo.demo.service.UserManagement;

/**
 * 
 * @author rochapaulo
 *
 */
@SpringBootApplication
public class Bootstrap {

    static final String CONTACT_POINT = "127.0.0.1";
    static final String CLUSTER_NAME = "Test Cluster";
    static final String KEYSPACE = "demo";
    
    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }

    @Bean
    public Session session() {
        Cluster cluster = Cluster.builder().addContactPoint(CONTACT_POINT).withClusterName(CLUSTER_NAME).build();
        return cluster.connect(KEYSPACE);
    }

    @Bean
    public FilterRegistrationBean loginFilter(SessionService service) {
    	
    	FilterRegistrationBean registration = new FilterRegistrationBean();
    	
    	registration.setFilter(new LoginFilter(service));
    	registration.addUrlPatterns("/secure/*");
        registration.setName("LoginFilter");
        registration.setOrder(1);
        
    	return registration;
    }
    
    @Bean
    public MappingManager mappingManager(Session session) {
        return new MappingManager(session);
    }
    
    @Bean
    public SessionService sessionService(MappingManager manager) {
        return new SessionService(manager);
    }

    @Bean
    public UserManagement userManagement(MappingManager manager, SessionService sessionService) {
        return new UserManagement(manager, sessionService);
    }

    @Bean
    public PhotoService photoService(MappingManager manager) {
        return new PhotoService(manager);
    }

    @Bean
    public BucketRepository imageDAO(MappingManager manager) {
        return new BucketRepository(manager);
    }

    @Bean
    public QueryFactory queryFactory(MappingManager manager) {
        return new QueryFactory(manager);
    }

}
