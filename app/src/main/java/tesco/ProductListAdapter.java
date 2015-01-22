/**
 * Copyright (C) 2014 Gimbal, Inc. All rights reserved.
 *
 * This software is the confidential and proprietary information of Gimbal, Inc.
 *
 * The following sample code illustrates various aspects of the Gimbal SDK.
 *
 * The sample code herein is provided for your convenience, and has not been
 * tested or designed to work on any particular system configuration. It is
 * provided AS IS and your use of this sample code, whether as provided or
 * with any modification, is at your own risk. Neither Gimbal, Inc.
 * nor any affiliate takes any liability nor responsibility with respect
 * to the sample code, and disclaims all warranties, express and
 * implied, including without limitation warranties on merchantability,
 * fitness for a specified purpose, and against infringement.
 */
package tesco;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import baileyae.gimbal24example.R;


public class ProductListAdapter extends BaseAdapter {
    private Activity activity;
    private List<ProductEvent> events = new ArrayList<ProductEvent>();


    public ProductListAdapter(Activity activity) {
        this.activity = activity;


    }

    public void setEvents(List<ProductEvent> events) {
        this.events.clear();
        this.events.addAll(events);
        notifyDataSetChanged();
    }

    public  void addEvent(ProductEvent event) {
        events.add(event);
        notifyDataSetChanged();
    }

    public void addAEvent(List<ProductEvent> mevents){
        events.addAll(mevents);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ProductEvent event = events.get(position);
        View view = convertView;
        if (view == null) {
            view = activity.getLayoutInflater().inflate(R.layout.prod_list_item, null);
        }

        ImageLoader imageLoader = ImageLoader.getInstance();
        ImageView myImage = (ImageView) view.findViewById(R.id.icon);

        imageLoader.displayImage(event.getImagePath(), myImage);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(event.getprodName());

        TextView baseproductid = (TextView) view.findViewById(R.id.baseproductid);
        baseproductid.setText(event.getBaseProductId().toString());



        //parent.setOnClickListener(new View.OnClickListener() {
         //   @Override
        //    public void onClick(View v) {
        //        Toast.makeText(activity, "Hello", Toast.LENGTH_SHORT).show();

       //     }
       // });


        return view;
    }



}
