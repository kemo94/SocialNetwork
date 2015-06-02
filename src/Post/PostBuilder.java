package Post;

public interface PostBuilder {


	public void createName (String name);

	public void createLikes (String likes);

	public void createPostContent(String postContent);

	public Post getPost();
	
	public void savePost(Post post);

	void createPrivacy(String privacy);
	
}
