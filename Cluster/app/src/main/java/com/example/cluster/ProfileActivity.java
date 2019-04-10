package com.example.cluster;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private Button btnChangePwd, btnDeleteAccount, signOut;

    private EditText password, newPassword;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        //btnChangePwd = (Button) findViewById(R.id.change_password_button);
        btnDeleteAccount = (Button) findViewById(R.id.btn_delete_account);
        //changePwd = (Button) findViewById(R.id.changePwd);
        signOut = (Button) findViewById(R.id.btn_sign_out);

        //password = (EditText) findViewById(R.id.password);
        //newPassword = (EditText) findViewById(R.id.newPassword);

        /*btnChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.VISIBLE);
                changePwd.setVisibility(View.VISIBLE);
                delete.setVisibility(View.GONE);
            }
        });

        changePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null && !newPassword.getText().toString().trim().equals("")) {
                    if (newPassword.getText().toString().trim().length() < 6) {
                        newPassword.setError("Password too short, enter minimum 6 characters");
                    } else {
                        user.updatePassword(newPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ProfileActivity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                            auth.signOut();
                                        } else {
                                            Toast.makeText(ProfileActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                } else if (newPassword.getText().toString().trim().equals("")) {
                    newPassword.setError("Enter password");
                }
            }
        });*/


        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ProfileActivity.this, android.app.AlertDialog.THEME_TRADITIONAL)
                        .setTitle(R.string.delete_account)
                        .setMessage(R.string.delete_account_confirm)
                        .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (user != null) {
                                    user.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(ProfileActivity.this, R.string.delete_account_failure, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }

                        })
                        .setNegativeButton(R.string.not_sure, null)
                        .show();

            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ProfileActivity.this, android.app.AlertDialog.THEME_TRADITIONAL)
                        .setTitle(R.string.sign_out_dialog)
                        .setMessage(R.string.sign_out_confirm)
                        .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                auth.signOut();
                            }

                        })
                        .setNegativeButton(R.string.not_sure, null)
                        .show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume(); }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}