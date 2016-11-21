package almeida.rochalabs.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;

import almeida.rochalabs.demo.api.filters.LoginFilter;
import almeida.rochalabs.demo.data.query.QueryFactory;
import almeida.rochalabs.demo.data.service.BucketService;
import almeida.rochalabs.demo.data.service.PhotoService;
import almeida.rochalabs.demo.data.service.SessionService;
import almeida.rochalabs.demo.data.service.UserManagement;

/**
 * 
 * @author rochapaulo
 *
 */
@SpringBootApplication
public class Bootstrap {

    static String CONTACT_POINT = "127.0.0.1";
    static String CLUSTER_NAME = "Test Cluster";
    static String KEYSPACE = "demo";
    
    public static void main(String[] args) {
    	
    	if (args.length > 0) {
    		CONTACT_POINT = args[0];
    	}
    	if (args.length > 1) {
    		CLUSTER_NAME = args[1];
    	}
    	if (args.length > 2) {
    		KEYSPACE = args[2];
    	}
    	
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
    	registration.addUrlPatterns("/api/secure/*");
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
    public BucketService imageDAO(MappingManager manager) {
        return new BucketService(manager);
    }

    @Bean
    public QueryFactory queryFactory(MappingManager manager) {
        return new QueryFactory(manager);
    }

}
