package bo;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class UserHandler {
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("Lab1");
	EntityManager em = emf.createEntityManager();
	
	public boolean login(String userName, String password){
		
		System.out.println("Debug: " + userName + " " + password);
		try{
			em.createNamedQuery("login").setParameter("username", userName).setParameter("password", password).getSingleResult();
			return true;
		}catch(NoResultException e){
			System.out.println("UserHandler error: No user");
			return false;
		}finally{
			em.close();
			emf.close();
		}
	}

	public static boolean register(String name, String password) {
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Lab1");
		EntityManager em = emf.createEntityManager();
		
		System.out.print("Regestering new user; name: " + name + " pass: " + password);
		
		UserBO newUser = new UserBO(name, password);
			try{
				em.getTransaction().begin();
				em.persist(newUser);
				em.getTransaction().commit();
				return true;
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				em.close();
				emf.close();
			}
		return false;
	}

	public static List<UserDTO> findUserByName(String searchValue) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Lab1");
		EntityManager em = emf.createEntityManager();
		try{
			Query query = em.createQuery("SELECT e FROM UserBO e where e.username = ?1", UserBO.class).setParameter(1, searchValue);
			List<UserDTO> matches = query.getResultList();
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
