package com.example.mkluf.hangmanapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class GameActivity extends AppCompatActivity {
    int winScore;
    int loseScore;
    String randomWord;
    Locale locale;
    ArrayList<String> gameWords;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        if(savedInstanceState != null) {
            locale = (Locale) savedInstanceState.getSerializable("LOCALE");
            if (!locale.equals(getResources().getConfiguration().locale)) {
                ChangeLanguage.changeLanguage(GameActivity.this, locale);
            }
        }
        locale = getResources().getConfiguration().locale;
    }

    public void fillKeyboard() {
        System.out.println(getResources().getConfiguration().locale.getLanguage());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("LOCALE", locale);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("er på pausa");
        System.out.println(getResources().getConfiguration().locale.getLanguage());
    }
}
