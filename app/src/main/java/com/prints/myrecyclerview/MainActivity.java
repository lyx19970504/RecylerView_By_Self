package com.prints.myrecyclerview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        Adapter adapter = new Adapter();
        recyclerView.setAdapter(adapter);
    }

    private class Adapter implements com.prints.myrecyclerview.Adapter {

        @Override
        public View onCreateViewHolder(ViewGroup parent,int position) {
            View recyclerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table,parent,false);
            TextView textView = recyclerView.findViewById(R.id.text1);
            textView.setText(position+"");
            return recyclerView;
        }

        @Override
        public View onBindViewHolder(View recyclerView, int position) {
            TextView textView = recyclerView.findViewById(R.id.text1);
            textView.setText(position+"");
            return recyclerView;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getCount() {
            return 300000;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public int getHeight() {
            return 100;
        }
    }
}
