package almeida.rochapaulo.demo.api.service;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;

import almeida.rochapaulo.demo.api.requests.CreateUserRequest;
import almeida.rochapaulo.demo.api.requests.VerifyCredentialsRequest;
import almeida.rochapaulo.demo.api.responses.CreateUserResponse;
import almeida.rochapaulo.demo.api.responses.VerifyCredentialsResponse;
import almeida.rochapaulo.demo.api.service.query.ProfileQuery;
import almeida.rochapaulo.demo.entities.UserCredential;
import almeida.rochapaulo.demo.entities.UserProfile;
import almeida.rochapaulo.demo.service.PasswordHash;
import almeida.rochapaulo.demo.service.exceptions.AuthException;
import almeida.rochapaulo.demo.service.exceptions.EntityAlreadyExists;

/**
 * 
 * @author rochapaulo
 *
 */
public class UserManagement {

	private final PasswordHash passwordHash = PasswordHash.instance();
	private final Mapper<UserProfile> profileMapper;
	private final Mapper<UserCredential> credentialMapper;
	
	@Autowired
	public UserManagement(MappingManager manager) {
		profileMapper = manager.mapper(UserProfile.class);
		credentialMapper = manager.mapper(UserCredential.class);
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
			
			UserProfile profile = new UserProfile(uuid, request.getFirstName(), request.getLastName(), request.getEmail(), now);
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
	public CompletableFuture<VerifyCredentialsResponse> verifyCredentials(VerifyCredentialsRequest request) throws AuthException {
		
		return CompletableFuture.supplyAsync(() -> {
			
			UserCredential credentials = credentialMapper.get(request.getEmail());
			boolean validated = passwordHash.validate(request.getPassword(), credentials.getPassword());
			
			if (validated) {
				return new VerifyCredentialsResponse(credentials.getUserId());
			}
			throw new AuthException();
		});
	}
	
	/**
	 * 
	 * @param query
	 * @return
	 */
	public CompletableFuture<UserProfile> findProfileBy(ProfileQuery query) {
		return query.execute();
	}
	
}
