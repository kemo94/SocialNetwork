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

public class PageEntity {

	public String name;
	public String ownerEmail;

	public PageEntity( String name, String ownerEmail) {
		this.name = name;
		this.ownerEmail = ownerEmail;

	}

	public static Boolean createPage( String pageName , String currentUserEmail  ) {

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		Query gaeQuery = new Query("pages");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		List<Entity> listOfPages = pq.asList(FetchOptions.Builder.withDefaults());

		try {
			Entity entityOfPost = new Entity("pages", listOfPages.size() + 1);

			entityOfPost.setProperty("page name", pageName );
			entityOfPost.setProperty("owner email", currentUserEmail);
			entityOfPost.setProperty("likes","0");

			entityOfPost.setProperty("number of seen","0");

			datastore.put(entityOfPost);
			txn.commit();
		}finally{
			if (txn.isActive()) {
				txn.rollback();
			}
		}


		return true;
	}

	public static ArrayList<String>  getPage(String page  ){
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query gaeQuery = new Query("pages");
		ArrayList<String> pageInfo = new ArrayList<String>();
		PreparedQuery pq = datastore.prepare(gaeQuery);


		for (Entity entityOfPage : pq.asIterable()) {

			if ( entityOfPage.getProperty("page name").toString().equals(page)  ) {

				pageInfo.add( entityOfPage.getProperty("page name").toString());
				pageInfo.add( entityOfPage.getProperty("likes").toString());
				pageInfo.add( entityOfPage.getProperty("number of seen").toString());



				String numOfSeen = entityOfPage.getProperty("number of seen").toString();


				int newNumOfSeen =  Integer.parseInt(numOfSeen) + 1;

				entityOfPage.setProperty("number of seen",Integer.toString( newNumOfSeen));

				datastore.put(entityOfPage);

				return pageInfo;
			}
		}


		return null ;

	}
	public static ArrayList<String>  showPagePosts(String page  ){
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query gaeQuery = new Query("posts");
		ArrayList<String> posts = new ArrayList<String>();
		PreparedQuery pq = datastore.prepare(gaeQuery);


		for (Entity entityOfpost : pq.asIterable()) {

			if ( entityOfpost.getProperty("name").toString().equals(page)  ) {

				posts.add( entityOfpost.getProperty("likes").toString());
				posts.add( entityOfpost.getProperty("post").toString());


			}
		}


		return posts;

	}

}
