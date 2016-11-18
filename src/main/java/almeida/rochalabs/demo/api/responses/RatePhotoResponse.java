package almeida.rochalabs.demo.api.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatePhotoResponse {

    private Integer stars;
    private Long votes;

}
