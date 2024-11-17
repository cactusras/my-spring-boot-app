package com.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

public class WebTreeConstructor {
	
    public WebTree webTree;

    public WebTreeConstructor(Map.Entry<String, String> rootEntry) throws IOException {

        WebPage rootPage = new WebPage(rootEntry.getValue(), rootEntry.getKey());
        webTree = new WebTree(rootPage);

        // Step 3: Build the web tree by fetching subpages recursively
        buildTree(webTree.root, 2); // Limit depth to prevent infinite loops
    }

    private void buildTree(WebNode currentNode, int depth) throws IOException {
        if (depth <= 0) {
            return; // Base case: stop recursion when max depth is reached
        }

        // Fetch subpages of the current webpage
        Set<String> subpages = getSubpages(currentNode.webPage.url);

        // For each subpage, create a child node and recursively build its subtree
        for (String subpageUrl : subpages) {
            WebPage subPage = new WebPage(subpageUrl, extractPageName(subpageUrl));
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
            Document doc = Jsoup.connect(baseUrl).get();

            // Extract all <a> tags
            Elements links = doc.select("a[href]");

            for (Element link : links) {
                String href = link.attr("href");

                // Handle relative and absolute URLs
                String fullUrl = href.startsWith("http") ? href : new URL(new URL(baseUrl), href).toString();

                // Filter links to include only subpages of the given domain
                if (fullUrl.startsWith(baseUrl)) {
                    subpages.add(fullUrl);
                }
            }
        } catch (Exception e) {
            // Handle errors gracefully, such as malformed URLs or inaccessible pages
            System.err.println("Error fetching subpages for: " + baseUrl);
            e.printStackTrace();
        }

        return subpages;
    }

    // Extract a human-readable page name from a URL
    private String extractPageName(String url) {
        try {
            return new URL(url).getPath().replace("/", "").replace("-", " ").trim();
        } catch (Exception e) {
            return url; // Fallback to URL if name extraction fails
        }
    }

    // Print the constructed tree in Euler format
    public void printTree() {
        webTree.eularPrintTree();
    }
	
}
