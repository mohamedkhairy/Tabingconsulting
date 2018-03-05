package com.khairy.tabingconsulting;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by khairy on 2/21/2018.
 */

public class ResendVerificationDialog extends DialogFragment {
    private static final String TAG = "ResendVerificationDialo";

    //widgets
    private EditText mConfirmPassword, mConfirmEmail;

    //vars
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_resend_verification, container, false);
        mConfirmPassword = (EditText) view.findViewById(R.id.confirm_password);
        mConfirmEmail = (EditText) view.findViewById(R.id.confirm_email);
        mContext = getActivity();


        TextView confirmDialog = (TextView) view.findViewById(R.id.dialogConfirm);
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to resend verification email.");

                if(!isEmpty(mConfirmEmail.getText().toString())
                        && !isEmpty(mConfirmPassword.getText().toString())){

                    authenticateAndResendEmail(mConfirmEmail.getText().toString() , mConfirmPassword.getText().toString());


                }else{
                    Toast.makeText(mContext, "all fields must be filled out", Toast.LENGTH_SHORT).show();
                }


            }
        });

        // Cancel button for closing the dialog
        TextView cancelDialog = (TextView) view.findViewById(R.id.dialogCancel);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return view;
    }

    private void authenticateAndResendEmail (String email , String pssword){
        AuthCredential credential = EmailAuthProvider.getCredential(email , pssword);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d(TAG , "OnComplete : Reauthenticate success");
                    sendVerificationEmail();
                    FirebaseAuth.getInstance().signOut();
                    getDialog().dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext , "Invalid credentials \nReset ur password and try again" , Toast.LENGTH_LONG).show();
                getDialog().dismiss();
            }
        });


    }



    /**
     * sends an email verification link to the user
     */
    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(mContext, "Senting Verification Email", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(mContext, "couldn't send email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    /**
     * Return true if the @param is null
     * @param string
     * @return
     */
    private boolean isEmpty(String string){
        return string.equals("");
    }

}
