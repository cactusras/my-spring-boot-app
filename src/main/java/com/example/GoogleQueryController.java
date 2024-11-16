package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
//import java.util.Map;

@RestController
public class GoogleQueryController {

    @GetMapping("/api/search")
    public HashMap<String, String> search(@RequestParam String keyword) {
        HashMap<String, String> response = new HashMap<>();
        try {
            GoogleQuery googleQuery = new GoogleQuery(keyword);
            response = googleQuery.query();
        } catch (IOException e) {
            response.put("error", "Error fetching search results.");
        }
        return response;
    }
}
