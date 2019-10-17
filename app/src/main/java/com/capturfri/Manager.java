package com.capturfri;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

class Manager {
    private static Manager instance;
    private Context context;
    private boolean task=false;
    private String result="";
    private String baseurl="http://backend-capturfri.7e14.starter-us-west-2.openshiftapps.com";
    private OnCompleteListener listener;
    private OnImageBitmapReadyListener bitmapReadyListener;

    static Manager getInstance() {
        if (instance==null){
            synchronized (Manager.class){
                if (instance==null){
                    instance=new Manager();
                }
            }
        }
        return instance;
    }

    private Manager() {
    }


    public Manager init(Context ctx){
        if (context==null){
            this.context=ctx;
        }
        return instance;
    }




    public interface OnCompleteListener{
        void OnComplete(boolean task, String result);
    }

    public interface OnImageBitmapReadyListener{
        void OnBitmapReady(boolean task, Bitmap bitmap);
    }

    public void addOnCompleteListener(OnCompleteListener listener){
        this.listener=listener;
    }


    public String getUsername() {
        return context.getSharedPreferences("login", Context.MODE_PRIVATE).getString("username", "");
    }

    public Manager setUsername(String username) {
        context.getSharedPreferences("login", Context.MODE_PRIVATE).edit().putString("username", username).commit();
        return instance;
    }

    public String getPassword() {
        return context.getSharedPreferences("login", Context.MODE_PRIVATE).getString("password", "");
    }

    public Manager setPassword(String password) {
        context.getSharedPreferences("login", Context.MODE_PRIVATE).edit().putString("password", password).commit();
        return instance;
    }

    public String getInstanceid() {
        return context.getSharedPreferences("profile",Context.MODE_PRIVATE).getString("instanceid","");
    }

    public Manager setInstanceid(String instanceid) {
        context.getSharedPreferences("profile", Context.MODE_PRIVATE).edit().putString("instanceid", instanceid).commit();
        return instance;
    }


    public boolean isVerified() {
        return context.getSharedPreferences("profile", Context.MODE_PRIVATE).getBoolean("validity", false);
    }

    public Manager setVerified(boolean isverified) {
        context.getSharedPreferences("profile", Context.MODE_PRIVATE).edit().putBoolean("validity", isverified).commit();
        return instance;
    }

    public String getName() {
        return context.getSharedPreferences("profile", Context.MODE_PRIVATE).getString("name", "");
    }

    public Manager setName(String name) {
        context.getSharedPreferences("profile", Context.MODE_PRIVATE).edit().putString("name", name).commit();
        return instance;
    }


    public String getDob() {
        return context.getSharedPreferences("profile", Context.MODE_PRIVATE).getString("dob", "");
    }

    public Manager setDob(String dob) {
        context.getSharedPreferences("profile", Context.MODE_PRIVATE).edit().putString("dob", dob).commit();
        return instance;
    }

    public String getGender() {
        return context.getSharedPreferences("profile", Context.MODE_PRIVATE).getString("gender", "");
    }

    public Manager setGender(String gender) {
        context.getSharedPreferences("profile", Context.MODE_PRIVATE).edit().putString("gender", gender).commit();
        return instance;
    }

    public String getProfilepicurl() {
        return context.getSharedPreferences("profile", Context.MODE_PRIVATE).getString("ppicurl", "");
    }

    public Manager setProfilepicurl(String profilepicurl) {
        context.getSharedPreferences("profile", Context.MODE_PRIVATE).edit().putString("ppicurl", profilepicurl).commit();
        return instance;
    }


    public Bitmap getProfileImage(){
        Bitmap bitmap=null;
        try {
            String image=   ;
            if (image.equals("")){
                bitmap=null;
            }else {
                byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public Manager reloadProfilePicture(OnImageBitmapReadyListener listener) {
        this.bitmapReadyListener=listener;
        new Manager.ReloadProfilePic().execute();
        return instance;
    }

    public Manager saveProfileImage(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        context.getSharedPreferences("profile",Context.MODE_PRIVATE).edit().putString("image",temp).commit();
        return instance;
    }





    public Manager login(String username, String password){
        new Manager.Login(username,password).execute();
        return instance;
    }

    public Manager signup(String username, String password){
        new Manager.Signup(username,password).execute();
        return instance;
    }

    public Manager updateprofile(){
        new Manager.SaveProfile().execute();
        return instance;
    }

    public Manager reloadprofile(){
        new Manager.Login(getUsername(),getPassword()).execute();
        return instance;
    }

    public Manager addPost(String type, String url){
        new Manager.AddPost(type,url).execute();
        return instance;
    }

    public Manager getPersonalPost(){
        new Manager.PersonalPosts().execute();
        return instance;
    }


    private class Login extends AsyncTask<Void,Void,String> {

        private String username;
        private String password;

        public Login(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(baseurl+"/login");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST"); 
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String urlpath = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
                        + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8")
                        + "&" + URLEncoder.encode("instanceid", "UTF-8") + "=" + URLEncoder.encode(getInstanceid(), "UTF-8");
                bufferedWriter.write(urlpath);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));
                String line;
                String result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                inputStream.close();
                bufferedReader.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            if (s==null){
                task=false;
                result="Connection error";
            }else if (s.equals("-3")){
                task=false;
                result="Error while connecting to server";
            }else if (s.equals("-1")){
                task=false;
                result="User does not exist";
            }else if (s.equals("0")){
                task=false;
                result="Incorrect password";
            }else {
                setUsername(username).setPassword(password);
                task=true;
                result="successfully logined";

                try {
                    String prevppicurl=getProfilepicurl();
                    JSONObject data=new JSONObject(s);
                    setVerified(data.getBoolean("verified"));
                    JSONObject ob=data.getJSONObject("profile");
                    setName(ob.getString("name"))
                            .setDob(ob.getString("dob"))
                            .setGender(ob.getString("gender"))
                            .setProfilepicurl(ob.getString("ppicurl"));
                    if (!getProfilepicurl().equals("") && !ob.getString("ppicurl").equals(prevppicurl)){
                        reloadProfilePicture(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (listener!=null){
                listener.OnComplete(task,result);
            }
        }
    }



    private class Signup extends AsyncTask<Void,Void,String>{

        private String username;
        private String password;


        public Signup(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(baseurl+"/signup");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String urlpath = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
                        + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                bufferedWriter.write(urlpath);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));
                String line;
                String result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                inputStream.close();
                bufferedReader.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s==null){
                task=false;
                result="Connection error";
            }else if (s.equals("-3")){
                task=false;
                result="Connection error";
            }else if (s.equals("1")){
                setUsername(username).setPassword(password);
                task=true;
                result="Account created";
            }else if (s.equals("-1")){
                task=false;
                result="Username already taken";
            }

            if (listener!=null){
                listener.OnComplete(task,result);
            }
        }
    }




    private class SaveProfile extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(baseurl+"/updateprofile");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String urlpath = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(getUsername(), "UTF-8")
                        + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(getPassword(), "UTF-8")
                        + "&" + URLEncoder.encode("gender",     "UTF-8") + "=" + URLEncoder.encode(getGender(), "UTF-8")
                        + "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(getName(), "UTF-8")
                        + "&" + URLEncoder.encode("dob", "UTF-8") + "=" + URLEncoder.encode(getDob(), "UTF-8")
                        + "&" + URLEncoder.encode("ppicurl", "UTF-8") + "=" + URLEncoder.encode(getProfilepicurl(), "UTF-8");
                bufferedWriter.write(urlpath);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));
                String line;
                String result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                inputStream.close();
                bufferedReader.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s==null){
                task=false;
                result="Connection error";
            }else if (s.equals("-3")){
                task=false;
                result="Connection error";
            }else if (s.equals("-1") || s.equals("0")){
                task=false;
                result="Authentication failed";
            }else if (s.equals("1")){
                task=true;
                result="Profile saved";
            }

            if (listener!=null){
                listener.OnComplete(task,result);
            }
        }
    }



    private class ReloadProfilePic extends AsyncTask<Void,Void,Void>{

        private Bitmap bitmap;
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                bitmap= Glide.with(context).asBitmap().load(getProfilepicurl()).into(500,500).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (bitmap!=null){
                saveProfileImage(bitmap);
                task=true;
                result="Done";
            }else {
                task=false;
                result="not Done";
            }

            if (bitmapReadyListener!=null){
                bitmapReadyListener.OnBitmapReady(task,bitmap);
            }
        }
    }





    private class AddPost extends AsyncTask<Void,Void,String> {

        private String type;
        private String assurl;

        public AddPost(String type, String assurl) {
            this.type = type;
            this.assurl = assurl;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(baseurl+"/addpost");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String urlpath = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(getUsername(), "UTF-8")
                        + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(getPassword(), "UTF-8")
                        + "&" + URLEncoder.encode("type",     "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8")
                        + "&" + URLEncoder.encode("url", "UTF-8") + "=" + URLEncoder.encode(assurl, "UTF-8");
                bufferedWriter.write(urlpath);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));
                String line;
                String result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                inputStream.close();
                bufferedReader.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s==null){
                task=false;
                result="Connection error";
            }else if (s.equals("-3")){
                task=false;
                result="Connection error";
            }else if (s.equals("-1") || s.equals("0")){
                task=false;
                result="Authentication failed";
            }else if (s.equals("1")){
                task=true;
                result="Post added";
            }

            if (listener!=null){
                listener.OnComplete(task,result);
            }
        }
    }


    private class PersonalPosts extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(baseurl+"/getpersonalpost");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String urlpath = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(getUsername(), "UTF-8");
                bufferedWriter.write(urlpath);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));
                String line;
                String result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                inputStream.close();
                bufferedReader.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            if (s==null){
                task=false;
                result="Connection error";
            }else if (s.equals("-3")){
                task=false;
                result="Failed to connect to server";
            }else {
                task=true;
                result=s;
            }


            if (listener!=null){
                listener.OnComplete(task,result);
            }
        }
    }
}
