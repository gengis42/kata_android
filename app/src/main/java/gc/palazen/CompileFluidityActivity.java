package gc.palazen;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class CompileFluidityActivity extends Activity {
	
	private ResponseReceiver receiver;
	
	private static KataSet kata;
	private static MysqlCommunicator myCom;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fluidity);
        
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   
        
        kata = KataActivity.kata;
        myCom = KataActivity.myCom;
        
        final TextView kataname = (TextView)findViewById(R.id.tvKataName);
        final TextView seekValue = (TextView)findViewById(R.id.textSeekValue);
        final TextView tvAllScore = (TextView)findViewById(R.id.tvAllScore);
        final SeekBar seek = (SeekBar)findViewById(R.id.seekBarFluidity);
        
        final Button btnPrevious = (Button)findViewById(R.id.btnPrevious);
        final Button btnNext = (Button)findViewById(R.id.btnNext);
        final Button btnValidate = (Button)findViewById(R.id.btnValidate);
        
        seekValue.setText(kata.getFcr() + "");
        seek.setProgress(kata.getFcr());
        
        //riempio la score
        tvAllScore.setText(kata.getStringAllScore());
        
        seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				int p = progress;
				int max = kata.getMaxFcr();

				if (p > max)
					p = max;

				seek.setProgress(p);
				seekValue.setText(p + "");
				btnNext.setVisibility(SeekBar.INVISIBLE);
			}

			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

		});
        
        btnPrevious.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v)
	        {
	        	onBackPressed();
	        }
        });
        
        btnNext.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v)
	        {
	        	btnNext.setVisibility(Button.INVISIBLE);
	        	btnPrevious.setVisibility(Button.INVISIBLE);
	        	//btnValidate.setVisibility(Button.INVISIBLE);
	        	seek.setVisibility(Button.INVISIBLE);
	        	seekValue.setVisibility(Button.INVISIBLE);
	        	kataname.setVisibility(Button.INVISIBLE);
	        	tvAllScore.setText("END!");
	        	btnValidate.setText("restart");
	        	btnValidate.setVisibility(View.INVISIBLE);
	        	
	            
	        	//sync
	        	/*Intent msgIntent = new Intent(getApplicationContext(), SyncIntentService.class);
	            msgIntent.putExtra(SyncIntentService.PARAM_URL, myCom.makeUrlRestart());
	            msgIntent.putExtra(SyncIntentService.PARAM_TYPE, SyncIntentService.TYPE_RESTART);
	            startService(msgIntent);*/
	        	myCom.sendRestart();
	        }
        });
        
        btnValidate.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v)
	        {
	        	if(btnValidate.getText().toString().equals(getResources().getString(R.string.restart).toString()))
	        	{
	        		if(myCom.sendDisassociation())
	        		{	        			
		        		Intent myIntent = new Intent(v.getContext(), KataActivity.class);
		                startActivityForResult(myIntent, 0);
	        		}
	        		else
	        		{
	        			Toast.makeText(getApplicationContext(), "Connection error!", Toast.LENGTH_SHORT).show();
	        		}
	        	}
	        	else
	        	{
		        	kata.setFcr(Integer.parseInt(seekValue.getText().toString()));
		        	//myCom.validateFluidity(seekValue.getText().toString());
		        	
		        	//sync
		        	/*Intent msgIntent = new Intent(getApplicationContext(), SyncIntentService.class);
		            msgIntent.putExtra(SyncIntentService.PARAM_URL, myCom.makeUrlValidateFcr(seekValue.getText().toString()));
		            msgIntent.putExtra(SyncIntentService.PARAM_TYPE, SyncIntentService.TYPE_FCR);
		            startService(msgIntent);
		        	*/
		        	myCom.sendValidateFcr(seekValue.getText().toString());
		        	btnNext.setVisibility(Button.VISIBLE);

					// AUTO NEXT ON without fluidity
					if(KataActivity.fluidityEnable == false)
					{
						btnNext.performClick();
					}
	        	}
	        }
        });
        
        if(KataActivity.fluidityEnable == false)
        {
        	seek.setProgress(0);
        	seekValue.setText("0");
        	//btnValidate.performClick();
        	//btnNext.performClick();
			seek.setVisibility(View.INVISIBLE);
			seekValue.setVisibility(View.INVISIBLE);
        } else {
			seek.setVisibility(View.VISIBLE);
			seekValue.setVisibility(View.VISIBLE);
		}
	}
	
	public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP = "gc.palazen.intent.action.MESSAGE_PROCESSED";
        @Override
        public void onReceive(Context context, Intent intent) {
        	
        	Log.d("mostra","si");
        	final Button btnValidate = (Button)findViewById(R.id.btnValidate);
        	btnValidate.setVisibility(View.VISIBLE);
        }
    }
	
	@Override
	protected void onResume()
	{
		//creo intent per servizio
        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);
	    super.onResume();
	}
	
	@Override
	protected void onPause()
	{
	    unregisterReceiver(receiver);
	    super.onPause();
	}
}
