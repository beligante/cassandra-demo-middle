package almeida.rochapaulo.cassandra.demo.lookuptables;

import java.util.UUID;

import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.Mapper.Option;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import almeida.rochapaulo.cassandra.demo.entity.User;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author rochapaulo
 *
 */
public class UserByUsername implements LookupTable<User> {

	private final Mapper<LookupEntry> mapper;
	
	public UserByUsername(MappingManager mappingManager) {
		mapper = mappingManager.mapper(LookupEntry.class);
	}
	
	@Override
	public Statement save(User user, Option... options) {
		return mapper.saveQuery(new LookupEntry(user.getUuid(), user.getUsername()), options);
	}

	@Getter @Setter
	@Table(name = "user_lookup_by_username")
	class LookupEntry {
		
		@Column(name = "user_id")
		private UUID userId;
		
		@PartitionKey
		@Column(name = "username")
		private String username;
		
		LookupEntry() {
			super();
		}
		
		LookupEntry(UUID userId, String username) {
			this.userId = userId;
			this.username = username;
		}
		
	}
	
}
