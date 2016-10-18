package almeida.rochapaulo.demo.api;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import almeida.rochapaulo.demo.api.requests.CreateUser;
import almeida.rochapaulo.demo.entities.UserProfile;
import almeida.rochapaulo.demo.service.UserManagement;

@RestController
public class UserManagementRS {

	private final UserManagement service;
	
	@Autowired
	public UserManagementRS(UserManagement service) {
		this.service = service;
	}
	
	@RequestMapping("/")
	public String hello() {
		return "Greetings from Spring Boot!";
	}
	
	@RequestMapping(path = "/users", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<Void> createUser(@RequestBody CreateUser request) throws Exception {

		UserProfile profileCreated = service.createUser(request);
		URI resourceLocation = new URI("/users/" + profileCreated.getUserId());
		
		return ResponseEntity.created(resourceLocation).build();
	}
	
	@RequestMapping(path = "/users/{userId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<UserProfile> getUserProfile(@PathVariable String userId) {
		
		return ResponseEntity.ok(service.findProfileById(UUID.fromString(userId)));
	}
	
	@RequestMapping(path = "/users", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<UserProfile>> getUsersProfile() {
		
		return ResponseEntity.ok(service.getAllProfiles());
	}
	
}
