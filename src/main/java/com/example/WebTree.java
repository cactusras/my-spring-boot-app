package com.example;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebTree{
	public WebNode root;

	public WebTree(WebPage rootPage){//can delete??
		this.root = new WebNode(rootPage);
	}
	
    public WebTree(Map.Entry<String, String> rootEntry) throws IOException {

        WebPage rootPage = new WebPage(rootEntry.getValue(), rootEntry.getKey());
        this.root = new WebNode(rootPage);

        // Step 3: Build the web tree by fetching subpages recursively
        buildTree(this.root, 1); // Limit depth to prevent infinite loops
    }

    private void buildTree(WebNode currentNode, int depth) throws IOException {
        if (depth <= 0) {
            return; // Base case: stop recursion when max depth is reached
        }

        // Fetch subpages of the current webpage
        Set<String> subpages = getSubpages(currentNode.webPage.url);//err

        // For each subpage, create a child node and recursively build its subtree
        for (String subpageUrl : subpages) {
            WebPage subPage = new WebPage(subpageUrl, extractPageName(subpageUrl));//err
            WebNode childNode = new WebNode(subPage);
            currentNode.addChild(childNode);
            buildTree(childNode, depth - 1); // Recursive call with decreased depth
        }
    }

    // Fetch subpages for a given URL
    public static Set<String> getSubpages(String baseUrl) throws IOException {
        Set<String> subpages = new HashSet<>();

        try {
            // Fetch and parse the webpage
            Document doc = Jsoup.connect(baseUrl)
            		.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.5304.107 Safari/537.36")
                    .referrer("https://www.google.com")
                    .timeout(10000)
                    .get();;

            // Extract all <a> tags
            Elements links = doc.select("a[href]");

            for (Element link : links) {
                String href = link.attr("href");
                String decodedHref = URLDecoder.decode(href, StandardCharsets.UTF_8.name());
                decodedHref = decodedHref.replace("%23", "#"); // Decode fragment

                // Handle relative and absolute URLs
                String fullUrl;
                if (decodedHref.startsWith("http")) {
                    fullUrl = decodedHref;
                } else {
                    URI baseUri = new URI(baseUrl);
                    fullUrl = new URI(
                            baseUri.getScheme(),
                            baseUri.getHost(),
                            decodedHref.startsWith("/") ? decodedHref : "/" + decodedHref,
                            null
                    ).toString().replace("%23", "#").replace("%3F", "?").replace(" ", "%20");
                }

                // Debugging output
//                System.out.println("href: " + decodedHref);
//                System.out.println("host: " + new URI(baseUrl).getHost());
//                System.out.println("full: " + fullUrl);

                // Filter links to include only subpages of the given domain
                if (fullUrl.startsWith(baseUrl) && !(decodedHref.startsWith("TEL:") || decodedHref.startsWith("/TEL:") || decodedHref.startsWith("/tel:"))) {
                    subpages.add(fullUrl);
                }
            }
        } catch (HttpStatusException e) {
            System.err.println("HTTP error fetching URL. Status=" + e.getStatusCode());

        } catch (Exception e) {
            // Handle errors gracefully, such as malformed URLs or inaccessible pages
            System.err.println("Error fetching subpages for URL.");
//            e.printStackTrace();
        }


        return subpages;
    }

    // Extract a human-readable page name from a URL
    private String extractPageName(String url) {
        try {
            return new URI(url).toURL().getPath().replace("/", "").replace("-", " ").trim();//err
        } catch (Exception e) {
        	e.printStackTrace();
            return url; // Fallback to URL if name extraction fails
        }
    }
	

	public void setPostOrderScore(ArrayList<Keyword> keywords) throws IOException, URISyntaxException{
		setPostOrderScore(root, keywords);
	}

	private void setPostOrderScore(WebNode startNode, ArrayList<Keyword> keywords) throws IOException, URISyntaxException{
		// YOUR TURN
		// 3. compute the score of children nodes via post-order, then setNodeScore for
		// startNode
		
		for (WebNode child : startNode.children) {
	        setPostOrderScore(child, keywords); // Post-order: process children first
	    }
	    startNode.setNodeScore(keywords); // Compute score of the current node
	}

	public void eularPrintTree(){//can delete??
		eularPrintTree(root);
	}

	private void eularPrintTree(WebNode startNode){
//		int nodeDepth = startNode.getDepth();
//
//		if (nodeDepth > 1)
//			System.out.print("\n" + repeat("\t", nodeDepth - 1));
//
//		System.out.print("(");
//		System.out.print(startNode.webPage.name + "," + startNode.nodeScore);
//		
//		// YOUR TURN
//		// 4. print child via pre-order
//
//		System.out.print(")");
//
//		if (startNode.isTheLastChild())
//			System.out.print("\n" + repeat("\t", nodeDepth - 2));
		
		int nodeDepth = startNode.getDepth();

	    // Print indentation based on depth, except for the root node
	    if (nodeDepth > 1) {
	        System.out.print("\n" + repeat("\t", nodeDepth - 1));
	    }

	    // Print the current node's name and score
	    System.out.print("(");
	    System.out.print(startNode.webPage.name + "," + startNode.nodeScore);

	    // Recursive pre-order traversal for each child
	    for (WebNode child : startNode.children) {
	        eularPrintTree(child); // Pre-order: process children immediately after the parent
	    }

	    System.out.print(")");

	    // Adjust indentation if it's the last child
	    if (startNode.isTheLastChild()) {
	        System.out.print("\n" + repeat("\t", nodeDepth - 2));
	    }
		
	}
	
	public double getRootScore() {
	    return this.root.nodeScore;
	}
	
	public String getUrl() {
	    return this.root.webPage.url;
	}
	
	public String getName() {
		return this.root.webPage.name;
	}

	private String repeat(String str, int repeat){
		String retVal = "";
		for (int i = 0; i < repeat; i++){
			retVal += str;
		}
		return retVal;
	}
}