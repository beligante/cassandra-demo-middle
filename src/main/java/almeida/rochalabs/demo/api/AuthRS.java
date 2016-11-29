package almeida.rochalabs.demo.api;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import almeida.rochalabs.demo.api.requests.AuthRequest;
import almeida.rochalabs.demo.api.responses.AuthResponse;
import almeida.rochalabs.demo.data.service.UserManagement;

/**
 * 
 * @author rochapaulo
 *
 */
@RestController
public class AuthRS {

    private final UserManagement service;

    @Autowired
    public AuthRS(UserManagement service) {
        this.service = service;
    }
    
    @RequestMapping(
            path = "/api/login", 
            method = RequestMethod.POST, 
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> login(@RequestBody AuthRequest request) throws Exception {
    
        AuthResponse authResponse = service.authenticate(request).exceptionally(ex -> null).get();
        if (authResponse != null) {
            return ResponseEntity.ok().header("Set-Cookie", "SID=" + authResponse.getSessionId(), "Path=/").build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
    }
    
    
    @RequestMapping(
            path = "/api/secure/logout", 
            method = RequestMethod.POST
    )
    public ResponseEntity<?> logout(@CookieValue("SID") String sessionID) throws Exception {
    
        service.logout(UUID.fromString(sessionID));
        return ResponseEntity.noContent().build();
    }
    
}
