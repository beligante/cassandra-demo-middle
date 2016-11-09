package almeida.rochapaulo.demo.api.responses;

import java.util.Date;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateUserResponse {

	private final UUID userId;
	private final String firstName;
	private final String lastName;
	private final String email;
	private final Date since;
	
}
