package com.scaha.filters;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

public class IPBlockFilter implements Filter {

    private List<Pattern> blockedPatterns = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Load the configuration file
        try (InputStream input = filterConfig.getServletContext().getResourceAsStream("/WEB-INF/blocked-ips.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            // Get the blocked IP patterns from the config file
            String blockedIps = prop.getProperty("blocked.ips");
            if (blockedIps != null) {
                String[] patterns = blockedIps.split(",");
                for (String pattern : patterns) {
                    // Convert wildcard patterns to regex
                    String regex = pattern.replace(".", "\\.").replace("*", ".*");
                    blockedPatterns.add(Pattern.compile(regex));
                }
            }
        } catch (IOException e) {
            throw new ServletException("Failed to load blocked IP patterns", e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String ipAddress = httpRequest.getHeader("X-Forwarded-For");

        if (ipAddress != null && !ipAddress.isEmpty()) {
            // X-Forwarded-For can contain multiple IPs; check the first one
            ipAddress = ipAddress.split(",")[0].trim();

            for (Pattern pattern : blockedPatterns) {
                if (pattern.matcher(ipAddress).matches()) {
                    // Block the request if the IP matches a blocked pattern
                    response.getWriter().write("Forbidden");
                    response.getWriter().flush();
                    return;
                }
            }
        }

        // Continue with the chain if the IP is not blocked
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}