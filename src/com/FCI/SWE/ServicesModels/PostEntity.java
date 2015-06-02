package com.FCI.SWE.ServicesModels;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;

public class PostEntity {

	private static long id;

	private String ownerEmail;
	
	public PostEntity (long id , String ownerEmail){
		this.id = id;
		this.ownerEmail = ownerEmail ;

	}
	public static int getIdPost(){

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		Query gaeQuery = new Query("posts");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());

		return list.size() + 1;
	}


	public static Boolean hashTag(String post ) {

		String[] hashTag = post.split(" ");

		DatastoreService datastoreInPostsTable = DatastoreServiceFactory
				.getDatastoreService();
		Query gaeQueryInPostsTable = new Query("posts");
		PreparedQuery pqInPostsTable = datastoreInPostsTable.prepare(gaeQueryInPostsTable);
		List<Entity>  listOfPosts = pqInPostsTable.asList(FetchOptions.Builder.withDefaults());

		DatastoreService datastoreInHashTable = DatastoreServiceFactory
				.getDatastoreService();
		Transaction txn = datastoreInHashTable.beginTransaction();
		Query gaeQuery = new Query("Hash");
		PreparedQuery pq = datastoreInHashTable.prepare(gaeQuery);
		List<Entity> listOfHash = pq.asList(FetchOptions.Builder.withDefaults());

		try {
			Entity entityOfHash = new Entity("Hash", listOfHash.size() + 1);

			for ( int i = 0 ; i < hashTag.length ; i ++ ){

				if ( hashTag[i].charAt(0) == '#' ){

					entityOfHash.setProperty("post", post );
					entityOfHash.setProperty("Hash Tag", hashTag[i] );
					entityOfHash.setProperty("idPost", listOfPosts.size()+1 );

					datastoreInHashTable.put(entityOfHash);
				}
			}
			txn.commit();
		}finally{
			if (txn.isActive()) {
				txn.rollback();
			}
		}


		return true;

	}

	public static ArrayList<String>  showPosts(String email , String privacyType ){
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query gaeQuery = new Query("posts");
		ArrayList<String> posts = new ArrayList<String>();
		PreparedQuery pq = datastore.prepare(gaeQuery);


		for (Entity entityOfPost : pq.asIterable()) {

			if (  privacyType.equals("friend") )
			{
				if ( entityOfPost.getProperty("name").toString().equals(email)  ) {

					posts.add( entityOfPost.getProperty("likes").toString());
					posts.add( entityOfPost.getProperty("post").toString());


					datastore.put(entityOfPost);
				}
			}
			else
			{
				if ( entityOfPost.getProperty("name").toString().equals(email) &&
						entityOfPost.getProperty("privacy").toString().equals("public")  ) {

					posts.add( entityOfPost.getProperty("likes").toString());
					posts.add( entityOfPost.getProperty("post").toString());

					datastore.put(entityOfPost);
				}

			}	 
		}


		return posts;

	}


	public static boolean  likePost(String email , String post ){
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query gaeQuery = new Query("posts");

		PreparedQuery pq = datastore.prepare(gaeQuery);

		for (Entity entityOfPost : pq.asIterable()) {



			if ( entityOfPost.getProperty("name").toString().equals(email)  &&
					entityOfPost.getProperty("post").toString().equals(post)  ) {

				String numOfLikes = entityOfPost.getProperty("likes").toString();

				int newNumOfLikes =  Integer.parseInt(numOfLikes) + 1;

				entityOfPost.setProperty("likes",Integer.toString( newNumOfLikes));

				datastore.put(entityOfPost);


				return true;
			}	
		}


		return false ;
	}




	public static  ArrayList <ArrayList<String>>  getHashPost(String hash ){
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query gaeQuery = new Query("Hash");
		ArrayList <ArrayList<String>> posts = new  ArrayList <ArrayList<String>>();
		PreparedQuery pq = datastore.prepare(gaeQuery);


		for (Entity entityOfHash : pq.asIterable()) 
			if ( entityOfHash.getProperty("Hash Tag").toString().equals(hash)  ) {

				posts.add(showHashPosts(entityOfHash.getProperty("idPost").toString()));

			}

		return posts;

	}


	public static ArrayList<String>  showHashPosts(String idPost  ){
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query gaeQuery = new Query("posts");
		ArrayList<String> posts = new ArrayList<String>();
		PreparedQuery pq = datastore.prepare(gaeQuery);


		for (Entity entityOfPost : pq.asIterable()) {

			if ( entityOfPost.getProperty("idPost").toString().equals(idPost)  ) {

				posts.add( entityOfPost.getProperty("name").toString());
				posts.add( entityOfPost.getProperty("likes").toString());
				posts.add( entityOfPost.getProperty("post").toString());


			}
		}


		return posts;

	}




}
