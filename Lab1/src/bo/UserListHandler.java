package bo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

//TODO: Don't think we need transaction/Look over SQL
public class UserListHandler {
	private final static String sqlAll = "FROM User"; 
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("Lab1");
	EntityManager em = emf.createEntityManager();
	
	public Map<String,Integer> getUserList(){
		em.getTransaction().begin();
		Map<String,Integer> userlist = new HashMap<String,Integer>();
		List<User> temp = em.createQuery(sqlAll,User.class).getResultList();
		for(User user:temp){
			System.out.println(user.getId() + user.getUsername());
			
			if(user.getFirstname() != null && user.getLastname() != null ){
			userlist.put(user.getUsername() + " <" + user.getFirstname() + " " + user.getLastname()+">",Integer.valueOf(user.getId()));
			}else{
				userlist.put(user.getUsername(),Integer.valueOf(user.getId()));
			}
		}
		return userlist;
	}
}
