package com.FCI.SWE.ServicesModels;

import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.FCI.SWE.Controller.UserController;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;

/**
 * <h1>User Entity class</h1>
 * <p>
 * This class will act as a model for user, it will holds user data
 * </p>
 *
 * @author Mohamed Samir
 * @version 1.0
 * @since 2014-02-12
 */
public class UserEntity {
	public String name;
	public String email;
	public String password;

	private static long id;
 
	/**
	 * Constructor accepts user data
	 * 
	 * @param name
	 *            user name
	 * @param email
	 *            user email
	 * @param password
	 *            user provided password
	 */

	public UserEntity( String email, String password) {
		this.email = email;
		this.password = password;

	}

	public UserEntity( String email) {
	
		this.email = email;
	}

	private void setId(long id){
		this.id = id;
	}

	public long getId(){
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPass() {
		return password;
	}



	/**
	 * 
	 * This static method will form UserEntity class using user name and
	 * password This method will serach for user in datastore
	 * 
	 * @param name
	 *            user name
	 * @param pass
	 *            user password
	 * @return Constructed user entity
	 */

	public static UserEntity getUser(String email, String pass) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query gaeQuery = new Query("users");

		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity entity : pq.asIterable()) {
			if (entity.getProperty("email").toString().equals(email) && entity.getProperty("password").toString().equals(pass)) {
				UserEntity returnedUser = new UserEntity( entity.getProperty("email")
						.toString(), entity.getProperty("password").toString());


				returnedUser.setId(entity.getKey().getId());
				return returnedUser;
			}
		}

		return null;
	}	

	/**
	 * This method will be used to save user object in datastore
	 * 
	 * @return boolean if user is saved correctly or not
	 */

	public Boolean saveUser() {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		Query gaeQuery = new Query("users");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());


		try {
			Entity newUser = new Entity("users", list.size() + 1);

			newUser.setProperty("name", this.name);
			newUser.setProperty("email", this.email);
			newUser.setProperty("password", this.password);



			datastore.put(newUser);
			txn.commit();
		}
		finally{
			if (txn.isActive()) {
				txn.rollback();
			}
		}
		return true;

	}

	public static UserEntity getUser(String email) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();


		Query gaeQuery = new Query("users");

		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity entity : pq.asIterable()) {
			if ( entity.getProperty("email").toString().equals(email) ) {
				UserEntity returnedUser = new UserEntity( entity.getProperty("email")
						.toString(), entity.getProperty("password").toString());


				returnedUser.setId(entity.getKey().getId());
				return returnedUser;
			}
		}

		return null;
	}


	/**
	 * This method will be used to add friend  datastore
	 * 
	 * @return boolean if add friend saved correctly or not
	 */
	public static Boolean sendFriendRequest(String currentUserEmail, String requestedUserEmail) {

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		Query gaeQuery = new Query("Friends");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());




		try {


			Entity newFriend = new Entity("Friends", list.size() + 1);

			newFriend.setProperty("User email",  currentUserEmail);
			newFriend.setProperty("Friend email",  requestedUserEmail);
			newFriend.setProperty("status", "0");

			datastore.put(newFriend);
			txn.commit();
		}
		finally{
			if (txn.isActive()) {
				txn.rollback();
			}
		}
		return true;

	}


	/**
	 * This method will be used to send message to friend  datastore
	 * 
	 * @return boolean if send message saved correctly or not
	 */
	public static Boolean sendMessage(String currentUserEmail, String requestedUserEmail , String message) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		Query gaeQuery = new Query("Messages");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		List<Entity> listOfMessages = pq.asList(FetchOptions.Builder.withDefaults());



		try {

			Entity newMessage = new Entity("Messages", listOfMessages.size() + 1);



			newMessage.setProperty("Sender email",  currentUserEmail);
			newMessage.setProperty("User email",  requestedUserEmail);
			newMessage.setProperty("Message", message);
			newMessage.setProperty("status", "not seen");

			datastore.put(newMessage);
			txn.commit();
		}finally{
			if (txn.isActive()) {
				txn.rollback();
			}
		}
		return true;

	}


	/**
	 * This method will be used to accept friend request  
	 * 
	 * @return boolean if accept friend request   saved correctly or not
	 */
	public static boolean  acceptRequest(String currentUserEmail, String friendRequestEmail){

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query gaeQuery = new Query("Friends");

		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity acceptFriend : pq.asIterable()) {
			if ( acceptFriend.getProperty("User email").toString().equals(friendRequestEmail ) &&
					acceptFriend.getProperty("Friend email").toString().equals(currentUserEmail )	) {

				acceptFriend.setProperty("status", "1" );
				datastore.put(acceptFriend);


				return true;
			}
		}

		return false;



	}

	/**
	 * This method will be used to show all friend request  
	 * 
	 * @return ArrayList<String> of friend requests
	 */
	public static ArrayList<String>  friendRequests(String currentUserEmail){

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query gaeQuery = new Query("Friends");
		ArrayList<String> friendRequests = new ArrayList<String>();
		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity getFriendRequests : pq.asIterable()) {
			if ( getFriendRequests.getProperty("Friend email").toString().equals(currentUserEmail) &&
					getFriendRequests.getProperty("status").toString().equals("0")	) {


				friendRequests.add( getFriendRequests.getProperty("User email").toString());

			}
		}

		return friendRequests;



	}


	public static ArrayList<String>  showFriends(String currentUserEmail){

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query gaeQuery = new Query("Friends");
		ArrayList<String> friends = new ArrayList<String>();
		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity showFriends : pq.asIterable()) {
			if ( (showFriends.getProperty("User email").toString().equals(currentUserEmail) ||
					showFriends.getProperty("Friend email").toString().equals(currentUserEmail) 
					) &&showFriends.getProperty("status").toString().equals("1")	) {

				if (showFriends.getProperty("User email").toString().equals(currentUserEmail) )
					friends.add( showFriends.getProperty("Friend email").toString());
				else if (showFriends.getProperty("Friend email").toString().equals(currentUserEmail) )
					friends.add( showFriends.getProperty("User email").toString());

			}
		}

		return friends;



	}


	/**
	 * This method will be used to show messages   
	 * 
	 * @return ArrayList<String> of all messages
	 */
	public static ArrayList<String>  showMessages(String currentUserEmail){

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query gaeQuery = new Query("Messages");
		ArrayList<String> messages = new ArrayList<String>();
		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity getMessages : pq.asIterable()) {
			if ( getMessages.getProperty("User email").toString().equals(currentUserEmail) &&
					getMessages.getProperty("status").toString().equals("not seen")	) {


				messages.add( getMessages.getProperty("Sender email").toString());
				messages.add( getMessages.getProperty("Message").toString());
				getMessages.setProperty("status", "seen" );


				datastore.put(getMessages);
			}
		}


		return messages;



	}


}
