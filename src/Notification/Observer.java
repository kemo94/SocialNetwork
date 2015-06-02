package Notification;

public abstract class Observer  {
	protected EventSubject eventSubject; 

	public abstract String update(String userEmail);
}
