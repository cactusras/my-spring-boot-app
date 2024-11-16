//package com.example;
//
//import java.io.IOException;
//
//public class main 
//{
//    public static void main(String[] args) 
//    {
//        try 
//        {
//            /*
//             * Using different keyword depends on the last number of your student ID
//             * 0,1:Tomato
//             * 2,3:Liver
//             * 4,5:Pokemon
//             * 6,7:Tissue
//             * 8,9:Process
//             */
//            System.out.println(new GoogleQuery("Tomato").query());
//            
////			System.out.println(new GoogleQuery("µf­X").query());
//        } 
//        catch (IOException e) 
//        {
//            e.printStackTrace();
//        }
//    }
//}


package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
