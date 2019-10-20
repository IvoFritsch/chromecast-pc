/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chromecast.pc;

import chromecast.pc.server.EmbeddedServer;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import su.litvak.chromecast.api.v2.ChromeCast;
import su.litvak.chromecast.api.v2.ChromeCasts;
import su.litvak.chromecast.api.v2.MediaStatus;
import su.litvak.chromecast.api.v2.Status;

/**
 *
 * @author Pedrivo
 */
public class ChromecastPc {

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        
        ChromeCasts.startDiscovery();
        EmbeddedServer.start();
    }
    
    
}
