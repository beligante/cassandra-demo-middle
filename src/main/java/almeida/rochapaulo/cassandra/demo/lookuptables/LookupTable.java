package almeida.rochapaulo.cassandra.demo.lookuptables;

import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Mapper.Option;

/**
 * 
 * @author rochapaulo
 *
 */
public interface LookupTable<E> {

	Statement save(E entity, Option... options);
	
}
