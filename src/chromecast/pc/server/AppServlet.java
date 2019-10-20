/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chromecast.pc.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import su.litvak.chromecast.api.v2.ChromeCasts;

/**
 *
 * @author Ivo
 */
public class AppServlet extends HttpServlet{

    private final String UNAUTHORIZED_RESPONSE = new JSONObject().put("status", "UNAUTHORIZED").toString();
    private final String EXPIRED_RESPONSE = new JSONObject().put("status", "EXPIRED").toString();
    private final String BAD_REQUEST_RESPONSE = new JSONObject().put("status", "BAD_REQUEST").toString();
    private static volatile int nextConnId = 1;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doMethod(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doMethod(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Credentials", "true");
        if(!EmbeddedServer.RUNNING_FROM_JAR) resp.addHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
        resp.addHeader("Access-Control-Allow-Headers", "Content-Type, jsessionid");
        resp.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    }
    
    private void doMethod(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.addHeader("Access-Control-Allow-Credentials", "true");
        if(!EmbeddedServer.RUNNING_FROM_JAR) resp.addHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
        resp.addHeader("Access-Control-Allow-Headers", "Content-Type, jsessionid");
        resp.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        try{
            String fullPath = req.getPathInfo();
            if(!fullPath.startsWith("/"))fullPath = "/" + fullPath;
            if(fullPath.equals("/") || fullPath.equals("/index.html")) fullPath = "/static/index.html";
            String masterPath = fullPath.split("/")[1];
            if(!masterPath.equals("static") && !fullPath.endsWith("/"))fullPath += "/";
            switch(masterPath){
                case "api":
                    supplyApi(req.getMethod()+" "+fullPath.split("/", 3)[2], req, resp);
                    break;
                case "static":
                    supplyStatic(fullPath.split("/", 3)[2], req, resp);
                    break;
            }
        } catch(Exception e){
        }
        if(!EmbeddedServer.RUNNING_FROM_JAR) addSameSiteCookieAttribute(resp);
    }
    
    private void supplyStatic(String resource, HttpServletRequest request, HttpServletResponse response){
        
        if(!EmbeddedServer.RUNNING_FROM_JAR) resource = "appbuild/" + resource;
        resource = "appbuild/" + resource;
        
        
        try{
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
            OutputStream outStream;
            // if you want to use a relative path to context root:
            try (InputStream fileStream = EmbeddedServer.getResource(resource)) {
                if(fileStream == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                // obtains ServletContext
                ServletContext context = getServletContext();
                // gets MIME type of the file
                String mimeType = context.getMimeType("a."+resource.substring(resource.lastIndexOf(".")));
                if (mimeType == null) {
                    // set to binary type if MIME mapping not found
                    mimeType = "application/octet-stream";
                }   // modifies response
                response.setContentType(mimeType);
                response.setHeader("Cache-Control", "public, max-age=31536000");
                // forces download
                String headerKey = "Content-Disposition";
                String headerValue = "inline";
                response.setHeader(headerKey, headerValue);
                // obtains response's output stream
                outStream = response.getOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                int totalSize = 0;
                while ((bytesRead = fileStream.read(buffer)) != -1) {
                    totalSize += bytesRead;
                    outStream.write(buffer, 0, bytesRead);
                }
                response.setContentLength(totalSize);
            }
            outStream.close();
        } catch(Exception e){}
    }
    
    
    private void addSameSiteCookieAttribute(HttpServletResponse response) {
        Collection<String> headers = response.getHeaders("Set-Cookie");
        boolean firstHeader = true;
        for (String header : headers) { // there can be multiple Set-Cookie attributes
            if (firstHeader) {
                response.setHeader("Set-Cookie", String.format("%s; %s", header, "SameSite=None"));
                firstHeader = false;
                continue;
            }
            response.addHeader("Set-Cookie", String.format("%s; %s", header, "SameSite=None"));
        }
    }
    
    private void supplyApi(String subPath, HttpServletRequest req, HttpServletResponse resp) throws IOException{
        PrintWriter writer = resp.getWriter();
        String resource = subPath.split("/", 2)[0];
        JSONObject respJSON = null;
        switch(resource){
            case "GET list-chromecasts":
                respJSON = listChromeCasts();
                break;
            case "GET list-directory":
                respJSON = listDirectory(subPath.split("/", 2)[1]);
                break;
            case "GET start-stream-serve":
                respJSON = startStreamServe(subPath.split("/", 2)[1]);
                break;
            case "GET stop-stream-serve":
                respJSON = stopStreamServe();
                break;
            default:
                respJSON = new JSONObject().put("status", "NOT_FOUND");
        }
        if(respJSON != null){
            writer.println(respJSON.toString());
        }
    }
    
    private JSONObject listChromeCasts(){
        JSONArray arr = new JSONArray();
        ChromeCasts.get().forEach(ch -> {
            arr.put(new JSONObject().put("id", ch.getName()).put("title", ch.getTitle()));
        });
        return new JSONObject().put("chromecasts", arr);
    }
    
    private JSONObject listDirectory(String directory){
        try{
            Collection<File> filesList = FileUtils.listFiles(new File(directory), new String[]{"mp4", "mp3"}, false);
            JSONArray arr = new JSONArray();
            filesList.stream().map(f -> new JSONObject().put("name", f.getName()).put("isDir", f.isDirectory())).forEach(f -> arr.put(f));
            return new JSONObject().put("files", arr);
        }catch (Exception e){
            return new JSONObject().put("error", true);
        }
    }
    
    private JSONObject startStreamServe(String file){
        boolean success = StreamingServlet.startServing(new File(file));
        if(!success){
            return new JSONObject().put("error", true);
        }
        return null;
    }
    
    private JSONObject stopStreamServe(){
        boolean success = StreamingServlet.stopServing();
        if(!success){
            return new JSONObject().put("error", true);
        }
        return null;
    }
}
