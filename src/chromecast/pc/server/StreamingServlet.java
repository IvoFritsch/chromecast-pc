/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chromecast.pc.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that provides the video stream, it supports HTTP range requests to increase streaming seek speed
 * 
 * @author Ivo
 */
public class StreamingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        File movie = new File("WildLife.mp4");
        // obtains ServletContext
        ServletContext context = getServletContext();
        // gets MIME type of the file
        String mimeType = context.getMimeType("BigBuckBunny.mp4");
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
}
