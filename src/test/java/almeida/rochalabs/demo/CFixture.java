package almeida.rochalabs.demo;

import static com.datastax.driver.core.querybuilder.QueryBuilder.truncate;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TableMetadata;

/**
 * 
 * @author rochapaulo
 *
 */
public class CFixture implements TestRule {

    public static CFixture connect(String keyspace) {
        return CFixture.connect("127.0.0.1", 9042, keyspace);
    }
    
    public static CFixture connect(String contactPoint, int port, String keyspace) {
        return new CFixture(contactPoint, port, keyspace);
    }
    
    
    private final Session session;
    private final Cluster cluster;
    private final String keyspace;
    
    private CFixture(String contactPoint, int port, String keyspace) {
        
        this.keyspace = keyspace;
        cluster = Cluster.builder().addContactPoint(contactPoint).withPort(port).build();
        session = cluster.connect(keyspace);
    }
    
    @Override
    public Statement apply(Statement base, Description description) {
        
        return new Statement() {
            
            @Override
            public void evaluate() throws Throwable {

                cluster.connect(keyspace);
                
                try {

                    base.evaluate();
                    
                } finally {
                    
                    KeyspaceMetadata ksMetadata = cluster.getMetadata().getKeyspace(keyspace);
                    for (TableMetadata table : ksMetadata.getTables()) {
                        session.executeAsync(truncate(table));
                    }
                    
                    cluster.closeAsync();
                }
                
            }
            
        };
    }
    
    public Session getSession() {
        
        return session;
    }

}
