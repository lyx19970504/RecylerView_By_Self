package com.prints.myrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RecyclerView extends ViewGroup {

    private static final String TAG = "RecyclerView";
    private Recycler mRecycler;
    private Adapter mAdapter;
    private int[] heights;
    private int mCount;
    private List<View> viewList;
    private ViewHolder mHolder;
    private boolean needLayout = false;
    private int scrollY = 0;
    private float touchSlop;
    private int firstRow;
    private int mHeight;
    private int mWidth;

    public RecyclerView(Context context) {
        super(context);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public RecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public RecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setAdapter(Adapter adapter){
        mAdapter = adapter;
        if(mAdapter != null) {
            mRecycler = new Recycler(mAdapter.getTypeNumber());
            mCount = mAdapter.getCount();
            heights = new int[mCount];
            viewList = new ArrayList<>();
            needLayout = true;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mHolder = mAdapter.onCreateViewHandler(this,0);
        View childView = mHolder.itemView;
        childView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        Log.d(TAG, "onMeasure: " + childView.getMeasuredHeight());
        for (int i=0;i<mCount;i++){
            heights[i] = childView.getMeasuredHeight();
        }
        int  h = Math.min(sumArray(heights,0,heights.length),mHeight);
        setMeasuredDimension(width,h);
    }

    private int sumArray(int[] array,int firstIndex,int count){
        int sum = 0;
        count += firstIndex;
        for (int i=firstIndex;i<count;i++){
            sum += array[i];
        }
        return sum;
    }

    @Override
    protected void onLayout(boolean b, int i0, int i1, int i2, int i3) {
        if(needLayout || b){
            needLayout = false;
            viewList.clear();
            removeAllViews();
            if(mAdapter != null) {
                int top = 0;
                int bottom = 0;
                mWidth = i2 - i0;
                for (int i = 0; i < mCount; i++) {
                    bottom += heights[i];
                    View view = createView(i, i0, top, i2, bottom);
                    viewList.add(view);
                    top = bottom;
                }
            }
        }

    }

    private View createView(int position, int left, int top, int right, int bottom){
        View view = obtainView(position,right - left,bottom - top);
        view.layout(left,top,right,bottom);
        return view;
    }

    private View obtainView(int position, int width, int height) {
//        key type
        int itemType= mAdapter.getItemViewType(position);
//       取不到
        View recyclerView = mRecycler.get(itemType);
        View view;
        if (recyclerView == null) {
            mHolder = mAdapter.onCreateViewHandler(this,position);
        }else{
            mHolder.itemView = recyclerView;
        }
        mAdapter.onBindViewHandler(mHolder,position);
        view = mHolder.itemView;

        view.setTag(R.id.item_type, itemType);
        view.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
                , MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        addView(view,0);
        return view;
    }

    private float y;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                y = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(Math.abs(y - ev.getRawY()) > touchSlop){
                    intercept = true;
                }
                break;
        }
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                scrollBy(0,(int) (y - event.getRawY()));
                break;
        }
        return true;
    }

    private int setBounds(int scrollY){
        if(scrollY > 0){
            return Math.min(scrollY,sumArray(heights,firstRow,heights.length - firstRow) - mHeight);
        }else{
            return Math.max(scrollY,-sumArray(heights,0,firstRow));
        }
    }

    @Override
    public void scrollBy(int x, int y) {
        scrollY += y;
        scrollY = setBounds(scrollY);
        //上滑
        if(scrollY > 0){
            while (scrollY > heights[firstRow]){
                removeView(viewList.remove(0));
                scrollY -= heights[firstRow];
                firstRow ++;
            }
            while (getFilledHeight() < 0){
                int nextItemIndex = viewList.size() + firstRow;
                View view = obtainView(nextItemIndex,mWidth,heights[nextItemIndex]);
                viewList.add(viewList.size(),view);
            }
        }else{
            while (scrollY < 0){
                firstRow --;
                scrollY += heights[firstRow];
                View view = obtainView(firstRow,mWidth,heights[firstRow]);
                viewList.add(0,view);
            }
            while(getFilledHeight() - heights[firstRow + viewList.size() - 1]>= 0){
                removeView(viewList.remove(viewList.size() -1));
            }
        }
        repositionViews();
    }

    private void repositionViews(){
        int bottom,top,i;
        top = -scrollY;
        i = firstRow;
        for (View view : viewList) {
            bottom = top + heights[i++];
            view.layout(0,top,mWidth,bottom);
            top = bottom;
        }
    }

    private int getFilledHeight(){
        return sumArray(heights,firstRow,viewList.size()) - scrollY - mHeight;
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        int key = (int) view.getTag(R.id.item_type);
        mRecycler.put(key,view);
    }

    public abstract static class Adapter<T extends ViewHolder> {


        public abstract void onBindViewHandler(ViewHolder holder, int position);

        public abstract T onCreateViewHandler(ViewGroup recyclerView, int viewType);

        public abstract int getCount();

        public int getTypeNumber() {return 1;}

        public int getItemViewType(int position) {
            return 0;
        }
    }
}
