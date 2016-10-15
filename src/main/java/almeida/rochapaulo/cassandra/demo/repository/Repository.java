package almeida.rochapaulo.cassandra.demo.repository;

/**
 * 
 * @author rochapaulo
 *
 */
public interface Repository<E> {

	void save(E entity);
	
}
