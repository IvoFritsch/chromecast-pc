/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chromecast.pc.server;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jcodec.api.FrameGrab;
import org.jcodec.common.DemuxerTrack;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.demuxer.MP4Demuxer;
import org.jcodec.containers.mp4.demuxer.MP4DemuxerTrackMeta;
import org.jcodec.scale.AWTUtil;

/**
 * Servlet that provides the video stream, it supports HTTP range requests to increase streaming seek speed
 * 
 * @author Ivo
 */
public class StreamingServlet extends HttpServlet {
    
    private static File movie = new File("test-videos/wildlife.mp4");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
            if(splitted[2].equals("frameAt")){
                doFrameAt(splitted, req, resp);
                return;
            }
        }
        
        // obtains ServletContext
        ServletContext context = getServletContext();
        // gets MIME type of the file
        String mimeType = context.getMimeType(movie.getName());
        if (mimeType == null) {
            // set to binary type if MIME mapping not found
            mimeType = "application/octet-stream";
        }   // modifies response
        resp.setContentType(mimeType);
        resp.setHeader("Accept-Ranges", "bytes");
        resp.setHeader("ETag", "cab08b36195edb1a1231d2d09fa450e0");
        String range = req.getHeader("Range");
        int rangeBytes = 0;
        if(range != null){
            rangeBytes = new Integer(range.replace("bytes=", "").split("\\-")[0]);
        }
        if(rangeBytes > 0){
            resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        } else {
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        try(InputStream is = new FileInputStream(movie); ServletOutputStream os = resp.getOutputStream()){
            int totalLength = is.available();
            if(rangeBytes > 0){
                resp.setHeader("Content-Range", "bytes "+rangeBytes+"-"+(totalLength-1)+"/"+totalLength);
            }
            is.skip(rangeBytes);
            resp.setContentLength(is.available());
            byte[] arr = new byte[5096];
            while(is.read(arr) != -1){
                os.write(arr);
            }
        } catch (Exception e){
            //e.printStackTrace();
        }
    }
    
    protected void doFrameAt(String[] splitted, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(splitted.length != 4){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        int second;
        FileChannelWrapper ch = null;
        try{
            second = new Integer(splitted[3]);
            Picture picture;
            BufferedImage bufferedImage;
            ch = NIOUtils.readableChannel(movie);
            picture = FrameGrab.createFrameGrab(ch).seekToSecondSloppy(second).getNativeFrame();
            bufferedImage = AWTUtil.toBufferedImage(picture);
            ByteArrayOutputStream memOs = new ByteArrayOutputStream(1000);
            ImageIO.write(bufferedImage, "png", memOs);
            byte[] imageBytes = memOs.toByteArray();
            resp.setContentType("image/png");
            try(ServletOutputStream os = resp.getOutputStream()){
                os.write(imageBytes);
            } catch (Exception e){
                throw e;
            }
        } catch(Exception e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } finally {
            NIOUtils.closeQuietly(ch);
        }
    }
    
    public static void main(String[] args) throws IOException {
        FileChannelWrapper ch = NIOUtils.readableFileChannel(movie.toString());
        MP4Demuxer demuxer = MP4Demuxer.createMP4Demuxer(ch);
        DemuxerTrack video_track = demuxer.getVideoTrack();
        System.out.println("video_duration: " + video_track.getMeta().getTotalDuration());
        System.out.println("video_frame: " + video_track.getMeta().getTotalFrames());
    }
}
