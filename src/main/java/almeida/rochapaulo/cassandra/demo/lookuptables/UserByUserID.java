package almeida.rochapaulo.cassandra.demo.lookuptables;

import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.Mapper.Option;
import com.datastax.driver.mapping.MappingManager;

import almeida.rochapaulo.cassandra.demo.entity.User;

/**
 * 
 * @author rochapaulo
 *
 */
public class UserByUserID implements LookupTable<User> {

	private final Mapper<User> mapper;
	
	public UserByUserID(MappingManager mappingManager) {
		mapper = mappingManager.mapper(User.class);
	}
	
	@Override
	public Statement save(User user, Option... options) {
		return mapper.saveQuery(user, options);
	}

}
