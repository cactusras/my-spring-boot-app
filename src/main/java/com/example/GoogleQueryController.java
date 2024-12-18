package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
//import java.util.Map;


@RestController
public class GoogleQueryController {

    @GetMapping("/api/search")
    public LinkedHashMap<String, String> search(@RequestParam String keyword) {
        LinkedHashMap<String, String> response = new LinkedHashMap<>();
        try {
//        	score formula construction
        	ArrayList<Keyword> keywordForSorting = new ArrayList<Keyword>();
            keywordForSorting.add(new Keyword("restaurant", 10));
            keywordForSorting.add(new Keyword("餐廳", 10));
            keywordForSorting.add(new Keyword("推薦", 10));
            keywordForSorting.add(new Keyword("recommend", 5));
//        	webpages and its subpages forms a web tree
        	List<WebTree> webTrees = new ArrayList<>();
            GoogleQuery googleQuery = new GoogleQuery(keyword);
            HashMap<String, String> webpageMap = googleQuery.query();
            Iterator<Map.Entry<String, String>> iterator = webpageMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                WebTree webTree = new WebTree(entry);
                webTree.setPostOrderScore(keywordForSorting);
                webTree.eularPrintTree();
                webTrees.add(webTree);
            }
            
            // Sorting the web trees by the root node's score in descending order
            webTrees.sort((tree1, tree2) -> Double.compare(tree2.getRootScore(), tree1.getRootScore()));

            // Populate the response with sorted results
            for (WebTree webTree : webTrees) {
                response.put(webTree.getName(), webTree.getUrl());
            }

        } catch (IOException e) {
        	e.printStackTrace();
            response.put("error", "Error fetching search results.");
        } catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.put("error", "Error fetching search results.");
		}
        return response;
    }
}
