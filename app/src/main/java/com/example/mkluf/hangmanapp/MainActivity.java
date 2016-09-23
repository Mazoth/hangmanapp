package com.example.mkluf.hangmanapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Buttons
        Button startGameButton = (Button) findViewById(R.id.play_button);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, GameActivity.class));
            }
        });
        Button readRulesButton = (Button) findViewById(R.id.rules_button);
        readRulesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RulesActivity.class));
            }
        });
        final Button changeLocaleButton = (Button) findViewById(R.id.change_language_button);
        changeLocaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Locale locale;
                if (getResources().getConfiguration()
                        .locale.getLanguage().equals("en")) locale = new Locale("nb");
                else locale = new Locale("en");
                ChangeLanguage.changeLanguage(MainActivity.this, locale);
            }
        });
        final Button quitButton = (Button) findViewById(R.id.quit_game_button);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });
    }
}
