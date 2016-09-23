package com.example.mkluf.hangmanapp;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    private int winScore;
    private int loseScore;
    private int correctGuesses, wrongGuesses;
    private int round;
    private int maxRounds;
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
                new AlertDialog.Builder(GameActivity.this)
                        .setTitle(getResources().getString(R.string.close_activity))
                        .setMessage(getResources().getString(R.string.are_you_sure))
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), null)
                        .setIcon(android.R.drawable.ic_dialog_alert).show();
            }
        });
        gameWords = getResources().getStringArray(R.array.possible_words);
        Collections.shuffle(Arrays.asList(gameWords));
        round = 0; //Start at first round.
        maxRounds = gameWords.length;
        newGame();
    }

    public void fillKeyboard() {

        /*I divided the keyboards into two to easily populate the gridviews dynamically
         *  for both landscape and portrait.
         */

        GridView keyboard1 = (GridView) findViewById(R.id.keyboard_container_1);
        GridView keyboard2 = (GridView) findViewById(R.id.keyboard_container_2);

        Button characterButton = null;
        ArrayList<Button> buttonSet1 = new ArrayList<Button>();
        ArrayList<Button> buttonSet2 = new ArrayList<Button>();
        int i = 0;
        boolean isEnglish = locale.getLanguage().equals("en");
        for (char character = 'A'; character <= 'Z'; character++) {
            characterButton = new Button(this);
            characterButton.setText(character + "");
            characterButton.setId(character);
            characterButton.setOnClickListener(this);
            characterButton.setFocusable(false);
            characterButton.setFocusableInTouchMode(false);
            //This if-statement fills every 4 buttons into the respective list
            if (i++ % 8 < 4) buttonSet1.add(characterButton);
            else buttonSet2.add(characterButton);
        }

        //In this case the locale is norwegian, and we need to add Æ, Ø and Å to the grid.
        if (!isEnglish) {
            char[] norwegianLetters = {'Æ', 'Ø', 'Å'};
            for (char ch : norwegianLetters) {
                Button norButton = new Button(this);
                norButton.setText(ch + "");
                norButton.setId(ch);
                norButton.setFocusable(false);
                norButton.setFocusableInTouchMode(false);
                norButton.setOnClickListener(this);
                //This test is to add the button to the proper location in the UI
                if (ch != 'Å') buttonSet1.add(norButton);
                else buttonSet2.add(norButton);
            }
        }
        keyboard1.setAdapter(new ButtonAdapter(buttonSet1));
        keyboard2.setAdapter(new ButtonAdapter(buttonSet2));
    }

    public void newGame() {
        //Only have 10 rounds in this example, but this makes the game scaleable
        fillKeyboard();
        ImageView hangedManReset = (ImageView) findViewById(R.id.hangman_picture_container);
        hangedManReset.setImageResource(0);
        currentWord = gameWords[round];
        correctGuesses = 0;
        wrongGuesses = 0;
        currentWordParted = currentWord.split("(?!^)");
        wordDisplayLetters = new String[currentWordParted.length];
        for (int i = 0; i < wordDisplayLetters.length; i++)
            wordDisplayLetters[i] = "_"; //Fill the word with _'s
        replaceTextView(wordDisplayLetters);
    }

    public boolean guessLetter(Button selectedButton) {
        char guess = selectedButton.getText().toString().charAt(0); // Converts String to char
        guess = Character.toLowerCase(guess);
        selectedButton.setOnClickListener(null); //Makes the already used letter un clickable
        int temp = correctGuesses; //In case there are duplicates
        for (int i = 0; i < currentWordParted.length; i++) {
            if (guess == currentWordParted[i].charAt(0)) {
                correctGuesses++;
                wordDisplayLetters[i] = guess + "";
            }
        }
        if (temp != correctGuesses) {
            // In this case, there was a new letter found and
            // we update the textview with the new correct letters
            selectedButton.getBackground().setColorFilter(0xFF669900, PorterDuff.Mode.MULTIPLY);
            replaceTextView(wordDisplayLetters);
            if (correctGuesses == currentWordParted.length) win();
            return true;
        }
        //If the letter is wrong, the background color of the button is changed to red
        //and another part gets added to the hangman.
        selectedButton.getBackground().setColorFilter(0xFFF44366, PorterDuff.Mode.MULTIPLY);
        wrongGuesses++;
        hangTheManMore();
        if (wrongGuesses == currentWordParted.length) lose();
        return false;
    }

    private void lose() {
    }

    public void win() {
        round++;
        if (round < maxRounds) {
            toaster(getResources().getString(R.string.guessed_correct_message));
            winScore++;
            TextView textView = (TextView) findViewById(R.id.win_score_counter);
            textView.setText(winScore+"");
            newGame();
        } else { //Then the player have beaten the game
        }
    }

    public void hangTheManMore() {
        ImageView hangedMan = (ImageView) findViewById(R.id.hangman_picture_container);
        //My pictures have a pattern where the file id = "hangman_state_" + int
        int nextPic = getResources()
                .getIdentifier("hangman_state_" + wrongGuesses, "drawable", getPackageName());
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
    public void onClick(View view) {
        Button selectedButton = (Button) view;
        guessLetter(selectedButton);
    }

    //Displays a short toast in the middle of the screen.
    public void toaster(String message) {
        Toast toast = Toast.makeText(GameActivity.this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
