package gc.palazen;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class KataActivity extends Activity {
	
	public static String serverIp = "";
	public static String macAddr = "";
	public static KataSet kata;
	public static MysqlCommunicator myCom;
	public static boolean fluidityEnable = false;
	
	public AutoRepeatButton btnTestConnection;
    /** Called when the activity is first created. */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final TextView tvTestConnectionResult = (TextView)findViewById(R.id.tvTestConnectionResult);
        //final TextView tvLabelServerIp = (TextView)findViewById(R.id.tvLabelServerIp);
        final TextView tvServerIp = (TextView)findViewById(R.id.tvServerIp);
        //final Button btnTestConnection = (Button)findViewById(R.id.btnTestConnection);
        btnTestConnection = (AutoRepeatButton)findViewById(R.id.btnTestConnection);
        final Button btnOpenForms = (Button)findViewById(R.id.btnOpenForms);
        final Button btnEditServerIp = (Button)findViewById(R.id.btnEditServerIp);
        
        try {
			String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			this.setTitle("Kata v" + versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
        
        //tolgo il focus al bottone principale
        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //disattivo standby
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   
        
        btnEditServerIp.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), SettingsActivity.class);
                startActivityForResult(myIntent, 0);
			}
		});
        
        btnTestConnection.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v)
	        {
	        	btnTestConnection.start();
	        	btnOpenForms.setVisibility(Button.INVISIBLE);
	        	tvTestConnectionResult.setText("");
	        	//salvo ipdelserver sulla stringa
	        	serverIp = tvServerIp.getText().toString();
	        	
	        	myCom = new MysqlCommunicator(getApplicationContext() , serverIp, macAddr);
	        	
				kata = new KataSet();
				
				myCom.requestTournamentData(kata);
				
				if(kata.getTournamentName()!=null)
				{
					Spanned result = kata.print();
			        tvTestConnectionResult.setText(result);
			        /*do
			        {
			        	tvTestConnectionResult.append(kata.getCurrentTechniqueName()+" "+kata.getCurrentPenalty()+"\n");
			        }while(kata.moveNext());
			        */
			        //if(!Html.toHtml(result).equals("<p>not associated</p>\n"))
			        if(!result.toString().replace("\r","").replace("\n","").equals("not associated"))
			        {
				        btnOpenForms.setVisibility(Button.VISIBLE);
			        }
				}
				else
				{
					tvTestConnectionResult.setText("error\n");
				}
	        }
	    });
        
        btnOpenForms.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	
            	//fermo aggiornamento botton
            	btnTestConnection.stop();
            	
            	IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        		Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
        		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        		int batteryPct = (int)((level / (float)scale) * 100);
            	
            	//myCom.sendBatteryStatus(batteryPct);
            	
            	//Intent msgIntent = new Intent(getApplicationContext(), SyncIntentService.class);
	            //msgIntent.putExtra(SyncIntentService.PARAM_URL, myCom.makeUrlBatteryStatus(batteryPct));
	            //msgIntent.putExtra(SyncIntentService.PARAM_TYPE, SyncIntentService.TYPE_BATTERY);
	            //startService(msgIntent);
        		myCom.sendBatteryStatus(batteryPct);
            	
                Intent myIntent = new Intent(view.getContext(), CompileFormActivity.class);
                startActivityForResult(myIntent, 0);
                
                btnOpenForms.setVisibility(Button.INVISIBLE);
            }
        });
        
        //btnTestConnection.start();
        
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	
    	WifiManager wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        macAddr = wifiInf.getMacAddress();
        
        if(macAddr==null) //emulatore
        	macAddr = "00:11:22:33:44:55";
        //mostro mac address
        TextView tvmac = (TextView)findViewById(R.id.tvmac);
        tvmac.setText(macAddr);
        //mostor ip
        final TextView tvip = (TextView)findViewById(R.id.tvip);
        tvip.setText(getLocalIpv4Address());
    	
    	TextView tvServerIp = (TextView)findViewById(R.id.tvServerIp);
    	final SharedPreferences preferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String ipserver = preferences.getString("ipserver", "192.168.0.254");
        tvServerIp.setText(ipserver);
        
        fluidityEnable = preferences.getBoolean("fluidityenable", false);
    	
    	btnTestConnection.start();
    }
    
    @Override
    public void onPause()
    {
    	super.onPause();
    	btnTestConnection.stop();
    }
    
    public String getLocalIpAddress() {
    	try {
    		Enumeration<NetworkInterface> en;
    		for (en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
    			NetworkInterface intf = en.nextElement();
    			Enumeration<InetAddress> enumIpAddr;
    			for (enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
    				InetAddress inetAddress = enumIpAddr.nextElement();
    				if (!inetAddress.isLoopbackAddress()) {
    					return inetAddress.getHostAddress().toString();
    				}
    			}
    		}
    	} catch (SocketException ex) {
    		Log.e("error ip", ex.toString());
    	}
    	return null;
    }
    
    public static String getLocalIpv4Address(){
        try {
            String ipv4;
            List<NetworkInterface>  nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
            if(nilist.size() > 0){
                for (NetworkInterface ni: nilist){
                    List<InetAddress>  ialist = Collections.list(ni.getInetAddresses());
                    if(ialist.size()>0){
                        for (InetAddress address: ialist){
                            if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4=address.getHostAddress())){ 
                                return ipv4;
                            }
                        }
                    }

                }
            }

        } catch (SocketException ex) {

        }
        return "";
    }
}