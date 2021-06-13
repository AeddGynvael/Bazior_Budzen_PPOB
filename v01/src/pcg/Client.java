package pcg;

import java.io.*;
import java.net.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Client extends JPanel implements ActionListener{
	
	private final String serverName;
    private final int serverPort;
	Socket socket;
	private OutputStream outt;
	private PrintWriter out;
	private InputStream input;
	private BufferedReader bufferedIn;
	Gracz pl=new Gracz();
	private String namee;
	private int pula,myBet;
	private boolean rozdane,wymiana;
	ImageIcon zdj[] = new ImageIcon[4];
	private JButton pass;
	private JLabel BetWiad;
	int toBet;
	

	private  JFrame frame,frameN;
	private  JPanel panel;
	private  JLabel nPieniadze;
	private  JLabel nPula,Status;
	private JButton[] karta = new JButton[5];
	private JButton Start,wymien;
	private JButton guzik;
	private JFrame BetFrame;
	
	
	private JTextField nazwa;
	private JTextField BET;
	private JButton BetButton;
	public Client(String name,int port) {
		this.serverName = name;
        this.serverPort = port;
        this.pula=0;
	}
	
	

	
	
	 
	public static void main(String[] args) throws IOException{
		try {
			Client client=new Client("localhost",8828);
			client.connect();
			client.wczytuj();
			
			
			client.SetNameGui();
			client.namee="unknown";
			
			
			
		
		
				}
		catch (IOException e) {
			e.printStackTrace();
	 	}
		}
	
		public void connect() throws IOException{
	this.socket=new Socket(serverName,serverPort);
	this.outt=socket.getOutputStream();
	this.out=new PrintWriter(outt,true);
	this.input=socket.getInputStream();
	this.bufferedIn=new BufferedReader(new InputStreamReader(input));
	this.pula=0;
	this.myBet=0;
	this.rozdane=false;
	this.wymiana=false;
	}
	
		public void wczytuj() {
		Thread t = new Thread() {
            @Override
            public void run() {
              wczytuj2();
            }
        };
        t.start();
		}

		private void wczytuj2() {
			 while(true) {
          	   try {
                     String wiad;
                     while ((wiad = bufferedIn.readLine()) != null) {
         				///System.out.println(wiad);
         				if(wiad.equals("q"))break;
         				
         				if(wiad.equals("ENDGAME")) {//koniec gry
         					out.println(wiad);
         					Koniec();
         					
         				}
         				
         				if(wiad.matches("BADNAME (.*)")) {
         					String[] cmd=wiad.split(" ");
         					guzik.setText("NAZWA - "+cmd[1]+" - jest zajeta");
         				}
         					
         				
         				if(wiad.equals("WYMIENIONE")) {
         					out.println(wiad);
         				}
         				
         				if(wiad.matches("name (.*)")) {
         					String[] cmd=wiad.split(" ");
         					namee=cmd[1];
         					frameN.setVisible(false);
         					
         					SetGui();
         				}
         				if(wiad.matches("WIN (.*)")) {
         					String[] cmd=wiad.split(" ");
         					int ilosc = Integer.parseInt(cmd[1]);
         					pl.Zarob(pula/ilosc);
         					nPieniadze.setText("Pieniadze: "+pl.getPieniadze());
         				}
         				if(wiad.equals("STARTGAME")) {
         					out.println(wiad);
         					gameStarted();
         					
         				}
         				if(wiad.equals("SORT")) {
         					SortujKarty();
         					Status.setText("Rozdano karty!");
         					
         					
         				}
         				if(wiad.matches("BETBACK (.*)")) {//nie udalo sie zbetowac
         					String[] cmd=wiad.split(" ");
         					pl.Zarob(Integer.parseInt(cmd[1]));
         					myBet=myBet-Integer.parseInt(cmd[1]);
         					pula=pula-Integer.parseInt(cmd[1]);
         					nPula.setText("Pula: "+pula);
         					nPieniadze.setText("Pieniadze: "+pl.getPieniadze());
         				}
         				
         				if(wiad.matches("MINBET (.*)")) {
         					out.println(wiad);
         				}
         				
         				if(wiad.matches("player (.*) BETTURN (.*)")) {//kolej gracza na bet
         					toBet=0;
         					String[] cmd=wiad.split(" ");
         					if(cmd[1].equals(namee)) {
         						Status.setText("Twoja kolej na bet");
         						
         						if(cmd[3].equals("start")) {
         							toBet=-1;
         							BetGUI(-1);
         						}
         						else if(myBet==Integer.parseInt(cmd[3])) {
         							out.println("ENDBET");//koniec betowania
         							myBet=0;
         						}
         						else{
         							toBet=Integer.parseInt(cmd[3])-myBet;
         							
         							if(pl.canBet(toBet)) {//betowanie
         								BetGUI(toBet);
         							}
         							else {//przegrana
         								out.println("player "+namee+" lost "+cmd[3]);
         								Koniec();
         							}
         							
         						}
         					}
         					Status.setText("Gracz "+cmd[1]+" betuje...");
         				}
         				
         				 if(wiad.matches("player (.*) lost (.*)")) {
         					 out.println(wiad);
         					String[] cmd=wiad.split(" ");
         					 Status.setText("Gracz "+cmd[1]+" przegrywa!");
         				 }
         				
         				if(wiad.matches("player (.*) BET (.*)")) {//ktos obstawil
         					String[] cmd=wiad.split(" ");
         					pula=pula+Integer.parseInt(cmd[3]);
         					out.println(wiad);
         					Status.setText("Gracz "+cmd[1]+" betuje "+cmd[3]);
         					nPula.setText("Pula: "+pula);
         				}
         				if(wiad.equals("ENDB")) {
         					out.println(wiad);
         					myBet=0;
         				}
         				
         				
         				if(wiad.matches("GETCARD (.*) (.*)")) //
         				{
         					String[] cmd=wiad.split(" ");
         					pl.DajKarte(cmd[2], Integer.parseInt(cmd[1]));
         					karta[Integer.parseInt(cmd[1])].setText(pl.talia[Integer.parseInt(cmd[1])].toString());
         					setZdjKarty(Integer.parseInt(cmd[1]));

         				}
         				
         				if(wiad.matches("player (.*) GETSCORE")) {
         					out.println(pl.GetScore());
         				}
         				
         				if(wiad.matches("player (.*) SWAPSTART")) { //Rozpoczyna wymieniac karty
         					out.println(wiad);
         					String[] cmd=wiad.split(" ");
         					if(cmd[1].equals(namee)) {
         						StartWymiana();
         					}
         					else {
         						Status.setText("Gracz "+cmd[1]+" Wymienia Karty");
         					}
         				}
         				
         				if(wiad.matches("player (.*) SWAPSTOP")) {
         					out.println(wiad);
         					String[] cmd=wiad.split(" ");
         					if(cmd[1].equals(namee)) {
         						StopWymiana();
         						out.println("PLAYERSCORE "+pl.GetScore());
         					}
         					else {
         					
         					}
         				}
         				
         				if(wiad.matches("CHECKWIN (.*) [0-9]")) {
         					String[] cmd=wiad.split(" ");
         					if(Double.parseDouble(cmd[1])==pl.GetScore()){
         						pl.Zarob(pula/Integer.parseInt(cmd[2]));
         					}
         				}
         				

                     }
                 } catch (Exception ex) {
                     ex.printStackTrace();
                     try {
                         socket.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
		}
		


		
		private void SetNameGui() {
			frameN = new JFrame();
			frameN.setSize(400,200);
			frameN.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			
			JPanel panelN=new JPanel();
			frameN.add(panelN);
			
			panelN.setLayout(null);
			
			
			JLabel tekst=new JLabel("Podaj nazwe gracza ");
			tekst.setBounds(10,20,150,25);
			
			panelN.add(tekst);
			
			nazwa=new JTextField(20);
			nazwa.setBounds(150, 20, 80, 25);
			
			guzik = new JButton("Zatwierdz");
			guzik.setBounds(10, 50, 300, 50);
			guzik.addActionListener(this);
			panelN.add(nazwa);
			panelN.add(guzik);
			frameN.setVisible(true);
		}
		
		private void SetGui() {
			
			
				zdj[0]= new ImageIcon(getClass().getResource("4.png"));
				zdj[1]= new ImageIcon(getClass().getResource("1.png"));
				zdj[2]= new ImageIcon(getClass().getResource("2.png"));
				zdj[3]= new ImageIcon(getClass().getResource("3.png"));
				
				for(int i=0;i<4;i++) {
					Image im=zdj[i].getImage();
					Image img=im.getScaledInstance( 10, 10,  java.awt.Image.SCALE_SMOOTH ) ;  
					zdj[i]= new ImageIcon(img);
				}
			frame = new JFrame(namee);
			frame.setSize(500,500);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			panel=new JPanel();
			frame.add(panel);
			
			panel.setLayout(null);
			
			JLabel nazwa=new JLabel("NAZWA : "+namee);
			nazwa.setBounds(10,0,100,20);
			panel.add(nazwa);
			nPieniadze=new JLabel("Pieniadze: "+pl.getPieniadze());
			nPieniadze.setBounds(10,20,800,25);
			nPula=new JLabel("Pula: "+pula);
			nPula.setBounds(10,50,80,25);
			panel.add(nPieniadze);
			panel.add(nPula);
			
			karta[0] = new JButton();
			karta[1] = new JButton();
			karta[2] = new JButton();
			karta[3] = new JButton();
			karta[4] = new JButton();
			for(int i=0;i<5;i++) {
				karta[i].setBounds(65+75*i,400,75,75);
				karta[i].addActionListener(this);
				panel.add(karta[i]);
			}
			Start=new JButton("START");
			Start.setBounds(300,50,100,100);
			Start.addActionListener(this);
			panel.add(Start);
			
			Status=new JLabel("Nie rozpoczeto gry");
			Status.setBounds(200,0,300,25);
			panel.add(Status);
			
			wymien = new JButton("koniec wymiany");
			wymien.setBounds(300,250,100,100);
			wymien.addActionListener(this);
			panel.add(wymien);
			
			//wymien.setVisible(false);
			frame.setVisible(true);
			
			
		}
		
		
		private void BetGUI(int toBet) {
			BetFrame=new JFrame(namee);
			BetFrame.setSize(400,200);
			
			JPanel BetPanel=new JPanel();
			BetPanel.setLayout(null);
			BetFrame.add(BetPanel);
			
			BetWiad=new JLabel("Aby uczestniczyc w grze musisz postawic min. "+toBet);
			if(toBet==-1) {
				BetWiad.setText("Ustalasz kwote poczatkowa jaka nalezy postawic");
			}
			
			BetWiad.setBounds(10,20,300,25);
			BetPanel.add(BetWiad);
			
			
			BET = new JTextField(20);
			BET.setBounds(10,50,50,25);
			BetPanel.add(BET);
			
			BetButton = new JButton("Zatwierdz");
			BetButton.setBounds(70, 50, 100, 50);
			BetButton.addActionListener(this);
			BetPanel.add(BetButton);
			
			pass=new JButton("Poddaj");
			pass.setBounds(30, 100, 150, 50);
			pass.addActionListener(this);
			BetPanel.add(pass);
			
			BetFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			
			BetFrame.setVisible(true);
			
		}
		
		
		
		
		@Override
	 	public void actionPerformed(ActionEvent ev) {
	 		Object source=ev.getSource();
	 		if(source==karta[0]&&wymiana) {
	 			out.println(0);
	 		}
	 		if(source==karta[1]&&wymiana) {
	 			out.println(1);
	 		}
	 		if(source==karta[2]&&wymiana) {
	 			out.println(2);
	 		}
	 		if(source==karta[3]&&wymiana) {
	 			out.println(3);
	 		}
	 		if(source==karta[4]&&wymiana) {
	 			out.println(4);
	 		}
	 		if(source==Start) {
	 			out.println("STARTGAME "+namee);
	 		}
	 		if(source==wymien) {
	 			out.println("SWAPSTOP");
	 		}
	 		if(source==guzik) {
	 			out.println("name "+nazwa.getText());
	 		}
	 		if(source==BetButton) {
	 			Bet(Integer.parseInt(BET.getText()));
	 			BetFrame.setVisible(false);
	 		}
	 		if(source==pass) {
	 			if(toBet==-1) out.println("player "+namee+" lost start");
	 			else out.println("player "+namee+" lost "+(toBet+myBet));
	 			BetFrame.setVisible(false);
	 		}
	 		
	 	}
		
		
		public void Bet(int x) {
		if(pl.canBet(x)) {
			out.println("player "+namee+" BET "+x);
			myBet=myBet+x;
			pula=pula+x;
			pl.makeBet(x);
			nPieniadze.setText("Pieniadze: "+pl.getPieniadze());
			nPula.setText("Pula : "+pula);
		}
		}

		public void gameStarted() {
			pula=0;
			myBet=0;
			rozdane=false;
			wymiana=false;
		}
		public void SortujKarty() {
			pl.Sortuj();
			for(int i=0;i<5;i++) {
				setZdjKarty(i);
			}
		}
		
		public void  setZdjKarty(int n) {
			int x=pl.talia[n].GetKolor();
			karta[n].setIcon(zdj[x]);
			String s=pl.talia[n].toString();
			
			karta[n].setText(s.substring(1));
		}
		
		public void StartGame() {
			Koniec();
			out.println("STARTGAME "+namee);
		}
		
		public void SetName(String n) {
			out.println("name "+n);
		}
		
		public void StartWymiana() {
			wymiana=true;
			Status.setText("Wymieniasz karty");
		}
		public void StopWymiana() {
			wymiana=false;
			SortujKarty();
		}
	
		public void Koniec() {
			pula=0;
			for(int i=0;i<5;i++) {
				karta[i].setText(" ");
			}
			rozdane=false;
			wymiana=false;
			myBet=0;
			Status.setText("Koniec gry");
			nPula.setText("Pula: "+pula);
					
		}
		

		
		

}
	
	




