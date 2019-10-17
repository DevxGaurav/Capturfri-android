package com.capturfri;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class Home_main extends Fragment {

    private Manager manager;
    private CircleImageView ppic;
    private TextView name;
    private TextInputEditText name_editprofile;
    private TextInputEditText dob_editprofile;
    private TextView gender_editprofile;
    private Switch gender_s_editprofile;
    private ProgressBar progressBar_editprofile;
    private ArrayList<Posts> list;
    private PostAdapter adapter;
    private TextView username;
    private ImageView reloadpost;
    private ProgressBar progressBar_post;
    private JSONArray jsonArray;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home_main,container,false);
        manager = Manager.getInstance();
        ppic=view.findViewById(R.id.ppic_home);
        name=view.findViewById(R.id.name_home);
        ImageView editppic=view.findViewById(R.id.editppic_home);
        ImageView editprofile=view.findViewById(R.id.edit_home);
        ImageView setting=view.findViewById(R.id.setting_home);
        ImageView notification=view.findViewById(R.id.notification_home);
        username=view.findViewById(R.id.username_home);

        ConstraintLayout editprofilesheet=view.findViewById(R.id.editprofilesheet_home);
        final BottomSheetBehavior editprofile_behavior=BottomSheetBehavior.from(editprofilesheet);
        final Button save_editprofile=view.findViewById(R.id.save_edit_home);
        final Button cancel_editprofile=view.findViewById(R.id.cancel_edit_home);
        name_editprofile=view.findViewById(R.id.name_edit_home);
        dob_editprofile=view.findViewById(R.id.dob_edit_home);
        gender_editprofile=view.findViewById(R.id.gender_edit_home);
        gender_s_editprofile=view.findViewById(R.id.switch_edit_home);
        progressBar_editprofile=view.findViewById(R.id.progressBar_editprofile_home);
        RecyclerView recyclerView_post=view.findViewById(R.id.recyclerview_post_home);
        reloadpost=view.findViewById(R.id.refresh_post_home);
        progressBar_post=view.findViewById(R.id.progressBar_post_home);

        recyclerView_post.setLayoutManager(new GridLayoutManager(getContext(),postcols()));
        list=new ArrayList<>();
        adapter=new PostAdapter(list);
        recyclerView_post.setAdapter(adapter);

        editppic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 100);
            }
        });


        editprofile_behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i==BottomSheetBehavior.STATE_COLLAPSED){
                    name_editprofile.setError(null);
                    dob_editprofile.setError(null);
                    name_editprofile.setText(manager.getName());
                    dob_editprofile.setText(manager.getDob());
                    if (manager.getGender().equals("male")){
                        gender_editprofile.setText(getContext().getString(R.string.boy));
                        gender_s_editprofile.setChecked(false);
                    }else {
                        gender_editprofile.setText(getContext().getString(R.string.girl));
                        gender_s_editprofile.setChecked(true);
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });


        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editprofile_behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });


        reloadpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reloadposts();
            }
        });


        save_editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=name_editprofile.getText().toString().trim();
                String dob=dob_editprofile.getText().toString().trim();
                String gender;
                int count=0;

                if (name.equals("")){
                    name_editprofile.setError("Enter name");
                    count++;
                }else if (!name.matches("^[ A-Za-z]+$")){
                    name_editprofile.setError("Invalid name");
                    count++;
                }

                if (dob.equals("")){
                    dob_editprofile.setError("select dob");
                    count++;
                }

                if (gender_s_editprofile.isChecked()){
                    gender="female";
                }else {
                    gender="male";
                }

                if (name.equals(manager.getName()) && dob.equals(manager.getDob()) && gender.equals(manager.getGender())){
                    count++;
                }

                if (count==0){
                    //save profile
                    progressBar_editprofile.setVisibility(View.VISIBLE);
                    save_editprofile.setClickable(false);
                    cancel_editprofile.setClickable(false);
                    manager.setName(name).setDob(dob).setGender(gender).updateprofile().addOnCompleteListener(new Manager.OnCompleteListener() {
                        @Override
                        public void OnComplete(boolean task, String result) {
                            progressBar_editprofile.setVisibility(View.GONE);
                            save_editprofile.setClickable(true);
                            cancel_editprofile.setClickable(true);
                            if (task){
                                editprofile_behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                updatecontent();
                                Toast.makeText(getContext(), "Profile saved", Toast.LENGTH_SHORT).show();
                            }else {
                                
                                ;
                            }
                        }
                    });
                }
            }
        });

        cancel_editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editprofile_behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });


        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        dob_editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dob_editprofile.setText(String.valueOf(year) + "-" + String.valueOf(monthOfYear + 1) + "-" + String.valueOf(dayOfMonth));
                    }
                }, yy, mm, dd);
                datePicker.getDatePicker().setMaxDate(new Date().getTime());
                datePicker.show();
            }
        });


        name_editprofile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                name_editprofile.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        dob_editprofile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dob_editprofile.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        gender_s_editprofile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    gender_editprofile.setText(getContext().getString(R.string.girl));
                }else {
                    gender_editprofile.setText(getContext().getString(R.string.boy));
                }
            }
        });
        updatecontent();
        return view;
    }


    private void updatecontent() {
        if (manager.getProfileImage()!=null){
            ppic.setImageBitmap(manager.getProfileImage());
        }else{
            if (manager.getProfilepicurl().equals("")){
                ppic.setImageDrawable(getContext().getDrawable(R.drawable.dppic));
            }else{
                manager.reloadProfilePicture(new Manager.OnImageBitmapReadyListener() {
                    @Override
                    public void OnBitmapReady(boolean task, Bitmap bitmap) {
                        if (task){
                            ppic.setImageBitmap(bitmap);
                        }else {
                            ppic.setImageDrawable(getContext().getDrawable(R.drawable.dppic));
                        }
                    }
                });
            }
        }
        name_editprofile.setText(manager.getName());
        username.setText("@"+manager.getUsername());
        name.setText(manager.getName());
        dob_editprofile.setText(manager.getDob());
        if (manager.getGender().equals("male")) {
            gender_editprofile.setText(getContext().getString(R.string.boy));
            gender_s_editprofile.setChecked(false);
        } else if (manager.getGender().equals("female")) {
            gender_editprofile.setText(getContext().getString(R.string.girl));
            gender_s_editprofile.setChecked(true);
        }
        reloadposts();
    }


    private void reloadposts(){
        progressBar_post.setVisibility(View.VISIBLE);
        reloadpost.setVisibility(View.GONE);
        manager.getPersonalPost().addOnCompleteListener(new Manager.OnCompleteListener() {
            @Override
            public void OnComplete(boolean task, String result) {
                progressBar_post.setVisibility(View.GONE);
                reloadpost.setVisibility(View.VISIBLE);
                if (task){
                    try {
                        jsonArray=new JSONArray(result);
                        JSONObject object;
                        list.clear();
                        for (int i=jsonArray.length()-1;i>=0;i--){
                            object=jsonArray.getJSONObject(i);
                            list.add(new Posts(object.getString("username"),object.getString("type"),object.getString("url")));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else{
                    Toast.makeText(getContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap!=null) {
                ppic.setImageBitmap(bitmap);
                uploadppic(uri, bitmap);
            }
        }
    }


    private void uploadppic(Uri file, final Bitmap bitmap){
        final StorageReference reference= FirebaseStorage.getInstance().getReference().child("ProfilePictures/"+ manager.getUsername());
        reference.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        manager.setProfilepicurl(uri.toString()).updateprofile().addOnCompleteListener(new Manager.OnCompleteListener() {
                            @Override
                            public void OnComplete(boolean task, String result) {
                                if (task){
                                    manager.saveProfileImage(bitmap);
                                    Toast.makeText(getContext(), "Picture uploaded", Toast.LENGTH_SHORT).show();
                                }else {
                                    reference.delete();
                                    manager.setProfilepicurl("");
                                    Toast.makeText(getContext(), "upload failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "failed to upload picture", Toast.LENGTH_SHORT).show();
                        ppic.setImageDrawable(getContext().getDrawable(R.drawable.dppic));
                        reference.delete();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "failed to upload picture", Toast.LENGTH_SHORT).show();
                ppic.setImageDrawable(getContext().getDrawable(R.drawable.dppic));
            }
        });
    }



    private int postcols() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 110);
    }



    private class Posts{

        private String username;
        private String type;
        private String url;

        public Posts(String username, String type, String url) {
            this.username = username;
            this.type = type;
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }


    private class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

        
        

        public PostAdapter(ArrayList<Posts> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_adapter_home,viewGroup,false));
        }

        @Override
        public void onBindViewHolder(@NonNull final PostAdapter.ViewHolder viewHolder, final int i) {
            if (list.get(i).getType().equals("picture")){
                viewHolder.videoView.setVisibility(View.GONE);
                Glide.with(ppic).load(list.get(i).getUrl()).into(viewHolder.imageView);
            }else if (list.get(i).getType().equals("video")){
                viewHolder.imageView.setVisibility(View.GONE);
                viewHolder.videoView.setVideoURI(Uri.parse(list.get(i).getUrl()));
                viewHolder.videoView.start();
                viewHolder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.setLooping(true);
                    }
                });
            }


            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle=new Bundle();
                    try {
                        bundle.putString("json",jsonArray.getJSONObject(list.size()-i-1).toString());
                        startActivityForResult(new Intent(getContext(),OpenPrivatePost.class).putExtras(bundle),10);
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "failed to open post", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            viewHolder.videoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Bundle bundle=new Bundle();
                    try {
                        bundle.putString("json",jsonArray.getJSONObject(list.size()-i-1).toString());
                        startActivityForResult(new Intent(getContext(),OpenPrivatePost.class).putExtras(bundle),10);
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "failed to open post", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });

            viewHolder.videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    mediaPlayer.setLooping(false);
                    viewHolder.videoView.stopPlayback();
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }



        public class ViewHolder extends RecyclerView.ViewHolder{

            private ImageView imageView;
            private VideoView videoView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView=itemView.findViewById(R.id.imageView_postadapter_home);
                videoView=itemView.findViewById(R.id.videoView_postadapter_home);
            }
        }
    }
}
