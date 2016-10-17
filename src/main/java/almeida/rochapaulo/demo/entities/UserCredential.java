package almeida.rochapaulo.demo.entities;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Table(name = "user_credentials")
public class UserCredential {

	@Column(name = "user_id")
	private UUID userId;
	
	@Column(name = "password")
	private String password;
	
	@PartitionKey
	@Column(name = "email")
	private String email;
	
}
