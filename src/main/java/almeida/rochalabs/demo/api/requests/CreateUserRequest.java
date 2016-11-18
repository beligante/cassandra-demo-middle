package almeida.rochalabs.demo.api.requests;

import lombok.Data;

/**
 * 
 * @author rochapaulo
 *
 */
@Data
public class CreateUserRequest {

    private String email;
    private String password;
    private String firstName;
    private String lastName;

}
