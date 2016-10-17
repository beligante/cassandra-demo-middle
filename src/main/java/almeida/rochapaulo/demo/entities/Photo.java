package almeida.rochapaulo.demo.entities;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Table(name = "photos")
public class Photo {

	@PartitionKey
	@Column(name = "photo_id")
	private UUID uuid;
	
	@Column(name = "user_id")
	private UUID userId;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "image")
	private String base64Image;
	
	@Column(name = "location")
	private String location;
	
	@Column(name = "tags")
	private Set<String> tags;
	
	@Column(name = "added_date")
	private Date addedDate;
	
}
