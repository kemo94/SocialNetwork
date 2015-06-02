package Post;

import java.util.List;

import com.FCI.SWE.Controller.Connection;
import com.FCI.SWE.ServicesModels.PostEntity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;

public class UserPostBuilder implements PostBuilder{


	public Post post ;

	public UserPostBuilder(){

		this.post = new Post();
	}
	@Override
	public void createPrivacy(String privacy) {
		post.setPrivacy(privacy);
	}

	@Override
	public void createName(String name) {
		post.setName(name);
	}

	@Override
	public void createLikes(String likes) {
		post.setLikes(likes);
	}

	@Override
	public void createPostContent(String postContent) {
		post.setPostContent(postContent);
	}
	public Post getPost(){
		return this.post ;

	}

	public void savePost(Post post){

        PostEntity.hashTag(post.postContent);
        
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		Query gaeQuery = new Query("posts");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());
       
		try {
			Entity entityOfPost = new Entity("posts", list.size() + 1);

			entityOfPost.setProperty("name", post.postName );
			entityOfPost.setProperty("privacy", post.postPrivacy);
			entityOfPost.setProperty("post",post.postContent);
			entityOfPost.setProperty("likes","0");
			entityOfPost.setProperty("idPost",list.size() + 1 );


			datastore.put(entityOfPost);
			txn.commit();
		}finally{
			if (txn.isActive()) {
				txn.rollback();
			}
		}



	}
}
