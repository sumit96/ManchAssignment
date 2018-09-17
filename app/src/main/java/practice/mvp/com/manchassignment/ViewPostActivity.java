package practice.mvp.com.manchassignment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import practice.mvp.com.manchassignment.types.PostItem;
import practice.mvp.com.manchassignment.utilities.Utils;

public class ViewPostActivity extends AppCompatActivity {

    private TextView mTxtTitle;
    private TextView mTxtDescription;
    private TextView mTitle;
    private ImageView mImageView;
    private Intent receiveIntent;
    private boolean isViewPost;
    private PostItem mPostItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        initilizeUI();
    }

    private void initilizeUI()
    {
        mTxtTitle=findViewById(R.id.txt_title);
        mTxtDescription=findViewById(R.id.txt_desc);
        mImageView=findViewById(R.id.image_view);
        mTitle=findViewById(R.id.title);
        mTitle.setText(getString(R.string.view_post_title));
        receiveIntent=getIntent();
        mPostItem=receiveIntent.getParcelableExtra("PostItem");
        isViewPost=receiveIntent.getBooleanExtra("ViewPost",false);

        displayData();
    }

    private void displayData()
    {
        if(isViewPost && mPostItem != null)
        {
            mTxtTitle.setText(mPostItem.getTitle());
            mTxtDescription.setText(mPostItem.getDescription());
            Bitmap bitmap= Utils.getImageBitmap(mPostItem.getImagePath());
            if(bitmap != null)
                mImageView.setImageBitmap(bitmap);

        }
    }
}
