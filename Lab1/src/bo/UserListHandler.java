package bo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class UserListHandler {
	private final static String sqlAll = "FROM User";
	private final static String errorMsg = "No Users";
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("Lab1");
	EntityManager em = emf.createEntityManager();

	public Map<String,String> getUserList(){
		Map<String,String> userlist = new HashMap<String,String>();
		try{
			List<User> temp = em.createQuery(sqlAll,User.class).getResultList();
	
			for(User user:temp){
				if(user.getFirstname() != null && user.getLastname() != null ){
					userlist.put(user.getUsername() + " <" + user.getFirstname() + " " + user.getLastname()+">",user.getUsername());
				}else{
					userlist.put(user.getUsername(),user.getUsername());
				}
			}
			/*Awesome coding thing*/
			userlist = userlist.entrySet().stream().sorted(Entry.comparingByValue()).collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		}
		catch(Exception Error){
			userlist.put(errorMsg,errorMsg);
		}
		return userlist;
	}
}
