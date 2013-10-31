package org.sagebionetworks.bridge.webapp.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class OriginFilter implements Filter {

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
            ServletException {
        
        // Store the origin URL so after authentication, we can direct the user to where
        // they came from. This isn't excellent but it'll do until we know the user's home community. 
        HttpServletRequest request = (HttpServletRequest)req;
        request.setAttribute("origin", request.getServletPath());
        
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }
}
