package bo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class UserListHandler {
	private final static String sqlAll = "FROM UserBO"; 
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("Lab1");
	EntityManager em = emf.createEntityManager();
	
	public Map<String,Integer> getUserList(){
		em.getTransaction().begin();
		Map<String,Integer> userlist = new HashMap<String,Integer>();
		List<UserBO> temp = em.createQuery(sqlAll,UserBO.class).getResultList();
		for(UserBO userbo:temp){
			System.out.println(userbo.getId() + userbo.getUsername());
			
			if(userbo.getFirstname() != null && userbo.getLastname() != null ){
			userlist.put(userbo.getUsername() + " <" + userbo.getFirstname() + " " + userbo.getLastname()+">",Integer.valueOf(userbo.getId()));
			}else{
				userlist.put(userbo.getUsername(),Integer.valueOf(userbo.getId()));
			}
		}
		return userlist;
	}
}
