package bo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class UserHandler {
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("Lab1");
	EntityManager em = emf.createEntityManager();
	
	public int login(String userName, String password){
		
		System.out.println("Debug: " + userName + " " + password);
		try{
			UserBO user = (UserBO) em.createNamedQuery("login").setParameter("username", userName).setParameter("password", password).getSingleResult();
			return user.getId();
		}catch(NoResultException e){
			System.out.println("UserHandler error: No user");
			return -1;
		}finally{
			em.close();
			emf.close();
		}
	}
}
