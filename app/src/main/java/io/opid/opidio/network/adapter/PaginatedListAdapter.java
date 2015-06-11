package io.opid.opidio.network.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import io.opid.opidio.Config;
import io.opid.opidio.OpidioApplication;
import io.opid.opidio.network.misc.JacksonRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A basic list adapter that takes care of downloading more items
 * as the list is scrolled down. It's used by all ListAdapters.
 *
 * @param <TSingle>    Example: Video
 * @param <TContainer> Example: Videos
 */
public abstract class PaginatedListAdapter<TSingle, TContainer> extends BaseAdapter {
    private final Fragment fragment;
    private final int rowResource;
    private List<TSingle> itemList = new ArrayList<>();
    private Activity activity;
    private LayoutInflater inflater;
    private Boolean loading;
    private int currentPage = 0;
    private int maxPages = 1; // Will be updated upon the first request
    private Class<TContainer> type;

    public PaginatedListAdapter(Class<TContainer> type, Fragment fragment, Activity activity, int rowResource) {
        this.activity = activity;
        this.fragment = fragment;
        this.type = type;
        this.rowResource = rowResource;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public int getRowResource() {
        return rowResource;
    }

    public LayoutInflater getInflater() {
        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        return inflater;
    }

    public Activity getActivity() {
        return activity;
    }

    protected void setCurrentPage(int page) {
        this.currentPage = page;
    }

    protected void setMaxPages(int pages) {
        this.maxPages = pages;
    }

    protected void addItems(List<TSingle> items) {
        itemList.addAll(items);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = getInflater();

        convertView = getConvertView(convertView, inflater);

        if (closeToEnd(position) && !loading) {
            loadMoreData();
        }

        return createView(position, convertView, itemList.get(position));
    }

    protected View getConvertView(View convertView, LayoutInflater inflater) {
        if (convertView == null) {
            convertView = inflater.inflate(rowResource, null);
        }
        return convertView;
    }

    public Map<String, String> getHeaders(Map<String, String> headers) {
        return headers;
    }

    public abstract View createView(int position, View view, TSingle element);

    protected void loadMoreData() {
        setLoading(true);
        int page = currentPage + 1;

        if (page > maxPages) {
            // The end was reached
            return;
        }

        OpidioApplication.getInstance().getRequestQueue().add(new JacksonRequest<>(Request.Method.GET, Config.HUB_SERVER + getURL() + page, getParams(), type, getHeaders(new HashMap<String, String>()),
                new Response.Listener<TContainer>() {
                    @Override
                    public void onResponse(TContainer response) {
                        handleResponse(response);

                        notifyDataSetChanged();
                        setLoading(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(activity, "Could not download " + type.getName(), Toast.LENGTH_SHORT).show();
                        setLoading(false);
                    }
                }));
    }

    /**
     * The relative url without pagination
     */
    protected abstract String getURL();

    protected abstract void handleResponse(TContainer response);

    public void setLoading(boolean loading) {
        this.loading = loading;
        ((AdapterStatusChanged) fragment).statusChanged(loading);
    }

    private boolean closeToEnd(int position) {
        return position + loadNext() > getCount();
    }

    /**
     * Begin loading more items when X from bottom
     */
    protected abstract int loadNext();

    public Map<String, String> getParams() {
        return null;
    }

    public interface AdapterStatusChanged {
        void statusChanged(boolean loading);
    }
}
