package Post;

public class Post implements PostElements {
 

	String postPrivacy ;
	String postName ;
	String postLikes;
	String postContent;
    public Post(String postPrivacy,String postName ,	String postLikes,String postContent){
    	

    	 this.postPrivacy  = postPrivacy ;
    	 this.postName = postName;
    	 this.postLikes = postLikes;
    	 this.postContent = postContent; 
    }
    public Post(){}

    @Override
	public void setPrivacy(String privacy) {


		postPrivacy = privacy ;
	}

	public String getPrivacy() {


		return postPrivacy ;
	}
	@Override
	public void setLikes(String likes) {

		postLikes = likes ;
	}
	@Override
	public void setName(String name) {

		postName = name ;
	}
	
	@Override
	public void setPostContent(String post) {

		postContent = post ;
	}
	
}
