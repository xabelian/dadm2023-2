package com.example.tictacgame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.util.Log;

public class TicTacToeGame {
    public static final char HUMAN_PLAYER = 'X';
    public static final char COMPUTER_PLAYER = 'O';
    public static final char OPEN_SPOT = ' ';
    public static final String TAG = "game.TicTacToeGame";

    public static enum DifficultyLevel {Easy, Harder, Expert };

    private DifficultyLevel mDifficultyLevel = DifficultyLevel.Easy;

    private char mBoard[] = { '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    public static final int BOARD_SIZE = 9;

    private Random mRand;

    public TicTacToeGame() {
        // Seed the random number generator
        mRand = new Random();
    }

    public void clearBoard() {
        for (int i = 0; i < mBoard.length; i++)
            mBoard[i] = OPEN_SPOT;
    }

    public boolean setMove(char player, int location) {
        if (location >= 0 && location <= 8 && mBoard[location] == OPEN_SPOT) {
            mBoard[location] = player;
            return true;
        }
        return false;
    }


    public int getComputerMove() {
        int move = -1;
        if (mDifficultyLevel == DifficultyLevel.Easy)
            move = getRandomMove();
        else if (mDifficultyLevel == DifficultyLevel.Harder) {
            move = getWinningMove();
            if (move == -1)
                move = getRandomMove();
        }
        else if (mDifficultyLevel == DifficultyLevel.Expert) {
    // Try to win, but if that's not possible, block.
    // If that's not possible, move anywhere.
            move = getWinningMove();
            if (move == -1)
                move = getBlockingMove();
            if (move == -1)
                move = getRandomMove();
        }
        return move;
    }

    public int getRandomMove() {
        int move;
        do {
            move = mRand.nextInt(BOARD_SIZE);
        } while (mBoard[move] != OPEN_SPOT);
        return move;
    }

    public int getWinningMove() {
        // Check for a winning move for the computer
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] == OPEN_SPOT) {
                mBoard[i] = COMPUTER_PLAYER;
                if (checkForWinner() == 3) {
                    mBoard[i] = OPEN_SPOT;
                    return i;
                }
                mBoard[i] = OPEN_SPOT; // Undo the move
            }
        }

        // Check for a winning move for the human
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] == OPEN_SPOT) {
                mBoard[i] = HUMAN_PLAYER;
                if (checkForWinner() == 2) {
                    mBoard[i] = OPEN_SPOT;
                    return i;
                }
                mBoard[i] = OPEN_SPOT; // Undo the move
            }
        }

        return -1;
    }

    public int getBlockingMove() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] == OPEN_SPOT) {
                mBoard[i] = HUMAN_PLAYER;
                if (checkForWinner() == 2) {
                    mBoard[i] = OPEN_SPOT;
                    return i;
                }
                mBoard[i] = OPEN_SPOT;
            }
        }


        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] == OPEN_SPOT) {
                mBoard[i] = COMPUTER_PLAYER;
                if (checkForWinner() == 3) {
                    mBoard[i] = OPEN_SPOT;
                    return i;
                }
                mBoard[i] = OPEN_SPOT; // Undo the move
            }
        }

        return -1; // No blocking move found
    }


    public int checkForWinner() {
        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3) {
            if (mBoard[i] == HUMAN_PLAYER && mBoard[i + 1] == HUMAN_PLAYER
                    && mBoard[i + 2] == HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER
                    && mBoard[i + 1] == COMPUTER_PLAYER
                    && mBoard[i + 2] == COMPUTER_PLAYER)
                return 3;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == HUMAN_PLAYER && mBoard[i + 3] == HUMAN_PLAYER
                    && mBoard[i + 6] == HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER
                    && mBoard[i + 3] == COMPUTER_PLAYER
                    && mBoard[i + 6] == COMPUTER_PLAYER)
                return 3;
        }

        if ((mBoard[0] == HUMAN_PLAYER && mBoard[4] == HUMAN_PLAYER && mBoard[8] == HUMAN_PLAYER)
                || (mBoard[2] == HUMAN_PLAYER && mBoard[4] == HUMAN_PLAYER && mBoard[6] == HUMAN_PLAYER))
            return 2;
        if ((mBoard[0] == COMPUTER_PLAYER && mBoard[4] == COMPUTER_PLAYER && mBoard[8] == COMPUTER_PLAYER)
                || (mBoard[2] == COMPUTER_PLAYER
                && mBoard[4] == COMPUTER_PLAYER && mBoard[6] == COMPUTER_PLAYER))
            return 3;

        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find a number, then no one has won yet
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER)
                return 0;
        }

        return 1;
    }

    public DifficultyLevel getDifficultyLevel() {
        return mDifficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        mDifficultyLevel = difficultyLevel;
    }

    public int getBoardOccupant(int pos) {
        return mBoard[pos];
    }

    public char[] getBoardState() {
        return mBoard;
    }

    public void setBoardState(char[] mBoard) {
        this.mBoard = mBoard;
    }
}