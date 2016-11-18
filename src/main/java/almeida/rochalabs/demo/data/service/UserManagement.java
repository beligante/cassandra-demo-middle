package almeida.rochalabs.demo.data.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.google.common.util.concurrent.ListenableFuture;

import almeida.rochalabs.demo.api.requests.AuthRequest;
import almeida.rochalabs.demo.api.requests.CreateUserRequest;
import almeida.rochalabs.demo.api.responses.AuthResponse;
import almeida.rochalabs.demo.api.responses.CreateUserResponse;
import almeida.rochalabs.demo.data.entities.UserCredential;
import almeida.rochalabs.demo.data.entities.UserProfile;
import almeida.rochalabs.demo.data.query.ProfileQuery;
import almeida.rochalabs.demo.exceptions.AuthException;
import almeida.rochalabs.demo.exceptions.EntityAlreadyExists;

/**
 * 
 * @author rochapaulo
 *
 */
public class UserManagement {

    private final PasswordHash passwordHash = PasswordHash.instance();
    private final Mapper<UserProfile> profileMapper;
    private final Mapper<UserCredential> credentialMapper;
    private final SessionService sessionService;

    @Autowired
    public UserManagement(MappingManager manager, SessionService sessionService) {
        
        profileMapper = manager.mapper(UserProfile.class);
        credentialMapper = manager.mapper(UserCredential.class);
        this.sessionService = sessionService;
    }

    /**
     * 
     * @param request
     * @return
     */
    public CompletableFuture<CreateUserResponse> createUser(CreateUserRequest request) {

        return CompletableFuture.supplyAsync(() -> {

            if (credentialMapper.get(request.getEmail()) != null) {
                throw new EntityAlreadyExists("User with email=" + request.getEmail() + " already exists");
            }

            final UUID uuid = UUID.randomUUID();
            final String hashedPassword = passwordHash.create(request.getPassword());
            final Date now = new Date();

            UserProfile profile = new UserProfile(uuid, request.getFirstName(), request.getLastName(),
                    request.getEmail(), now);
            UserCredential credential = new UserCredential(uuid, hashedPassword, request.getEmail());

            profileMapper.save(profile);
            credentialMapper.save(credential);

            return CreateUserResponse.builder()
                        .email(request.getEmail())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .userId(uuid)
                        .since(now)
                        .build();
        });
    }

    /**
     * 
     * @param request
     * @throws AuthException
     * @return
     */
    public CompletableFuture<AuthResponse> authenticate(AuthRequest request) throws AuthException {

        return CompletableFuture.supplyAsync(() -> {

            UserCredential credentials = credentialMapper.get(request.getEmail());
            boolean validated = passwordHash.validate(request.getPassword(), credentials.getPassword());

            if (validated) {
                try {
                    UUID sessionUUID = sessionService.put(credentials.getUserId()).get();
                    return new AuthResponse(credentials.getUserId(), sessionUUID);
                } catch (Exception ex) {
                  throw new RuntimeException(ex);  
                }
            }
            
            throw new AuthException();
        });
    }
    
    public void logout(UUID sessionID) {
        
        sessionService.purge(sessionID);
    }

    /**
     * 
     * @param query
     * @return
     */
    public ListenableFuture<List<UserProfile>> findProfileBy(ProfileQuery query) {
        return query.execute();
    }

}
