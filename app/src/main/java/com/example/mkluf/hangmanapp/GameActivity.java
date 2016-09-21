package com.example.mkluf.hangmanapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

public class GameActivity extends AppCompatActivity {
    private int winScore;
    private int loseScore;
    private int correctGuesses, wrongGuesses;
    private int round;
    private String currentWord;
    private String[] currentWordParted;
    private Locale locale;
    private String[] gameWords;
    private String[] wordDisplayLetters;
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

        ImageButton quit_game_button = (ImageButton) findViewById(R.id.quit_game_button);
        quit_game_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); //TODO: AlertDialog that confirms the exit.
            }
        });

        fillKeyboard();
        gameWords = getResources().getStringArray(R.array.possible_words);
        Collections.shuffle(Arrays.asList(gameWords));
        round = 0; //Start at first round.
        boolean removeMe = newGame();
    }

    public void fillKeyboard() {

        /*I divided the keyboards into two to easily populate the gridviews dynamically
         *  for both landscape and portrait.
         */

        GridView keyboard1 = (GridView) findViewById(R.id.keyboard_container_1);
        GridView keyboard2 = (GridView) findViewById(R.id.keyboard_container_2);

        keyboard1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(GameActivity.this, "" + i, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
            characterButton.setFocusable(false);
            characterButton.setFocusableInTouchMode(false);
            //This if-statement fills every 4th
            if (i++ % 8 < 4) buttonSet1.add(characterButton);
            else buttonSet2.add(characterButton);
        }

        //In this case the locale is norwegian, and we need to add Æ, Ø and Å to the grid.
        if (!isEnglish) {
            char[] norwegianLetters = {'Æ', 'Ø', 'Å'};
            for (char ch : norwegianLetters) {
                Button norButton = new Button(this);
                norButton.setText(ch+"");
                norButton.setId(ch);
                norButton.setFocusable(false);
                norButton.setFocusableInTouchMode(false);
                //This test is to add the button to the proper location in the UI
                if (ch != 'Å') buttonSet1.add(norButton);
                else buttonSet2.add(norButton);
            }
        }
        keyboard1.setAdapter(new ButtonAdapter(buttonSet1));
        keyboard2.setAdapter(new ButtonAdapter(buttonSet2));
        keyboard1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println(i + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public boolean newGame() {
        if(round < gameWords.length) currentWord = gameWords[round]; //Only have 10 rounds,
        else return false;                                           //initially, but this makes it scaleable
        correctGuesses = 0;
        wrongGuesses = 0;
        currentWordParted = currentWord.split("(?!^)");
        wordDisplayLetters = new String[currentWordParted.length];
        for(int i = 0; i < wordDisplayLetters.length; i++) wordDisplayLetters[i] = " _ "; //Fill the word with _'s
        replaceTextView(wordDisplayLetters);
        return true;
    }
    
    public boolean guessLetter(String selectedLetter) {
        int temp = correctGuesses; //In case there are duplicates
        for(int i = 0; i < currentWordParted.length; i++) {
            if (selectedLetter.equals(currentWordParted[i])) {
                correctGuesses++;
                wordDisplayLetters[i] = selectedLetter;
            }
        }
        if(temp != correctGuesses ) {
            // In this case, there was a new letter found and
            // we update the textview with the new correct letters
            replaceTextView(wordDisplayLetters);
            return true;
        }
        //In this case, we need to add more pieces to the gallow
        wrongGuesses++;
        hangTheManMore();
        return false;
    }

    public void hangTheManMore() {
        ImageView hangedMan = (ImageView) findViewById(R.id.hangman_picture_container);
        //My pictures have a pattern where the file id = "hangman_state_" + int
        int nextPic = getResources()
                .getIdentifier("hangman_state_" + wrongGuesses, "id", getPackageName());
        hangedMan.setImageResource(nextPic);
    }

    public void replaceTextView(String[] letters) {
        TextView textView = (TextView) findViewById(R.id.current_word_textview);
        textView.setText(Arrays.toString(letters)
                .replace("[", "").replace(",", "").replace("]", ""));
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
