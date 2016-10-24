package almeida.rochapaulo.demo.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;

import almeida.rochapaulo.demo.accessors.UserProfileAccessor;
import almeida.rochapaulo.demo.api.requests.CreateUser;
import almeida.rochapaulo.demo.entities.UserCredential;
import almeida.rochapaulo.demo.entities.UserProfile;

public class UserManagement {

	private final Hasher hasher = Hasher.instance();
	private final MappingManager manager;
	private final Mapper<UserProfile> profileMapper;
	private final Mapper<UserCredential> credentialMapper;
	private final UserProfileAccessor profilesAccessor;
	
	@Autowired
	public UserManagement(MappingManager manager) {
		this.manager = manager;
		profileMapper = manager.mapper(UserProfile.class);
		credentialMapper = manager.mapper(UserCredential.class);
		profilesAccessor = manager.createAccessor(UserProfileAccessor.class);
	}
	
	public UserProfile createUser(CreateUser createUser) throws Exception {
		
		BatchStatement batch = new BatchStatement();
		
		UUID userID = UUID.randomUUID();
		
		UserProfile profile = new UserProfile();
		profile.setUserId(userID);
		profile.setEmail(createUser.getEmail());
		profile.setFirstName(createUser.getFirstName());
		profile.setLastName(createUser.getLastName());
		profile.setSince(new Date());
		
		UserCredential credential = new UserCredential();
		credential.setUserId(userID);
		credential.setEmail(createUser.getEmail());
		credential.setPassword(hasher.hash(createUser.getPassword()));
		
		batch.add(profileMapper.saveQuery(profile));
		batch.add(credentialMapper.saveQuery(credential));
	
		Session session = manager.getSession();
		session.execute(batch);
		
		return profile;
	}

	public UserProfile findProfileById(UUID userId) {
		return profileMapper.get(userId);
	}

	public List<UserProfile> getAllProfiles() {
		Result<UserProfile> profiles = profilesAccessor.getAll();
		return profiles.all();
	}

}
