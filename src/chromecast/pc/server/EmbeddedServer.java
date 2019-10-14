/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chromecast.pc.server;

import chromecast.pc.NoLogging;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 *
 * @author Pedrivo
 */
public class EmbeddedServer implements Runnable {
    private static org.eclipse.jetty.server.Server server;
    private static final int SERVER_PORT = 3132;
    public static final boolean RUNNING_FROM_JAR = EmbeddedServer.class.getResource("EmbeddedServer.class").toString().startsWith("jar:");
    
    public static void start(){
        new Thread(new EmbeddedServer()).start();
    }
    
    
    @Override
    public void run() {
        try {
            org.eclipse.jetty.util.log.Log.setLog(new NoLogging());
            server = new org.eclipse.jetty.server.Server(SERVER_PORT);
            
//            ServletContextHandler ctxHand = new ServletContextHandler(ServletContextHandler.SESSIONS);
//            ctxHand.setContextPath("/");
//            ctxHand.setServer(server);
            
            ServletContextHandler handler = new ServletContextHandler(server, "/");
            handler.addServlet(AppServlet.class, "/app/*");
//            ctxHand.addServlet(new ServletHolder(new AppServlet()), "/app");
            
            server.setHandler(handler);
            server.start();
            //HsqldbManager.updateTrayMenus();
            server.join();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static InputStream getResource(String name){
        if(name.contains("..")) return null;
        if(RUNNING_FROM_JAR){
            if(!name.startsWith("/")) name = "/" + name;
            return EmbeddedServer.class.getResourceAsStream(name);
        } else {
            try {
                return new FileInputStream(new File(name));
            } catch (FileNotFoundException ex) {
                return null;
            }
        }
    }
    
}
