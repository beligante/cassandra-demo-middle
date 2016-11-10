package almeida.rochapaulo.demo.api;

import static java.util.stream.Collectors.toList;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import almeida.rochapaulo.demo.api.requests.CreateUserRequest;
import almeida.rochapaulo.demo.api.responses.CreateUserResponse;
import almeida.rochapaulo.demo.api.service.query.QueryFactory;
import almeida.rochapaulo.demo.entities.UserProfile;
import almeida.rochapaulo.demo.service.UserManagement;
import almeida.rochapaulo.demo.service.exceptions.EntityAlreadyExists;

/**
 * 
 * @author rochapaulo
 *
 */
@RestController
public class UserManagementRS {

    private final UserManagement service;
    private final QueryFactory queryFactory;

    @Autowired
    public UserManagementRS(UserManagement service, QueryFactory queryFactory) {
        this.service = service;
        this.queryFactory = queryFactory;
    }

    @RequestMapping(path = "/api/users", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) throws Exception {

        try {

            CreateUserResponse profileCreated = service.createUser(request).get();
            URI resourceLocation = new URI("/users/" + profileCreated.getUserId());

            return ResponseEntity.created(resourceLocation).build();

        } catch (EntityAlreadyExists ex) {

            String message = "user email " + request.getEmail() + " already exists";
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
        }

    }

    @RequestMapping(path = "/api/secure/users/{userId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getUserProfile(@PathVariable String userId) throws Exception {

        List<UserProfile> profile = service.findProfileBy(queryFactory.profileByUUID(UUID.fromString(userId))).get();

        if (profile == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(profile);
    }

    @RequestMapping(path = "/api/secure/users", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getUsersProfile(
    		@RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName,
            @RequestParam(name = "email", required = false) String email) throws Exception {

        List<UserProfile> profiles = 
            service
                .findProfileBy(queryFactory.allProfiles())
                .get()
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

    
    public static Predicate<? super UserProfile> byFirstName(String firstName) {
        return profile -> (firstName == null) ? true : contains(profile.getFirstName(), firstName);
    }

    public static Predicate<? super UserProfile> byLastname(String lastName) {
        return profile -> (lastName == null) ? true : contains(profile.getLastName(), lastName);
    }

    public static Predicate<? super UserProfile> byEmail(String email) {
        return profile -> (email == null) ? true : Objects.equals(email, profile.getEmail());
    }

    private static boolean contains(String source, String filter) {
        return source.contains(filter);
    }
    
}
