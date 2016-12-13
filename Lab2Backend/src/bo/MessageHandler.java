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
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import bo.Message.MessageType;

//TODO: Delete/Remove Methods
@Path("/message")
public class MessageHandler {

	//We can now use this method to get both private & public messages (better since they did the same anyway)
	@GET
	@Path("/getMessages/{name}/{type}")
	@Produces("application/json")
	public static List<MessageDTO> getMessages(@PathParam("name") String userName, @PathParam("type") MessageType type) {

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Lab1");
		EntityManager em = emf.createEntityManager();

		System.out.println("MessageHandler: Fetching messages to " + userName + " of type: " + type);
		try {

			// TODO: Code looks better but we should replace the Query with a named one
			TypedQuery<User> query = em.createQuery("SELECT u FROM User u where u.username = ?1", User.class).
					setParameter(1, userName);
			User user = query.getSingleResult();
			List<Message> messages = user.RecievedMessages;
			
			// Sort messages in chronological order
			Collections.sort(messages, new Comparator<Message>() {
				public int compare(Message o1, Message o2) {
					return o2.getTimestamp().compareTo(o1.getTimestamp());
				}
			});
			
			// Convert the DB-Object and discard messages of the wrong type
			List<MessageDTO> messageList = new ArrayList<MessageDTO>();
			for (Message message : messages) {
				if(message.getType().equals(type)){
					messageList.add(new MessageDTO(message.getId(), message.getMessage(), message.getSender().getUsername(),
							message.getTimestamp(),message.getSubject()));
				}
			}

			return messageList;

		} catch (NoResultException e) {
			//System.out.println("UserHandler error: No messages");
			return null;
		} finally {
			em.close();
			emf.close();
		}
	}

	/** 
	 * Converts and adds a message to the DB. Requires type in {@code message}
	 * @param message The MessageDTO to be converted to Message and Added to the DB
	 * @return 
	 *
	 **/
	@POST
	@Path("/post")
	@Consumes("application/json")
	public static Response post(MessageDTO message) {
		
		System.out.println("MessageHandler-Post: ");
		System.out.println("message: " + message.getMessage());
		
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Lab1");
		EntityManager em = emf.createEntityManager();
		
		System.out.println("MessageHandler: Postin in " + message.getReceiver() + "s flow");
		try {

			em.getTransaction().begin();
			User reciever = (User) em.createQuery("Select u FROM User u where u.username = ?1")
					.setParameter(1, message.getReceiver()).getSingleResult();
			User sender = (User) em.createQuery("Select u FROM User u where u.username = ?1")
					.setParameter(1, message.getSender()).getSingleResult();

			Message newMessage = new Message(sender, message.getMessage(), new Date(), message.getSubject(), message.getType());
			reciever.RecievedMessages.add(newMessage);
			em.persist(newMessage);
			em.getTransaction().commit();
			return Response.status(Response.Status.CREATED).entity("ok").build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.CREATED).entity("fail").build();
		} finally {
			em.close();
			emf.close();
		}
		
		
	}
}
