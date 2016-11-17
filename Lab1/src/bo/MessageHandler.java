package bo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.hibernate.Hibernate;
import org.hibernate.Transaction;

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
	
	public boolean addNewMessage(String sender, String reciver,String message ){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Lab1");
		EntityManager em = emf.createEntityManager();
		Message msg = new Message();
		System.out.println("MessageHandler: Inserting a new message");
		try{
			em.getTransaction().begin();
			msg.setMessage(message);
			msg.setSender(sender);
			msg.setReceiver(reciver);
			em.persist(msg);
			em.getTransaction().commit();
			return true;
		}catch(PersistenceException er){
			System.out.println(er.getMessage());
			return false;
		}finally{
			em.close();
			emf.close();
		}
	}


	
}
