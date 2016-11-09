package almeida.rochapaulo.demo.api.responses;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyCredentialsResponse {

	private final UUID userId;
}
