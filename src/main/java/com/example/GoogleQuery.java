package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

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
            System.out.println(e.getMessage());
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
			return e.toString();
		}
    }

    public HashMap<String, String> query() throws IOException
    {
        if(content == null)
        {
            content = fetchContent();
        }

        HashMap<String, String> retVal = new HashMap<String, String>();

        /* 
         * some Jsoup source
         * https://jsoup.org/apidocs/org/jsoup/nodes/package-summary.html
         * https://www.1ju.org/jsoup/jsoup-quick-start
         */

        //using Jsoup analyze html string
        Document doc = Jsoup.parse(content);

        //select particular element(tag) which you want 
        Elements lis = doc.select("div");
        lis = lis.select(".kCrYT");

        for(Element li : lis)
        {
            try 
            {
            	Elements aTags = li.select("a");
                if (aTags.isEmpty()) {
                    continue; // Skip if there are no <a> tags
                }
                
                String citeUrl = aTags.get(0).attr("href").replace("/url?q=", "");
                
                Elements titleElements = aTags.get(0).select(".vvjwJb");
                if (titleElements.isEmpty()) {
                    continue; // Skip if there are no titles
                }
                
                String title = titleElements.text();

                if (title.equals("")) {
                    continue; // Skip if title is empty
                }

                System.out.println("Title: " + title + " , url: " + citeUrl);

                // Put title and URL pair into HashMap
                retVal.put(title, citeUrl);

            } catch (IndexOutOfBoundsException e) 
            {
				e.printStackTrace();
            }
        }

        return retVal;
    }
    
    
    
}
