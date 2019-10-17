package com.capturfri;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private Fragment active;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Home_main home_main=new Home_main();
        final Dashboard_main dashboard_main=new Dashboard_main();
        final AddPost_main addPost_main=new AddPost_main();
        final Chat_main chat_main=new Chat_main();
        final Shop_main shop_main=new Shop_main();


        fragmentManager=getSupportFragmentManager();

        fragmentManager.beginTransaction().add(R.id.container_main,shop_main).hide(shop_main).commit();
        fragmentManager.beginTransaction().add(R.id.container_main,chat_main).hide(chat_main).commit();
        fragmentManager.beginTransaction().add(R.id.container_main,addPost_main).hide(addPost_main).commit();
        fragmentManager.beginTransaction().add(R.id.container_main,dashboard_main).hide(dashboard_main).commit();
        fragmentManager.beginTransaction().add(R.id.container_main,home_main).commit();
        active=home_main;

        BottomNavigationView navigation = findViewById(R.id.navigation_main);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        if(active!=home_main) {
                            fragmentManager.beginTransaction().hide(active).show(home_main).commit();
                        }
                        active=home_main;
                        return true;
                    case R.id.navigation_dashboard:
                        if(active!=dashboard_main) {
                            fragmentManager.beginTransaction().hide(active).show(dashboard_main).commit();
                        }
                        active=dashboard_main;
                        return true;
                    case R.id.navigation_add:
                        if (active!=addPost_main){
                            fragmentManager.beginTransaction().hide(active).show(addPost_main).commit();
                        }
                        active=addPost_main;
                        return true;
                    case R.id.navigation_chat:
                        if(active!=chat_main) {
                            fragmentManager.beginTransaction().hide(active).show(chat_main).commit();
                        }
                        active=chat_main;
                        return true;
                    case R.id.navigation_shop:
                        if(active!=shop_main) {
                            fragmentManager.beginTransaction().hide(active).show(shop_main).commit();
                        }
                        active=shop_main;
                        return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }
}
