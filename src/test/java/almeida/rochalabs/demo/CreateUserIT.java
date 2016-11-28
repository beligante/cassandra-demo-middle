package almeida.rochalabs.demo;

import static org.springframework.http.HttpMethod.POST;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Rule;
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

import almeida.rochalabs.demo.api.requests.CreateUserRequest;
import almeida.rochalabs.demo.conf.LocalConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { LocalConfiguration.class })
public class CreateUserIT {

    @Rule
    public CFixture cFixture = CFixture.connect("demo");
    
    @Autowired
    public TestRestTemplate client;
    
    @Test
    public void success() {

        CreateUserRequest body = new CreateUserRequest();
        body.setEmail("rochapaulo@domain.com");
        body.setFirstName("Paulo");
        body.setLastName("Almeida");
        body.setPassword("password");
        
        HttpEntity<CreateUserRequest> entity = new HttpEntity<CreateUserRequest>(body);
        ResponseEntity<Void> response = client.exchange("/api/users", POST, entity, Void.class);
        
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        HttpHeaders headers = response.getHeaders();
        String location = headers.get("Location").get(0);

        Assert.assertTrue(location.startsWith("/users/"));
        UUID.fromString(location.replace("/users/", ""));
        
    }
 
    @Test
    public void entityAlreadyExists() {
        
        CreateUserRequest body = new CreateUserRequest();
        body.setEmail("rochapaulo@domain.com");
        body.setFirstName("Paulo");
        body.setLastName("Almeida");
        body.setPassword("password");
        
        HttpEntity<CreateUserRequest> entity = new HttpEntity<CreateUserRequest>(body);
        client.exchange("/api/users", POST, entity, Void.class);
        
        ResponseEntity<Void> response = client.exchange("/api/users", POST, entity, Void.class);
        Assert.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
    
}
