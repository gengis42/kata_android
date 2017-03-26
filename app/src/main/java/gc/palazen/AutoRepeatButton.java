package gc.palazen;

import java.util.TimerTask;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Surprisingly, an AutoRepeatButton doesn't seem to be in the standard Android View frameworks.
 * So, simple implementation here. When Button is held down its onClickListener is called on a timer.
 *  
 * @author Scott Powell
 */
public class AutoRepeatButton extends Button
{
	private static boolean isActive = false;
	
    public AutoRepeatButton(Context c)
    {
        super(c);
    }
    
    /**
     * Constructor used when inflating from layout XML 
     */
    public AutoRepeatButton(Context c, AttributeSet attrs)
    {
        super(c, attrs);
    }

    /**
     * TODO: These could be made variable, and/or set from the XML (ie. AttributeSet) 
     */
    private static final int INITIAL_DELAY = 1000;
    private static final int REPEAT_INTERVAL = 5000;
    
    private TimerTask mTask = new TimerTask()
    {
        @Override
        public void run()
        {
        	//Log.d("premuto", "no");
            if (!isPressed())
            {
                performClick();  // simulate a 'click'
                //Log.d("premuto", "si");
                postDelayed(this, REPEAT_INTERVAL);   // rinse and repeat...
            }
        }
    };
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
    	//Log.d("a","b");
        //if (event.getAction() == MotionEvent.ACTION_DOWN)
        //    postDelayed(mTask, INITIAL_DELAY);
        //else if (event.getAction() == MotionEvent.ACTION_UP)
        //    removeCallbacks(mTask);  // don't want the pending TimerTask to run now that Button is released!

        return super.onTouchEvent(event); 
    }
    
    public void start()
    {
    	if(!isActive)
    		postDelayed(mTask, INITIAL_DELAY);
    	isActive = true;
    }
    
    public void stop()
    {
    	removeCallbacks(mTask);
    	isActive = false;
    }
    
}