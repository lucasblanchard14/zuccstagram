package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.R;

import org.w3c.dom.Text;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


public class DetailPostFragment extends AppCompatActivity implements CommentDialog.CommentDialogListener {

    private ImageButton btncomment;
    private ImageButton btnpostfollow;
    private TextView emptycomments;

    private TextView comment_username;
    private TextView comment_content;

    TableLayout tableLayout;

    private static final String TAG = "DetailPostFragment";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detailpost);

        tableLayout = findViewById(R.id.comment_list);
        emptycomments = findViewById(R.id.empty_comments);
        btncomment = findViewById(R.id.comment_button);

        btncomment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showCommentDialog();
            }
        });
    }


    public void showCommentDialog(){
        CommentDialog dialog = new CommentDialog();
        dialog.show(getSupportFragmentManager(), "test");
    }


    @Override
    public void applyTexts(String username, String comment) {
        generateTableRow();
        comment_username.setText(username);
        comment_content.setText(comment);
        emptycomments.setVisibility(View.INVISIBLE);
    }


    public void generateTableRow() {

        //USERNAME
        TableRow rowUsername = new TableRow(this);
        comment_username = new TextView(this);
        comment_username.setTextSize(18);
        comment_username.setTextColor(Color.BLACK);
        comment_username.setWidth(MATCH_PARENT);
        rowUsername.addView(comment_username);

        //COMMENT
        TableRow rowComment = new TableRow(this);
        comment_content = new TextView(this);
        comment_content.setWidth(MATCH_PARENT);
        rowComment.addView(comment_content);

        //ADD ELEMENTS TO TABLE
        tableLayout.addView(rowComment,0);
        tableLayout.addView(rowUsername,0);

    }
}
