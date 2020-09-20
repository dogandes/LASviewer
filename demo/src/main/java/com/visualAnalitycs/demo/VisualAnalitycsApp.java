package com.visualAnalitycs.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class VisualAnalitycsApp {
	
	private LASReader lasReader;

	public static void main(String[] args) {
		SpringApplication.run(VisualAnalitycsApp.class, args);
	}

}
