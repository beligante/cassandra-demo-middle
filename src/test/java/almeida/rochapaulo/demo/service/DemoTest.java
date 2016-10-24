package almeida.rochapaulo.demo.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import almeida.rochapaulo.demo.Bootstrap;
import almeida.rochapaulo.demo.api.requests.CreatePhoto;
import almeida.rochapaulo.demo.api.requests.CreateUser;

@Ignore
public class DemoTest {

	@Test
	public void createUser() throws Exception {
	
		ApplicationContext ctx = SpringApplication.run(Bootstrap.class);
		
		UserManagement service = ctx.getBean(UserManagement.class);
		UUID userId = null;
		{
			CreateUser request = new CreateUser();
			request.setEmail("almeida.paulorocha@gmail.com");
			request.setFirstName("Paulo");
			request.setLastName("Almeida");
			request.setPassword("password");
			
			userId = service.createUser(request).getUserId();
		}
		
		PhotoService photoService = ctx.getBean(PhotoService.class);
		{
			CreatePhoto request = new CreatePhoto();
			request.setName("CN Tower");
			request.setDescription("Trip to Canadá, 2005");
			request.setBase64Image(toBase64("CN_Tower.jpg"));
			
			Set<String> tags = new HashSet<>();
			tags.add("Canadá");
			tags.add("Toronto");
			tags.add("CN Tower");
			tags.add("Tower");
			request.setTags(tags);
			
			photoService.save(userId, request);
		}
		
	}
	
	private static String toBase64(String imageName) throws Exception {
		
		BufferedImage buff = ImageIO.read(new File(DemoTest.class.getClassLoader().getResource(imageName).getPath()));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(buff, "jpg", baos);
		
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		
		Encoder encoder = Base64.getEncoder();
		return encoder.encodeToString(imageInByte);
	}
	
}
