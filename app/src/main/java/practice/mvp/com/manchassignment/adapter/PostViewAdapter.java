package practice.mvp.com.manchassignment.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import practice.mvp.com.manchassignment.CreatePostActivity;
import practice.mvp.com.manchassignment.EditPostActivity;
import practice.mvp.com.manchassignment.R;
import practice.mvp.com.manchassignment.ViewPostActivity;
import practice.mvp.com.manchassignment.types.PostItem;
import practice.mvp.com.manchassignment.utilities.Utils;

/**
 * Created by admin on 9/17/18.
 */

public class PostViewAdapter extends RecyclerView.Adapter<PostViewAdapter.MyViewHolder> {
    private Context context;
    private List<PostItem> recordList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTxtTitle, mTxtDesc;
        public ImageView mImageView;

        public MyViewHolder(View view) {
            super(view);
            mTxtTitle = view.findViewById(R.id.title_item);
            mTxtDesc = view.findViewById(R.id.desc_item);
            mImageView = view.findViewById(R.id.image_item);

        }
    }


    public PostViewAdapter(Context context, List<PostItem> recordList) {
        this.context = context;
        this.recordList = recordList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final PostItem item = recordList.get(position);
        holder.mTxtTitle.setText(item.getTitle());
        holder.mTxtDesc.setText(item.getDescription());

        Bitmap bitmap= Utils.getImageBitmap(item.getImagePath());
        if(bitmap != null)
            holder.mImageView.setImageBitmap(bitmap);


        holder.mImageView.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                Intent viewPostIntent=new Intent(context, ViewPostActivity.class);
                viewPostIntent.putExtra("ViewPost",true);
                viewPostIntent.putExtra("PostItem", item);
                context.startActivity(viewPostIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

}


