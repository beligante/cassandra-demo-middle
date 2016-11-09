package almeida.rochapaulo.demo.filters;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;

import almeida.rochapaulo.demo.entities.Session;
import almeida.rochapaulo.demo.service.SessionService;

/**
 * 
 * @author rochapaulo
 *
 */
public class LoginFilter extends GenericFilterBean {

    private final SessionService service;
    
    @Autowired
    public LoginFilter(SessionService service) {
        this.service = service;
    }
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest httpRequest = asHttp(req);
        HttpServletResponse httpResponse = asHttp(res);
        
        Optional<String> sid = Optional.ofNullable(httpRequest.getHeader("SID"));
        
        if (sid.isPresent()) {
            final boolean authenticated = checkSession(sid.get());
            if (authenticated) {
            	chain.doFilter(httpRequest, httpResponse);
            }
        }
        
        httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
    	httpResponse.sendRedirect("/login");    
    }

    private HttpServletRequest asHttp(ServletRequest request) {
        return (HttpServletRequest) request;
    }

    private HttpServletResponse asHttp(ServletResponse response) {
        return (HttpServletResponse) response;
    }
    
    private boolean checkSession(String sid) {
        
        try {
            Optional<Session> session = service.getSession(UUID.fromString(sid)).get();
            return session.isPresent();
        } catch (Exception ex) {
            return false;
        }
    }
    
}
