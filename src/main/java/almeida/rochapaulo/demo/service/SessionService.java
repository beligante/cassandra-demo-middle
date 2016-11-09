package almeida.rochapaulo.demo.service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.Mapper.Option;
import com.datastax.driver.mapping.MappingManager;

import almeida.rochapaulo.demo.entities.Session;

/**
 * 
 * @author rochapaulo
 *
 */
public class SessionService {

    private static final int TTL_IN_SEC = (int) TimeUnit.HOURS.toSeconds(1L);
    private final Mapper<Session> sessionMapper;
    
    @Autowired
    public SessionService(MappingManager manager) {
        
        sessionMapper = manager.mapper(Session.class);
    }
    
    /**
     * 
     * @param userUUID
     * @return
     */
    public CompletableFuture<UUID> put(UUID userUUID) {
    
        return CompletableFuture.supplyAsync(() -> {
            
            UUID sessionID = UUID.randomUUID();
            sessionMapper.save(new Session(sessionID, userUUID), Option.ttl(TTL_IN_SEC));
            
            return sessionID;
        });
    }
    
    /**
     * 
     * @param sessionUUID
     * @return
     */
    public CompletableFuture<Optional<Session>> getSession(UUID sessionUUID) {
        
        return CompletableFuture.supplyAsync(() -> Optional.ofNullable(sessionMapper.get(sessionUUID)));
    }
    
    /**
     * 
     * @param sessionUUID
     * @return
     */
    public void purge(UUID sessionUUID) {
        
        sessionMapper.delete(sessionUUID);
    }
    
}
