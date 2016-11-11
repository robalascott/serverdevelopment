package bo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class UserHandler {
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("Lab1");
	EntityManager em = emf.createEntityManager();
	
	public boolean login(String userName, String password){
		
		
		return false;
	}
}
