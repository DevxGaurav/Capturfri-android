package com.capturfri;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;

public class PostPreview extends AppCompatActivity {

    private FloatingActionButton ok;
    private FloatingActionButton cancel;
    private ProgressBar progressBar;
    private Manager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_preview);
        manager=Manager.getInstance();

        ImageView image=findViewById(R.id.image_postpreview);
        final VideoView videoView=findViewById(R.id.videoView_postpreview);
        ok=findViewById(R.id.ok_postpreview);
        cancel=findViewById(R.id.cancel_postpreview);
        progressBar=findViewById(R.id.progressBar_postpreview);


        final Uri uri=Uri.parse(getIntent().getExtras().getString("uri"));
        final String type=getIntent().getExtras().getString("type");


        if (type!=null && uri!=null){
            if (type.equals("picture")){
                videoView.setVisibility(View.GONE);
                Bitmap bitmap=null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (bitmap!=null){
                    image.setImageBitmap(bitmap);
                }else {
                    Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }else if (type.equals("video")){
                image.setVisibility(View.GONE);
                videoView.setVideoURI(uri);
                videoView.start();
            }
        }else{
            Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
            finish();
        }


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("picture")){
                    uploadpic(uri);
                }else if (type.equals("video")){
                    uploadvideo(uri);
                }
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });
    }



    private void uploadpic(Uri file){
        progressBar.setVisibility(View.VISIBLE);
        cancel.setClickable(false);
        ok.setClickable(false);
        final StorageReference reference= FirebaseStorage.getInstance().getReference().child("Uploads/Images/"+ manager.getUsername()+" "+ Calendar.getInstance().getTime());
                reference.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        manager.addPost("picture",uri.toString()).addOnCompleteListener(new Manager.OnCompleteListener() {
                            @Override
                            public void OnComplete(boolean task, String result) {
                                progressBar.setVisibility(View.GONE);
                                ok.setClickable(true);
                                cancel.setClickable(true);
                                if (task){
                                    Toast.makeText(PostPreview.this, "Picture uploaded", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else {
                                    reference.delete();
                                    Toast.makeText(PostPreview.this, "upload failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        ok.setClickable(true);
                        cancel.setClickable(true);
                        Toast.makeText(PostPreview.this, "failed to upload picture", Toast.LENGTH_SHORT).show();
                        reference.delete();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                ok.setClickable(true);
                cancel.setClickable(true);
                Toast.makeText(PostPreview.this, "failed to upload picture", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void uploadvideo(Uri file){
        progressBar.setVisibility(View.VISIBLE);
        cancel.setClickable(false);
        ok.setClickable(false);
        final StorageReference reference= FirebaseStorage.getInstance().getReference().child("Uploads/Videos/"+ manager.getUsername()+" "+ Calendar.getInstance().getTime());
        reference.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        manager.addPost("video",uri.toString()).addOnCompleteListener(new Manager.OnCompleteListener() {
                            @Override
                            public void OnComplete(boolean task, String result) {
                                progressBar.setVisibility(View.GONE);
                                ok.setClickable(true);
                                cancel.setClickable(true);
                                if (task){
                                    Toast.makeText(PostPreview.this, "Video uploaded", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else {
                                    reference.delete();
                                    Toast.makeText(PostPreview.this, "upload failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        ok.setClickable(true);
                        cancel.setClickable(true);
                        Toast.makeText(PostPreview.this, "failed to upload video", Toast.LENGTH_SHORT).show();
                        reference.delete();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                ok.setClickable(true);
                cancel.setClickable(true);
                Toast.makeText(PostPreview.this, "failed to upload video", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
