package com.example.vulnchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * A deliberately ordinary Spring Boot application.
 *
 * It renders server-side pages with a JavaScript template engine
 * (ScriptTemplateView) -- a normal, documented Spring MVC feature. Nothing
 * in the application's own code is obviously a security hole.
 *
 * The compromise emerges only when three boring facts line up:
 *
 *   1. (app)       Spring Framework 6.2.16 -> CVE-2026-22737, an
 *                  unsanitized template path in ScriptTemplateView.   -> pom.xml
 *   2. (container) The image runs as root and bakes a real secret
 *                  onto its filesystem.                               -> Dockerfile
 *   3. (chain)     #1 reads arbitrary files; #2 puts something worth
 *                  reading on that filesystem.                        -> README.md
 *
 * A Trivy scan of this image and its SBOM reports ZERO HIGH/CRITICAL.
 * The exploit still works. That is the whole point.
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
