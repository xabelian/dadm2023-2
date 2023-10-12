package com.example.tictacgame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnTouchListener;
import androidx.appcompat.app.AppCompatActivity;

public class AndroidTicTacToeActivity extends AppCompatActivity {
    static final int DIALOG_DIFFICULTY_ID = 0;
    private static final int DIALOG_QUIT_ID = 1;
    private Button[] mBoardButtons;
    private TextView mInfoTextView;
    private TicTacToeGame mGame;

    private boolean mGameOver = false;

    private BoardView mBoardView;

    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;

    @Override
    protected void onResume(){
        super.onResume();

        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sound_user);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sound_pc);


    }

    @Override
    protected void onPause(){
        super.onPause();
        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }
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
        mInfoTextView = (TextView) findViewById(R.id.information);

        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);

        startNewGame();
    }

    // Listen for touches on the board
    private OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;
            if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos)) {
                // If no winner yet, let the computer make a move
                mHumanMediaPlayer.start();
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    int move = mGame.getComputerMove();
                    mComputerMediaPlayer.start();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                    if (winner != 0) {
                        handleGameResult(winner);
                    }
                }
            }

            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };

    private void startNewGame() {
        mGame.clearBoard();
        mBoardView.invalidate();

        mInfoTextView.setText("You go first.");
    }

    private void handleGameResult(int winner) {
        mGameOver = true;
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
                mGameOver = false;
            }
        });

        // Add the result button to your layout
        LinearLayout layout = findViewById(R.id.play_grid);
        layout.addView(resultButton);
    }

    private boolean setMove(char player, int location) {
        if (mGame.setMove(player, location)) {
            mBoardView.invalidate(); // Redraw the board
            return true;
        }
        return false;
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

