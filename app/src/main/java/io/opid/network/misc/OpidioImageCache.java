package io.opid.network.misc;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import com.android.volley.toolbox.ImageLoader;

public class OpidioImageCache implements ImageLoader.ImageCache {
    LruCache<String, Bitmap> bitmapLruCache = new LruCache<>(10);

    @Override
    public Bitmap getBitmap(String url) {
        return bitmapLruCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        bitmapLruCache.put(url, bitmap);
    }
}
