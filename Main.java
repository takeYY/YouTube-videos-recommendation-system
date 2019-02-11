import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Scanner;


public class Main {
	//動画を最大で何本表示させたいか
	private static int videoNum;

	public static void main(String[] args){
		Controller con = new Controller();
		//SteamIDの値からユニークなsteamidを取得する
		while(con.steamID.equals("")){
			System.out.println("SteamIDを入力してね！");
			Scanner sc = new Scanner(System.in);
			con.steamID = sc.nextLine();
			Request req = new Request(con.setSteamIDnumURL());
			con.steamIDnum = req.getSteamNumID();
			line();
			if(con.steamIDnum.equals("")){
				System.out.println("steamIDが違うよ！");
				line();
				con.steamID = "";
			}
		}
		System.out.println("steamIDnumは"+con.steamIDnum+"だよ");
		line();
		//チャンネル名からユニークなchannelidを取得する
		while(con.channelID.equals("")){
			System.out.println("YouTubeのチャンネル名を入力してね！");
			Scanner sc = new Scanner(System.in);
			con.channelID = sc.nextLine();
			Request req = new Request(con.setChannelIdURL());
			con.channelIDnum = req.getChannelID();
			line();
			if(con.channelIDnum.equals("")){
				System.out.println("チャンネル名が違うよ！");
				line();
				con.channelID = "";
			}
		}
		System.out.println("channelIDnumは"+con.channelIDnum+"だよ");
		line();
		System.out.println("動画を何本表示する？");
		Scanner sc = new Scanner(System.in);
		videoNum = sc.nextInt();
		System.out.println("動画を最大で"+videoNum+"本表示するよ！");
		line();
		//最近プレイしたゲーム情報を取得する
		Request req = new Request(con.setRecentlyPlayedGamesURL());
		con.gameMap = req.getRecentlyPlayedGamesMap();
		req = new Request(con.setSubscriptionChannelsURL());
		con.channelSet = req.getChannelIDset(con.setSubscriptionChannelsURL(),con.channelIDnum);
		System.out.println(con.channelID+"は"+con.channelSet.size()+"件チャンネルを登録しているよ！");
		line();
		int max = 0;
		for(String key : con.gameMap.keySet()){
			max += con.gameMap.get(key);
		}
		HashMap<String,Integer>searchTagList = new HashMap<String,Integer>();
		for(String key : con.gameMap.keySet()){
			String[] searchArray = key.split(" ");
			String searchTag = "";
			for(String str : searchArray){
				searchTag += str + ",";
			}
			double time = con.gameMap.get(key);
			double d = (time/max);
			BigDecimal bd = new BigDecimal(String.valueOf(d));
			BigDecimal bd2 = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
			int n = (int)(bd2.doubleValue() * videoNum);
			System.out.println(key+"はここ2週間で"+con.gameMap.get(key)/60+"時間プレイしているよ！");
			line();
			System.out.println(key+"("+n+"本表示)で検索したいキーワードを入力してね！"+
										"複数ある場合は[,]で区切ってね！"+
										"\nex)pro,ゆっくり実況,ネタばれ,etc");
			sc = new Scanner(System.in);
			if(sc.hasNext()){
				searchTag += sc.nextLine();
				System.out.println(searchTag+"で後ほど検索されるよ！\n");
				searchTagList.put(searchTag,n);
			}
			else{
				sc.close();
			}
			line();
		}
		line2();
		int num = 1;
		for(String searchTag : searchTagList.keySet()){
			req = new Request(con.setYouTubeSearchResultURL(searchTag));
			con.videoList = req.getVideosInfo(con.channelSet);
			for(int i=0; i<searchTagList.get(searchTag); i++){
				System.out.println(num+"本目の動画");
				System.out.println("タイトル:\n\t"+con.videoList.get(i).title+
									"\n動画のURL:\n\t"+con.videoList.get(i).videoID+
									"\nチャンネル名:\n\t"+con.videoList.get(i).channelTitle+
									"\nチャンネルURL:\n\t"+con.videoList.get(i).channelID);
				line2();
				num ++;
			}
			con.videoList.clear();
		}
	}

	private static void line(){
		System.out.println("---------------------------------------------"+
							"---------------------------------------------"+
							"---------------------------------------------");
	}
	private static void line2(){
		System.out.println("============================================="+
							"============================================="+
							"=============================================");
	}
}
