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

	public static ArrayList<MessageDTO> getFlowMessages(String userName) {

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Lab1");
		EntityManager em = emf.createEntityManager();

		System.out.println("MessageHandler: Fetching messages from DB");
		try {

			// TODO: This code needs a rework
			
			//Fetches user info and tables to user_message
			User user = (User) em.createQuery("SELECT u FROM User u where u.username = ?1").setParameter(1, userName)
					.getSingleResult();
			List<Message> messages = user.RecievedMessages;
			
			//Sorts messages but date
			Collections.sort(messages, new Comparator<Message>() {
				public int compare(Message o2, Message o1) {
					return o1.getTimestamp().compareTo(o2.getTimestamp());
				}
			});
			//Create new ArrayList
			ArrayList<MessageDTO> messageList = new ArrayList<MessageDTO>();
			for (Message message : messages) {
				//Nasty compare function 
				if(Message.MessageType.PUBLIC.equals(message.getType())){
					messageList.add(new MessageDTO(message.getId(), message.getMessage(), message.getSender().getUsername(),
							message.getTimestamp()));
				}
			}
			return messageList;

		} catch (NoResultException e) {
			System.out.println("UserHandler error: No messages");
			return null;
		} finally {
			em.close();
			emf.close();
		}
	}

	/* Flow Message */
	public static void post(String activeUser, String messageText, String displayedUser) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Lab1");
		EntityManager em = emf.createEntityManager();

		System.out.println("MessageHandler: Postin in " + displayedUser + "s flow");
		try {

			em.getTransaction().begin();
			User reciever = (User) em.createQuery("Select u FROM User u where u.username = ?1")
					.setParameter(1, displayedUser).getSingleResult();
			User sender = (User) em.createQuery("Select u FROM User u where u.username = ?1")
					.setParameter(1, activeUser).getSingleResult();

			Message newMessage = new Message(sender, messageText, new Date(), Message.MessageType.PUBLIC);

			reciever.RecievedMessages.add(newMessage);
			em.persist(newMessage);
			em.getTransaction().commit();

			return;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			em.close();
			emf.close();
		}
		return;
	}

	/* Mail message */
	public boolean addNewMessage(String senderName, String receiverName, String message, String subject) {
		System.out.println(senderName+receiverName + message + subject);
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Lab1");
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			User senderUser = (User) em.createQuery("Select u FROM User u where u.username = ?1")
					.setParameter(1, senderName).getSingleResult();
			User receiverUser = (User) em.createQuery("Select u FROM User u where u.username = ?1")
					.setParameter(1, receiverName).getSingleResult();
			Message msg = new Message(senderUser, message, new Date(), subject, Message.MessageType.PRIVATE);
			receiverUser.RecievedMessages.add(msg);
			em.persist(msg);
			em.getTransaction().commit();
			return true;
		} catch (PersistenceException er) {
			System.out.println(er.getMessage());
		} finally {
			em.close();
			emf.close();
		}
		return false;
	}
	public static ArrayList<MessageDTO> getInboxMessages(String userName) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Lab1");
		EntityManager em = emf.createEntityManager();
		System.out.println("MessageHandler: Fetching messages from DB");
		try {

			// TODO: This code needs a rework
			
			//Fetches user info and tables to user_message
			User user = (User) em.createQuery("SELECT u FROM User u where u.username = ?1").setParameter(1, userName)
					.getSingleResult();
			List<Message> messages = user.RecievedMessages;
			
			//Sorts messages but date
			Collections.sort(messages, new Comparator<Message>() {
				public int compare(Message o2, Message o1) {
					return o1.getTimestamp().compareTo(o2.getTimestamp());
				}
			});
			//Create new ArrayList
			ArrayList<MessageDTO> messageList = new ArrayList<MessageDTO>();
			for (Message message : messages) {
				//Nasty compare function 
				if(Message.MessageType.PRIVATE.equals(message.getType())){
					messageList.add(new MessageDTO(message.getId(), message.getMessage(), message.getSender().getUsername(),
							message.getTimestamp()));
				}
			}
			return messageList;

		} catch (NoResultException e) {
			System.out.println("UserHandler error: No messages");
			return null;
		} finally {
			em.close();
			emf.close();
		}
	}
}
