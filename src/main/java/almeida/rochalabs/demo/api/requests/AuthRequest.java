package almeida.rochalabs.demo.api.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author rochapaulo
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

    private String email;
    private String password;

}
