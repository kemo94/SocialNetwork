package com.FCI.SWE.Controller;

import java.util.ArrayList;

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

import Post.PagePostBuilder;
import Post.Post;
import Post.PostBuilder;
import Post.PostCreater;
import Post.UserPostBuilder;

import com.FCI.SWE.ServicesModels.UserEntity;

@Path("/")
@Produces("text/html")
public class PostController {

	String userEmail ;
	String post ;
	String hashTag ;
	String pageName;


	@GET
	@Path("/pagePost")
	public Response pagePost() {

		return Response.ok(new Viewable("/jsp/postPage")).build();
	}

	@GET
	@Path("/searchPage")
	public Response searchPage() {

		return Response.ok(new Viewable("/jsp/searchPage")).build();
	}

	@GET
	@Path("/createPage")
	public Response createPage() {

		return Response.ok(new Viewable("/jsp/createPage")).build();
	}

	@GET
	@Path("/searchHashPosts")
	public Response searchHashPosts() {
		return Response.ok(new Viewable("/jsp/searchHashPosts")).build();
	}

	@POST
	@Path("/pagePost")
	@Produces("text/html")
	public String postPage( @FormParam("post") String post  ) {

		PostBuilder pagePost = new PagePostBuilder();

		PostCreater postCreater = new PostCreater(pagePost);

		String numOfLikes = "0" ;
		String postPrivacy = "public";
		postCreater.makePost( postPrivacy ,UserController.userPage , numOfLikes, post);



		return "post success";

	}
	@POST
	@Path("/userPost")
	@Produces("text/html")
	public String post( @FormParam("post") String post,@FormParam("privacy") String postPrivacy ) {


		PostBuilder userPost = new UserPostBuilder();

		PostCreater postCreater = new PostCreater(userPost);
		String numOfLikes = "0" ;

		postCreater.makePost(postPrivacy,UserController.currentActiveUser.getEmail() , numOfLikes , post);



		return "post success";

	}
	@POST
	@Path("/sharePosts")
	@Produces("text/html")
	public String sharePosts( @FormParam("post") String post ) {

		Connection.connect( 
				"http://localhost:8888/rest/postService", "currentUserEmail=" +  UserController.currentActiveUser.getEmail() 
				+ "&privacy=public"+ "&post=" + post ,
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");

		return "share post success";

	}


	@GET
	@Path("/likePost")
	@Produces("text/html")
	public String likePost( ) {

		Connection.connect( 
				"http://localhost:8888/rest/likePostService", "currentUserEmail=" +  UserController.requestedUser.getEmail() 
				+ "&post=" +  UserController.likedPost ,
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");

		return "like post success";

	}


	@POST
	@Path("/responsePage")
	@Produces(MediaType.TEXT_PLAIN)
	public String responsePage(@FormParam("pageName") String pageName ) {

		String serviceUrl = "http://localhost:8888/rest/createPageService";
		String urlParameters = "pageName=" + pageName  + "&currentUserEmail=" +  UserController.currentActiveUser.getEmail();
		String retJson = Connection.connect(serviceUrl, urlParameters, "POST",
				"application/x-www-form-urlencoded;charset=UTF-8");
		JSONParser parser = new JSONParser();
		Object obj;
		try {

			obj = parser.parse(retJson);
			JSONObject object = (JSONObject) obj;
			UserController.userPage = pageName;
			if (object.get("Status").equals("OK"))
				return "create page Successfully";

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "Failed";
	}

	@POST
	@Path("/hashPosts")
	public String hashPosts(@FormParam("hash") String hash){

		String html = "" ;
		String urlParameters = "hash=" + hash;
		String retJson = Connection.connect(
				"http://localhost:8888/rest/hashPostsService", urlParameters,
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
		JSONParser parser = new JSONParser();
		Object obj;

		try {

			obj = parser.parse(retJson);

			JSONObject object = (JSONObject) obj;


			ArrayList <ArrayList<String>> hashPosts = ( ArrayList <ArrayList<String>>)object.get("hashPosts");

			html +=  "<form action='/social/sharePosts' method='post'>";

			for(int i = 0; i < hashPosts.size(); i++ ){
				UserController.likedPost =  hashPosts.get(i).get(1)	; 

				html +=		hashPosts.get(i).get(0) + "<br><br>" +
						"<a href='/social/likePost/'> Like </a><input type='submit' value='share' />  " +

							hashPosts.get(i).get(1)	+ "  like  <br><br> <br><br> " +
							"<textarea name='post' rows='4' cols='50' name='post'>"+ hashPosts.get(i).get(2)	+
							"	</textarea> <br> ---------------------------------- <br> <br><br><br>";

			}
			html += "</form>" ;


			return html ;
		}

		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}





		return null;
	}


	@POST
	@Path("/searchPage")
	public String searchPage(@FormParam("page") String page){


		String urlParameters = "page=" + page;

		String retJson = Connection.connect(
				"http://localhost:8888/rest/searchPageService", urlParameters,
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
		JSONParser parser = new JSONParser();
		Object obj;
		String html = "" ;
		try {
			obj = parser.parse(retJson);
			JSONObject object = (JSONObject) obj;

			ArrayList<String> getPage = (ArrayList <String>)object.get("page" );
			if (getPage.size()  != 0 ){
				html +=  "<p> Page name : "+ getPage.get(0) + " </p>";
				html +=  "<p> likes "+ getPage.get(1) + " </p><br>";
				html +=  "<p> number of seen "+ getPage.get(2) + " </p>";
			}

			ArrayList<String> pagePosts = (ArrayList <String>)object.get("pagePosts" );

			if  ( pagePosts.size()  != 0 ){
				html +=  "<form action='/social/sharePosts' method='post'>";

				for(int i = 0; i < pagePosts.size(); i+=2){
					UserController.likedPost = pagePosts.get(i+1)	; 

					html +="<a href='/social/likePost/'> Like </a><input type='submit' value='share' />  " + pagePosts.get(i)	+ "  like  <br><br> <br><br> " +
							"<textarea name='post' rows='4' cols='50' name='post'>"+ pagePosts.get(i+1)	+
							"	</textarea> <br> ---------------------------------- <br> <br><br><br>";

				}
				html += "</form>" ;

				return html ;
			}
		}

		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




		if ( html.length() != 0 )
			return html ;
		else
			return "this page not exist";
	}
}
