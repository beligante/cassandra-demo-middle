package almeida.rochapaulo.demo.api.responses;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 
 * @author rochapaulo
 *
 */
@Builder
@Getter
@AllArgsConstructor
public class CreatePhotoResponse {

    private final UUID photoUUID;
    
}
