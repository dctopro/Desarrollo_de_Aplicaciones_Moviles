package co.edu.unal.tictactoe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_ABOUT = 2;
    // Represents the internal state of the game
    private TicTacToeGame mGame;
    // Buttons making up the board
    private Button mBoardButtons[];

    // Various text displayed
    private TextView mInfoTextView;
    private boolean mGameOver;
    private Button mNew_game;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
            case R.id.about:
                showDialog(DIALOG_ABOUT);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        mNew_game=(Button) findViewById(R.id.new_game);
        mNew_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });
        startNewGame();
    }
    // Set up the game board.
    private void startNewGame() {
        mGame.clearBoard();
        // Reset all buttons
        mGameOver=false;
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }
        // Human goes first
        mInfoTextView.setText(R.string.first_human);
        // End of startNewGame
    }
    private class ButtonClickListener implements View.OnClickListener {
        int location;
        public ButtonClickListener(int location) {
            this.location = location;
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
        public void onClick(View v) {
            if (mBoardButtons[location].isEnabled() && !mGameOver) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);
                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText("Turno de la Maquina");
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }
                if (winner == 0)
                    mInfoTextView.setText("Su turno");
                else if (winner == 1)
                    mInfoTextView.setText("Empate");
                else if (winner == 2)
                    mInfoTextView.setText("Ganas, humano");
                else
                    mInfoTextView.setText("Android Gana");
                mGameOver=winner!=0;
            }
        }

    }
    @Override
    protected Dialog onCreateDialog(int id) {

        //int selected=0;
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch(id) {
            case DIALOG_ABOUT:
                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.about_dialog, null);
                builder.setView(layout);
                builder.setPositiveButton("OK", null);
                //Dialog
                dialog = builder.create();
                break;
            case DIALOG_QUIT_ID:
// Create the quit confirmation dialog
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
            case DIALOG_DIFFICULTY_ID: builder.setTitle(R.string.difficulty_choose); final CharSequence[] levels = {
                    getResources().getString(R.string.difficulty_easy),
                    getResources().getString(R.string.difficulty_harder),
                    getResources().getString(R.string.difficulty_expert)};
                    int selected=0;
                    // TODO: Set selected, an integer (0 to n-1), for the Difficulty dialog.
                    // selected is the radio button that should be selected.
                builder.setSingleChoiceItems(levels, selected, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        dialog.dismiss(); // Close dialog
                        switch (item){
                            case 1:
                                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                                break;
                            case 2:
                                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
                                break;
                            default:
                                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                                break;
                        }
                        // TODO: Set the diff level of mGame based on which item was selected.
                        // Display the selected difficulty level
                        Toast.makeText(getApplicationContext(), levels[item],
                                Toast.LENGTH_SHORT).show();
                    }
                });
                dialog = builder.create();
                break;
        }
        return dialog;
    }
}