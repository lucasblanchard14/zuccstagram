package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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
import com.example.myapplication.ui.DetailedPost.CommentDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

        fetchComments();
    }

    public void fetchComments(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Comments")
                .whereEqualTo("Post", "TestPostID")
	            .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String user = document.get("User").toString();
                                String text = document.get("Text").toString();

                                applyTexts(user, text);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
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

        //uploadComment();

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
