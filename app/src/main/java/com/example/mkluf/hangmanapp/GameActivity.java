package com.example.mkluf.hangmanapp;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
//import android.widget.Toast;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    private int winScore, loseScore, correctGuesses, wrongGuesses, round, maxRounds, maxWrongGuesses;
    private String currentWord;
    private String[] currentWordParted, gameWords, wordDisplayLetters;
    private Locale locale;
    private ArrayList<Character> correctGuessedLetters, wrongGuessedLetters;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ImageButton quit_game_button = (ImageButton) findViewById(R.id.quit_game_button);
        ImageButton help_button = (ImageButton) findViewById(R.id.help_button);
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
        help_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helpDialogBuilder();
            }
        });
        locale = getResources().getConfiguration().locale;
        gameWords = getResources().getStringArray(R.array.possible_words);
        Collections.shuffle(Arrays.asList(gameWords));
        maxWrongGuesses = 8;
        round = 0; //Start at first round.
        maxRounds = gameWords.length;
        maxRounds = 2; //TODO: Remove this test condition upon delivery
        newRound();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        locale = (Locale) savedInstanceState.getSerializable("LOCALE");
        if (!locale.equals(getResources().getConfiguration().locale)) {
            ChangeLanguage.changeLanguage(GameActivity.this, locale);
        }
        locale = getResources().getConfiguration().locale;
        gameWords = (String[]) savedInstanceState.getSerializable("GAMEWORDS");
        round = (int) savedInstanceState.getSerializable("ROUND");
        maxRounds = (int) savedInstanceState.getSerializable("MAXROUNDS");
        correctGuesses = (int) savedInstanceState.getSerializable("CORRECTGUESSES");
        wrongGuesses = (int) savedInstanceState.getSerializable("WRONGGUESSES");
        currentWord = (String) savedInstanceState.getSerializable("CURRENTWORD");
        currentWordParted = (String[]) savedInstanceState.getSerializable("CURRENTWORDPARTED");
        wordDisplayLetters = (String[]) savedInstanceState.getSerializable("WORDDISPLAYLETTERS");
        winScore = (int) savedInstanceState.getSerializable("WINSCORE");
        loseScore = (int) savedInstanceState.getSerializable("LOSESCORE");
        correctGuessedLetters = (ArrayList<Character>) savedInstanceState.getSerializable("CORRECTGUESSEDLETTERS");
        wrongGuessedLetters = (ArrayList<Character>) savedInstanceState.getSerializable("WRONGGUESSEDLETTERS");
        System.out.println(correctGuessedLetters);
        System.out.println(wrongGuessedLetters);
        hangTheManMore(); //Sets the correct state automatically depending on amount of Wrong guesses
        fillKeyboard();
        replaceArraySymbolsInTextView(wordDisplayLetters);
        changeATextView(R.id.win_score_counter, winScore + "");
        changeATextView(R.id.lost_score_counter, loseScore + "");
    }
    /*
     * This function fills the two grid views with buttons which functions as a physical keyboard
     * I made two grid views because I wished to be able to dynamically change the layout
     * when the orientation is changed. This is apparent in landscape mode
     */

    public void fillKeyboard() {
        GridView keyboard1 = (GridView) findViewById(R.id.keyboard_container_1);
        GridView keyboard2 = (GridView) findViewById(R.id.keyboard_container_2);

        Button characterButton;
        ArrayList<Button> buttonSet1 = new ArrayList<>();
        ArrayList<Button> buttonSet2 = new ArrayList<>();
        ArrayList<Character> characters = new ArrayList<>();
        int i = 0;
        boolean isEnglish = locale.getLanguage().equals("en");
        for (char character = 'A'; character <= 'Z'; character++) characters.add(character);
        //In case the chosen locale is Norwegian
        if (!isEnglish) {
            characters.add('Æ');
            characters.add('Ø');
            characters.add('Å');
        }
        //Looping through the whole ArrayList, 28 letters if the locale is English, 31 otherwise
        for (char character : characters) {
            characterButton = new Button(this);
            characterButton.setText(Character.toString(character));
            characterButton.setId(character);
            characterButton.setOnClickListener(this);
            characterButton.setFocusable(false);
            characterButton.setFocusableInTouchMode(false);
            //These statements are intended for when the activity is resumed.
            if (wrongGuessedLetters.contains(character))
                changeButtonColor(characterButton, 0xFFF44399);
            else if (correctGuessedLetters.contains(character))
                changeButtonColor(characterButton, 0xFF669900);
            //This if-statement fills every 4 buttons into the respective list
            if (i++ % 8 < 4) buttonSet1.add(characterButton);
            else buttonSet2.add(characterButton);
        }

        keyboard1.setAdapter(new ButtonAdapter(buttonSet1));
        keyboard2.setAdapter(new ButtonAdapter(buttonSet2));
    }

    //This function starts a new round when it's called, and the values are scale able
    public void newRound() {
        correctGuessedLetters = new ArrayList<>();
        wrongGuessedLetters = new ArrayList<>();
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
        replaceArraySymbolsInTextView(wordDisplayLetters);
    }

    //This function is called when a button is pressed and the game is changed to reflect this
    public boolean guessLetter(Button selectedButton) {
        char guess = selectedButton.getText().toString().charAt(0); // Converts String to char
        selectedButton.setOnClickListener(null); //Makes the already used letter un clickable
        int temp = correctGuesses; //In case there are duplicates
        for (int i = 0; i < currentWordParted.length; i++)
            if (Character.toLowerCase(guess) == currentWordParted[i].charAt(0)) {
                correctGuesses++;
                wordDisplayLetters[i] = guess + "";
            }
        if (temp != correctGuesses) {
            // In this case, there was a new letter found and
            // we update the textview with the new correct letters
            correctGuessedLetters.add(guess);
            changeButtonColor(selectedButton, 0xFF669900);
            replaceArraySymbolsInTextView(wordDisplayLetters);
            if (correctGuesses == currentWordParted.length) win();
            return true;
        }
        //If the letter is wrong, the background color of the button is changed to red
        //and another part gets added to the hangman.
        changeButtonColor(selectedButton, 0xFFF44366);
        wrongGuessedLetters.add(guess);
        wrongGuesses++;
        hangTheManMore();
        if (wrongGuesses == maxWrongGuesses) lose();
        return false;
    }

    //This function is called when a round is lost
    public void lose() {
        round++;
        loseScore++;
        hangTheManMore();
        changeATextView(R.id.lost_score_counter, loseScore + "");
        if (round < maxRounds) nextGameDialog(false);
        else ranOutOfWordsDialog(); //Then it was the last round
    }

    //This function is called when a round is won
    public void win() {
        round++;
        winScore++;
        changeATextView(R.id.win_score_counter, winScore + "");
        if (round < maxRounds) nextGameDialog(true);
        else ranOutOfWordsDialog(); //Then the player have beaten the game
    }

    //Streamlined the Dialog that pops up whenever a round is won/lost
    public void nextGameDialog(boolean guessedCorrect) {
        int stringID = guessedCorrect ?
                R.string.guessed_correct_message : R.string.guessed_wrong_message;
        AlertDialog dialog = new AlertDialog.Builder(GameActivity.this)
                .setMessage(getResources().getString(stringID) + " " + currentWord)
                .setNeutralButton(getResources().getString(R.string.next_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                newRound();
                            }
                        })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        newRound();
                    }
                }).create();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        dialog.show();
    }

    //For when we've iterated through all the available words
    public void ranOutOfWordsDialog() {
        new AlertDialog.Builder(GameActivity.this)
                .setTitle(getResources().getString(R.string.beat_the_game_title) + " " + currentWord)
                .setMessage(getResources().getString(R.string.you_beat_the_game_message)
                        + " " + winScore + " - " + loseScore + getResources().getString(R.string.play_again_message))
                .setPositiveButton(getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                resetGame();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        resetGame();
                    }
                }).show();
    }

    public void helpDialogBuilder() {
        AlertDialog dialog = new AlertDialog.Builder(GameActivity.this)
                .setTitle(getResources().getString(R.string.rules_dialog_title))
                .setIcon(getResources().getDrawable(R.drawable.rule_book_icon))
                .setMessage(getResources().getString(R.string.rules_dialog_line1)
                        + " " + maxWrongGuesses + " " + getResources().getString(R.string.rules_dialog_line2)
                        + " " + maxRounds + " " + getResources().getString(R.string.rules_dialog_line3))
                .setNeutralButton(getResources().getString(R.string.thank_you_reply), null)
                .create();
        dialog.show();
        //Window window = dialog.getWindow();
    }

    //Adds another piece to the man, while in reality just changing the picture
    public void hangTheManMore() {
        ImageView hangedMan = (ImageView) findViewById(R.id.hangman_picture_container);
        //My pictures have a pattern where the file id = "hangman_state_" + int
        int nextPic = getResources()
                .getIdentifier("hangman_state_" + wrongGuesses, "drawable", getPackageName());
        hangedMan.setImageResource(nextPic);
    }

    //Flushes all the stats
    public void resetGame() {
        round = 0;
        winScore = 0;
        changeATextView(R.id.win_score_counter, winScore + "");
        loseScore = 0;
        changeATextView(R.id.lost_score_counter, loseScore + "");
        newRound();
    }

    //Replaces the [ , ] symbols in an array and makes it readable
    public void replaceArraySymbolsInTextView(String[] letters) {
        String displayWord = Arrays.toString(letters).replace("[", "").replace(",", "").replace("]", "");
        changeATextView(R.id.current_word_textview, displayWord);
    }

    //Streamlined function to reduce duplicate code
    public void changeATextView(int iDofView, String newText) {
        TextView textView = (TextView) findViewById(iDofView);
        textView.setText(newText);
    }

    //Changes the color of the button with the color sent as a parameter
    public void changeButtonColor(Button button, int color) {
        button.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        button.setOnClickListener(null);
    }

    //Save all the data for instances where the activity is reset
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("CORRECTGUESSEDLETTERS", correctGuessedLetters);
        outState.putSerializable("WRONGGUESSEDLETTERS", wrongGuessedLetters);
        outState.putSerializable("WORDDISPLAYLETTERS", wordDisplayLetters);
        outState.putSerializable("ROUND", round);
        outState.putSerializable("MAXROUNDS", maxRounds);
        outState.putSerializable("WRONGGUESSES", wrongGuesses);
        outState.putSerializable("CORRECTGUESSES", correctGuesses);
        outState.putSerializable("CURRENTWORD", currentWord);
        outState.putSerializable("CURRENTWORDPARTED", currentWordParted);
        outState.putSerializable("GAMEWORDS", gameWords);
        outState.putSerializable("LOSESCORE", loseScore);
        outState.putSerializable("WINSCORE", winScore);
        outState.putSerializable("LOCALE", locale);
        super.onSaveInstanceState(outState);
    }

    //Need a onClick method to override the default behavior, and I've set this to guess letter
    @Override
    public void onClick(View view) {
        Button selectedButton = (Button) view;
        guessLetter(selectedButton);
    }

//    //Displays a short toast in the middle of the screen.
//    public void toaster(String message) {
//        Toast toast = Toast.makeText(GameActivity.this, message, Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.show();
//    }
}
