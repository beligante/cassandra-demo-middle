package almeida.rochapaulo.cassandra.demo.entity;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author rochapaulo
 *
 */
@Getter @Setter
@Table(name = "users")
public class User {

	@PartitionKey
	@Column(name = "user_id")
	private UUID uuid;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;

}
