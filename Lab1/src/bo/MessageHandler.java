package bo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;

import org.hibernate.Hibernate;

public class MessageHandler {
	
	public static ArrayList<MessageDTO> getMessages(String userName){
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Lab1");
		EntityManager em = emf.createEntityManager();
		
		System.out.println("MessageHandler: Fetching messages from DB");
		try{
			em.getTransaction().begin();
			Collection<Message> messages = em.createNamedQuery("getMessagesTo").setParameter("receiver", userName).getResultList();
			
			//This can be done much nicer
			ArrayList<MessageDTO> messageList = new ArrayList<MessageDTO>();
			for(Message message : messages){
				messageList.add(new MessageDTO(message.getId(), message.getMessage(), message.getReceiver(), message.getSender()));
			}
			Hibernate.initialize(messageList);
			em.getTransaction().commit();
			return messageList;
			
		}catch(NoResultException e){
			System.out.println("UserHandler error: No messages");
			return null;
		}finally{
			em.close();
			emf.close();
		}
	}

	public static void post(String activeUser, String messageText, String displayedUser) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Lab1");
		EntityManager em = emf.createEntityManager();
		
		Message newMessage = new Message(activeUser, messageText, displayedUser);
		
		System.out.println("MessageHandler: Postin in " + displayedUser + "s flow");
		try{
			
			em.getTransaction().begin();
			em.persist(newMessage);
			em.getTransaction().commit();
			
			return;
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			em.close();
			emf.close();
		}	
		return;
	}
}
