package almeida.rochalabs.demo.api.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.GenericFilterBean;

/**
 * 
 * @author rochapaulo
 *
 */
public class CORSFilter extends GenericFilterBean {
	
	@Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
	
		HttpServletResponse httpResponse = (HttpServletResponse) res;
		
		httpResponse.setHeader("Access-Control-Allow-Origin", "*");
		httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST");
		httpResponse.setHeader("Access-Control-Allow-Headers", "content-type, accept");
		
		chain.doFilter(req, res);
	}
	
}
