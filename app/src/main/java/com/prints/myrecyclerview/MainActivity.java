package com.prints.myrecyclerview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        Adapter adapter = new Adapter();
        recyclerView.setAdapter(adapter);
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.Holder>{


        @Override
        public void onBindViewHandler(ViewHolder holder, int position) {
            View view = holder.itemView;
            TextView textView = view.findViewById(R.id.text1);
            textView.setText("以父之名"+position);
        }

        @Override
        public Holder onCreateViewHandler(ViewGroup parent, int viewType) {
            View recyclerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed,parent,false);
            return new Holder(recyclerView);
        }

        @Override
        public int getCount() {
            return 30;
        }

        private class Holder extends ViewHolder{

            public Holder(View view) {
                super(view);
            }
        }
    }
}


