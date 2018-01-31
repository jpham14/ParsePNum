package net.codejava;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import com.google.i18n.phonenumbers.AsYouTypeFormatter;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.ShortNumberInfo;

import java.net.URLDecoder;


/**
 * Servlet implementation class HelloServlet
 */

@WebServlet(urlPatterns = {"/phonenumbers/parse/text/*", "/phonenumbers/parse/file"})
public class Phonenumbers extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Phonenumbers() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = null;
		StringBuffer fullUrl = request.getRequestURL();
		String baseUrl = request.getScheme() + "://" + 
				request.getServerName() + ":" + 
				request.getServerPort() + 
				request.getContextPath() + 
				request.getServletPath();
		if (baseUrl.equals(fullUrl.toString())) {
			name = "";
		}else {
			String relUrl =  fullUrl.substring(baseUrl.length() + 1);
			if (request.getQueryString() != null) {
				relUrl = relUrl + "?" + request.getQueryString();
			}
			name = URLDecoder.decode(relUrl, "UTF-8");			
		}				
		Iterable<PhoneNumberMatch> matches = phoneUtil.findNumbers(name, "CA");
		Iterator<PhoneNumberMatch> i = matches.iterator();
		response.getWriter().print("[");
		while(i.hasNext()) {
			PhoneNumberMatch m = i.next();
			String out = "\"" + phoneUtil.format(m.number(), PhoneNumberFormat.NATIONAL) + "\"";
			response.getWriter().print(i.hasNext() ? out + "," : out);			
		}
		response.getWriter().print("]");	
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletFileUpload upload = new ServletFileUpload();
		upload.setSizeMax(50000);
		String contents = null;
		try {
		      FileItemIterator iterator = upload.getItemIterator(request);
		      while (iterator.hasNext()) {
		    	  FileItemStream item = iterator.next();		    	 
		          InputStream in = item.openStream();
		          contents = Streams.asString(in, UTF_8.name());
		      }
		      
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		response.setContentType("text/html");
	    response.setCharacterEncoding(UTF_8.name());
	    Iterable<PhoneNumberMatch> matches = phoneUtil.findNumbers(contents, "CA");
	    Iterator<PhoneNumberMatch> i = matches.iterator();
	    response.getWriter().print("[");
		while(i.hasNext()) {
			PhoneNumberMatch m = i.next();
			String out = "\"" + phoneUtil.format(m.number(), PhoneNumberFormat.NATIONAL) + "\"";
			response.getWriter().print(i.hasNext() ? out + "," : out);			
		}
		response.getWriter().print("]");	    
	}

}
