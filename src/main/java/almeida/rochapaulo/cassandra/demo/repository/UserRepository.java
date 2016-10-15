package almeida.rochapaulo.cassandra.demo.repository;

import java.util.List;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TimestampGenerator;
import com.datastax.driver.mapping.Mapper.Option;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import almeida.rochapaulo.cassandra.demo.entity.User;
import almeida.rochapaulo.cassandra.demo.lookuptables.LookupTable;

/**
 * 
 * @author rochapaulo
 *
 */
public class UserRepository implements Repository<User> {

	private final List<LookupTable<User>> tables;
	private final Session session;
	private final TimestampGenerator ts;
	
	@Inject
	public UserRepository(@Named("UserTables") List<LookupTable<User>> tables, Session session, TimestampGenerator ts) {
		this.tables = tables;
		this.session = session;
		this.ts = ts;
	}
	
	@Override
	public void save(User user) {
		final BatchStatement batch = new BatchStatement();
		tables.forEach(table -> batch.add(table.save(user, Option.timestamp(ts.next()))));
		session.execute(batch);
	}

}
