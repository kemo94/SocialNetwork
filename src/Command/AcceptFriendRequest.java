package Command;

public class AcceptFriendRequest implements  Command{

	/**
	 * Action function to execute the chosen command
	 * 
	 * 
	 * @return String path
	 */
	public String execute(Receiver receiver) {

		String  command = receiver.acceptFriendRequest();
		return command ;
	} 

}
