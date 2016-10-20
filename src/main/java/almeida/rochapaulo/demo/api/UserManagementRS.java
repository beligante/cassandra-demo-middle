package almeida.rochapaulo.demo.api;

import static almeida.rochapaulo.demo.filters.ProfileFilters.byEmail;
import static almeida.rochapaulo.demo.filters.ProfileFilters.byFirstName;
import static almeida.rochapaulo.demo.filters.ProfileFilters.byLastname;
import static java.util.stream.Collectors.toList;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	@RequestMapping(path = "/users", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<Void> createUser(@RequestBody CreateUser request) throws Exception {

		UserProfile profileCreated = service.createUser(request);
		URI resourceLocation = new URI("/users/" + profileCreated.getUserId());
		
		return ResponseEntity.created(resourceLocation).build();
	}
	
	@RequestMapping(path = "/users/{userId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> getUserProfile(@PathVariable String userId) {
		
		UserProfile profile = service.findProfileById(UUID.fromString(userId));
		
		if (profile == null) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.ok(profile);
	}
	
	@RequestMapping(path = "/users", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> getUsersProfile(
			@RequestParam(name = "firstName", required = false) String firstName,
			@RequestParam(name = "lastName", required = false) String lastName,
			@RequestParam(name = "email", required = false) String email
	) {
		
		List<UserProfile> profiles = 
				service.getAllProfiles()
					.parallelStream()
						.filter(byFirstName(firstName))
						.filter(byLastname(lastName))
						.filter(byEmail(email))
					.collect(toList());
		
		if (profiles.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.ok(profiles);
	}

}
