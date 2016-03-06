package com.yoavgibri.cash;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;
import java.util.Objects;

/**
 * Created by Yoav on 03/03/16.
 */
public class SpinnerAdapter extends ArrayAdapter<Objects> {

    public SpinnerAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    @Override
    public int getCount() {
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }
}
