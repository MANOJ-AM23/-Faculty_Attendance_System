package com.example.attendance.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Resolve absolute path to frontend directory
        String frontendPath = new File("../frontend").getAbsolutePath();
        if (!frontendPath.endsWith(File.separator)) {
            frontendPath += File.separator;
        }

        System.out.println("Mapping / static resources to: file:" + frontendPath);

        registry.addResourceHandler("/**")
                .addResourceLocations("file:" + frontendPath)
                .addResourceLocations("classpath:/static/");

        // Also map specific paths for clarity if needed
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("file:" + frontendPath + "assets/");
        registry.addResourceHandler("/css/**")
                .addResourceLocations("file:" + frontendPath + "css/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("file:" + frontendPath + "js/");
    }
}
