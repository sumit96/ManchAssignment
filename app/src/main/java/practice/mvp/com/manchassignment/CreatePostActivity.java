package practice.mvp.com.manchassignment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import practice.mvp.com.manchassignment.adapter.PostViewAdapter;
import practice.mvp.com.manchassignment.database.SQLManager;
import practice.mvp.com.manchassignment.types.PostItem;

public class CreatePostActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<PostItem> postList=new ArrayList<PostItem>();
    private Button mBtnCreatePost;
    private TextView mTitle;
    private RecyclerView mRecyclerView;
    private LinearLayout mTopBarCreateBtn;
    private LinearLayout mPostListViewLayout;
    private final int CREATE_POST_REQUEST=1;
    private PostViewAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        initializeUI();
    }


    private void initializeUI()
    {
        mBtnCreatePost=findViewById(R.id.btn_create_post);
        mRecyclerView=findViewById(R.id.recycler_list);
        mTopBarCreateBtn=findViewById(R.id.btn_top_create_post);
        mPostListViewLayout=findViewById(R.id.post_view_layout);
        mTitle=findViewById(R.id.title);
        mTitle.setText(getString(R.string.create_post_title));

        handleViewVisibility();

        mTopBarCreateBtn.setOnClickListener(this);
        mBtnCreatePost.setOnClickListener(this);
    }
    private void handleViewVisibility()
    {
        postList= SQLManager.getInstance().getPostData();

        if(postList.size() == 0)
        {
            mBtnCreatePost.setVisibility(View.VISIBLE);
            mPostListViewLayout.setVisibility(View.GONE);
            mTopBarCreateBtn.setVisibility(View.GONE);

        }
        else{
            mBtnCreatePost.setVisibility(View.GONE);
            mPostListViewLayout.setVisibility(View.VISIBLE);
            mTopBarCreateBtn.setVisibility(View.VISIBLE);
            mAdapter = new PostViewAdapter(this, postList);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            mRecyclerView.setAdapter(mAdapter);

        }
    }

    @Override
    public void onClick(View v) {
        Intent createPostIntent=new Intent(CreatePostActivity.this, EditPostActivity.class);
        startActivityForResult(createPostIntent,CREATE_POST_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_CANCELED)
            return;

        if(requestCode == CREATE_POST_REQUEST)
        {
            handleViewVisibility();
        }
    }
}
