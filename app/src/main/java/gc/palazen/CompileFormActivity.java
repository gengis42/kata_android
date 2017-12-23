package gc.palazen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CompileFormActivity extends Activity {
	
	private static KataSet kata;
	private static MysqlCommunicator myCom;
	
	private CheckImage p1p, p2p, p3p, p4p, p5p;
	private CheckImage p1, p2, p3, p4, p5;
	private CheckImage p1n, p2n, p3n, p4n, p5n;

	private HalvedRadio php, rPlus, rEqual, rMinus, phn;
	
	private Button btnPrevious, btnNext, btnValidate;
	
	private TextView tvTotalP, tvTotal, tvTotalN;
	
	private TextView tvKataNameP, tvKataName, tvKataNameN;
	private TextView tvSetNameP, tvSetName, tvSetNameN;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form);
        
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        
        btnPrevious = (Button)findViewById(R.id.btnPrevious);
        btnNext = (Button)findViewById(R.id.btnNext);
        btnValidate = (Button)findViewById(R.id.btnValidate);
        
        tvTotalP = (TextView)findViewById(R.id.tvTotalP);
        tvTotal = (TextView)findViewById(R.id.tvTotal);
        tvTotalN = (TextView)findViewById(R.id.tvTotalN);
        
        tvKataNameP = (TextView)findViewById(R.id.tvKataNameP);
        tvKataName = (TextView)findViewById(R.id.tvKataName);
        tvKataNameN = (TextView)findViewById(R.id.tvKataNameN);
        
        tvSetNameP = (TextView)findViewById(R.id.tvSetNameP);
        tvSetName = (TextView)findViewById(R.id.tvSetName);
        tvSetNameN = (TextView)findViewById(R.id.tvSetNameN);
        
        p1p = (CheckImage)findViewById(R.id.p1P);
    	p2p = (CheckImage)findViewById(R.id.p2P);
    	p3p = (CheckImage)findViewById(R.id.p3P);
        p4p = (CheckImage)findViewById(R.id.p4P);
        p5p = (CheckImage)findViewById(R.id.p5P);
        
        p1 = (CheckImage)findViewById(R.id.p1);
    	p2 = (CheckImage)findViewById(R.id.p2);
    	p3 = (CheckImage)findViewById(R.id.p3);
        p4 = (CheckImage)findViewById(R.id.p4);
        p5 = (CheckImage)findViewById(R.id.p5);
		php = (HalvedRadio) findViewById(R.id.phP);

		rPlus = (HalvedRadio)findViewById(R.id.radio_plus);
		rEqual = (HalvedRadio)findViewById(R.id.radio_equal);
		rMinus = (HalvedRadio)findViewById(R.id.radio_minus);

		rPlus.setType(HalvedRadio.IS_PLUS);
		rPlus.setChecked(false);

		rMinus.setType(HalvedRadio.IS_MINUS);
		rMinus.setChecked(false);

		rEqual.setType(HalvedRadio.IS_EQUAL);
		rEqual.setChecked(true);
        
        p1n = (CheckImage)findViewById(R.id.p1N);
    	p2n = (CheckImage)findViewById(R.id.p2N);
    	p3n = (CheckImage)findViewById(R.id.p3N);
        p4n = (CheckImage)findViewById(R.id.p4N);
        p5n = (CheckImage)findViewById(R.id.p5N);
		phn = (HalvedRadio)findViewById(R.id.phN);
        
        kata = KataActivity.kata;
        myCom = KataActivity.myCom;

        p1p.setIsBlur(true);
        p2p.setIsBlur(true);
        p3p.setIsBlur(true);
        p4p.setIsBlur(true);
        p5p.setIsBlur(true);
		php.setIsBlur(true);
        
        p1.setPosition(1);
        p2.setPosition(2);
        p3.setPosition(3);
        p4.setPosition(4);
        p5.setPosition(5);
        
        p1n.setIsBlur(true);
        p2n.setIsBlur(true);
        p3n.setIsBlur(true);
        p4n.setIsBlur(true);
        p5n.setIsBlur(true);
		phn.setIsBlur(true);
        
        compileForm();
        
        OnClickListener changeValueDisablePreviousAndNextPlusUpdateTotalScore = new View.OnClickListener() {
			public void onClick(View v) {
				
				btnPrevious.setVisibility(Button.INVISIBLE);
	        	btnNext.setVisibility(Button.INVISIBLE);
	        	
	        	//se è stato premuto il forgotten elimino gli altri altrimenti il contrario
	        	if(v.getId() == p5.getId() && p5.isChecked())
	        	{
	        		p1.setChecked(false);
	        		p2.setChecked(false);
	        		p3.setChecked(false);
	        		p4.setChecked(false);
	        	}
	        	if(v.getId() != p5.getId())
	        		p5.setChecked(false);

	        	double halved = 0;
	        	if (rPlus.isChecked())
					halved = 0.5;
				else if (rMinus.isChecked())
					halved = -0.5;
	        	
	        	//stampo il totale
	        	tvTotal.setText(calculateScoreFromPenality(p1.isChecked(), p2.isChecked(), p3.isChecked(), p4.isChecked(), p5.isChecked(), halved) + "");
			}
        };
        
        p1.setOnClickListener(changeValueDisablePreviousAndNextPlusUpdateTotalScore);
        p2.setOnClickListener(changeValueDisablePreviousAndNextPlusUpdateTotalScore);
        p3.setOnClickListener(changeValueDisablePreviousAndNextPlusUpdateTotalScore);
        p4.setOnClickListener(changeValueDisablePreviousAndNextPlusUpdateTotalScore);
        p5.setOnClickListener(changeValueDisablePreviousAndNextPlusUpdateTotalScore);

		rPlus.setOnClickListener(changeValueDisablePreviousAndNextPlusUpdateTotalScore);
		rEqual.setOnClickListener(changeValueDisablePreviousAndNextPlusUpdateTotalScore);
		rMinus.setOnClickListener(changeValueDisablePreviousAndNextPlusUpdateTotalScore);
        
        btnPrevious.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v)
	        {
	        	kata.movePrevious();
	        	compileForm();
	        	if(kata.getIsValidated())
	            	btnNext.setVisibility(Button.VISIBLE);
	        	else
	        		btnNext.setVisibility(Button.INVISIBLE);
	        	
	        	showHideCheckbox();
	        	
	        	if(kata.getIndex() == 0)
	        		btnPrevious.setVisibility(View.INVISIBLE);
	        }
        });
        
        
        btnNext.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v)
	        {
	        	if(kata.moveNext())
	        	{
	        		if(kata.getIndex() != 0)
		        		btnPrevious.setVisibility(View.VISIBLE);
	        		compileForm();
		        	if(kata.getIsValidated())
		        	{
		            	btnNext.setVisibility(Button.VISIBLE);
		        	}
		        	else
		        		btnNext.setVisibility(Button.INVISIBLE);
	        	}
	        	else
	        	{
	        		//Faccio partire activity fluidity
	        		Intent myIntent = new Intent(v.getContext(), CompileFluidityActivity.class);
	                startActivityForResult(myIntent, 0);
	        	}
	        	showHideCheckbox();
	        }
        });
        
        
        btnValidate.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v)
	        {
	        	String pen = getStringPenalty();

	        	kata.setIsValidated(true);
	        	kata.setCurrentPenalty(pen);
	        	if(kata.getIndex()>0)
	        		btnPrevious.setVisibility(Button.VISIBLE);
	        	btnNext.setVisibility(Button.VISIBLE);
	        	
	        	//sync
	        	/*Intent msgIntent = new Intent(getApplicationContext(), SyncIntentService.class);
	            msgIntent.putExtra(SyncIntentService.PARAM_URL, myCom.makeUrlValidateForm(kata.getIndex(),pen));
	            msgIntent.putExtra(SyncIntentService.PARAM_TYPE, SyncIntentService.TYPE_FORM);
	            startService(msgIntent);
	            */
	        	myCom.sendValidateForm(kata.getIndex(), pen);
	        }
        });
        
        if(kata.getIsValidated())
        {
        	btnNext.setVisibility(Button.VISIBLE);
        }
        showHideCheckbox();
	}
	
	private String getStringPenalty()
	{   
        String pen = "";
        
        if(p1.isChecked()) pen += "1";
    	else pen += "0";
    	if(p2.isChecked()) pen += "1";
    	else pen += "0";
    	if(p3.isChecked()) pen += "1";
    	else pen += "0";
    	if(p4.isChecked()) pen += "1";
    	else pen += "0";
    	if(p5.isChecked()) pen += "1";
    	else pen += "0";

		if(rPlus.isChecked()) pen += "p";
		else if(rMinus.isChecked()) pen += "m";
		else pen += "e";
    	
        return pen;
	}
	
	private void compileForm()
	{
		compilePreviousForm();
		compileNextForm();
		
		LinearLayout border = (LinearLayout)findViewById(R.id.border);
		if(kata.getBorderColor()==0)
			border.setBackgroundResource(R.drawable.border_red);
		else
			border.setBackgroundResource(R.drawable.border_blue);
		
        tvKataName.setText(kata.getCurrentTechniqueName());
        tvSetName.setText(kata.getCurrentTechniqueSet());
        
        String penalty = kata.getCurrentPenalty();
        if(penalty.length() == 6)
        {
        	for(int i=0; i<5; i++)
        	{
        		if((penalty.substring(i, i+1)).equals("1"))
        		{
        			switch(i)
        			{
        				case 0: p1.setChecked(true); break;
        				case 1: p2.setChecked(true); break;
        				case 2: p3.setChecked(true); break;
        				case 3: p4.setChecked(true); break;
        				case 4: p5.setChecked(true); break;
        			}
        		}
            	else
            	{
            		switch(i)
        			{
        				case 0: p1.setChecked(false); break;
        				case 1: p2.setChecked(false); break;
        				case 2: p3.setChecked(false); break;
        				case 3: p4.setChecked(false); break;
        				case 4: p5.setChecked(false); break;
        			}
            	}
        	}

			double halved = 0;
        	char h = penalty.substring(5, 6).charAt(0);
        	switch (h) {
				case 'p': halved = +0.5; rPlus.setChecked(true); break;
				case 'm': halved = -0.5; rMinus.setChecked(true); break;
				default: rEqual.setChecked(true); break;
			}
			//stampo il totale
			tvTotal.setText(KataSet.beautyFormat(calculateScoreFromPenality(p1.isChecked(), p2.isChecked(), p3.isChecked(), p4.isChecked(), p5.isChecked(), halved)));
        }

	}
	
	private void compilePreviousForm()
	{
        tvKataNameP.setText(kata.getPreviousTechniqueName());
        tvSetNameP.setText(kata.getPreviousTechniqueSet());

		double halved = 0;
        String penalty = kata.getPreviousPenalty();
        if(penalty.length() == 6)
        {
        	for(int i=0; i<5; i++)
        	{
        		if((penalty.substring(i, i+1)).equals("1"))
        		{
        			switch(i)
        			{
        				case 0: p1p.setChecked(true); break;
        				case 1: p2p.setChecked(true); break;
        				case 2: p3p.setChecked(true); break;
        				case 3: p4p.setChecked(true); break;
        				case 4: p5p.setChecked(true); break;
        			}
        		}
            	else
            	{
            		switch(i)
        			{
        				case 0: p1p.setChecked(false); break;
        				case 1: p2p.setChecked(false); break;
        				case 2: p3p.setChecked(false); break;
        				case 3: p4p.setChecked(false); break;
        				case 4: p5p.setChecked(false); break;
        			}
            	}

				char h = penalty.substring(5, 6).charAt(0);
				switch (h) {
					case 'p': halved = +0.5; php.setType(HalvedRadio.IS_PLUS); break;
					case 'm': halved = -0.5; php.setType(HalvedRadio.IS_MINUS); break;
					default:
						php.setType(HalvedRadio.IS_EQUAL); break;
				}
        	}

			//stampo il totale
			tvTotalP.setText(KataSet.beautyFormat(calculateScoreFromPenality(p1p.isChecked(), p2p.isChecked(), p3p.isChecked(), p4p.isChecked(), p5p.isChecked(), halved)));
        }

	}
	private void compileNextForm()
	{
        tvKataNameN.setText(kata.getNextTechniqueName());
        tvSetNameN.setText(kata.getNextTechniqueSet());

		double halved = 0;
        String penalty = kata.getNextPenalty();
        if(penalty.length() == 6)
        {
        	for(int i=0; i<5; i++)
        	{
        		if((penalty.substring(i, i+1)).equals("1"))
        		{
        			switch(i)
        			{
        			case 0: p1n.setChecked(true); break;
    				case 1: p2n.setChecked(true); break;
    				case 2: p3n.setChecked(true); break;
    				case 3: p4n.setChecked(true); break;
    				case 4: p5n.setChecked(true); break;
        			}
        		}
            	else
            	{
            		switch(i)
        			{
        			case 0: p1n.setChecked(false); break;
    				case 1: p2n.setChecked(false); break;
    				case 2: p3n.setChecked(false); break;
    				case 3: p4n.setChecked(false); break;
    				case 4: p5n.setChecked(false); break;
        			}
            	}

				char h = penalty.substring(5, 6).charAt(0);
				switch (h) {
					case 'p': halved = +0.5; phn.setType(HalvedRadio.IS_PLUS); break;
					case 'm': halved = -0.5; phn.setType(HalvedRadio.IS_MINUS); break;
					default:
						phn.setType(HalvedRadio.IS_EQUAL); break;
				}
        	}

			//stampo il totale


			tvTotalN.setText(KataSet.beautyFormat(calculateScoreFromPenality(p1n.isChecked(), p2n.isChecked(), p3n.isChecked(), p4n.isChecked(), p5n.isChecked(), halved)));
        }

	}
	private void showHideCheckbox()
	{
		int index = kata.getIndex();
		int end = kata.getSize();
		
		if(index==0)
		{
	        p1p.setVisibility(View.INVISIBLE);
	        p2p.setVisibility(View.INVISIBLE);
	        p3p.setVisibility(View.INVISIBLE);
	        p4p.setVisibility(View.INVISIBLE);
	        p5p.setVisibility(View.INVISIBLE);
			php.setVisibility(View.INVISIBLE);
	        
	        tvTotalP.setText("");
		}
		else
		{
			p1p.setVisibility(View.VISIBLE);
	        p2p.setVisibility(View.VISIBLE);
	        p3p.setVisibility(View.VISIBLE);
	        p4p.setVisibility(View.VISIBLE);
	        p5p.setVisibility(View.VISIBLE);
			php.setVisibility(View.VISIBLE);
		}
		
		if(index==end - 1)
		{   
	        p1n.setVisibility(View.INVISIBLE);
	        p2n.setVisibility(View.INVISIBLE);
	        p3n.setVisibility(View.INVISIBLE);
	        p4n.setVisibility(View.INVISIBLE);
	        p5n.setVisibility(View.INVISIBLE);
			phn.setVisibility(View.INVISIBLE);
	        
	        tvTotalN.setText("");
		}
		else
		{
			p1n.setVisibility(View.VISIBLE);
	        p2n.setVisibility(View.VISIBLE);
	        p3n.setVisibility(View.VISIBLE);
	        p4n.setVisibility(View.VISIBLE);
	        p5n.setVisibility(View.VISIBLE);
			phn.setVisibility(View.VISIBLE);
		}
	}
	
	private double calculateScoreFromPenality(boolean c1, boolean c2, boolean c3, boolean c4, boolean c5, double halved) {
		float total=10;
    	if(c1)
    		total -= 1;
    	if(c2)
    		total -= 1;
    	if(c3)
    		total -= 3;
    	if(c4)
    		total -= 5;
    	if(c5)
    		total = 0;
    	if(c1 && c2 && c3 && c4)
    		total = 1;

    	if (!c5)
    		total += halved;
    	
    	return total;
	}
}
