package gc.palazen;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.IntentService;
import android.content.Intent;

import gc.palazen.CompileFluidityActivity.ResponseReceiver;

public class SyncIntentService extends IntentService {
    //public static final String PARAM_IN_MSG = "imsg";
	//public static final String PARAM_OUT_MSG = "omsg";
	public static final String PARAM_RETURN = "return";
    
    public static final String PARAM_URL = "url";
    public static final String PARAM_TYPE = "type";
    
    public static final int TYPE_FORM = 1;
    public static final int TYPE_FCR = 2;
    public static final int TYPE_RESTART = 3;
    public static final int TYPE_BATTERY = 4;
    
    private int timeout = 1000;
	private HttpParams httpParameters;
	private DefaultHttpClient httpClient;
	private String data;
	private HttpResponse httpResponse;
	private HttpEntity entity;

    public SyncIntentService() {
        super("SyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String url = intent.getStringExtra(PARAM_URL);
        int type = intent.getIntExtra(PARAM_TYPE, 0);
        

        httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeout);
		HttpConnectionParams.setSoTimeout(httpParameters, timeout);
		httpClient = new DefaultHttpClient(httpParameters);
		
		HttpGet httpGet = new HttpGet(url);
		
		while(true){

			data = null;
			try {
				httpResponse = httpClient.execute(httpGet);
				entity = httpResponse.getEntity();
				data = EntityUtils.toString(entity);
				
				if(data.equals("updated"))
					break;

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        
		if(type == TYPE_RESTART)
		{
	        Intent broadcastIntent = new Intent();
	        broadcastIntent.setAction(ResponseReceiver.ACTION_RESP);
	        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
	        broadcastIntent.putExtra(PARAM_RETURN, "finito");
	        sendBroadcast(broadcastIntent);
		}
    }

}
