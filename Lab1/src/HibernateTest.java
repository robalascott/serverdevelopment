import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class HibernateTest {

	public static void main(String[] args) {
		
		EntityManagerFactory emf;
		EntityManager em;
		EntityTransaction tx;
		
		emf = Persistence.createEntityManagerFactory("Lab1");
		em = emf.createEntityManager();
		tx = em.getTransaction();

		Query query = em.createQuery("from TextMessage");
		List<TextMessage> messages = query.getResultList();
		
		for(int i=0; i<messages.size();i++){
			System.out.println("Message: " + messages.get(i).getText());
		}
	
	}

}
