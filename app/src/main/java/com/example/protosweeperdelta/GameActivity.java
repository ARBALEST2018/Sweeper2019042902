package com.example.protosweeperdelta;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    //private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    //private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    //private static final int UI_ANIMATION_DELAY = 300;
    //private final Handler mHideHandler = new Handler();
    //private View mContentView;

    private TableLayout board;
    private int width;
    private int height;
    private int number;
    private boolean[][] detected;
    private boolean[][] mines;
    private boolean[][] locked;
    private int[][] around;
    private TextView[][] cells;
    private Button newGameButton;
    private Button backButton;
    private Button flagButton;
    private boolean toMark;
    private int flagsLeft;
    private TextView flagsLeftLabel;
    int detect = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game_activity);
        Intent intent = getIntent();
        int type = intent.getIntExtra("type", 0);
        if (type == 0) {
            width = 6;
            height = 6;
            number = 10;
        } else if (type == 1) {
            width = 9;
            height = 9;
            number = 15;
        } else if (type == 2) {
            width = 12;
            height = 12;
            number = 20;
        }
        board = findViewById(R.id.board_container);
        newGameButton = findViewById(R.id.new_game);
        backButton = findViewById(R.id.back_to_setup);
        flagButton = findViewById(R.id.to_mark);
        flagsLeftLabel = findViewById(R.id.mines_left);
        newGameButton.setOnClickListener(v -> setupBoard());
        backButton.setOnClickListener(v -> {
            Intent setupIntent = new Intent(this, SetupActivity.class);
            startActivity(setupIntent);
            finish();
        });
        setupBoard();
        finish();




    }

    protected void setupBoard() {
        findViewById(R.id.failure).setVisibility(View.GONE);
        findViewById(R.id.victory).setVisibility(View.GONE);
        flagButton.setTextColor(Color.BLACK);
        flagButton.setEnabled(true);
        board.removeAllViews();
        detected = new boolean[width][height];
        mines = new boolean[width][height];
        locked = new boolean[width][height];
        around = new int[width][height];
        cells = new TextView[width][height];
        flagsLeft = number;
        flagsLeftLabel.setText(flagsLeft);
        flagButton.setOnClickListener(v -> {
            toMark = true;
            flagButton.setTextColor(Color.RED);
        });
        for (int i = 0; i < width; i++) {
            TableRow row = new TableRow(this);
            row.setId(i);
            row.setGravity(Gravity.CENTER);
            for (int j = 0; j < height; j++) {
                detected[i][j] = false;
                mines[i][j] = false;
                locked[i][j] = false;
                TextView cell = new TextView(this);
                cell.setId(j);
                cell.setText("");
                cell.setTextColor(Color.BLACK);
                cell.setBackgroundColor(Color.WHITE);
                cell.setBackground(getDrawable(R.drawable.square));
                cell.setGravity(Gravity.CENTER);
                cell.setWidth(board.getWidth() / width);
                cell.setHeight(board.getHeight() / height);
                cell.setOnClickListener(v -> {
                    cellClicked(row.getId(), cell.getId());
                });
                row.addView(cell);
                cells[i][j] = cell;

            }
            board.addView(row);
        }
        for (int k = 0; k < number; k++) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            if (mines[x][y]) {
                k--;
            } else {
                mines[x][y] = true;
            }
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int count = 0;
                for (int k = 0; k < 9; k++) {
                    if (k != 4) {
                        int x = k / 3 - 1;
                        int y = k % 3 - 1;
                        if (getMine(i + x, j + y)) {
                            count++;
                        }
                    }
                }
                around[i][j] = count;
            }
        }
    }

    private void cellClicked(int x, int y) {
        if (!detected[x][y]) {
            if (toMark) {
                locked[x][y] = !locked[x][y];
                if (locked[x][y]) {
                    cells[x][y].setBackground(getDrawable(R.drawable.flag_icon));
                    flagsLeft--;
                    flagsLeftLabel.setText(flagsLeft);
                    if (flagsLeft < 0) {
                        flagsLeftLabel.setTextColor(Color.RED);
                    }
                } else {
                    cells[x][y].setBackground(getDrawable(R.drawable.square));
                    flagsLeft++;
                    flagsLeftLabel.setText(flagsLeft);
                    if (flagsLeft >= 0) {
                        flagsLeftLabel.setTextColor(Color.BLACK);
                    }
                }
                toMark = false;
                flagButton.setTextColor(Color.BLACK);
            } else {
                if (!locked[x][y]) {
                    if (mines[x][y]) {
                        cells[x][y].setBackground(getDrawable(R.drawable.timg2019042801));
                        for (int i = 0; i < width; i++) {
                            for (int j = 0; j < height; j++) {
                                detected[i][j] = true;
                                if (mines[i][j]) {
                                    cells[i][j].setBackground(getDrawable(R.drawable.timg2019042801));
                                }
                            }
                        }
                        flagButton.setEnabled(false);
                        findViewById(R.id.failure).setVisibility(View.VISIBLE);
                    } else {
                        detected[x][y] = true;
                        detect++;
                        cells[x][y].setBackgroundColor(Color.GRAY);
                        if (around[x][y] == 0) {
                            for (int k = 0; k < 9; k++) {
                                if (k != 4) {
                                    int i = k / 3 - 1;
                                    int j = k % 3 - 1;
                                    cellClicked(x + i, y + j);
                                }
                            }
                        } else {
                            cells[x][y].setText(around[x][y]);
                            cells[x][y].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        }
                    }



                }
            }
        }
        if (flagsLeft == 0 && detect + number == width * height) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    detected[i][j] = true;
                }
            }
            flagButton.setEnabled(false);
            findViewById(R.id.victory).setVisibility(View.VISIBLE);
        }
    }
    private boolean getMine(int x, int y) {
        if (x >= 0 && x <= width) {
            if (y >= 0 && y <= height) {
                return mines[x][y];
            }
        }
        return false;
    }

}
