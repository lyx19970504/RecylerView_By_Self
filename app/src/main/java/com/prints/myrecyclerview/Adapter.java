package com.prints.myrecyclerview;

import android.view.View;
import android.view.ViewGroup;

public interface Adapter {

    View onCreateViewHolder(ViewGroup parent, int position);

    View onBindViewHolder(View recyclerView, int position);

    int getItemViewType(int position);

    int getCount();

    int getViewTypeCount();

    int getHeight();
}
