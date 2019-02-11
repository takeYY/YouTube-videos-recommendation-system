import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Request {
	private URL url;

	Request(URL url){
		this.url = url;
	}

	public String getSteamNumID(){
		String steamid = "";
		try {
			BufferedReader fin = setURLReader();
			String line = fin.readLine();
			line = fin.readLine();
			while ((line = fin.readLine()) != null){
				String[] str1 = line.split("<");
				for(int i=0; i<str1.length; i++){
					String[] str2 = str1[i].split(">");
					for(int j=0; j<str2.length; j++){
						if(str2[j].equals("steamid")){
							steamid = str2[j+1];
						}
					}
				}
			}
			return steamid;
		} catch (IOException e) {
			System.err.println("I/O Error: " + e.toString());
			return steamid;
		}
	}

	public HashMap<String,Integer> getRecentlyPlayedGamesMap(){
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		String title = "";
		Integer playTime = 0;
		try{
			BufferedReader fin = setURLReader();
			String line = fin.readLine();
			line = fin.readLine();
			while( (line=fin.readLine()) != null ){
				String[] str1 = line.split("<");
				for(int i=0; i<str1.length; i++){
					String[] str2 = str1[i].split(">");
					for(int j=0; j<str2.length; j++){
						if(str2[j].equals("name")){
							title = specialString(str2[j+1]);
						}else if(str2[j].equals("playtime_2weeks")){
							playTime = Integer.parseInt(str2[j+1]);
						}
					}
					if(!title.equals("") && playTime/60!=0){
						map.put(title, playTime);
						title = "";
						playTime = 0;
					}
				}
			}
			return map;
		} catch(IOException e){
			System.out.println("I/O Error"+e.toString());
			return map;
		}
	}

	public String getChannelID(){
		String channelID = "";
		try{
			BufferedReader fin = setURLReader();
			String line = fin.readLine();
			line = fin.readLine();
			while( (line=fin.readLine())!=null ){
				String[] str1 = line.split("\"");
				for(int i=0; i<str1.length; i++){
					if(str1[i].equals("id")){
						channelID = str1[i+2];
					}
				}
			}
			return channelID;
		}catch(IOException e){
			System.out.println("I/O Error"+e.toString());
			return channelID;
		}
	}

	public Set<String> getChannelIDset(URL url,String channelIDnum){
		Set<String> channelSet = new HashSet<String>();
		String channelID = "";
		boolean hasNextToken = false;
		String pageToken = "";
		try{
			BufferedReader fin = setURLReader();
			String line = fin.readLine();
			while( (line=fin.readLine()) != null ){
				String[] str1 = line.split(":\"");
				for(int i=0; i<str1.length; i++){
					if(str1[i].contains("channelId")){
						String[] str2 = str1[i].split(":");
						for(int k=0; k<str2[1].length(); k++){
							if(trueURL(str2[1].charAt(k))){
								channelID += str2[1].charAt(k);
							}
						}
					}else if(str1[i].contains("nextPageToken")){
						hasNextToken = true;
						String[] str2 = str1[i].split(":");
						for(int k=0; k<str2[1].length(); k++){
							if(trueURL(str2[1].charAt(k))){
								pageToken += str2[1].charAt(k);
							}
						}
					}
				}
				if(!channelID.equals("")){
					channelID = "https://www.youtube.com/channel/" + channelID;
					channelSet.add(channelID);
					channelID = "";
				}
			}
			if(hasNextToken){
				Controller con = new Controller();
				con.channelIDnum = channelIDnum;
				this.url = con.setSubscriptionChannelsNextURL(pageToken);
				channelSet.addAll(this.getChannelIDset(url,channelIDnum));
			}
			return channelSet;
		}catch(IOException e){
			System.out.println("I/O Error"+e.toString());
			return channelSet;
		}
	}

	public ArrayList<Video> getVideosInfo(Set<String> channelSet){
		String title = "";
		String videoID = "";
		String channelTitle = "";
		String channelID = "";
		ArrayList<Video>videosList = new ArrayList<Video>();
		try{
			BufferedReader fin = setURLReader();
			String line = fin.readLine();
			while( (line=fin.readLine()) != null ){
				String[] str1;
				if(line.contains("title") || line.contains("channelTitle")){
					str1 = line.split("\":");
				}else{
					str1 = line.split(":");
				}
				for(int i=0; i<str1.length; i++){
					if(str1[i].contains("title") && i+1!=str1.length){
						title = str1[i+1].substring(2,str1[i+1].length()-2);
					}else if(str1[i].contains("videoId")){
						for(int k=0; k<str1[i+1].length(); k++){
							if(trueURL(str1[i+1].charAt(k))){
								videoID += str1[i+1].charAt(k);
							}
						}
					}else if(str1[i].contains("channelId")){
						for(int k=0; k<str1[i+1].length(); k++){
							if(trueURL(str1[i+1].charAt(k))){
								channelID += str1[i+1].charAt(k);
							}
						}
					}else if(str1[i].contains("channelTitle")){
						channelTitle = str1[i+1].substring(2, str1[i+1].length()-2);
					}
				}
				if(!title.equals("") && !videoID.equals("") && !channelTitle.equals("")){
					videoID = "https://www.youtube.com/watch?v=" + videoID;
					channelID = "https://www.youtube.com/channel/" + channelID;
					if(!channelSet.contains(channelID)){
						videosList.add(new Video(title,videoID,channelTitle,channelID));
					}
					title = new String("");
					videoID = new String("");
					channelTitle = new String("");
					channelID = new String("");
				}
			}
		}catch(IOException e){

		}
		return videosList;
	}

	private BufferedReader setURLReader(){
		BufferedReader fin = null;
		try{
			URLConnection urlConnection = url.openConnection();
			urlConnection.connect();
			fin = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"utf-8"));
			return fin;
		} catch(IOException e){
			System.out.println("I/O Error" + e.toString());
			return fin;
		}
	}

	//特殊文字を正しい文字に変換する
	//今のところ[']と[&]のみ
	private String specialString(String name){
		String newName = name;
		if(name.contains("&apos;")){
			String[] str = name.split("&apos;");
			newName = str[0] + "'" +str[1];
		}else if(name.contains("&amp;")){
			String[] str = name.split("&amp;");
			newName = str[0] + "&" + str[1];
		}
		return newName;
	}

	private boolean trueURL(char ch){
		String str = "" + ch;
		try{
			if(str.matches("[0-9]") ||
					str.matches("[a-z]") ||
					str.matches("[A-Z]") ||
					str.matches("-") ||
					str.matches("_")){
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			return false;
		}
	}
}
