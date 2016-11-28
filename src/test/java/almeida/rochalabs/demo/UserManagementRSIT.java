package almeida.rochalabs.demo;

import static com.google.common.collect.Maps.uniqueIndex;
import static org.springframework.http.HttpMethod.POST;

import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.ImmutableMap;

import almeida.rochalabs.demo.api.requests.CreateUserRequest;
import almeida.rochalabs.demo.api.responses.CreateUserResponse;
import almeida.rochalabs.demo.conf.LocalConfiguration;
import almeida.rochalabs.demo.data.entities.UserProfile;
import almeida.rochalabs.demo.data.service.SessionService;
import almeida.rochalabs.demo.data.service.UserManagement;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { LocalConfiguration.class })
public class UserManagementRSIT {

    @Rule
    public CFixture cFixture = CFixture.connect("demo");
    
    @Autowired public TestRestTemplate client;
    @Autowired public SessionService sessionService;
    @Autowired public UserManagement userService;
    
    @Test
    public void createUser_success() {

        CreateUserRequest body = new CreateUserRequest("rochapaulo@domain.com", "password", "Paulo", "Almeida");
        
        HttpEntity<CreateUserRequest> entity = new HttpEntity<CreateUserRequest>(body);
        ResponseEntity<Void> response = client.exchange("/api/users", POST, entity, Void.class);
        
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        HttpHeaders headers = response.getHeaders();
        String location = headers.get("Location").get(0);

        Assert.assertTrue(location.startsWith("/users/"));
        UUID.fromString(location.replace("/users/", ""));
        
    }
 
    @Test
    public void createUser_entityAlreadyExists() throws Exception {
        
    	CreateUserRequest body = new CreateUserRequest("rochapaulo@domain.com", "password", "Paulo", "Almeida");
        userService.createUser(body).get();
        
        ResponseEntity<Void> response = client.exchange("/api/users", POST, new HttpEntity<CreateUserRequest>(body), Void.class);
        Assert.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
    
    @Test
    public void getUserByID_success() throws Exception {
    	
        CreateUserResponse createUserResponse = 
        		userService.createUser(new CreateUserRequest("rochapaulo@domain.com", "password", "Paulo", "Almeida")).get();
        
        UUID sessionID = sessionService.put(createUserResponse.getUserId()).get();

        
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.COOKIE, "SID=" + sessionID);
        HttpEntity<?> requestEntity = new HttpEntity<>(requestHeaders);
        
        ResponseEntity<UserProfile> response = 
	        client.exchange(
	        		"/api/secure/users/" + createUserResponse.getUserId(),
	        		HttpMethod.GET,
	        		requestEntity,
	        		UserProfile.class);
        
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        
        UserProfile profile = response.getBody();
        Assert.assertEquals("rochapaulo@domain.com", profile.getEmail());
        Assert.assertEquals("Paulo", profile.getFirstName());
        Assert.assertEquals("Almeida", profile.getLastName());
        Assert.assertEquals(createUserResponse.getSince(), profile.getSince());
        Assert.assertEquals(createUserResponse.getUserId(), profile.getUserId());
    }
    
    @Test
    public void getUserByID_notFound() throws Exception {
    	
    	UUID fakeUserID = UUID.randomUUID();
    	UUID sessionID = sessionService.put(fakeUserID).get();
    	
    	HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.COOKIE, "SID=" + sessionID);
        HttpEntity<?> requestEntity = new HttpEntity<>(requestHeaders);
        
        ResponseEntity<UserProfile> response = 
    	        client.exchange(
    	        		"/api/secure/users/" + UUID.randomUUID(),
    	        		HttpMethod.GET,
    	        		requestEntity,
    	        		UserProfile.class);
            
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    @Test
    public void getUsersProfile_success() throws Exception {
    	
    	CreateUserResponse createUser1 = 
    			userService.createUser(new CreateUserRequest("levi.howard14@example.com", "password", "Levi", "Howard")).get();
    	
    	CreateUserResponse createUser2 = 
    			userService.createUser(new CreateUserRequest("rochapaulo@domain.com", "password", "Paulo", "Almeida")).get();
    	
    	UUID fakeUserID = UUID.randomUUID();
    	UUID sessionID = sessionService.put(fakeUserID).get();
    	
    	HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.COOKIE, "SID=" + sessionID);
        HttpEntity<?> requestEntity = new HttpEntity<>(requestHeaders);
        
        ResponseEntity<List<UserProfile>> response = 
    	        client.exchange(
    	        		"/api/secure/users/",
    	        		HttpMethod.GET,
    	        		requestEntity,
    	        		new ParameterizedTypeReference<List<UserProfile>>() {});
        
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        
        ImmutableMap<String, UserProfile> profiles = uniqueIndex(response.getBody(), UserProfile::getEmail);
        UserProfile profile1 = profiles.get("levi.howard14@example.com");
        Assert.assertEquals("Levi", profile1.getFirstName());
        Assert.assertEquals("Howard", profile1.getLastName());
        Assert.assertEquals(createUser1.getSince(), profile1.getSince());
        Assert.assertEquals(createUser1.getUserId(), profile1.getUserId());
        
        UserProfile profile2 = profiles.get("rochapaulo@domain.com");
        Assert.assertEquals("Paulo", profile2.getFirstName());
        Assert.assertEquals("Almeida", profile2.getLastName());
        Assert.assertEquals(createUser2.getSince(), profile2.getSince());
        Assert.assertEquals(createUser2.getUserId(), profile2.getUserId());
        
        Assert.assertEquals(2, profiles.size());
    }
    
    @Test
    public void getUsersProfile_filterByFirstName_success() throws Exception {
    	
    	userService.createUser(new CreateUserRequest("paulo.almeida@example.com", "password", "Paulo", "Almeida")).get();
    	userService.createUser(new CreateUserRequest("levi.howard@domain.com", "password", "Levi", "Howard")).get();
    	userService.createUser(new CreateUserRequest("levi.howard15@domain.com", "password", "Levi", "Almeida")).get();
    	userService.createUser(new CreateUserRequest("paulo.silva@domain.com", "password", "Paulo", "Silva")).get();

    	CreateUserResponse expected = userService.createUser(new CreateUserRequest("rochapaulo@domain.com", "password", "Paulo", "Almeida")).get();

    	
    	UUID fakeUserID = UUID.randomUUID();
    	UUID sessionID = sessionService.put(fakeUserID).get();
    	
    	HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.COOKIE, "SID=" + sessionID);
        HttpEntity<?> requestEntity = new HttpEntity<>(requestHeaders);
        
        ResponseEntity<List<UserProfile>> response = 
    	        client.exchange(
    	        		"/api/secure/users?firstName=Paulo&lastName=Almeida&email=rochapaulo@domain.com",
    	        		HttpMethod.GET,
    	        		requestEntity,
    	        		new ParameterizedTypeReference<List<UserProfile>>() {});
        
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        
        ImmutableMap<String, UserProfile> profiles = uniqueIndex(response.getBody(), UserProfile::getEmail);
        
        UserProfile profile2 = profiles.get("rochapaulo@domain.com");
        Assert.assertEquals("Paulo", profile2.getFirstName());
        Assert.assertEquals("Almeida", profile2.getLastName());
        Assert.assertEquals(expected.getSince(), profile2.getSince());
        Assert.assertEquals(expected.getUserId(), profile2.getUserId());
        
        Assert.assertEquals(1, profiles.size());
    }
    
    @Test
    public void getUsersProfile_notFound() throws Exception {
    
    	UUID fakeUserID = UUID.randomUUID();
    	UUID sessionID = sessionService.put(fakeUserID).get();
    	
    	HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.COOKIE, "SID=" + sessionID);
        HttpEntity<?> requestEntity = new HttpEntity<>(requestHeaders);
        
        ResponseEntity<List<UserProfile>> response = 
    	        client.exchange(
    	        		"/api/secure/users/",
    	        		HttpMethod.GET,
    	        		requestEntity,
    	        		new ParameterizedTypeReference<List<UserProfile>>() {});
        
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
}
