/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chromecast.pc;

import chromecast.pc.server.EmbeddedServer;
import java.io.IOException;
import java.util.List;
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
        
//        
//        while(true){
//            if(ChromeCasts.get().size() > 0) break;
//            Thread.sleep(400);
//        }
//        ChromeCast ch = ChromeCasts.get().get(0);
//        Status status = ch.getStatus();
//        System.out.println("st "+status);
//        ch.launchApp("CC1AD845");
//        ch.load("Big Buck Bunny",           // Media title
//                "",  // URL to thumbnail based on media URL
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", // media URL
//                null // media content type (optional, will be discovered automatically)
//        );
//        
//        ch.play();
//         MediaStatus mediaStatus = ch.getMediaStatus();
//         System.out.println("ms "+mediaStatus);
         //ch.stopApp();
        // TODO code application logic here
    }
    
    
}
