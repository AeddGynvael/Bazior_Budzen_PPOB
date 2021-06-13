package pcg;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Talia {
	 private List<Karta> talia = new ArrayList<Karta>();
	
	public Talia() {
		this.reset();
	}
	
	public void reset() {
		if(talia.size()==52) return ;
		
		talia.clear();
		
		for(int i=0;i<4;i++) {
			for(int j=2;j<15;j++) { 
				talia.add(new Karta(i,j));
			}
		}
	}
	
	public String Daj() {
		Karta k;
		Random r=new Random();
		int x=r.nextInt(talia.size());
		k=talia.get(x);
		talia.remove(k);
		
		return k.toString();
	}
	

	
}
