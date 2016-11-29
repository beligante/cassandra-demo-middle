package almeida.rochalabs.demo;

import static com.datastax.driver.core.querybuilder.QueryBuilder.truncate;
import static org.springframework.http.HttpMethod.POST;

import java.util.Optional;
import java.util.UUID;

import org.cassandraunit.CassandraCQLUnit;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;

import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TableMetadata;

import almeida.rochalabs.demo.api.requests.AuthRequest;
import almeida.rochalabs.demo.api.requests.CreateUserRequest;
import almeida.rochalabs.demo.api.responses.CreateUserResponse;
import almeida.rochalabs.demo.conf.LocalConfiguration;
import almeida.rochalabs.demo.data.service.SessionService;
import almeida.rochalabs.demo.data.service.UserManagement;

/**
 * 
 * @author rochapaulo
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = LocalConfiguration.class)
public class AuthRSIT {

    @ClassRule
    public static CassandraCQLUnit cassandra = 
        new CassandraCQLUnit(new ClassPathCQLDataSet("cassandra/cql/load.cql"), "cassandra_config.yaml");
    
    @Autowired 
    public TestRestTemplate client;
    
    @Autowired 
    public UserManagement userService;
    
    @Autowired 
    public SessionService sessionService;
    
    
    @After
    public void cleanUp() {
        
        Session session = cassandra.getSession();
        Metadata metadata = cassandra.getCluster().getMetadata();
        KeyspaceMetadata keyspaceMetadata = metadata.getKeyspace("demo");
        for (TableMetadata table : keyspaceMetadata.getTables()) {
            session.execute(truncate(table));
        }
        
    }
    
    @Test
    public void success() throws Exception {
        
        CreateUserResponse createUser = 
                userService.createUser(new CreateUserRequest("rochapaulo@domain.com", "password", "Paulo", "Almeida")).get();
        
        AuthRequest body = new AuthRequest("rochapaulo@domain.com", "password");
        HttpEntity<AuthRequest> entity = new HttpEntity<AuthRequest>(body);
        
        ResponseEntity<Void> response = client.exchange("/api/login", POST, entity, Void.class);
        
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        
        HttpHeaders headers = response.getHeaders();
        String cookie = headers.get("Set-Cookie").get(0);
        
        UUID sidCookie = UUID.fromString(cookie.replace("SID=", ""));
        Optional<almeida.rochalabs.demo.data.entities.Session> appSession = sessionService.getSession(sidCookie).get();
        
        Assert.assertTrue(appSession.isPresent());
        Assert.assertEquals(createUser.getUserId(), appSession.get().getUserId());
    }
    
    @Test
    public void invalidUsername() {
        
        userService.createUser(new CreateUserRequest("rochapaulo@domain.com", "password", "Paulo", "Almeida"));
        
        AuthRequest body = new AuthRequest("invaliduser@domain.com", "password");
        HttpEntity<AuthRequest> entity = new HttpEntity<AuthRequest>(body);
        
        ResponseEntity<Void> response = client.exchange("/api/login", POST, entity, Void.class);
        
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    
    @Test
    public void invalidPassword() {
        
        userService.createUser(new CreateUserRequest("rochapaulo@domain.com", "password", "Paulo", "Almeida"));
        
        AuthRequest body = new AuthRequest("rochapaulo@domain.com", "invalidpassword");
        HttpEntity<AuthRequest> entity = new HttpEntity<AuthRequest>(body);
        
        ResponseEntity<Void> response = client.exchange("/api/login", POST, entity, Void.class);
        
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    
    @Test
    public void logout() {
        
        userService.createUser(new CreateUserRequest("rochapaulo@domain.com", "password", "Paulo", "Almeida"));
        
        AuthRequest body = new AuthRequest("rochapaulo@domain.com", "password");
        ResponseEntity<Void> authResponse = client.exchange("/api/login", POST, new HttpEntity<AuthRequest>(body), Void.class);
        
        HttpHeaders responseHeaders = authResponse.getHeaders();
        String cookie = responseHeaders.get("Set-Cookie").get(0);
        
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.COOKIE, cookie);
        
        ResponseEntity<Void> logoutResponse = client.exchange("/api/secure/logout", POST, new HttpEntity<>(headers), Void.class);
        Assert.assertEquals(HttpStatus.NO_CONTENT, logoutResponse.getStatusCode());
    }
    
}
