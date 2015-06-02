package com.FCI.SWE.Services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.mvc.Viewable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.FCI.SWE.Controller.UserController;
import com.FCI.SWE.ServicesModels.PageEntity;
import com.FCI.SWE.ServicesModels.PostEntity;
import com.FCI.SWE.ServicesModels.UserEntity;

/**
 * This class contains REST services, also contains action function for web
 * application
 * 
 * @author Mohamed Samir
 * @version 1.0
 * @since 2014-02-12
 *
 */
@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public class UserServices {




	/**
	 * Registration Rest service, this service will be called to make
	 * registration. This function will store user data in data store
	 * 
	 * @param uname
	 *            provided user name
	 * @param email
	 *            provided user email
	 * @param pass
	 *            provided password
	 * @return Status json
	 */
	@POST
	@Path("/RegistrationService")
	public String registrationService(@FormParam("uname") String uname,
			@FormParam("email") String email, @FormParam("password") String pass) {
		UserEntity user = new UserEntity(email, pass);

		user.saveUser();
		JSONObject object = new JSONObject();
		object.put("Status", "OK");
		return object.toString();
	}

	@POST
	@Path("/searchService")
	public String searchService(@FormParam("email") String requestedEmail) {

		JSONObject object = new JSONObject();
		UserEntity reqUser = UserEntity.getUser(requestedEmail);
		if(reqUser == null)
			object.put("Status","Failed" );
		else{
			object.put("Status", "OK");
			object.put("name", reqUser.getName());
			object.put("email", reqUser.getEmail());
			object.put("password", reqUser.getPass());
			object.put("id", reqUser.getId());
		}
		return object.toString();
	}

	/**
	 * Login Rest Service, this service will be called to make login process
	 * also will check user data and returns new user from datastore
	 * @param uname provided user name
	 * @param pass provided user password
	 * @return user in json format
	 */
	@POST
	@Path("/LoginService")
	public String loginService(@FormParam("email") String userEmail,
			@FormParam("password") String pass) {
		JSONObject object = new JSONObject();

		UserEntity user = UserEntity.getUser(userEmail, pass);
		if (user == null) {
			object.put("Status", "Failed");

		} else {
			object.put("Status", "OK");
			object.put("name", user.getName());
			object.put("email", user.getEmail());
			object.put("password", user.getPass());
			object.put("id", user.getId());
		}
		return object.toString();

	}


	/**
	 * Login Rest Service, this service will be called to make login process
	 * also will check user data and returns new user from datastore
	 * @param uname provided user name
	 * @param pass provided user password
	 * @return user in json format
	 */
	@POST
	@Path("/sendFriendRequestService")
	public String sendFriendRequestService(@FormParam("currentUserEmail") String currentUserEmail,
			@FormParam("requestedUserEmail") String requestedUserEmail) {
		JSONObject object = new JSONObject();

		UserEntity.sendFriendRequest(currentUserEmail, requestedUserEmail);


		return object.toString();

	}
	/**
	 * send message  Service, this service will be called to send message process
	 * 
	 * @param currentUserEmail provided user email
	 * @param requestedUserEmail provided receiver email
	 * @param Message provided message to receiver email
	 * @return status in json format
	 */
	@POST
	@Path("/sendMessageService")
	public String sendMessage(@FormParam("currentUserEmail") String currentUserEmail,
			@FormParam("friendEmail") String requestedUserEmail,
			@FormParam("Message") String message) {


		JSONObject object = new JSONObject();



		UserEntity.sendMessage(currentUserEmail, requestedUserEmail,message);

		object.put("status", "OK" );


		return object.toString();

	}

	/**
	 * show Friend Request Service, this service will be called to show all friend request
	 * 
	 * @param currentUserEmail provided user email
	 *
	 * @return  all friend request in json format
	 */
	@POST
	@Path("/showFriendRequestService")
	public String showFriendRequestService(@FormParam("currentUserEmail") String currentUserEmail) {
		JSONObject object = new JSONObject();

		ArrayList<String> friendRequests = new ArrayList<String>(UserEntity.friendRequests(currentUserEmail));


		object.put("friendRequests", friendRequests );



		return object.toString();

	}

	@POST
	@Path("/showFriendsService")
	public String showFriendsService(@FormParam("currentUserEmail") String currentUserEmail) {
		JSONObject object = new JSONObject();

		ArrayList<String> friendRequests = new ArrayList<String>(UserEntity.showFriends(currentUserEmail));
		object.put("friendRequests", friendRequests );


		object.put("friends",friendRequests);



		return object.toString();

	}


	/**
	 * show messages  Service, this service will be called to show all messages 
	 * 
	 * @param currentUserEmail provided user email
	 *
	 * @return  all messages in json format
	 */
	@POST
	@Path("/showMessagesService")
	public String showMessagesService(@FormParam("currentUserEmail") String currentUserEmail) {
		JSONObject object = new JSONObject();

		ArrayList<String> messages = new ArrayList<String>(UserEntity.showMessages(currentUserEmail));
		object.put("Messages", messages );

		return object.toString();

	}

	@POST
	@Path("/acceptRequestService")
	public String acceptRequestService(@FormParam("currentUserEmail") String currentUserEmail, @FormParam("friendRequestEmail") String friendRequestEmail){
		UserEntity.acceptRequest(currentUserEmail, friendRequestEmail);


		return "You Are Now Friend With " + friendRequestEmail;
	}
	
	@POST
	@Path("showPostService")
	public String showPostService( @FormParam("email") String requestEmail , @FormParam("privacyType") String privacyType  ) {

		JSONObject object = new JSONObject();

		ArrayList<String> showPosts = new ArrayList<String>(PostEntity.showPosts(requestEmail , privacyType));
		object.put("posts", showPosts );


		return object.toString();

	}

	@POST
	@Path("likePostService")
	public String likePostService( @FormParam("currentUserEmail") String currentUserEmail , @FormParam("post") String post  ) {

		JSONObject object = new JSONObject();
		if ( PostEntity.likePost(currentUserEmail , post) == true )
			object.put("status", "OK" );
		else
			object.put("status", "Fail" );



		return object.toString();

	}

	@POST
	@Path("/hashPostsService")
	public String hashPostsService(@FormParam("hash") String hash ){
		JSONObject object = new JSONObject();


		ArrayList <ArrayList<String>> showPosts = new  ArrayList <ArrayList<String>>(PostEntity.getHashPost(hash));
		object.put("hashPosts", showPosts );

		return object.toString();
	}
	@POST
	@Path("/createPageService")
	public String createPageService(@FormParam("currentUserEmail") String currentUserEmail , @FormParam("pageName") String pageName ) {

		JSONObject object = new JSONObject();
		PageEntity.createPage(pageName , currentUserEmail);
		object.put("Status", "OK");

		return object.toString();
	}

	@POST
	@Path("/searchPageService")
	public String searchPageService(@FormParam("page") String page){

		JSONObject object = new JSONObject();
		ArrayList<String> getPage = new  ArrayList <String>(PageEntity.getPage(page ));

		ArrayList<String> showPosts = new  ArrayList <String>(PageEntity.showPagePosts(page ));

		object.put("page", getPage );
		object.put("pagePosts", showPosts );


		return object.toString();
	}
}