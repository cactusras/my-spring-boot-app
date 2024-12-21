package com.example;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

public class WordCounter
{
	private String urlStr;
	private String content;

	public WordCounter(String urlStr)
	{
		this.urlStr = urlStr;
	}

	private String fetchContent() throws IOException, URISyntaxException
	{
		try{
			URL url = new URI(this.urlStr).toURL();
//			System.out.println(urlStr);
			URLConnection conn = url.openConnection();
            conn.setRequestProperty("User-agent", "Chrome/107.0.5304.107");
            conn.setConnectTimeout(10000); // Wait up to 10 seconds for a connection
            conn.setReadTimeout(15000);    // Wait up to 15 seconds to read data
            conn.connect();
			
	        // Check if the connection is an instance of HttpURLConnection
	        if (conn instanceof HttpURLConnection) {
	            HttpURLConnection httpConn = (HttpURLConnection) conn;
	            int responseCode = httpConn.getResponseCode();
	            
	            if (responseCode == 429) {
	                System.err.println("Error: Too many requests. Server returned HTTP 429.");
	                return null;
	            }
	        }
			
			InputStream in = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
	
			String retVal = "";
	
			String line = null;
	
			while ((line = br.readLine()) != null)
			{
				retVal = retVal + line + "\n";
			}
	
			return retVal;
		} catch (FileNotFoundException e) {
            // FileNotFoundException caught - return null
            System.out.println("File not found at URL: " + urlStr);
//			e.printStackTrace();
            return null;
		} catch (IOException e) {
	        
	        System.err.println("IOException caught for URL: " + urlStr);
//	        e.printStackTrace();
	        return null;
	    }

	}

	public int countKeyword(String keyword) throws IOException, URISyntaxException
	{
		if (content == null)
		{
			content = fetchContent();
		}
		
		if(content == null) {
			return 0;
		}
		// To do a case-insensitive search, we turn the whole content and keyword into
		// upper-case:
		content = content.toUpperCase();
		keyword = keyword.toUpperCase();

		int retVal = 0;
		int fromIdx = 0;
		int found = -1;

		while ((found = content.indexOf(keyword, fromIdx)) != -1)
		{
			retVal++;
			fromIdx = found + keyword.length();
		}

		return retVal;
	}
}
