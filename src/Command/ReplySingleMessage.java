package Command;

public class ReplySingleMessage implements  Command{


	/**
	 * Action function to execute the chosen command
	 * 
	 * 
	 * @return String path
	 */
	 public String execute(Receiver receiver) {
		 
		 String  command = receiver.replySingleMessage();
		 return command ;
	 } 
	 
}
