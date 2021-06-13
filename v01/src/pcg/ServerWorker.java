package pcg;



import java.io.*;
import java.net.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;



public class ServerWorker extends Thread{

	
	private final Server server;
	private final Socket client;
	private OutputStream outt;
	private String namee;
	private PrintWriter out;
	private ArrayList<ServerWorker> InGame=new ArrayList<ServerWorker>();
	private int currBet;
	private int totalBet;
	private int wymiany;
	private boolean wymienione;
	
	private double PlayerScore;
	
	public ServerWorker(Server s,Socket socket) {
		this.server=s;
		this.client=socket;
	}
	
	@Override 
	public void run() {
		 try {
			 ObslugaKlienta();
		 
	 }
	 catch(IOException e) {
		 e.printStackTrace();
	 }
	   catch (InterruptedException e) {
          e.printStackTrace();
      }
		
	}
	
	
	 private void ObslugaKlienta() throws IOException, InterruptedException {
		 InputStream input = client.getInputStream();
		 outt=client.getOutputStream();
		 out = new PrintWriter(outt,true);
		 
		 BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		 String msg;
		
		 
		 
		 
		 
		 while(true) {
			 msg=reader.readLine();
			
			 ///System.out.println("MSG "+namee+"= "+msg);
			 
			 if(InGame.size()==1 && server.inGame()) {
				 KoniecGry(InGame);
			 }
			 
			 if(msg.matches("name (.*)")) {
				 String[] cmd=msg.split(" ");
				 
				 boolean jest=false;
				 List<ServerWorker> list = server.getConnectedList();

				 for(ServerWorker x : list) {
					 if(x.getNamee().equals(cmd[1])) jest=true;
				 }
				 if(jest) {
					 send("BADNAME "+cmd[1]);
				 }
				 else {
					 setNamee(cmd[1]);
				 }				 
			 }
				 if(msg.equals("STARTGAME")&&InGame.isEmpty()) {// po rozpoczeciu gry nie przez tego greacza
				 currBet=0;
				 totalBet=0;
				 wymienione=false;
				 PlayerScore=0;
				 wymiany=0;
				 InGame.clear();

				 List<ServerWorker> list = server.getConnectedList();
					 for(ServerWorker x: list) {
						 InGame.add(x);
					 }
					 
			 }
			 
			 if(msg.equals("STARTGAME "+namee) && !server.inGame()) {//zaczyna gre
				 currBet=0;
				 totalBet=0;
				 wymienione=false;
				 InGame.clear();
				 wymiany=0;
				 SendAll("STARTGAME");
				 server.inGameSet(true);

				 List<ServerWorker> list = server.getConnectedList();
				 for(ServerWorker x: list) {
					 InGame.add(x);
				 }
				 
				 Start(InGame);
				 
				 SendAll("player "+FindNext(InGame)+" BETTURN start");
				 
			 }
			 
			 
			 if(msg.equals("ENDGAME")) {
				 currBet=0;
				 totalBet=0;
				 wymienione=false;
				 wymiany=0;
				 InGame.clear();
				 PlayerScore=0;
				 if(server.inGame()) server.inGameSet(false);
			 }
			 

			 if(msg.matches("PLAYERSCORE (.*)")) {
				 String[] cmd = msg.split(" ");
				 PlayerScore=Double.parseDouble(cmd[1]);
			 }
		
			 
			
			 if(msg.matches("player (.*) lost (.*)") && !InGame.isEmpty()) {//jak ktos odpadl
				 String[] cmd=msg.split(" ");
				 if(cmd[1].equals(namee)) {
					 SendAllNotMe(msg);
					 SendAll("player "+FindNext(InGame)+" BETTURN "+cmd[3]);
					 InGame.clear();				
				 }
				 else {
						 if(InGame.size()>1) {
							 for(ServerWorker x: InGame) {
								 if(x.getNamee().equals(cmd[1])) {
									 InGame.remove(x);
									 break;
								 }
							 }

						 }
				 }
			 }
			 
			 if(msg.matches("player (.*) BET (.*)")) {//jezeli ktos da bet
				 String[] cmd=msg.split(" ");


				 if(cmd[1].equals(namee)) {
					 if(totalBet+Integer.parseInt(cmd[3])>=currBet) {
						 totalBet=totalBet+Integer.parseInt(cmd[3]);

					 currBet=totalBet;
					 SendAll("MINBET "+currBet);
					 SendAllNotMe(msg);
		
					 SendAll("player "+FindNext(InGame)+" BETTURN "+currBet);
					 }
					 else {
						send("BETBACK "+cmd[3]);
						send("player "+namee+" BETTURN "+currBet);
					 }

				 }
			 }
			 
			 if(msg.equals("pass")) {
				 send("player "+namee+" lost "+currBet);
			 }
			 
			 if(msg.matches("MINBET (.*)")) {//zmienia kwote minimalna 
				 String[] cmd=msg.split(" ");
				 currBet=Integer.parseInt(cmd[1]);
			 }
			 
			 if(msg.equals("ENDBET") && server.inGame()) { //koniec betowania
				SendAllNotMe("ENDB");
				currBet=0;
				totalBet=0;
				 if(wymienione) {
					 KoniecGry(InGame);
				 }
				 else {
					 
					 SendAll("player "+namee+" SWAPSTART");
				 }
			 }
			 
			 if(msg.equals("ENDB")) { //koniec betowania
				currBet=0;
				totalBet=0;
			 }
			 
			 if(msg.matches("player (.*) SWAPSTART")) {
				 String[] cmd=msg.split(" ");
				 if(cmd[1].equals(namee)) {
					 wymien();
				 }
			 }
			 
			 if(msg.matches("player (.*) SWAPSTOP")) {
				 wymiany++;
			 }
			 
			 if(msg.equals("WYMIENIONE")) {
				 wymienione=true;
			 }
			
			 
			 if(msg.equals("q")) {
				 server.disconnect(this);
				 send("q");
				 break;
			 }
		 }
		 
		 //client.close();
	 }
	 private void setNamee(String n) throws IOException, InterruptedException {
		 this.namee=n;
		 send("name "+namee);
	 }
	 
	 public double getPlayerScore() {
		 return PlayerScore;
	 }
	 
	 public String getNamee() throws IOException{
		 if(namee==null) return " ";
		 return namee;
	 }
	 
	 private void SendAll(String msg) throws IOException{
		 List<ServerWorker> conn = server.getConnectedList();
		 
		 for(ServerWorker x : conn) 
			 x.send(msg);
	 }
	 private void SendAllNotMe(String msg) throws IOException{
		 List<ServerWorker> conn = server.getConnectedList();
		 
		 for(ServerWorker x : conn) {
			 if(!x.equals(this))
			 x.send(msg);
			 
		 }
	 }
	 
	 public void send(String msg) throws IOException {

	            out.println(msg);
	        
	    }
	 public String FindNext(ArrayList<ServerWorker> list) throws IOException{
		  boolean next=false;
		
		  while(true) {
			
		 for(ServerWorker x : list) {
			
			 if(next==true) return x.getNamee();
			 if(x.getNamee().equals(namee)) {
				 next=true;
			 }
		 }
		 }
		 
	 }
	 public void Start(ArrayList<ServerWorker> list) throws IOException{
		for(ServerWorker x : list) {
			 for(int i=0;i<5;i++) {
				 x.send("GETCARD "+i+" "+server.getCard());
			 }
			 x.send("SORT");
		}
		
	 }
	 
	 public void wymien() throws IOException {//wymiana kart
		
		 InputStream input = client.getInputStream();
		 outt=client.getOutputStream();
		 out = new PrintWriter(outt,true);
		 System.out.println("Gracz "+namee+" wymienia karty");
		 BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		 String msg="k";
		 int a;
		 boolean[] wym=new boolean[5];
		 for(int i=0;i<5;i++) wym[i]=true;
		 
		 while(!msg.equals("SWAPSTOP")) {
			 
			 msg=reader.readLine();
			 if(msg.equals("SWAPSTOP")) break;
			 a=Integer.parseInt(msg);
			 if(wym[a]) {
				 wym[a]=false;
				 send("GETCARD "+a+" "+server.getCard());
			 }
			 for(int i=0;i<5;i++) {
				 if(wym[i]) {
					break; 
				 }
				 if(i==4&&!wym[i]) {
					 msg="SWAPSTOP";
				 }
			 }
		 }
		 
		 send("player "+namee+" GETSCORE");
		 
		 SendAll("player "+namee+" SWAPSTOP");
		 
		 if(wymiany==InGame.size()-1) {
			 SendAll("WYMIENIONE");
			 SendAll("player "+FindNext(InGame)+" BETTURN start");
		 }
		 else {
			 SendAll("player "+FindNext(InGame)+" SWAPSTART");
		 }
	 }
	 
	 public void KoniecGry(ArrayList<ServerWorker> list)throws IOException{
		 if(list.size()==1) {
			 InGame.clear();
			 send("WIN 1");
			 wymienione=false;
			 wymiany=0;
			 SendAll("ENDGAME");
			 server.inGameSet(false);
			 
		 }
		 double max=0;
		 int ilosc=1;
		 for(ServerWorker x : list ) {
			 if(x.getPlayerScore()==max) ilosc++;
			 
			 if(x.getPlayerScore()>max) {
				 max=x.getPlayerScore();
				 ilosc=1;
			 }
			 
		 }
		 for(ServerWorker x : list) {
			 if(x.getPlayerScore()==max) {
				x.send("WIN "+ilosc);
			 }
		 }
		 
		 SendAll("ENDGAME");
	 }
	 
}
