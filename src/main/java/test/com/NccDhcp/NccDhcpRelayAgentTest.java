package test.com.NccDhcp; 

import com.NccDhcp.NccDhcpRelayAgent;
import com.NccDhcp.NccDhcpRelayAgentData;
import com.NccSystem.NccUtils;
import com.NccSystem.SQL.NccSQLPool;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.junit.*;

import java.io.IOException;

import static com.Ncc.sqlPool;
import static org.junit.Assert.assertNotNull;

/** 
* NccDhcpRelayAgent Tester. 
* 
* @author <Authors name> 
* @since <pre>июл 10, 2016</pre> 
* @version 1.0 
*/ 
public class NccDhcpRelayAgentTest { 

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
}

@BeforeClass
public static void beforeClass(){
    CompositeConfiguration config = new CompositeConfiguration();
    try {
        String current = new java.io.File(".").getCanonicalPath();
    } catch (IOException e) {
        e.printStackTrace();
    }
    config.addConfiguration(new SystemConfiguration());
    try {
        config.addConfiguration(new PropertiesConfiguration("config.properties"));
    } catch (ConfigurationException e) {
        e.printStackTrace();
    }

    String connectString = "jdbc:mysql://" + config.getString("db.host") + ":" + config.getInt("db.port") + "/" +
            config.getString("db.dbname") + "?useUnicode=yes&characterEncoding=UTF-8";
    sqlPool = new NccSQLPool(connectString, config.getString("db.user"), config.getString("db.password"));
}

/**
* 
* Method: getRelayAgent() 
* 
*/ 
@Test
public void testGetRelayAgent() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getRelayAgent(Integer id) 
* 
*/ 
@Test
public void testGetRelayAgentId() throws Exception { 
    NccDhcpRelayAgentData agentData = new NccDhcpRelayAgent().getRelayAgent(1);

    assertNotNull(agentData);
}

/** 
* 
* Method: getRelayAgentByIP(Long ip) 
* 
*/ 
@Test
public void testGetRelayAgentByIP() throws Exception { 
    NccDhcpRelayAgentData agentData = new NccDhcpRelayAgent().getRelayAgentByIP(NccUtils.ip2long("10.201.0.2"));

    Assert.assertNotNull(agentData);
} 

/** 
* 
* Method: getRelayAgentTypes() 
* 
*/ 
@Test
public void testGetRelayAgentTypes() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: createRelayAgent(NccDhcpRelayAgentData relayAgentData) 
* 
*/ 
@Test
public void testCreateRelayAgent() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: updateRelayAgent(NccDhcpRelayAgentData relayAgentData) 
* 
*/ 
@Test
public void testUpdateRelayAgent() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: deleteRelayAgent(Integer id) 
* 
*/ 
@Test
public void testDeleteRelayAgent() throws Exception { 
//TODO: Test goes here... 
} 


/** 
* 
* Method: fillRelayAgentType(CachedRowSetImpl rs) 
* 
*/ 
@Test
public void testFillRelayAgentType() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = NccDhcpRelayAgent.getClass().getMethod("fillRelayAgentType", CachedRowSetImpl.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

} 
