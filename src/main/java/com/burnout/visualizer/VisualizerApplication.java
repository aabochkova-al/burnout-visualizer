package com.burnout.visualizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class VisualizerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VisualizerApplication.class, args);
	}

}
