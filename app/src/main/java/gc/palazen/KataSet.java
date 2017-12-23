package gc.palazen;

import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

public class KataSet {
	
	private int index; //posizione all'interno dell'array
	private int maxIndex;
	
	private String kataName;
	private String tournamentName;
	private int formNum;
	private String judge;
	private String judoka;
	private String[] arrayTechniques;
	private String[] arrayPenalty;
	private String[] arraySet;
	private int[] arrayBorderSet;
	private boolean[] arrayValidated;
	private int fcr;
	private boolean fcrValidated;
	
	
	//costruttore
	KataSet()
	{
		index=0;
		maxIndex=0;
	}
	
	public Spanned print()
	{
		if(tournamentName.equals("not associated"))
			return Html.fromHtml("not associated");
		String out = "";
		out = out + "Tournament: " + tournamentName + "\n";
		out = out + kataName + " ";
		out = out + "[" + formNum + "]\n";
		out = out + "Judge: " + judge + "\n";
		out = out + "Pair: " + judoka;
		return Html.fromHtml("Tournament:" + tournamentName +  "<br />" + 
	            "Kata: " + kataName + " [" + formNum + "]" + "<br />" + 
	            "Judge: " + judge + "<br />" + 
	            "Pair: " + "<b>" + judoka + "</b>");
	}
	public String printold()
	{
		if(tournamentName.equals("not associated"))
			return "not associated";
		String out = "";
		out = out + "Tournament: " + tournamentName + "\n";
		out = out + kataName + " ";
		out = out + "[" + formNum + "]\n";
		out = out + "Judge: " + judge + "\n";
		out = out + "Pair: " + judoka;
		return out;
	}
	
	public void loadTournament(String tn,String kn, String fn, String j, String jud)
	{
		tournamentName=tn;
		kataName=kn;
		if(!"".equals(fn))
			formNum=Integer.parseInt(fn);
		judge=j;
		judoka=jud;
	}
	
	//caricamento Tecniche
	public void loadTechniquesPenaltySetValidates(String[] t, String[] p, String[] s, boolean[] v, boolean fVal, int f)
	{
		arrayTechniques = t;
		maxIndex = arrayTechniques.length;
		arrayPenalty = p;
		arraySet = s;
		arrayValidated = v;
		fcrValidated = fVal;
		fcr = f;
		
		loadArrayBorderSet();
	}
	
	public int getBorderColor(){
		return arrayBorderSet[index];
	}
	
	private void loadArrayBorderSet()
	{
		if(arraySet.length<=0)
			return;
		arrayBorderSet = new int[arraySet.length];
		int current = 0;
		String prevname = "" + arraySet[0];
		
		arrayBorderSet[0]=current;
		for(int i=1; i<arrayBorderSet.length; i++)
		{
			if(!prevname.equals(arraySet[i])){
				if(current == 0)
					current = 1;
				else
					current = 0;
			}
			prevname = arraySet[i];
			
			arrayBorderSet[i]=current;
		}
	}
	
	public int getIndex()
	{
		return index;
	}
	public int getSize()
	{
		return maxIndex;
	}
	
	public String getTournamentName()
	{
		return tournamentName;
	}
	public String getKataName()
	{
		return kataName;
	}
	
	////////////////////////////////////////////////////////////
	public String getCurrentTechniqueName()
	{
		if(index < maxIndex)
			return arrayTechniques[index];
		return null;
	}
	public String getCurrentTechniqueSet()
	{
		if(index < maxIndex)
			return arraySet[index];
		return null;
	}
	public String getCurrentPenalty()
	{
		if(index < maxIndex)
			return arrayPenalty[index];
		return null;
	}
	
	//
	public String getPreviousTechniqueName()
	{
		int i = index - 1;
		if(i < maxIndex && i >= 0)
			return arrayTechniques[i];
		
		return "#";
	}
	public String getPreviousTechniqueSet()
	{
		int i = index - 1;
		if(i < maxIndex && i >= 0)
			return arraySet[i];
		return "";
	}
	
	public String getPreviousPenalty()
	{
		int i = index - 1;
		if(i < maxIndex && i >= 0)
			return arrayPenalty[i];
		return "00000e";
	}
	
	//
	public String getNextTechniqueName()
	{
		int i = index + 1;
		if(i < maxIndex && i >= 0)
			return arrayTechniques[i];
		return "#";
	}
	public String getNextTechniqueSet()
	{
		int i = index + 1;
		if(i < maxIndex && i >= 0)
			return arraySet[i];
		return "";
	}
	
	public String getNextPenalty()
	{
		int i = index + 1;
		if(i < maxIndex && i >= 0)
			return arrayPenalty[i];
		return "00000e";
	}
	////////////////////////////////////////////////////////////
	
	public void setCurrentPenalty(String p)
	{
		arrayPenalty[index] = p;
	}
	
	
	public boolean getIsValidated()
	{
		if(index < maxIndex)
			return arrayValidated[index];
		return false;
	}
	
	public void setIsValidated(boolean v)
	{
		if(index < maxIndex)
			arrayValidated[index] = v;
	}
	

	public boolean movePrevious()
	{
		if(index <= 0)
			return false;
		index--;
		return true;
	}
	public boolean moveNext()
	{
		if(index >= maxIndex-1)
			return false;
		index++;
		return true;
	}
	
	public int getFcr()
	{
		if(fcrValidated)
			return fcr;
		else
		{
			//quello di default
			if(getMaxFcr() == 0)
				return 0;
			else
				return 5;
		}
	}
	
	public void setFcr(int f)
	{
		fcrValidated = true;
		fcr=f;
	}
	
	public int getMaxFcr() {
		String temp;
		int max = 10;
		for(int i=0; i<arrayPenalty.length; i++)
		{
			temp = arrayPenalty[i];
			if(temp!=null && temp.length() == 5)
			{
				if("1".equals(temp.substring(4, 5)))
					return 0;
			}
			if("1".equals(temp.substring(3, 4)))
				max = 5;
		}
		return max;
	}
	
	public double getPoint(int i)
	{
		double total = 10.0;
		String pen = arrayPenalty[i];
		
		if(pen.length() != 6)
			return 0;
		
		int c1 = Integer.parseInt(pen.substring(0,1));
		int c2 = Integer.parseInt(pen.substring(1,2));
		int c3 = Integer.parseInt(pen.substring(2,3));
		int c4 = Integer.parseInt(pen.substring(3,4));
		int c5 = Integer.parseInt(pen.substring(4,5));
		char h = pen.substring(5,6).charAt(0);

		double halved = 0.0;
		switch(h) {
			case 'p': halved = 0.5; break;
			case 'm': halved = 0.5; break;
		}
		
    	if(c1 == 1)
    		total -= 1;
    	if(c2 == 1)
    		total -= 1;
    	if(c3 == 1)
    		total -= 3;
    	if(c4 == 1)
    		total -= 5;

		total += halved;

    	if(c5 == 1)
    		total = 0;
    	if(c1 == 1 && c2 == 1 && c3 == 1 && c4 == 1)
    		total = 1 + halved;
    	
    	return total;
	}
	
	/*public String getStringAllScore()
	{
		String ret = "";
		for(int i=0; i<arrayPenalty.length; i++)
			ret += getPoint(i) + " ";
		return ret;
	}
	*/

	public static String beautyFormat(double d)
	{
		if(d == (long) d)
			return String.format("%d",(long)d);
		else
			return String.format("%s",d);
	}

	public SpannableStringBuilder getStringAllScore() {
        SpannableStringBuilder result = new SpannableStringBuilder("");
        double n;
        for(int i=0; i<arrayPenalty.length; i++)
        {
        	n = getPoint(i);
	        Spannable number = new SpannableString("" + beautyFormat(n));
	        number.setSpan(new ForegroundColorSpan(getColor(n)), 0, number.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        result.append(" ");
	        result.append(number);
        }
        
        return result;
	}
	
	private int getColor(double num) {
		switch((int)num)
		{
		/*case 0: return Color.rgb(0xFF, 0x00, 0x00);
		case 1: return Color.rgb(0xFF, 0x33, 0x00);
		case 2: return Color.rgb(0xFF, 0x66, 0x00);
		case 3: return Color.rgb(0xFF, 0x99, 0x00);
		case 4: return Color.rgb(0xFF, 0xCC, 0x00);
		case 5: return Color.rgb(0xFF, 0xFF, 0x00);
		case 6: return Color.rgb(0xCC, 0xFF, 0x00);
		case 7: return Color.rgb(0x99, 0xFF, 0x00);
		case 8: return Color.rgb(0x66, 0xFF, 0x00);
		case 9: return Color.rgb(0x33, 0xFF, 0x00);
		case 10: return Color.rgb(0x00, 0xFF, 0x00);
		*/
		case 0:
		case 1:
		case 2: return Color.rgb(0xFF, 0x00, 0x00);
		case 3: 
		case 4: 
		case 5: return Color.rgb(0xFF, 0x77, 0x00);
		case 6: 
		case 7: 
		case 8: return Color.rgb(0xFF, 0xFF, 0x00);
		case 9: 
		case 10: return Color.rgb(0x00, 0xFF, 0x00);
		}
		
		return Color.WHITE;
	}
}
