package pcg;

public class Karta {
	
	int kolor,znak;
	
	public Karta(int k,int z) {
		
		this.kolor=k;
		
		this.znak=z;
		
	}
	public Karta(String K) {
		
		this.kolor=Character.getNumericValue(K.charAt(0));
		if(K.charAt(1)=='2') this.znak=2;
		if(K.charAt(1)=='3') this.znak=3;
		if(K.charAt(1)=='4') this.znak=4;
		if(K.charAt(1)=='5') this.znak=5;
		if(K.charAt(1)=='6') this.znak=6;
		if(K.charAt(1)=='7') this.znak=7;
		if(K.charAt(1)=='8') this.znak=8;
		if(K.charAt(1)=='9') this.znak=9;
		if(K.charAt(1)=='1') this.znak=10;
		if(K.charAt(1)=='J') this.znak=11;
		if(K.charAt(1)=='D') this.znak=12;
		if(K.charAt(1)=='K') this.znak=13;
		if(K.charAt(1)=='A') this.znak=14;
		
	}
	
	public String toString() {
		String K="11";
		
		if(znak==10) K=new String(String.valueOf(kolor)+"10");
		if(znak==11) K=new String(String.valueOf(kolor)+"J");
		if(znak==12) K=new String(String.valueOf(kolor)+"D");
		if(znak==13) K=new String(String.valueOf(kolor)+"K");
		if(znak==14) K=new String(String.valueOf(kolor)+"A");
						
		
		if(znak<10) {
		K=new String(String.valueOf(kolor)+String.valueOf(znak));	
		}
		
		
				
		
		
		return K;
	}
	
	public int GetKolor() {
		return kolor;
	}
	public int GetZnak() {
		return znak;
	}
	
}
