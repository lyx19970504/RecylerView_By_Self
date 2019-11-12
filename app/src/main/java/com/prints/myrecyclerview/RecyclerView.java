package com.prints.myrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class RecyclerView extends ViewGroup {

    private Adapter adapter;

    private Recycler recycler;

    private List<View> viewList;   //当前屏幕中存在的item

    private int touchSlop;   //最小滑动距离

    private int[] heights;

    private int width;
    private int height;

    private int originY;

    private int firstRow;

    private int scrollY;

    private boolean needLayout;

    public RecyclerView(Context context) {
        this(context,null);
    }

    public RecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        touchSlop = configuration.getScaledTouchSlop();
        needLayout= true;
    }

    public void setAdapter(Adapter adapter){
        Log.d(TAG, "setAdapter: ");
        this.adapter = adapter;
        if(adapter != null){
            recycler = new Recycler(adapter.getViewTypeCount());
            viewList = new ArrayList<>();
            heights = new int[adapter.getCount()];
            needLayout= true;
            requestLayout();

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure: ");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG, "onMeasure: " + heightSize);
        if(adapter != null){
            for (int i=0;i<adapter.getCount();i++){
                heights[i] = adapter.getHeight();
            }
            int h = Math.min(sumArray(heights,0,heights.length),heightSize);
            setMeasuredDimension(widthSize,h);
        }
    }



    private int sumArray(int[] array, int firstIndex,int count){
        int sum = 0;
        count += firstIndex;
        for (int i = firstIndex;i<count;i++){
            sum += array[i];
        }
        return sum;
    }

    private static final String TAG = "RecyclerView";
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "onLayout:");
        if(needLayout || changed) {
            needLayout = false;
            viewList.clear();
            removeAllViews();
            if (adapter != null) {
                width = r - l;
                height = b - t;
                int top = 0, bottom = 0;
                for (int i = 0; i < adapter.getCount() && top < height; i++) {
                    bottom += heights[i];
                    View view = makeAndStep(i, 0, top, width, bottom);
                    viewList.add(view);
                    top = bottom;
                }
            }
        }
    }

    private View makeAndStep(int row, int left, int top, int right, int bottom) {
        View view = obtainView(row, right - left, bottom - top);
        view.layout(left, top, right, bottom);
        return view;
    }
    private View obtainView(int position, int width, int height) {
//        key type
        int itemType= adapter.getItemViewType(position);
//       取不到
        View recyclerView = recycler.get(itemType);
        View view;
        if (recyclerView == null) {
            view = adapter.onCreateViewHolder(this,position);
            if (view == null) {
                throw new RuntimeException("onCreateViewHodler  必须填充布局");
            }
        }else {
            view = adapter.onBindViewHolder(recyclerView,position);
        }
        view.setTag(R.id.type_item, itemType);
        view.measure(MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY)
                ,MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY));
        addView(view,0);
        return view;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept =false;
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                originY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(Math.abs(originY - ev.getRawY()) > touchSlop){
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
                int diffY = (int) (originY - event.getRawY()) / 6;
                scrollBy(0,diffY);
                break;
        }
        return true;
    }

    private int setBounds(int scrollY){
        if(scrollY >0){
            scrollY = Math.min(scrollY,sumArray(heights,firstRow,heights.length - firstRow) - height);
        }else{
            scrollY = Math.max(scrollY,-sumArray(heights,0,firstRow));
        }
        return scrollY;
    }

    @Override
    public void scrollBy(int x, int y) {
        scrollY += y;

        scrollY = setBounds(scrollY);
        //上滑
        if(scrollY > 0){
            Log.d(TAG, "scrollBy: " + "上滑");
            while (scrollY > heights[firstRow]){
                removeView(viewList.remove(0));
                scrollY -= heights[firstRow];
                firstRow ++;
            }
            while(getFillHeight() < height){
                int nextItemIndex = viewList.size() + firstRow;
                View view = obtainView(nextItemIndex,width,heights[nextItemIndex]);
                viewList.add(viewList.size(),view);
            }
        }
        //下滑
        else if(scrollY < 0){
            while(scrollY<0){
                firstRow--;
                scrollY += heights[firstRow];
                View view = obtainView(firstRow,width,heights[firstRow]);
                viewList.add(0,view);
            }
            while(sumArray(heights,firstRow,viewList.size()) - scrollY - heights[firstRow + viewList.size() - 1] >= height){
                removeView(viewList.remove(viewList.size() -1));
            }
        }
        repositionViews();
    }

    private void repositionViews(){
        int top,bottom,i;
        top = - scrollY;
        i = firstRow;
        for (View view : viewList){
            bottom = top + heights[i++];
            view.layout(0,top,width,bottom);
            top = bottom;
        }
    }

    private int getFillHeight(){
        return sumArray(heights,firstRow,viewList.size()) - scrollY;
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        int key = (int) view.getTag(R.id.type_item);
        recycler.put(view,key);
    }
}
