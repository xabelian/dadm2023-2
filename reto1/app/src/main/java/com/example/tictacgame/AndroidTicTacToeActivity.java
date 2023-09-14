package com.example.tictacgame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AndroidTicTacToeActivity extends AppCompatActivity {
    static final int DIALOG_DIFFICULTY_ID = 0;
    private static final int DIALOG_QUIT_ID = 1;
    private Button[] mBoardButtons;
    private TextView mInfoTextView;
    private TicTacToeGame mGame;

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch(id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)
                };

                int selected = mGame.getDifficultyLevel().ordinal();

                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss(); // Close the dialog

                                // Set the difficulty level of mGame based on the selected item
                                switch (item) {
                                    case 0: // Easy
                                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                                        break;
                                    case 1: // Harder
                                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                                        break;
                                    case 2: // Expert
                                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
                                        break;
                                    default:
                                        break;
                                }
                                startNewGame();

                                // Display the selected difficulty level
                                Toast.makeText(getApplicationContext(), levels[item], Toast.LENGTH_SHORT).show();
                            }
                        });

                dialog = builder.create();
                break;

            case DIALOG_QUIT_ID:
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AndroidTicTacToeActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
        }

        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.one);
        mBoardButtons[1] = (Button) findViewById(R.id.two);
        mBoardButtons[2] = (Button) findViewById(R.id.three);
        mBoardButtons[3] = (Button) findViewById(R.id.four);
        mBoardButtons[4] = (Button) findViewById(R.id.five);
        mBoardButtons[5] = (Button) findViewById(R.id.six);
        mBoardButtons[6] = (Button) findViewById(R.id.seven);
        mBoardButtons[7] = (Button) findViewById(R.id.eight);
        mBoardButtons[8] = (Button) findViewById(R.id.nine);
        mInfoTextView = (TextView) findViewById(R.id.information);

        mGame = new TicTacToeGame();

        startNewGame();
    }


    private void startNewGame() {
        mGame.clearBoard();

        for (int i=0; i < mBoardButtons.length; i++){
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }

        mInfoTextView.setText("You go first.");

    }

    private class ButtonClickListener implements View.OnClickListener {
        int location;
        public ButtonClickListener(int location) {
            this.location = location;
        }
        public void onClick(View view) {
            if (mBoardButtons[location].isEnabled()) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText("It's Android's turn.");
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }
                if (winner == 0) {
                    mInfoTextView.setText("It's your turn.");
                }
                else {
                    String resultText;
                    if (winner == 1) {
                        resultText = "It's a tie!";
                    } else if (winner == 2) {
                        resultText = getString(R.string.result_human_wins);
                    } else {
                        resultText = "Android won!";
                    }

                    // Create a button with the game result text
                    Button resultButton = new Button(AndroidTicTacToeActivity.this);
                    resultButton.setText(resultText);
                    resultButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            // Start a new game when the result button is clicked
                            startNewGame();
                            mInfoTextView.setText("It's your turn.");

                            // Remove the result button from the parent layout
                            ViewGroup layout = (ViewGroup) v.getParent();
                            layout.removeView(v);
                        }
                    });

                    // Add the result button to your layout
                    LinearLayout layout = findViewById(R.id.play_grid); // Replace with your layout ID
                    layout.addView(resultButton);
                }
            }
        }
    }
    private void setMove(char player, int location) {
        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
        else
            mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.new_game) {
            startNewGame();
            return true;
        } else if (itemId == R.id.ai_difficulty) {
            showDialog(DIALOG_DIFFICULTY_ID);
            return true;
        } else if (itemId == R.id.quit) {
            showDialog(DIALOG_QUIT_ID);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

