/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chromecast.pc;

import chromecast.pc.server.EmbeddedServer;
import java.io.IOException;
import su.litvak.chromecast.api.v2.ChromeCasts;

/**
 *
 * @author Pedrivo
 */
public class ChromecastPc {

    
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        
        ChromeCasts.startDiscovery();
        EmbeddedServer.start();
    }
    
    
}
