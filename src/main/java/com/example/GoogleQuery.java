package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GoogleQuery 
{
    public String searchKeyword;//user input
    public String url;//search query
    public String content;//google search result page html

    public GoogleQuery(String searchKeyword)
    {
        this.searchKeyword = searchKeyword;
        try 
        {
            // This part has been specially handled for Chinese keyword processing. 
            // You can comment out the following two lines 
            // and use the line of code in the lower section. 
            // Also, consider why the results might be incorrect 
            // when entering Chinese keywords.
            String encodeKeyword=java.net.URLEncoder.encode(searchKeyword,"utf-8");
            this.url = "https://www.google.com/search?q="+encodeKeyword+"&oe=utf8&num=20";

            // this.url = "https://www.google.com/search?q="+searchKeyword+"&oe=utf8&num=20";
        }
        catch (Exception e)
        {
            System.out.println("google_query_constructor"+e.getMessage());
        }
    }

    private String fetchContent() throws IOException
    {
    	try {
	        String retVal = "";
	
	        URL u = new URI(this.url).toURL();
	        URLConnection conn = u.openConnection();
	        //set HTTP header
	        conn.setRequestProperty("User-agent", "Chrome/107.0.5304.107");
	        InputStream in = conn.getInputStream();
	
	        InputStreamReader inReader = new InputStreamReader(in, "utf-8");
	        BufferedReader bufReader = new BufferedReader(inReader);
	        String line = null;
	
	        while((line = bufReader.readLine()) != null)
	        {
	            retVal += line;
	        }
	        return retVal;
    	} catch (URISyntaxException e) {
			e.printStackTrace();
			System.out.println("google_query_fetchContent");
			return e.toString();
		}
    }
    
//    public HashMap<String, String> query() throws IOException
//    {
//        if(content == null)
//        {
//            content = fetchContent();
//        }
//
//        HashMap<String, String> retVal = new HashMap<String, String>();
//
//        /* 
//         * some Jsoup source
//         * https://jsoup.org/apidocs/org/jsoup/nodes/package-summary.html
//         * https://www.1ju.org/jsoup/jsoup-quick-start
//         */
//
//        //using Jsoup analyze html string
//        Document doc = Jsoup.parse(content);
//
//        //select particular element(tag) which you want 
//        Elements lis = doc.select("div");
//        lis = lis.select(".kCrYT");
//
//        for(Element li : lis)
//        {
//            try 
//            {
//            	Elements aTags = li.select("a");
//                if (aTags.isEmpty()) {
//                    continue; // Skip if there are no <a> tags
//                }
//                
//                String citeUrl = aTags.get(0).attr("href").replace("/url?q=", "");
//                
//                Elements titleElements = aTags.get(0).select(".vvjwJb");
//                if (titleElements.isEmpty()) {
//                    continue; // Skip if there are no titles
//                }
//                
//                String title = titleElements.text();
//
//                if (title.equals("")) {
//                    continue; // Skip if title is empty
//                }
//
//                System.out.println("Title: " + title + " , url: " + citeUrl);
//
//                // Put title and URL pair into HashMap
//                retVal.put(title, citeUrl);
//
//            } catch (IndexOutOfBoundsException e) 
//            {
//				e.printStackTrace();
//            }
//        }
//
//        return retVal;
//    }

    public LinkedHashMap<String, String> query() throws IOException
    {
        
        /* 
         * some Jsoup source
         * https://jsoup.org/apidocs/org/jsoup/nodes/package-summary.html
         * https://www.1ju.org/jsoup/jsoup-quick-start
         */
    	if (content == null) {
    	    content = fetchContent();
    	}

    	LinkedHashMap<String, String> retVal = new LinkedHashMap<>();
    	Document doc = Jsoup.parse(content);
    	Elements lis = doc.select("div").select(".kCrYT");

    	for (Element li : lis) {
    	    try {
    	        Elements aTags = li.select("a");
    	        if (aTags.isEmpty()) {
    	            continue; // Skip if there are no <a> tags
    	        }

    	        String rawUrl = aTags.get(0).attr("href").replace("/url?q=", "");
    	        String accessibleUrl = isURLAccessible(rawUrl); // Use the updated method

    	        if (accessibleUrl != null) {
    	            Elements titleElements = aTags.get(0).select(".vvjwJb");
    	            if (titleElements.isEmpty()) {
    	                continue; // Skip if there are no titles
    	            }

    	            String title = titleElements.text();
    	            if (!title.isEmpty()) {
    	                System.out.println("Accessible URL - Title: " + title + " , url: " + accessibleUrl);
    	                retVal.put(title, accessibleUrl);
    	            }
    	        } else {
//    	            System.out.println("Skipped inaccessible URL: " + rawUrl);
    	        }

    	    } catch (IndexOutOfBoundsException e) {
    	        e.printStackTrace();
    	        System.out.println("google_query_query");
    	    }
    	}

    	return retVal;

    }
    
    private String isURLAccessible(String url) {
        try {
            // Step 1: Decode the URL twice to handle double encoding
            String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8.name());
            decodedUrl = URLDecoder.decode(decodedUrl, StandardCharsets.UTF_8.name());

            // Step 2: Remove unnecessary parameters (e.g., after "&")
            if (decodedUrl.contains("&")) {
                decodedUrl = decodedUrl.substring(0, decodedUrl.indexOf("&")); // Extract base URL
            }

            // Step 3: Convert the cleaned URL string into a URI and then a URL
            URL cleanedUrl = new URI(decodedUrl).toURL();

            // Step 4: Check accessibility
            URLConnection conn = cleanedUrl.openConnection();
            conn.setRequestProperty("User-agent", "Chrome/107.0.5304.107");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();

            // Step 5: Verify HTTP response status
            if (conn instanceof java.net.HttpURLConnection) {
                java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) conn;
                int responseCode = httpConn.getResponseCode();
                System.out.println("response code: " + responseCode);
                if (responseCode >= 200 && responseCode < 400) {
                    return cleanedUrl.toString(); // Return the accessible URL
                }
            }
            
            System.out.println("Skipped inaccessible URL: " + decodedUrl);
            return null; // URL is inaccessible

        } catch (Exception e) {
            // Handle any exceptions and return null
            e.printStackTrace();
            return null;
        }
    }



    
}
