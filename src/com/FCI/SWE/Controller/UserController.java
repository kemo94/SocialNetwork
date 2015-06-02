package com.FCI.SWE.Controller;

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

import Command.AcceptFriendRequest;
import Command.Invoker;
import Command.Receiver;
import Command.ReplyGroupMessage;
import Command.ReplySingleMessage;
import Notification.EventSubject;
import Notification.NotificationFriendRequest;
import Notification.NotificationSingleMessage;

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
@Produces("text/html")
public class UserController {
	public static UserEntity currentActiveUser = null; 

	public static String userPage = ""; 
	public static UserEntity requestedUser = null; 
	public static ArrayList<String> membersOfGroupMessage = new ArrayList<String>() ;
	public static String likedPost = "" ;

	/**
	 * Action function to render Signup page, this function will be executed
	 * using url like this /rest/signup
	 * 
	 * @return sign up page
	 */

	@GET
	@Path("/signup")
	public Response signUp() {

		return Response.ok(new Viewable("/jsp/register")).build();
	}


	@GET
	@Path("/search")
	public Response search(){
		return Response.ok(new Viewable("/jsp/search")).build();
	}
	/**
	 * Action function to render home page of application, home page contains
	 * only signup and login buttons
	 * 
	 * @return enty point page (Home page of this application)
	 */
	@GET
	@Path("/")
	public Response index() {
		return Response.ok(new Viewable("/jsp/entryPoint")).build();
	}

	/**
	 * Action function to render login page this function will be executed using
	 * url like this /rest/login
	 * 
	 * @return login page
	 */
	@GET
	@Path("/login")
	public Response login() {
		return Response.ok(new Viewable("/jsp/login")).build();
	}


	@POST
	@Path("/doSearch")
	public String usersList(@FormParam("email") String userEmail){

		requestedUser = new UserEntity(userEmail ) ;
		String urlParameters = "email=" + userEmail;
		String retJsonSearch = Connection.connect(
				"http://localhost:8888/rest/searchService", urlParameters,
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
		JSONParser parser = new JSONParser();
		Object objSearch;
		try {
			objSearch = parser.parse(retJsonSearch);
			JSONObject jsonObjectSearch = (JSONObject) objSearch;
			if (jsonObjectSearch.get("Status").equals("Failed"))
				return null;
			else{


				try {


					String retJsonShowFriends   = Connection.connect(
							"http://localhost:8888/rest/showFriendsService","currentUserEmail=" + currentActiveUser.getEmail(),
							"POST", "application/x-www-form-urlencoded;charset=UTF-8");
					JSONParser parser2 = new JSONParser();
					Object objShowFriends= parser2.parse(retJsonShowFriends);

					JSONObject objectShowFriends = (JSONObject)objShowFriends ;



					ArrayList<String> friends = (ArrayList<String>)objectShowFriends.get("friends");
					String privacyType = "";



					if ( friends.indexOf(userEmail) != -1 )

						privacyType = "friend";
					else
						privacyType = "not friend";

					urlParameters = "email=" + userEmail + "&privacyType=" + privacyType ; 


					String retJsonShowPost = Connection.connect(
							"http://localhost:8888/rest/showPostService", urlParameters,
							"POST", "application/x-www-form-urlencoded;charset=UTF-8");
					JSONParser parserShowPost = new JSONParser();
					Object objShowPost = parserShowPost.parse(retJsonShowPost);
					JSONObject objectShowPost = (JSONObject) objShowPost;


					ArrayList<String> userPosts = ( ArrayList<String>) objectShowPost.get("posts") ; 
					requestedUser = new UserEntity (userEmail , "");
					String html = "  <p>User INFO</p>" +
							"<p>  <br> Email: " + userEmail + "</p>"+
							"<a href='/social/sendFriendRequest'>Send Friend Request</a><br>"+
							"<a href='/social/sendMessages'>Send Message<a><br>"+
							"<br><br>" ;


					html +=  "<form action='/social/sharePosts' method='post'>";

					for(int i = 0; i < userPosts.size(); i+=2){
						likedPost = userPosts.get(i+1)	; 

						html +="<a href='/social/likePost/'> Like </a><input type='submit' value='share' />  " + userPosts.get(i)	+ "  like  <br><br> <br><br> " +
								"<textarea name='post' rows='4' cols='50' name='post'>"+ userPosts.get(i+1)	+
								"	</textarea> <br> ---------------------------------- <br> <br><br><br>";

					}
					html += "</form>" ;

					return html ;
				}

				catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}catch (ParseException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		return null;
	}

	@POST
	@Path("/response")
	@Produces(MediaType.TEXT_PLAIN)
	public String response(@FormParam("uname") String uname,
			@FormParam("email") String email, @FormParam("password") String pass) {

		String serviceUrl = "http://localhost:8888/rest/RegistrationService";
		String urlParameters = "uname=" + uname + "&email=" + email
				+ "&password=" + pass;
		String retJson = Connection.connect(serviceUrl, urlParameters, "POST",
				"application/x-www-form-urlencoded;charset=UTF-8");
		JSONParser parser = new JSONParser();
		Object obj;
		try {
			// System.out.println(retJson);
			obj = parser.parse(retJson);
			JSONObject object = (JSONObject) obj;
			if (object.get("Status").equals("OK"))
				return "Registered Successfully";

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * UserEntity user = new UserEntity(uname, email, pass);
		 * user.saveUser(); return uname;
		 */
		return "Failed";
	}




	/**
	 * Action function to response to login request. This function will act as a
	 * controller part, it will calls login service to check user data and get
	 * user from datastore
	 * 
	 * @param uname
	 *            provided user name
	 * @param pass
	 *            provided user password
	 * @return Home page view
	 */
	@POST
	@Path("/home")
	@Produces("text/html")
	public String home(@FormParam("email") String userEmail,
			@FormParam("password") String pass ) {

		String urlParameters = "email=" + userEmail + "&password=" + pass;
		String retJson = Connection.connect(
				"http://localhost:8888/rest/LoginService", urlParameters,
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");

		JSONParser parser = new JSONParser();
		Object obj;

		try {
			obj = parser.parse(retJson);
			JSONObject object = (JSONObject) obj;
			if (object.get("Status").equals("Failed"))
				return "fail";

			currentActiveUser = new  UserEntity(  userEmail,  pass) ;

			urlParameters = "email=" + userEmail + "&privacyType=public" ; 

			String retJsonShowPosts = Connection.connect(
					"http://localhost:8888/rest/showPostService", urlParameters,
					"POST", "application/x-www-form-urlencoded;charset=UTF-8");
			JSONParser parserShowPosts = new JSONParser();
			Object objShowPosts;

			objShowPosts = parserShowPosts.parse(retJsonShowPosts);
			JSONObject objectShowPosts = (JSONObject) objShowPosts;

			objectShowPosts = (JSONObject) objShowPosts;

			ArrayList<String> posts = ( ArrayList<String>) objectShowPosts.get("posts") ; 


			String html = "<p> Welcome b2a ya " + userEmail +"</p> " + 
					"<a href='/social/search/'>Search for a Friend</a><br>" +
					"<a href='/social/showFriends'>Show all Friends<a><br>" + 
					"<a href='/social/Notification'>Notification <a><br>"+
					"<a href='/social/createPage'>Create Page<a><br>"+

					"<a href='/social/searchPage'>Search page<a><br>"+
					"<a href='/social/pagePost'>Post with your page<a><br>"+
					"<a href='/social/searchHashPosts'>show hash tag posts <a><br><br><br>"+
					
					"<form action='/social/userPost' method='post'>"+
					"<textarea rows='4' cols='50' name='post'>"+
					"</textarea><br><br><br>"+
					"Privacy : <select name = 'privacy'>"+
					"<option value = 'public'>Public</option>"+
					"<option value = 'private''>Private</option>"+
					"</select>"+
					"<input type='submit' value='post' />"+
					"</form>" ;

			for(int i = 0; i < posts.size(); i+=2)
				html +=  "<p>" + posts.get(i)	+ "  like  <br> <br> "  + posts.get(i+1)	+"' </p>  <br> ---------------------------------- <br>";


			return html ;


		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;

	}
	/**
	 * Action function to string to send friend request. This function will act as a
	 * controller part, it will calls sendFriendRequestService to send to user friend request
	 * 
	 */
	@GET
	@Path("/sendFriendRequest")
	@Produces("text/html")
	public String sendFriendRequest() {

		String retJsonSendFriendRequest = Connection.connect(
				"http://localhost:8888/rest/sendFriendRequestService","currentUserEmail=" + currentActiveUser.getEmail() + "&requestedUserEmail="+requestedUser.getEmail() ,
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");

		return "Request Sent Successfully";

	}
	/**
	 * Action function to  notify current user. 
	 *  it will calls notification type from observer
	 * 
	 */
	@GET
	@Path("/Notification")
	@Produces("text/html")
	public String notification() {

		String html = "" ; 

		EventSubject sub = new EventSubject();

		new NotificationFriendRequest( sub );
		new NotificationSingleMessage( sub );

		html += sub.setState(currentActiveUser.getEmail());
		if ( html.length() == 0 )
			return "You Have No Notification" ;
		return html ;
	}

	/**
	 * Action function to response to perform command , that current user execute it 
	 *  
	 * 
	 */
	@POST
	@Path("/performCommand")
	@Produces("text/html")
	public Response performCommand(@FormParam("email") String userEmail ,  @FormParam("command") String command) {

		String path = "";
		Receiver receiver = new Receiver(currentActiveUser.getEmail(),userEmail);
		Invoker invoker = new Invoker();


		if ( command.equals("message") )

			path =invoker.execute(new ReplySingleMessage() , receiver);
		else if (command.equals("Friend Request") )
		{

			invoker.execute(new AcceptFriendRequest() , receiver);
			return null;
		}

		Map<String, String> map = new HashMap<String, String>();


		map.put("email", userEmail);

		return Response.ok(new Viewable("/jsp/"+path, map)).build();

	}



	/**
	 * Action function to show friends, This function will act as
	 * a controller part and it will calls showFriendsService 
	 * 
	 * 
	 * 
	 * @return String of jsp
	 */
	@GET
	@Path("/showFriends")
	@Produces("text/html")
	public String showFriends() {

		String retJsonShowFriends = Connection.connect(
				"http://localhost:8888/rest/showFriendsService","currentUserEmail=" + currentActiveUser.getEmail(),
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");

		JSONParser parserShowFriends = new JSONParser();
		try{
			Object objShowFriends = parserShowFriends.parse(retJsonShowFriends);
			JSONObject objectShowFriends = (JSONObject)objShowFriends;
			ArrayList<String> friends = (ArrayList<String>)objectShowFriends.get("friends");
			String html =  "<form action=\"/social/doSearch\" method=\"post\"> ";
			if(friends.size() == 0)
				return "You Have No Friends.";
			for(int i = 0; i < friends.size(); i++)
				html += "<input type=\"submit\" name = \"email\" value=\"" + friends.get(i)	+"\">" + "<br>";
			html += "</form>";
			return html;
		}

		catch(Exception e){
			e.printStackTrace();
		}

		return "";

	}


}