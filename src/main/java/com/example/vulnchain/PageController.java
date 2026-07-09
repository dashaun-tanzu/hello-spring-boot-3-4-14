package com.example.vulnchain;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The app's page-rendering surface.
 *
 *   GET /page?name=welcome   -> renders classpath:scripts/welcome through the
 *                               JavaScript template engine.
 *
 * The view name comes straight from the user-supplied "name" parameter. For a
 * CMS/theming feature this is completely normal: pick which page/theme file to
 * render. On a patched Spring it is harmless -- the resolver keeps you inside
 * the templates directory. On Spring Framework 6.2.16 (CVE-2026-22737) the
 * name is concatenated into a filesystem path with no normalization and no
 * boundary check, so a crafted name escapes the directory and the engine
 * renders -- i.e. returns -- the contents of any file the process can read.
 */
@Controller
public class PageController {

    @GetMapping("/page")
    public String page(@RequestParam(defaultValue = "welcome") String name) {
        // Return the view name as-is. This is the application-level behavior
        // that, combined with the framework bug, becomes an arbitrary file read.
        return name;
    }
}
