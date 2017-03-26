package gc.palazen;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class SettingsActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editserverip);
        
        final EditText etServerIp = (EditText)findViewById(R.id.etServerIp);
        final Button btnSaveAndClose = (Button)findViewById(R.id.btnSaveAndClose);
        final Button btnUpdate = (Button)findViewById(R.id.btnUpdate);
        final CheckBox chkFluidityEnable = (CheckBox)findViewById(R.id.chkFluidityEnable);
        
        //salvataggio dell'ip server
        final SharedPreferences preferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        String ipserver = preferences.getString("ipserver", "192.168.0.254");
        etServerIp.setText(ipserver);
        chkFluidityEnable.setChecked(preferences.getBoolean("fluidityenable", false));
        
        btnSaveAndClose.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v)
	        {
	        	SharedPreferences.Editor editor = preferences.edit();
            	editor.putString("ipserver", etServerIp.getText().toString());
            	editor.putBoolean("fluidityenable", chkFluidityEnable.isChecked());
            	editor.commit();
            	
	        	finish();
	        }
	    });
        
        
        btnUpdate.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v)
	        {
	        	UpdateApp atualizaApp = new UpdateApp();
	            atualizaApp.setContext(getApplicationContext());
	            String url = "http://" + etServerIp.getText().toString() + "/kata.apk";
	            //atualizaApp.execute("http://palazen.org/kata.apk");
	            atualizaApp.execute(url);
	        }
	    });
    }
}