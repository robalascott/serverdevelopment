package bo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

//TODO: Delete/Remove Methods
public class MessageHandler {
	
	public static ArrayList<MessageDTO> getMessages(String userName){
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Lab1");
		EntityManager em = emf.createEntityManager();
		
		System.out.println("MessageHandler: Fetching messages from DB");
		try{
			
			//TODO: This code needs a rework
			
			//Don't need to start a transaction when we only read/get data
			//em.getTransaction().begin();
			//Collection<Message> messages = em.createNamedQuery("getMessagesTo").setParameter("receiver", userName).getResultList();
			User user = (User) em.createQuery("SELECT u FROM User u where u.username = ?1").setParameter(1, userName).getSingleResult();
			List<Message> messages = user.RecievedMessages;
			
			Collections.sort(messages, new Comparator<Message>() {
				  public int compare(Message o1, Message o2) {
				      return o1.getTimestamp().compareTo(o2.getTimestamp());
				  }
			});
			
			ArrayList<MessageDTO> messageList = new ArrayList<MessageDTO>();
			for(Message message : messages){
				messageList.add(new MessageDTO(message.getId(), message.getMessage(), message.getSender().getUsername(), message.getTimestamp()));
			}
			
			// If we need to use a list of DB objects in another class, we need to initialize the list
			// Hibernate.initialize(messageList);
			
			//em.getTransaction().commit();
			
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
		
		
		
		System.out.println("MessageHandler: Postin in " + displayedUser + "s flow");
		try{
			
			em.getTransaction().begin();
			User reciever = (User) em.createQuery("Select u FROM User u where u.username = ?1").setParameter(1, displayedUser).getSingleResult();
			User sender = (User) em.createQuery("Select u FROM User u where u.username = ?1").setParameter(1, activeUser).getSingleResult();
			
			
			Message newMessage = new Message(sender, messageText, new Date(), Message.MessageType.PUBLIC);
			
			reciever.RecievedMessages.add(newMessage);
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
	
	public boolean addNewMessage(String senderName, String receiver,String message ){
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Lab1");
		EntityManager em = emf.createEntityManager();
		
		User senderUser = (User) em.createQuery("Select u FROM User u where u.username = ?1").setParameter(1, senderName).getSingleResult();
		
		Message msg = new Message(senderUser, message, new Date(), Message.MessageType.PRIVATE);
		
		System.out.println("MessageHandler: Inserting a new message");
		try{
			em.getTransaction().begin();
			em.persist(msg);
			em.getTransaction().commit();
			return true;
		}catch(PersistenceException er){
			System.out.println(er.getMessage());
		}finally{
			em.close();
			emf.close();
		}
		return false;
	}
		
}
