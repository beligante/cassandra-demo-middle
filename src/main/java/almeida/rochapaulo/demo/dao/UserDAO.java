package almeida.rochapaulo.demo.dao;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;

import almeida.rochapaulo.demo.api.requests.CreateUser;
import almeida.rochapaulo.demo.dao.accessor.UserProfileAccessor;
import almeida.rochapaulo.demo.entities.UserCredential;
import almeida.rochapaulo.demo.entities.UserProfile;
import almeida.rochapaulo.demo.service.Hasher;
import almeida.rochapaulo.demo.service.exceptions.EntityAlreadyExists;

public class UserDAO {

	private final Hasher hasher = Hasher.instance();
	private final Mapper<UserProfile> profileMapper;
	private final Mapper<UserCredential> credentialMapper;
	private final UserProfileAccessor profilesAccessor;
	
	@Autowired
	public UserDAO(MappingManager manager) {
		profileMapper = manager.mapper(UserProfile.class);
		credentialMapper = manager.mapper(UserCredential.class);
		profilesAccessor = manager.createAccessor(UserProfileAccessor.class);
	}
	
	public UserProfile createUser(CreateUser createUser) throws Exception {
		
		if (credentialMapper.get(createUser.getEmail()) != null) {
			throw new EntityAlreadyExists();
		}
		
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
		
		profileMapper.save(profile);
		credentialMapper.save(credential);
	
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
