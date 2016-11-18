package ui;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(filterName="AuthFilter",urlPatterns ="*.xhtml" )
public class AuthorizationFilter implements Filter{
	public final static String pathName = "/faces/login.xhtml";
	public final static String pathNamelogin = "/login.xhtml";
	public final static String pathNameResource ="javax.faces.resource";
	public AuthorizationFilter(){};  
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
	
		HttpServletRequest reqt = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession ses = reqt.getSession(false);
		String reqURI = reqt.getRequestURI();
	
		
	
		try{
		if(reqURI.indexOf(pathNamelogin)>=0 || (ses!=null && ses.getAttribute("username")!=null)
				||reqURI.indexOf("/public/")>=0 || reqURI.contains(pathNameResource)){
			chain.doFilter(request, response);
		}else{
			res.sendRedirect(reqt.getContextPath()+pathName);
		}
		}catch(Exception error){
			System.out.println(error.getMessage());
		}
		
	}

}
