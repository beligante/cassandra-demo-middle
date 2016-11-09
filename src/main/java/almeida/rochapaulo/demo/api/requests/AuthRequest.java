package almeida.rochapaulo.demo.api.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author rochapaulo
 *
 */
@Data
@NoArgsConstructor
public class AuthRequest {

    private String email;
    private String password;

}
