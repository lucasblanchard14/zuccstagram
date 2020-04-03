package com.example.myapplication.ui.DetailedPost;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.myapplication.R;
import com.example.myapplication.R.layout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommentDialog extends AppCompatDialogFragment {

    private TextView d_username;
    private EditText d_comment;
    private CommentDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.comment_dialog, null);

        builder.setView(view)
                .setTitle("Comment Here")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String username = d_username.getText().toString();
                        String comment = d_comment.getText().toString();
                        uploadComment(username, comment);
                        listener.applyTexts(username,comment);
                    }
                });

        d_comment = view.findViewById(R.id.dialog_comment);
        d_username = view.findViewById(R.id.dialog_username);


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (CommentDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement ExampleDialogListener");
        }
    }

    public interface CommentDialogListener{
        void applyTexts(String username, String comment);
    }

    public void uploadComment(String username, String content){
        Map<String, Object> docData = new HashMap<>();
        Timestamp ts = new Timestamp(new Date());
        docData.put("Comment", "TestPostID|"+ts.getSeconds());
        docData.put("Post", "TestPostID");
        docData.put("User", username);
        docData.put("Text", content);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Comments").document("TestPostID|"+ts.getSeconds())
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "DocumentSnapshot successfully written!");
                        // SUCCESS
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error writing document", e);
                        // ERROR HANDLER
                    }
                });
    }
}
