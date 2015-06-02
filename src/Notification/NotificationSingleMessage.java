package Notification;

import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.FCI.SWE.Controller.Connection;

public class NotificationSingleMessage extends Observer{

	public NotificationSingleMessage( EventSubject s ) { 
		eventSubject = s; 
		eventSubject.attach( this ); 
	}

	@Override
	public String update(String userEmail) {


		String retJson = Connection.connect(
				"http://localhost:8888/rest/showMessagesService","currentUserEmail=" + userEmail ,
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
		String html = "" ;

		JSONParser parser = new JSONParser();
		
		try{
			Object obj = parser.parse(retJson);
			JSONObject object = (JSONObject)obj;
			ArrayList<String> messages = (ArrayList<String>)object.get("Messages");
			html +=  "<form action=\"/social/performCommand\" method=\"post\"> "
					+ "<input type='text' name='command' value='message' />";
			
			for(int i = 0; i < messages.size(); i+=2)
				html += "<p> You got a new  meesage from  </p> <input type='text'  name='email' value='" + messages.get(i) +"' /> <br> <p>"+  messages.get(i+1) +
				"  </p> <input type='submit' value='Reply' /><br>";
			html += "</form>";

			if(messages.size() != 0)
				return html;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return "";

	}
}
