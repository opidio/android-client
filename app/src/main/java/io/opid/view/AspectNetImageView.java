package io.opid.view;

import android.content.Context;
import android.util.AttributeSet;
import com.android.volley.toolbox.NetworkImageView;

public class AspectNetImageView extends NetworkImageView {
    public AspectNetImageView(Context context) {
        super(context);
    }

    public AspectNetImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectNetImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int w, int h) {
        super.onMeasure(w, h);

        setMeasuredDimension(w, (int) ((double)9/16 * w));
    }
}
