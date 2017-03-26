package gc.palazen;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CheckImage extends ImageView {
	
	private boolean isChecked;
	private boolean isBlur;
	private int position;
	 /**
     * @param context
     */
    public CheckImage(Context context)
    {
        super(context);
        // TODO Auto-generated constructor stub
        isChecked = false;
        isBlur = false;
        position = 0;
    }

    /**
     * @param context
     * @param attrs
     */
    public CheckImage(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        isChecked = false;
        isBlur = false;
        position = 0;
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public CheckImage(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        isChecked = false;
        isBlur = false;
        position = 0;
    }
	
	public boolean isChecked()
	{
		return isChecked;
	}
	
	public void setIsBlur(boolean val)
	{
		isBlur = val;
	}
	
	public void setPosition(int p)
	{
		position = p;
		setChecked(isChecked); //così aggiorna
	}
	
	public void setChecked(boolean val)
	{
		isChecked = val;
		
		if(isBlur)
		{
			if(isChecked)
				this.setImageResource(R.drawable.checkbox_on_background_blur);
			else
				this.setImageResource(R.drawable.checkbox_off_background_blur);
		}
		else
		{
			if(isChecked)
				this.setImageResource(R.drawable.checkbox_on_background);
			else
			{
				switch (position) {
				case 1:
				case 2:
					this.setImageResource(R.drawable.checkbox_off_background_s);
					break;
				case 3:
					this.setImageResource(R.drawable.checkbox_off_background_m);
					break;
				case 4:
					this.setImageResource(R.drawable.checkbox_off_background_b);
					break;
				case 5:
					this.setImageResource(R.drawable.checkbox_off_background_f);
					break;

				default:
					this.setImageResource(R.drawable.checkbox_off_background);
					break;
				}
			}
		}
	}
	
	@Override
	public boolean performClick() {
		// TODO Auto-generated method stub
		setChecked(!isChecked);
		return super.performClick();
	}
}