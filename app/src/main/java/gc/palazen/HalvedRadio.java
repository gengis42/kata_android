package gc.palazen;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;
import android.widget.Toast;

/**
 * Created by geko on 26/11/17.
 */

public class HalvedRadio extends RadioButton {

    public static final int IS_PLUS = 0;
    public static final int IS_EQUAL = 1;
    public static final int IS_MINUS = 2;

    private int type = IS_EQUAL;

    private boolean isBlur = false;

    public HalvedRadio(Context context) {
        super(context);
    }

    public HalvedRadio(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HalvedRadio(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setIsBlur(boolean val)
    {
        isBlur = val;
    }

    public void setType(int t) {
        type = t;

        if (isBlur) {
            switch (type) {
                case IS_PLUS: {
                    this.setBackgroundResource(R.drawable.radio_plus_blur);
                } break;
                case IS_EQUAL: {
                    this.setBackgroundResource(R.drawable.radio_equal_blur);
                } break;
                case IS_MINUS: {
                    this.setBackgroundResource(R.drawable.radio_minus_blur);
                } break;
            }
        }
    }

    public void setChecked(boolean val)
    {
        super.setChecked(val);
        if (isBlur)
            return;

        switch (type) {
            case IS_PLUS: {
                if (isChecked()) {
                    this.setBackgroundResource(R.drawable.radio_plus_on);
                } else {
                    this.setBackgroundResource(R.drawable.radio_plus_off);
                }
            } break;
            case IS_EQUAL: {
                if (isChecked()) {
                    this.setBackgroundResource(R.drawable.radio_equal_on);
                } else {
                    this.setBackgroundResource(R.drawable.radio_equal_off);
                }
            } break;
            case IS_MINUS: {
                if (isChecked()) {
                    this.setBackgroundResource(R.drawable.radio_minus_on);
                } else {
                    this.setBackgroundResource(R.drawable.radio_minus_off);
                }
            } break;
        }
    }
}
