package com.example.protosweeperdelta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class SetupActivity extends AppCompatActivity {

    private Button smallGame;

    private Button middleGame;

    private Button largeGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_activity);
        setTitle("Sweeper Options");

        smallGame = findViewById(R.id.small_game);
        middleGame = findViewById(R.id.middle_game);
        largeGame = findViewById(R.id.large_game);
        smallGame.setOnClickListener(v -> startGame(0));
        middleGame.setOnClickListener(v -> startGame(1));
        largeGame.setOnClickListener(v -> startGame(2));

    }

    protected void startGame(int type) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
        finish();
    }
}
