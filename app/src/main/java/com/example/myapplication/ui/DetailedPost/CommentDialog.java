package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.myapplication.R;
import com.example.myapplication.R.layout;

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
}
