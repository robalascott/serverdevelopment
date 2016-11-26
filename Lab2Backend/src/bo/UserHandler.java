package bo;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Path("/user")
public class UserHandler {
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("Lab1");
	EntityManager em = emf.createEntityManager();
	
	@GET
	@Path("/login/{name}/{pass}")
	@Produces("application/json")
	public int login(@PathParam("name") String userName, @PathParam("pass") String password){
		
		System.out.println("Debug: " + userName + " " + password);
		try{
			User user = (User) em.createNamedQuery("login").setParameter("username", userName).setParameter("password", password).getSingleResult();
			return user.getId();
		}catch(NoResultException e){
			System.out.println("UserHandler error: No user");
			return -1;
		}finally{
			em.close();
			emf.close();
		}
		
	}
	
	@GET
	@Path("/getUserInfo/{name}")
	@Produces("application/json")
	public UserDTO userDetails(@PathParam("name") String userName){
		try{
			System.out.println("UserHandler:userDetails: Getting userdetails for: " + userName);
			//User user = (User) em.createNamedQuery("userdetails").setParameter("username", userName).getSingleResult();
			TypedQuery<User> query = em.createNamedQuery("userdetails", User.class);
			User user = query.setParameter("username", userName).getSingleResult();
			UserDTO userDTO = new UserDTO(user.getUsername(), user.getFirstname(), user.getLastname());
			
			return userDTO;
		}catch(NoResultException e){
			System.out.println("UserHandler error: No user");
			return null;
		}finally{
			em.close();
			emf.close();
		}
	}
	
	@POST
	@Path("/register")
	@Consumes("application/json")
	public static Response register(UserDTO user) {
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Lab1");
		EntityManager em = emf.createEntityManager();
		
		System.out.println("Regestering new user; name: " + user.getUsername() + " pass: " + user.getPassword());
		
		User newUser = new User(user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName());
			try{
				em.getTransaction().begin();
				em.persist(newUser);
				em.getTransaction().commit();
				return Response.status(Response.Status.CREATED).entity("ok").build();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				em.close();
				emf.close();
			}
			//TODO: Add custom Responses with sensible information
			return Response.status(Response.Status.NOT_FOUND).entity("FAIL").build();
	}
	
	@GET
	@Path("/getAllUsers")
	@Produces("application/json")
	public Map<String,String> getUserList(){
		
		final String sqlAll = "FROM User"; 
		
		Map<String,String> userlist = new HashMap<String,String>();
		List<User> temp = em.createQuery(sqlAll,User.class).getResultList();
		for(User user:temp){
			//System.out.println(user.getId() + user.getUsername());
			
			if(user.getFirstname() != null && user.getLastname() != null ){
				userlist.put(user.getUsername() + " <" + user.getFirstname() + " " + user.getLastname()+">",user.getUsername());
			}else{
				userlist.put(user.getUsername(),user.getUsername());
			}
		}
		
		return userlist;
	
	}
	
	@GET
	@Path("/findUserByName/{name}")
	@Produces("application/json")
	public static List<UserDTO> findUserByName(@PathParam("name") String searchValue) {
		System.out.println("UserHandler:findUserByName: " + "Finding matches for " + searchValue);
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Lab1");
		EntityManager em = emf.createEntityManager();
		try{
			TypedQuery<User> query = em.createQuery("SELECT e FROM User e where e.username = ?1", User.class).setParameter(1, searchValue);
			//TODO: Convert User to UserDTO
			List<User> userList = query.getResultList();
			List<UserDTO> matches = new ArrayList<UserDTO>();
			for(User user: userList){
				matches.add(new UserDTO(user.getUsername()));
			}
			System.out.println("Returning with matches");
			return matches;
		}catch(NoResultException e){
			System.out.println("UserHandler error: No user");
			return null;
		}finally{
			em.close();
			emf.close();
		}
	}
}
