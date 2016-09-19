package com.example.mkluf.hangmanapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Locale;

public class GameActivity extends AppCompatActivity {
    int winScore;
    int loseScore;
    String randomWord;
    Button selectedButton;
    Locale locale;
    ArrayList<String> gameWords;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        if (savedInstanceState != null) {
            locale = (Locale) savedInstanceState.getSerializable("LOCALE");
            if (!locale.equals(getResources().getConfiguration().locale)) {
                ChangeLanguage.changeLanguage(GameActivity.this, locale);
            }
        }
        locale = getResources().getConfiguration().locale;
        fillKeyboard();
        ImageButton quit_game_button = (ImageButton) findViewById(R.id.quit_game_button);
        quit_game_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); //TODO: AlertDialog that confirms the exit.
            }
        });

    }

    public void fillKeyboard() {

        /*I divided the keyboards into two to easily populate the gridviews dynamically
         *  for both landscape and portrait.
         */

        GridView keyboard1 = (GridView) findViewById(R.id.keyboard_container_1);
        GridView keyboard2 = (GridView) findViewById(R.id.keyboard_container_2);
//        keyboard1.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                    return true;
//                }
//                return false;
//            }
//        });
//        keyboard2.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                    return true;
//                }
//                return false;
//            }
//        });
        Button characterButton = null;
        ArrayList<Button> buttonSet1 = new ArrayList<Button>();
        ArrayList<Button> buttonSet2 = new ArrayList<Button>();
        int i = 0;
        boolean isEnglish = locale.getLanguage().equals("en");
        for (char character = 'A'; character <= 'Z'; character++) {
            characterButton = new Button(this);
            characterButton.setText(character + "");
            characterButton.setId(character);
            if (i++ % 8 < 4) {
                buttonSet1.add(characterButton);
            } else buttonSet2.add(characterButton);
        }

        //In this case the locale is norwegian, and we need to add Æ, Ø and Å to the grid.
        if (!isEnglish) {
            char[] norwegianLetters = {'Æ', 'Ø', 'Å'};
            for (char ch : norwegianLetters) {
                Button norButton = new Button(this);
                norButton.setText(ch+"");
                norButton.setId(ch);
                //This test is to add the button to the proper location in the UI
                if (ch != 'Å') buttonSet1.add(norButton);
                else buttonSet2.add(norButton);
            }
        }
        Button confirmButton = new Button(this);
        keyboard1.setAdapter(new ButtonAdapter(buttonSet1));
        keyboard2.setAdapter(new ButtonAdapter(buttonSet2));
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

    public boolean guessLetter(char guessedLetter) {
        return true;
    }
}
