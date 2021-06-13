package pcg;


public class Gracz {
	Karta[] talia=new Karta[5];
	double Score;
	int Pieniadze;
	
	public Gracz() {
		this.Score=0;
		this.Pieniadze=1000;
	}
	
	
	
	public boolean canBet(int x) {
		if(this.Pieniadze<x) return false;
	
		return true;
	}
	
	public void makeBet(int x) {
		this.Pieniadze=this.Pieniadze-x;
	}
	
	public void Zarob(int x) {
		this.Pieniadze=this.Pieniadze+x;
	}
	
	public int getPieniadze() {
		return Pieniadze;
	}
	
	
	
	
	
	public void DajKarte(String K,int pos) {
		talia[pos]=new Karta(K);
	}
	
	
	public void wypisz() {
		for(int j=0;j<5;j++) {
			System.out.println(j+" : "+talia[j].toString());
		}
	}
	
	
	public double GetScore() {
		Sprawdz();
		
		return Score;
	}
	
	public void Sortuj() {
		for(int i=0;i<5;i++) {
			for(int j=0;j<4;j++) {
				if(talia[j].GetZnak()<talia[j+1].GetZnak()) {
					Karta tmp=talia[j];
					talia[j]=talia[j+1];
					talia[j+1]=tmp;
				}
			}
		}
	}
	
	
	
	
	
	//////////////////////////////////
	
	public void Sprawdz() {
		if(SprawdzPoker()==0){
			if(SprawdzCztery()==0) {
				if(SprawdzFull()==0) {
					if(SprawdzKolor()==0) {
						if(SprawdzSchodki()==0) {
							if(SprawdzTrzy()==0) {
								if(Sprawdz2Pary()==0) {
									if(SprawdzPare()==0) {
										Score=talia[0].GetZnak()*0.01;
										
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	
	double SprawdzPoker() {
		if(SprawdzSchodki()!=0 && SprawdzKolor()!=0) {
			Score=40+SprawdzSchodki();
			return Score;
		}
		Score=0;
		return 0;
	}
	
	
	double SprawdzCztery() {
		for(int i=0;i<2;i++) {
			if(talia[i].GetZnak()==talia[i+1].GetZnak() &&talia[i].GetZnak()==talia[i+2].GetZnak() &&talia[i].GetZnak()==talia[i+3].GetZnak() )	{
				Score = 40+0.01*talia[i].GetZnak();
				return Score;
			}
		}
		return 0;
	}
	
	
	double SprawdzFull() {
		if(talia[0].GetZnak()==talia[1].GetZnak()&&talia[0].GetZnak()==talia[2].GetZnak() && talia[3].GetZnak()==talia[4].GetZnak()) {
			Score=32+talia[0].GetZnak()*0.01+talia[3].GetZnak()*0.0001;
					return Score;
		}
		return 0;
	}
	
	
	double SprawdzKolor() {
		
		for(int i=1;i<5;i++) {
		if(talia[0].GetKolor()!=talia[i].GetKolor()) {
			return 0;
		}
		}
		Score=20;
		return Score;
	}
	
	
	double SprawdzSchodki() {
		int licz=0;
		int Najw=0;
		
		for(int i=0;i<4;i++) {
			if(talia[i].GetZnak()-talia[i+1].GetZnak()>1) licz=0; 
			if(talia[i].GetZnak()-talia[i+1].GetZnak()==1) {
				if(licz==0) Najw=talia[i].GetZnak();
				licz++;
				
			}
		}
		if(talia[4].GetZnak()==2 && talia[0].GetZnak()==14) licz++;
		
		if (licz==4) {
			Score=10+Najw*0.01;
			return Score;
		}
		return 0;
	}
	
	
	double SprawdzTrzy() {
		for(int i=0;i<3;i++) {
			if(talia[i].GetZnak()==talia[i+1].GetZnak() &&talia[i].GetZnak()==talia[i+2].GetZnak())	{
				Score = 5+0.01*talia[i].GetZnak();
				return 5+0.01*talia[i].GetZnak();
			}
	}
		return 0;
	}
	
	
	double Sprawdz2Pary() {
		for(int i=0;i<2;i++) {
			if(talia[i].GetZnak()==talia[i+1].GetZnak()) {
				Score= 0.01*talia[i].GetZnak();
			}
		}
		if(Score==0) return 0;
		for(int i =2;i<4;i++) {
			if(talia[i].GetZnak()==talia[i+1].GetZnak()) {
				Score=Score + 4+ 0.0001*talia[i].GetZnak();
				return Score;
			}
		}
		return 0;
	}
	
	
	double SprawdzPare() {
		for(int i=0;i<4;i++) {
			if(talia[i].GetZnak()==talia[i+1].GetZnak()) {
				Score= 2+ 0.01*talia[i].GetZnak();
				return Score;
			}
	}
		return 0;
	}
		
	
	
	
}
