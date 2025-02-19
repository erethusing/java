package com.example.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContentController {


    @GetMapping("/req/login")
    public String login(){
        return "login";
    }
    
    @GetMapping("/req/signup")
    public String signup(){
        return "signup";
    }
    @GetMapping("/index")
    public String home(){
        return "index";
    }

    @GetMapping("/addTable")
    public String addTable(){
        return "addTable";
    }

    @GetMapping("/reserveTable")
    public String reserveTable(){
        return "reserveTable";
    }
    @GetMapping("/deleteTable")
    public String deleteTable(){
        return "deleteTable";
    }

    @GetMapping("/allTabless")
    public String allTabless(){
        return "allTabless";
    }

    @GetMapping("/editTable")
    public String editTable(){
        return "editTable";
    }

    @GetMapping("/resT")
    public String resT(){
        return "resT";
    }

    @GetMapping("/myReservation")
    public String myReservation(){
        return "myReservation";
    }



}
