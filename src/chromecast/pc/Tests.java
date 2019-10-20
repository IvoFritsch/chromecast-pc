/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chromecast.pc;

import java.io.IOException;
import java.util.Scanner;
import su.litvak.chromecast.api.v2.ChromeCast;
import su.litvak.chromecast.api.v2.ChromeCasts;

/**
 *
 * @author Ivo
 */
public class Tests {
    public static void main(String[] args) throws IOException, InterruptedException {
        
        ChromeCasts.startDiscovery();
        //EmbeddedServer.start();
        
        
        while(true){
            if(ChromeCasts.get().size() > 0) break;
            Thread.sleep(400);
        }
        ChromeCast ch = ChromeCasts.get().get(0);
        //Status status = ch.getStatus();
        //System.out.println("st "+status);
        ch.launchApp("CC1AD845");
        new Scanner(System.in).nextLine();
        ch.load("WildLife",           // Media title
                "",  // URL to thumbnail based on media URL
                "http://192.168.1.6:3132/stream", // media URL
                null // media content type (optional, will be discovered automatically)
        );
        ch.play();
         //MediaStatus mediaStatus = ch.getMediaStatus();
         //System.out.println("ms "+mediaStatus);
         //ch.stopApp();
        // TODO code application logic here
    }
}
