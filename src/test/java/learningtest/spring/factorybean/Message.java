package learningtest.spring.factorybean;

public class Message {
	String text;

	private Message(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	// Static Factory Method
	public static Message newMessage(String text) {
		return new Message(text);
	}
}
