package Command;

public class ReplyGroupMessage implements  Command{

	/**
	 * Action function to execute the chosen command
	 * 
	 * 
	 * @return String path
	 */
	public String execute(Receiver receiver) {

		String command = receiver.replyGroupMessage();
		return command ;
	} 
}
