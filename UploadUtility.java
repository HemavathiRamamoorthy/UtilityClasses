package com.utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
/**
 * 
 * @author Hemavathi R
 *
 */
@MultipartConfig(maxFileSize = 16177215)
public interface UploadUtility {
	/**
	 * 
	 * @param request -  requires filePath and uploadButtonName as request attribute
	 * @return fileName if the upload file (copied to AMW file system) is successful
	 * @throws IOException
	 * @throws ServletException
	 */
	static String  uploadFile(HttpServletRequest request) throws IOException,ServletException
	{
		String fileName=null;
		InputStream filecontent = null;
	    boolean isMultipart = ServletFileUpload.isMultipartContent(request);
	    if (!isMultipart) {
	      return null;
	    }
	    try {
	    	 /**
	    	  * filePath is required to store the uploaded file to specific directory in AMW tool
	    	  */
	         String savePath = (String) request.getAttribute("filePath"); 
	         /**
	          * This is name of input type="file" (if <input type="file" name="upload" /> , then set uploadInputName as "upload" )
	          */
	         String fileToUpload= (String) request.getAttribute("uploadInputName");
	        /**
	         * Create file directory path , if directory doesn't exist
	         */
	         File fileSaveDir = new File(savePath);
	         if (!fileSaveDir.exists()) {
	             fileSaveDir.mkdirs();
	         }
	         Part filePart = request.getPart(fileToUpload); 
	         fileName= extractFileName(filePart);
	         filecontent = filePart.getInputStream();
	         Files.copy(filecontent, Paths.get(savePath+  File.separator +fileName));
	        
	    } catch (Exception e) {
	      e.getMessage();
	    }
	    finally
	    {	
	    	if(null!=filecontent) {
	    	 filecontent.close();
	    	}	
	    }
	    return fileName;
	}
	
	static String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length()-1);
            }
        }
        return "";
    }
	/**
	 * 
	 * @param request with attribute uploadInputName
	 * @return fileName as String
	 * @throws IllegalStateException
	 * @throws IOException
	 * @throws ServletException
	 */
	static String getFileName(HttpServletRequest request)
	{  
		String fileName = null;
		String fileToUpload= (String) request.getAttribute("uploadInputName");
		Part filePart;
		try {
			
			filePart = request.getPart(fileToUpload);
			fileName=extractFileName(filePart);
		} catch (IllegalStateException | IOException | ServletException e) {
			e.getMessage();
		} 
		return fileName;
	}
}
