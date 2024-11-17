package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
//import java.util.Map;
import java.util.Map;

@RestController
public class GoogleQueryController {

    @GetMapping("/api/search")
    public HashMap<String, String> search(@RequestParam String keyword) {
        HashMap<String, String> response = new HashMap<>();
        try {
        	ArrayList<Keyword> keywordForSorting = new ArrayList<Keyword>();
            keywordForSorting.add(new Keyword("restaurant", 10));
            keywordForSorting.add(new Keyword("recommend", 5));
        	
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
            
//            sorting and store it into response
            
            response = googleQuery.query();
            
        } catch (IOException e) {
            response.put("error", "Error fetching search results.");
        }
        return response;
    }
}
