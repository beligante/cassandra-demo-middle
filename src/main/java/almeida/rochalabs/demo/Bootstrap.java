package almeida.rochalabs.demo;

import java.nio.charset.Charset;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import com.google.common.io.Resources;

import almeida.rochalabs.demo.api.filters.CORSFilter;
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
    public Session session() throws Exception {
        Cluster cluster = Cluster.builder().addContactPoint(CONTACT_POINT).withClusterName(CLUSTER_NAME).build();
        
        Session session = cluster.connect();
        String schema = Resources.toString(Resources.getResource("cassandra/cql/load.cql"), Charset.forName("UTF-8"));
        
        final String[] commands = schema.replace(System.lineSeparator(), "").split(";");
        for (String command : commands) {
        	session.execute(command);
        }
        session.close();
        
        return cluster.connect(KEYSPACE);
    }

    @Bean
    public FilterRegistrationBean corsFilter() {
    	
    	FilterRegistrationBean registration = new FilterRegistrationBean();
    	
    	registration.setFilter(new CORSFilter());
    	registration.addUrlPatterns("*");
    	registration.setName("CORS Filter");
    	registration.setOrder(1);
    	
    	return registration;
    }
    
	
    @Bean
    public FilterRegistrationBean loginFilter(SessionService service) {
    	
    	FilterRegistrationBean registration = new FilterRegistrationBean();
    	
    	registration.setFilter(new LoginFilter(service));
    	registration.addUrlPatterns("/api/secure/*");
        registration.setName("LoginFilter");
        registration.setOrder(2);
        
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
