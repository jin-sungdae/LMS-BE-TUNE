package com.savelms.api.root;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class rootGetMapping  {
    @GetMapping("/spring")
    public String helloSpring(){

        return "hello Spring6";
    }

}
