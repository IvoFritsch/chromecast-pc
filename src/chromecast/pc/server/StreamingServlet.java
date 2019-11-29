/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chromecast.pc.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jcodec.api.FrameGrab;
import org.jcodec.common.DemuxerTrackMeta;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.demuxer.MP4Demuxer;
import org.jcodec.scale.AWTUtil;
import org.json.JSONObject;

/**
 * Servlet that provides the video stream, it supports HTTP range requests to increase streaming seek speed
 * 
 * @author Ivo
 */
public class StreamingServlet extends HttpServlet {
    
    private volatile static File serving = null;
    private volatile static String ETag = null;
    private volatile static int openStreams = 0;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(serving == null){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String uri = req.getRequestURI();
        if(!uri.endsWith("/")){
            uri += "/";
        }
        String[] splitted = uri.split("/");
        int index = 0;
        for (String string : splitted) {
            index++;
        }
        if(splitted.length > 2){
            switch(splitted[2]){
                case "frameAt":
                    doFrameAt(splitted, req, resp);
                    break;
                case "metadata":
                    doMetadata(req, resp);
                    break;
            }
            return;
        }
        
        // obtains ServletContext
        ServletContext context = getServletContext();
        // gets MIME type of the file
        String mimeType = context.getMimeType(serving.getName());
        if (mimeType == null) {
            // set to binary type if MIME mapping not found
            mimeType = "application/octet-stream";
        }   // modifies response
        resp.setContentType(mimeType);
        resp.setHeader("Accept-Ranges", "bytes");
        resp.setHeader("ETag", ETag);
        String range = req.getHeader("Range");
        long rangeBytes = 0;
        if(range != null){
            rangeBytes = new Long(range.replace("bytes=", "").split("\\-")[0]);
        }
        if(rangeBytes > 0){
            resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        } else {
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        try(InputStream is = new FileInputStream(serving); ServletOutputStream os = resp.getOutputStream()){
            openStreams++;
            long totalLength = serving.length();
            if(rangeBytes > 0){
                resp.setHeader("Content-Range", "bytes "+rangeBytes+"-"+(totalLength-1)+"/"+totalLength);
            }
            is.skip(rangeBytes);
            resp.setHeader("Content-Length", Long.toString(totalLength - rangeBytes));
            byte[] arr = new byte[10192];
            while(is.read(arr) != -1){
                os.write(arr);
            }
        } catch (Exception e){
            //e.printStackTrace();
        } finally {
            openStreams--;
        }
    }
    
    private void doFrameAt(String[] splitted, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(splitted.length != 4){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        int second;
        FileChannelWrapper ch = null;
        try{
            openStreams++;
            second = new Integer(splitted[3]);
            Picture picture;
            BufferedImage bufferedImage;
            ch = NIOUtils.readableChannel(serving);
            picture = FrameGrab.createFrameGrab(ch).seekToSecondSloppy(second).getNativeFrame();
            resp.setContentType("image/jpeg");
            try(ServletOutputStream os = resp.getOutputStream(); ByteArrayOutputStream memOs = new ByteArrayOutputStream()){
                bufferedImage = AWTUtil.toBufferedImage(picture);
                // Writes to an in-memory OutputStream instead of directly to the socket because this method take long to convert the image, creating an ugly load in the frontend
                ImageIO.write(bufferedImage, "jpeg", memOs);
                memOs.writeTo(os);
            } catch (Exception e){
                throw e;
            } 
        } catch(Exception e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } finally {
            NIOUtils.closeQuietly(ch);
            openStreams--;
        }
    }
    
    private void doMetadata(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        FileChannelWrapper ch = NIOUtils.readableFileChannel(serving.toString());
        MP4Demuxer demuxer = MP4Demuxer.createMP4Demuxer(ch);
        DemuxerTrackMeta meta = demuxer.getVideoTrack().getMeta();
        
        JSONObject ret = new JSONObject().put("duration", (int) meta.getTotalDuration());
        
        try(ServletOutputStream os = resp.getOutputStream()){
            os.print(ret.toString());
        } catch (Exception e){
        }
    }
    
    public static boolean startServing(File f){
        
        if(serving != null){
            return false;
        }
        if(!f.exists() || f.isDirectory()){
            return false;
        }
        ETag = UUID.randomUUID().toString();
        serving = f;
        return true;
    }
    
    public static boolean stopServing(){
        if(openStreams > 0){
            return false;
        }
        serving = null;
        ETag = null;
        return true;
    }
}
