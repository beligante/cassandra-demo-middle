package almeida.rochapaulo.demo.api.requests;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreatePhoto {

	private String name;
	private String description;
	private String location;
	private Set<String> tags;
	private String addeDdate;
	private String base64Image;
	
}
