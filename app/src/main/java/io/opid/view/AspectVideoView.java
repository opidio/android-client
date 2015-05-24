package io.opid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class AspectVideoView extends VideoView {
    public AspectVideoView(Context context) {
        super(context);
    }

    public AspectVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int w, int h) {
        super.onMeasure(w, h);


        setMeasuredDimension(w, (int) ((double)9/16 * w));
    }
}
