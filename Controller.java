package jp.ac.dendai.cps.ryo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Controller {
	String steamAPIkey;
	String youtubeAPIkey;
	String steamID;
	String steamIDnum;
	String channelID;
	String channelIDnum;
	HashMap<String,Integer>gameMap;
	Set<String>channelSet;
	ArrayList<Video>videoList;

	Controller(){
		this.steamAPIkey = "ここにSteamAPIkeyを入れる";
		this.youtubeAPIkey = "ここにYouTubeAPIkeyを入れる";
		this.steamID = "";
		this.channelID = "";
		this.steamIDnum = "";
		this.channelIDnum = "";
		gameMap = new HashMap<String,Integer>();
		channelSet = new HashSet<String>();
		videoList = new ArrayList<Video>();
	}

	//ユニークなSteamIDをリクエストするURL
	public URL setSteamIDnumURL(){
		String str = "http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/?key="+
				steamAPIkey+"&vanityurl="+steamID+"&format=xml&include_appinfo=1";
		URL url = null;
		try {
			url = new URL(str);
		} catch (MalformedURLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return url;
	}

	//最近2週間にプレイしたゲーム情報をリクエストするURL
	public URL setRecentlyPlayedGamesURL(){
		String str = "https://api.steampowered.com/IPlayerService/GetRecentlyPlayedGames/v1/?key="+
					steamAPIkey+"&steamid="+steamIDnum+"&format=xml&include_appinfo=1";
		URL url = null;
		try {
			url = new URL(str);
		} catch (MalformedURLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return url;
	}

	//ユニークなチャンネルIDをリクエストするURL
	public URL setChannelIdURL(){
		String str = "https://www.googleapis.com/youtube/v3/channels?part=id&forUsername="+
				channelID+"&key="+youtubeAPIkey;
		URL url = null;
		try {
			url = new URL(str);
		} catch (MalformedURLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return url;
	}

	//チャンネルIDの登録者情報をリクエストするURL
	public URL setSubscriptionChannelsURL(){
		String str = "https://www.googleapis.com/youtube/v3/subscriptions?part=snippet&maxResults=50&channelId="+
				channelIDnum+"&key="+youtubeAPIkey;
		URL url = null;
		try {
			url = new URL(str);
		} catch (MalformedURLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return url;
	}

	public URL setSubscriptionChannelsNextURL(String pageToken){
		String str = setSubscriptionChannelsURL() + "&pageToken=" + pageToken;
		URL url = null;
		try {
			url = new URL(str);
		} catch (MalformedURLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return url;
	}

	public URL setYouTubeSearchResultURL(String searchTag){
		String str = "https://www.googleapis.com/youtube/v3/search?type=video&part=snippet&maxResults=50&q="+
				searchTag+"&key="+youtubeAPIkey;
		URL url = null;
		try {
			url = new URL(str);
		} catch (MalformedURLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return url;
	}
}
