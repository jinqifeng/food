package com.chaoyu.jongwn.taximeter.utils.util;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.chaoyu.jongwn.taximeter.PrintActivity;
import com.chaoyu.jongwn.taximeter.R;

import java.util.List;

/**
 * Created by JongWN on 12/10/2017.
 */

public class FareAdapter extends RecyclerView.Adapter<FareAdapter.MyViewHolder> {

        private List<FareResult> contactList;

        public FareAdapter(List<FareResult> contactList) {
            this.contactList = contactList;
        }

        @Override
        public int getItemCount() {
            return contactList.size();
        }

        @Override
        public void onBindViewHolder(MyViewHolder fareViewHolder, int i) {
            FareResult ci = contactList.get(i);
            fareViewHolder.vTitle.setText(ci.vTitle);
            fareViewHolder.vTax.setText(ci.vTax);
            fareViewHolder.tvPeriod.setText(ci.tvPeriod);
            fareViewHolder.tvStart.setText(ci.tvStart );
            fareViewHolder.tvEnd.setText(ci.tvEnd );
            fareViewHolder.tvDistance.setText(ci.tvDistance );
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.card_view, viewGroup, false);

            return new MyViewHolder(itemView);
        }

        public static class MyViewHolder extends RecyclerView.ViewHolder {
            protected TextView vTitle;
            protected TextView vTax;
            protected TextView tvPeriod;
            protected TextView tvStart;
            protected TextView tvEnd;
            protected TextView tvDistance;
            protected ImageButton ibPrint;
            public MyViewHolder(View v) {
                super(v);
                vTitle =  (TextView) v.findViewById(R.id.title);
                vTax = (TextView)  v.findViewById(R.id.tvTax);
                tvPeriod = (TextView)  v.findViewById(R.id.tvPeriod);
                tvStart = (TextView) v.findViewById(R.id.tvStart);
                tvEnd = (TextView) v.findViewById(R.id.tvEnd);
                tvDistance = (TextView) v.findViewById(R.id.tvDistance);
                ibPrint = (ImageButton) v.findViewById(R.id.imPrint);
                ibPrint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(v.getContext(), PrintActivity.class);
                        v.getContext().startActivity(intent);
                      //  Toast.makeText(v.getContext(), "os version is: " + feed.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                });

            }


        }
}

