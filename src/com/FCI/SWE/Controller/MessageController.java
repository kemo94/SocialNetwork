package com.FCI.SWE.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.mvc.Viewable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Path("/")
@Produces("text/html")
public class MessageController {

	String msg ;
	String userEmail ;
	String receiverEmail ;


	/**
	 * Action function to render sendMessages page this function will be executed using
	 * url like this /rest/sendMessages
	 * 
	 * @return sendMessages page
	 */
	@GET
	@Path("/sendMessages")
	public Response sendMessage() {

		Map<String, String> map = new HashMap<String, String>();
		map.put("email", UserController.requestedUser.getEmail());
		return Response.ok(new Viewable("/jsp/sendMessage", map)).build();


	}
	/**
	 * Action function to send message to friends, This function will act as
	 * a controller part and it will calls showFriendsService and user will choose his friend
	 * 
	 * 
	 * 
	 * @return String of jsp
	 */

	@POST
	@Path("/saveMessage")
	@Produces("text/html")
	public String sendMessages(@FormParam("email") String requestedEmail,
			@FormParam("Message") String Message ) {


		String ret = Connection.connect(
				"http://localhost:8888/rest/sendMessageService","currentUserEmail=" + UserController.currentActiveUser.getEmail()+"&friendEmail=" + requestedEmail + "&Message=" + Message,
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");




		return "Send Message success";

	}

}
