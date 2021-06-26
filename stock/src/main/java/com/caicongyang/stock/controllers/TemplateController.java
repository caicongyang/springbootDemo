package com.caicongyang.stock.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/templates")
public class TemplateController {
	
	@RequestMapping("/index.html")
	public String index(Model model){
		model.addAttribute("content", "tom");
		return "index";
	}
}
