package com.capturfri;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OpenPrivatePost extends AppCompatActivity {

    private ImageView imageView;
    private VideoView videoView;
    private JSONObject jsonObject;
    private int state=0;
    private CommentAdapter adapter;
    private ArrayList<Comments> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_private_post);
        imageView=findViewById(R.id.imageView_openprivatepost);
        videoView=findViewById(R.id.videoView_openprivatepost);

        RecyclerView recyclerView=findViewById(R.id.recycler_comment_openprivatepost);
        list=new ArrayList<>();
        adapter=new CommentAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true));
        recyclerView.setAdapter(adapter);

        try {
            jsonObject=new JSONObject(getIntent().getExtras().getString("json",""));
            if (jsonObject.getString("type").equals("picture")){
                videoView.setVisibility(View.GONE);
                Glide.with(imageView).load(jsonObject.getString("url")).into(imageView);
            }else if (jsonObject.getString("type").equals("video")){
                imageView.setVisibility(View.GONE);
                videoView.setVideoURI(Uri.parse(jsonObject.getString("url")));
                videoView.start();
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.setLooping(true);
                    }
                });
            }


            JSONArray array=jsonObject.getJSONArray("comment");
            for (int i=array.length()-1;i>=0;i--){
                list.add(new Comments(array.getJSONObject(i).getString("username"),array.getJSONObject(i).getString("comment")));
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (state==0){
                    videoView.pause();
                    state=1;
                }else if (state==1){
                    videoView.start();
                    state=0;
                }
                return false;
            }
        });
    }



    private class Comments{
        private String username;
        private String comment;

        public Comments(String username, String comment) {
            this.username = username;
            this.comment = comment;
        }

        public String getUsername() {
        }

            return username;
        public String getComment() {
            return comment;
        }
    }   


    private class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

        private ArrayList<Comments> list;

        public CommentAdapter(ArrayList<Comments> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater=LayoutInflater.from(viewGroup.getContext());
            return new ViewHolder(inflater.inflate(R.layout.comment_adapter,viewGroup,false));
        }

        @Override
        public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder viewHolder, int i) {
            viewHolder.textView.setText(list.get(i).getComment());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder{

            private TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView=itemView.findViewById(R.id.text_comment_adapter);
            }
        }
    }
}
