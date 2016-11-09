package almeida.rochapaulo.demo.api.requests;

import lombok.Data;

@Data
public class CreateUserRequest {

	private String email;
	private String password;
	private String firstName;
	private String lastName;
	
}
