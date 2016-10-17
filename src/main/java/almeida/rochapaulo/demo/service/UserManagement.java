package almeida.rochapaulo.demo.service;

import java.util.Date;
import java.util.UUID;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.google.inject.Inject;

import almeida.rochapaulo.demo.api.requests.CreateUser;
import almeida.rochapaulo.demo.entities.UserCredential;
import almeida.rochapaulo.demo.entities.UserProfile;

public class UserManagement {

	private final Mapper<UserProfile> profileMapper;
	private final Mapper<UserCredential> credentialMapper;
	
	@Inject
	public UserManagement(MappingManager manager) {
		profileMapper = manager.mapper(UserProfile.class);
		credentialMapper = manager.mapper(UserCredential.class);
	}
	
	public UserProfile createUser(CreateUser createUser) {
		
		UUID userID = UUID.randomUUID();
		
		UserProfile profile = new UserProfile();
		profile.setUserId(userID);
		profile.setEmail(createUser.getEmail());
		profile.setFirstName(createUser.getFirstName());
		profile.setLastName(createUser.getLastName());
		profile.setSince(new Date());
		profileMapper.save(profile);
		
		UserCredential credential = new UserCredential();
		credential.setUserId(userID);
		credential.setEmail(createUser.getEmail());
		credential.setPassword(createUser.getPassword());
		credentialMapper.save(credential);
	
		return profile;
	}
	
}
