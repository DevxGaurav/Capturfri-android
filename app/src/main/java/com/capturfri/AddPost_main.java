package com.capturfri;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static android.app.Activity.RESULT_OK;

public class AddPost_main extends Fragment {

    private TextView picture;
    private TextView video;
    private TextView golive;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_addpost_main,container,false);
        picture=view.findViewById(R.id.picture_addpost);
        video=view.findViewById(R.id.video_addpost);
        golive=view.findViewById(R.id.golive_addpost);
        ConstraintLayout sheet=view.findViewById(R.id.sheet_addpost);
        final BottomSheetBehavior behavior=BottomSheetBehavior.from(sheet);
        final TextView golive2=view.findViewById(R.id.golive2_addpost);
        ImageView close=view.findViewById(R.id.close_addpost);

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 100);
            }
        });


        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Video"),101);

            }
        });


        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i==BottomSheetBehavior.STATE_COLLAPSED){
                    picture.setClickable(true);
                    video.setClickable(true);
                    golive.setClickable(true);
                }else if (i==BottomSheetBehavior.STATE_EXPANDED){
                    picture.setClickable(false);
                    video.setClickable(false);
                    golive.setClickable(false);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        golive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });


        golive2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //golive
            }
        });
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            Bundle bundle=new Bundle();
            bundle.putString("uri",uri.toString());
            bundle.putString("type","picture");
            startActivityForResult(new Intent(getContext(),PostPreview.class).putExtras(bundle),103);
        }

        if (requestCode == 101 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            Bundle bundle=new Bundle();
            bundle.putString("uri",uri.toString());
            bundle.putString("type","video");
            startActivityForResult(new Intent(getContext(),PostPreview.class).putExtras(bundle),103);
        }
    }
}
