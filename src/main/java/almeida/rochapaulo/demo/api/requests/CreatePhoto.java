package almeida.rochapaulo.demo.api.requests;

import java.util.Set;

import lombok.Data;

@Data
public class CreatePhoto {

	private String name;
	private String description;
	private Set<String> tags;
	private String addeDdate;
	private String base64Image;
	
}
