package Post;

public class PostCreater {

	public PostBuilder postBuilder ;

	public PostCreater( PostBuilder postBuilder ){

		this.postBuilder = postBuilder ;
	}
	public Post getPost(){
		return this.postBuilder.getPost() ;

	}


	public void makePost(String privacy , String name , String likes , String postContent){

		this.postBuilder.createPrivacy(privacy);
		this.postBuilder.createName(name);
		this.postBuilder.createLikes(likes);
		this.postBuilder.createPostContent(postContent);
        
		Post post = new Post(privacy, name, likes, postContent);
	

		if ( postBuilder instanceof  PagePostBuilder )
			postBuilder.savePost(post);
		

		if ( postBuilder instanceof UserPostBuilder )
			postBuilder.savePost(post);

		
	}



}
