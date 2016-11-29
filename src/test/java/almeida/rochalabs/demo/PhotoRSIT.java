package almeida.rochalabs.demo;

import static com.datastax.driver.core.querybuilder.QueryBuilder.truncate;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.io.Resources.getResource;
import static com.google.common.io.Resources.toByteArray;
import static org.springframework.http.HttpMethod.POST;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.cassandraunit.CassandraCQLUnit;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;

import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TableMetadata;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import almeida.rochalabs.demo.api.requests.CreatePhotoRequest;
import almeida.rochalabs.demo.api.requests.CreateUserRequest;
import almeida.rochalabs.demo.api.responses.CreateUserResponse;
import almeida.rochalabs.demo.api.responses.PhotoMetadata;
import almeida.rochalabs.demo.conf.LocalConfiguration;
import almeida.rochalabs.demo.data.service.PhotoService;
import almeida.rochalabs.demo.data.service.SessionService;
import almeida.rochalabs.demo.data.service.UserManagement;

/**
 * 
 * @author rochapaulo
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = LocalConfiguration.class)
public class PhotoRSIT {

    @ClassRule
    public static CassandraCQLUnit cassandra = 
        new CassandraCQLUnit(new ClassPathCQLDataSet("cassandra/cql/load.cql"), "cassandra_config.yaml", 20000L);
    
    @Autowired 
    public TestRestTemplate client;
    
    @Autowired 
    public UserManagement userService;
    
    @Autowired
    public SessionService sessionService;
    
    @Autowired
    public PhotoService photoService;
    
    @After
    public void cleanUp() {
        
        Session session = cassandra.getSession();
        Metadata metadata = cassandra.getCluster().getMetadata();
        KeyspaceMetadata keyspaceMetadata = metadata.getKeyspace("demo");
        for (TableMetadata table : keyspaceMetadata.getTables()) {
            session.execute(truncate(table));
        }
        
    }
    
    @Test
    public void upload_success() throws Exception {
        
        CreateUserResponse user = 
                userService.createUser(new CreateUserRequest("rochapaulo@domain.com", "password", "Paulo", "Almeida")).get();
        
        CreatePhotoRequest body = new CreatePhotoRequest();
        body.setUserId(user.getUserId());
        body.setName("Iron Man");
        body.setDescription("Iron Man - The Avengers");
        body.setTags(Sets.newHashSet("iron man", "avengers", "marvel"));
        body.setBase64Image(encodeImage("ironman.jpg"));
        
        UUID sessionID = sessionService.put(user.getUserId()).get();
        
        ResponseEntity<Void> response = 
                client.exchange("/api/secure/photos", POST, new HttpEntity<>(body, authHeaders(sessionID)), Void.class);
        
        Assert.assertEquals(HttpStatus.CREATED , response.getStatusCode());
        
        HttpHeaders headers = response.getHeaders();
        
        String location = headers.get(HttpHeaders.LOCATION).get(0);
        Assert.assertTrue(location.startsWith("/photos/"));
        
        UUID.fromString(location.replace("/photos/", ""));
    }
    
    @Test
    public void getAllPhotos_noPhotos() throws Exception {
        
        UUID userID1 = UUID.randomUUID();
        UUID sessionID = sessionService.put(userID1).get();
        
        ResponseEntity<List<PhotoMetadata>> response = 
                client.exchange(
                        "/api/secure/photos", 
                        HttpMethod.GET, 
                        new HttpEntity<>(authHeaders(sessionID)), 
                        new ParameterizedTypeReference<List<PhotoMetadata>>() {});
        
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    @Test
    public void getAllPhotos_success() throws Exception {
        
        UUID userID1 = UUID.randomUUID();
        UUID userID2 = UUID.randomUUID();
        UUID userID3 = UUID.randomUUID();
        
        CreatePhotoRequest ironman = new CreatePhotoRequest();
        ironman.setUserId(userID1);
        ironman.setName("Iron Man");
        ironman.setDescription("Iron Man - The Avengers");
        ironman.setTags(Sets.newHashSet("iron man", "avengers", "marvel"));
        ironman.setBase64Image(encodeImage("ironman.jpg"));
        
        CreatePhotoRequest cnTower = new CreatePhotoRequest();
        cnTower.setUserId(userID2);
        cnTower.setName("CN Tower");
        cnTower.setDescription("Top of Canada - CN Tower");
        cnTower.setTags(Sets.newHashSet("CNTower", "Skyscraper", "Toronto"));
        cnTower.setBase64Image(encodeImage("cntower.jpg"));
        
        UUID ironmanUUID = photoService.upload(ironman).get().getPhotoUUID();
        UUID cnTowerUUID = photoService.upload(cnTower).get().getPhotoUUID();
        
        UUID sessionID = sessionService.put(userID3).get();
        
        ResponseEntity<List<PhotoMetadata>> response = 
                client.exchange(
                        "/api/secure/photos", 
                        HttpMethod.GET, 
                        new HttpEntity<>(authHeaders(sessionID)), 
                        new ParameterizedTypeReference<List<PhotoMetadata>>() {});
        
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        
        ImmutableMap<String, PhotoMetadata> photos = uniqueIndex(response.getBody(), PhotoMetadata::getName);
        PhotoMetadata ironManMetadata = photos.get("Iron Man");
        Assert.assertEquals(userID1, ironManMetadata.getUser());
        Assert.assertEquals(newHashSet("iron man", "avengers", "marvel"), ironManMetadata.getTags());
        Assert.assertEquals("/api/secure/bucket/images/" + ironmanUUID, ironManMetadata.getLocation());
        Assert.assertEquals("Iron Man - The Avengers", ironManMetadata.getDescription());
        
        PhotoMetadata cnTowerMetadata = photos.get("CN Tower");
        Assert.assertEquals(userID2, cnTowerMetadata.getUser());
        Assert.assertEquals(newHashSet("CNTower", "Skyscraper", "Toronto"), cnTowerMetadata.getTags());
        Assert.assertEquals("/api/secure/bucket/images/" + cnTowerUUID, cnTowerMetadata.getLocation());
        Assert.assertEquals("Top of Canada - CN Tower", cnTowerMetadata.getDescription());
    }
    
    @Test
    public void getPhotoByID_success() throws Exception {
        
        UUID userID1 = UUID.randomUUID();
        UUID sessionID = sessionService.put(userID1).get();
        
        CreatePhotoRequest ironman = new CreatePhotoRequest();
        ironman.setUserId(userID1);
        ironman.setName("Iron Man");
        ironman.setDescription("Iron Man - The Avengers");
        ironman.setTags(Sets.newHashSet("iron man", "avengers", "marvel"));
        ironman.setBase64Image(encodeImage("ironman.jpg"));
        
        UUID ironmanUUID = photoService.upload(ironman).get().getPhotoUUID();
        
        ResponseEntity<PhotoMetadata> response = 
                client.exchange(
                        "/api/secure/photos/" + ironmanUUID, 
                        HttpMethod.GET, 
                        new HttpEntity<>(authHeaders(sessionID)), 
                        PhotoMetadata.class);
        
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        
        PhotoMetadata ironManMetadata = response.getBody();
        Assert.assertEquals(userID1, ironManMetadata.getUser());
        Assert.assertEquals(newHashSet("iron man", "avengers", "marvel"), ironManMetadata.getTags());
        Assert.assertEquals("/api/secure/bucket/images/" + ironmanUUID, ironManMetadata.getLocation());
        Assert.assertEquals("Iron Man - The Avengers", ironManMetadata.getDescription());
    }
    
    @Test
    public void getPhotoByID_notFound() throws Exception {
        
        UUID userID1 = UUID.randomUUID();
        UUID sessionID = sessionService.put(userID1).get();
        
        ResponseEntity<PhotoMetadata> response = 
                client.exchange(
                        "/api/secure/photos/" + UUID.randomUUID(), 
                        HttpMethod.GET, 
                        new HttpEntity<>(authHeaders(sessionID)), 
                        PhotoMetadata.class);
        
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getPhotoByUserID_success() throws Exception {
        
        UUID userID1 = UUID.randomUUID();
        UUID sessionID = sessionService.put(userID1).get();
        
        CreatePhotoRequest ironman = new CreatePhotoRequest();
        ironman.setUserId(userID1);
        ironman.setName("Iron Man");
        ironman.setDescription("Iron Man - The Avengers");
        ironman.setTags(Sets.newHashSet("iron man", "avengers", "marvel"));
        ironman.setBase64Image(encodeImage("ironman.jpg"));
        
        UUID ironmanUUID = photoService.upload(ironman).get().getPhotoUUID();
        
        ResponseEntity<List<PhotoMetadata>> response = 
                client.exchange(
                        "/api/secure/photos/user/" + userID1, 
                        HttpMethod.GET, 
                        new HttpEntity<>(authHeaders(sessionID)), 
                        new ParameterizedTypeReference<List<PhotoMetadata>>() {});
        
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        
        PhotoMetadata ironManMetadata = response.getBody().get(0);
        Assert.assertEquals(userID1, ironManMetadata.getUser());
        Assert.assertEquals(newHashSet("iron man", "avengers", "marvel"), ironManMetadata.getTags());
        Assert.assertEquals("/api/secure/bucket/images/" + ironmanUUID, ironManMetadata.getLocation());
        Assert.assertEquals("Iron Man - The Avengers", ironManMetadata.getDescription());
        
    }
    
    @Test
    public void getPhotoByUserID_notFound() throws Exception {
     
        UUID userID1 = UUID.randomUUID();
        UUID sessionID = sessionService.put(userID1).get();
        
        ResponseEntity<PhotoMetadata> response = 
                client.exchange(
                        "/api/secure/photos/user/" + UUID.randomUUID(), 
                        HttpMethod.GET, 
                        new HttpEntity<>(authHeaders(sessionID)), 
                        PhotoMetadata.class);
        
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    private LinkedMultiValueMap<String, String> authHeaders(UUID sessionID) throws Exception {
        
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.COOKIE, "SID=" + sessionID.toString());
        
        return headers;
    }
    
    private String encodeImage(String resource) throws Exception {
        
        return Base64.getEncoder().encodeToString(toByteArray(getResource(resource)));
    }
    
}
