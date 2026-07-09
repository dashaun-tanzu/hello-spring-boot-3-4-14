package com.example.vulnchain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.script.ScriptTemplateConfigurer;
import org.springframework.web.servlet.view.script.ScriptTemplateViewResolver;

/**
 * Wires up Spring MVC's ScriptTemplateView with a Nashorn (JavaScript) engine.
 *
 * This is a textbook ScriptTemplateView setup straight from the Spring
 * reference docs -- a "theme"/"page" rendering feature. Templates live under
 * ./templates and are rendered by the render() function in render.js.
 *
 * The vulnerability is NOT in this configuration; it is in how Spring
 * Framework 6.2.16's ScriptTemplateView.getResource() resolves a view name
 * into a file (no path normalization, no boundary check). Upgrading Spring
 * Boot replaces that class with the fixed 6.2.17 version and the demo dies.
 */
@Configuration
public class TemplateConfig implements WebMvcConfigurer {

    @Bean
    public ScriptTemplateConfigurer scriptTemplateConfigurer() {
        ScriptTemplateConfigurer configurer = new ScriptTemplateConfigurer();
        configurer.setEngineName("nashorn");
        // render.js defines: function render(template, model, context) { ... }
        configurer.setScripts("render.js");
        configurer.setRenderFunction("render");
        // Templates are packaged on the classpath (src/main/resources/scripts).
        // Ordinary pages resolve under classpath:scripts/. This is NOT a served
        // static location, so the template files are not directly fetchable
        // over HTTP. The arbitrary-file-read still works because CVE-2026-22737
        // honors a file: scheme in the view name itself (see PageController /
        // exploit.py), independent of this loader path.
        configurer.setResourceLoaderPath("classpath:scripts/");
        configurer.setCharset(java.nio.charset.StandardCharsets.UTF_8);
        return configurer;
    }

    @Bean
    public ViewResolver scriptTemplateViewResolver() {
        ScriptTemplateViewResolver resolver = new ScriptTemplateViewResolver();
        // No prefix/suffix: the view name IS the template path. Ordinary pages
        // are named like "welcome" -> resolved under classpath:scripts/welcome.
        resolver.setPrefix("");
        resolver.setSuffix("");
        return resolver;
    }
}
