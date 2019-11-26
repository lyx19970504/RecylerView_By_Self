package com.prints.myrecyclerview;

import android.view.View;

import java.util.Stack;

public class Recycler {

    private Stack[] stacks;

    public Recycler(int typeNumber){
        stacks = new Stack[typeNumber];
        for (int i=0;i<stacks.length;i++){
            stacks[i] = new Stack<View>();
        }
    }

    public void put(int typeNumber, View view){
        stacks[typeNumber].push(view);
    }

    public View get(int typeNumber){
        try {
            return (View) stacks[typeNumber].pop();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
