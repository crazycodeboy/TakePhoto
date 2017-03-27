package multipleimageselect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import java.util.ArrayList;

/**
 * Created by Darshan on 4/24/2015.
 */
public abstract class CustomGenericAdapter<T> extends BaseAdapter {
    protected ArrayList<T> arrayList;
    protected Context context;
    protected LayoutInflater layoutInflater;

    protected int size;

    public CustomGenericAdapter(Context context, ArrayList<T> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    public T getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setLayoutParams(int size) {
        this.size = size;
    }

    public void releaseResources() {
        arrayList = null;
        context = null;
    }
}
