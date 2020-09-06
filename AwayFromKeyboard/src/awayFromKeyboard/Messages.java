package awayFromKeyboard;

public class Messages {
	private AwayFromKeyboard afk;
	
	public Messages(AwayFromKeyboard afk) {
		this.afk = afk;
	}
	
	public String isNowAfk = afk.getConfig().getString("isNowAfkMessage").replaceAll("'", "");
}
