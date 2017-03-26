package gc.palazen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MysqlCommunicator {
	private String serverIp;
	private String androidMac;
	
	private Context context;
	
	MysqlCommunicator(Context c, String si, String am)
	{
		serverIp = si;
		androidMac = am;
		context = c;
	}
	
	public void requestTournamentData(KataSet k)
	{
		try
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	        nameValuePairs.add(new BasicNameValuePair("androidmac", androidMac));
			//String result = postRequest("http://"+serverIp+"/tablet/request.php", nameValuePairs, 1000);
	        String result = postRequest("http://"+serverIp+"/tablet/request.php", nameValuePairs);
			
	        JSONObject json = new JSONObject(result);
			if(json.getInt("i") == 0)
			{
				k.loadTournament("not associated", "", "", "", "");
			}
			else
			{
				JSONObject am;
				JSONArray at, as, ap;
				int af;
				am = json.getJSONObject("m");
				at = json.getJSONArray("t");
				as = json.getJSONArray("s");
				ap = json.getJSONArray("p");
				af = json.getInt("f");
				k.loadTournament(am.getString("tname"), am.getString("kname"),am.getString("nform"),am.getString("jname"),am.getString("pname"));
				
				int numTechniques = am.getInt("ntech");
				String[] techniques = new String[numTechniques];
				String[] set = new String[numTechniques];
				String[] penalty = new String[numTechniques];
				boolean[] validated = new boolean[numTechniques];
				String tmp;
				int i;
				for(i=0; i<numTechniques; i++)
				{
					techniques[i] = at.getString(i);
					set[i] = as.getString(i);
					
					tmp = ap.getString(i);
					
					if("null".equals(tmp)) {
						penalty[i] = "00000";
						validated[i] = false;
					}
					else {
						penalty[i] = tmp;
						validated[i] = true;
					}
				}
				
				boolean fcrValidate=false;
				int fcr = 5;
				
				if(af >= 0)
				{
					fcrValidate=true;
					fcr = af;
				}
				
				k.loadTechniquesPenaltySetValidates(techniques, penalty, set, validated, fcrValidate, fcr);
			}
		}catch(java.lang.NumberFormatException e)
		{
			e.printStackTrace();
		}
		catch(java.lang.IndexOutOfBoundsException e)
		{
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendValidateForm(int position, String value)
	{
		//List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		//nameValuePairs.add(new BasicNameValuePair("mac", androidMac));
		//nameValuePairs.add(new BasicNameValuePair("pos", ""+position));
		//nameValuePairs.add(new BasicNameValuePair("value", value));
		
		String url = "http://"+serverIp+"/tablet/sendvalidate.php"+
				"?mac=" + androidMac +
				"&pos=" + position +
				"&value=" + value;
		
		Intent msgIntent = new Intent(context, SyncIntentService.class);
        msgIntent.putExtra(SyncIntentService.PARAM_URL, url);
        msgIntent.putExtra(SyncIntentService.PARAM_TYPE, SyncIntentService.TYPE_FORM);
        context.startService(msgIntent);
		
	}
	public void sendValidateFcr(String value)
	{
		String url = "http://"+serverIp+"/tablet/sendvalidate.php"+
				"?mac=" + androidMac +
				"&fluidity=true" +
				"&value=" + value;
		
		Intent msgIntent = new Intent(context, SyncIntentService.class);
        msgIntent.putExtra(SyncIntentService.PARAM_URL, url);
        msgIntent.putExtra(SyncIntentService.PARAM_TYPE, SyncIntentService.TYPE_FCR);
        context.startService(msgIntent);
	}
	
	public void sendRestart()
	{
		String url = "http://"+serverIp+"/tablet/restart.php?restart=restart";
		
		Intent msgIntent = new Intent(context, SyncIntentService.class);
        msgIntent.putExtra(SyncIntentService.PARAM_URL, url);
        msgIntent.putExtra(SyncIntentService.PARAM_TYPE, SyncIntentService.TYPE_RESTART);
        context.startService(msgIntent);
	}
	
	public void sendBatteryStatus(int perc){
		
		String url = 
				"http://"+serverIp+"/tablet/sendbatterystatus.php"+
				"?mac=" + androidMac +
				"&battery=" + perc;
		
		Intent msgIntent = new Intent(context, SyncIntentService.class);
        msgIntent.putExtra(SyncIntentService.PARAM_URL, url);
        msgIntent.putExtra(SyncIntentService.PARAM_TYPE, SyncIntentService.TYPE_BATTERY);
        context.startService(msgIntent);
    }
	public boolean sendDisassociation()
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("mac", androidMac));
        nameValuePairs.add(new BasicNameValuePair("disassociation", "true"));
        
		//postRequest("http://"+serverIp+"/tablet/senddisassociation.php", nameValuePairs, 0);
        String result = postRequest("http://"+serverIp+"/tablet/senddisassociation.php", nameValuePairs);
        if("updated".equals(result))
        	return true;
        else
        	return false;
	}
	///////////////////////////////////////////////////
	
    private String postRequest(String url, List<NameValuePair> vp) {
    	int timeout = 200;
    	
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeout);
        HttpConnectionParams.setSoTimeout(httpParameters, timeout);
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
        
        HttpPost httpPost = new HttpPost(url);
        
        List<NameValuePair> nameValuePairs = vp;
        try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        
        int retry = 5, count = 0;
        HttpResponse httpResponse;
        HttpEntity entity;
        while (count < retry) {
            count += 1;
        
	        try {
	            // Execute HTTP Post Request
	
	            httpResponse = httpClient.execute(httpPost);
	            
	            entity = httpResponse.getEntity();
	            String data = EntityUtils.toString(entity);
	            //Toast.makeText(context, count + " " + httpResponse.getStatusLine().getStatusCode(), 100).show();
	            return data;
	        } catch (ClientProtocolException e) {
	            // TODO Auto-generated catch block
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	        }
	        catch (IllegalArgumentException e) {
				// TODO: handle exception
			}
        }
        
        return "";
    }
}
