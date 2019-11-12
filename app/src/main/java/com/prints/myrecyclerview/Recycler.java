package com.prints.myrecyclerview;

import android.view.View;

import java.util.Stack;

public class Recycler {

    private Stack[] stacks;

    public Recycler(int typeNumber){
        stacks = new Stack[typeNumber];
        for (int i = 0; i<typeNumber;i++){
            stacks[i] = new Stack<View>();
        }
    }

    public View get(int typeNumber){
        try {
            return (View) stacks[typeNumber].pop();
        }catch (Exception e){
            return null;
        }
    }

    public void put(View view,int type){
        stacks[type].push(view);
    }
}
