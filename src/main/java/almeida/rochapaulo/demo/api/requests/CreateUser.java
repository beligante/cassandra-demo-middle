package almeida.rochapaulo.demo.api.requests;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateUser {

	private String email;
	private String password;
	private String firstName;
	private String lastName;
	
}
