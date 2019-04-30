package com.appcentricity.cluster;

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

import com.example.cluster.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private Button btnChangePwd, changePwdConf, btnDeleteAccount, btnSignOut;

    private EditText pwd, newPwd;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    private boolean pwdVisible = false;

    private final int MIN_PWD_LEN = 6;

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
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        btnChangePwd = (Button) findViewById(R.id.btn_change_pwd);
        changePwdConf = (Button) findViewById(R.id.btn_change_pwd_confirm);
        btnDeleteAccount = (Button) findViewById(R.id.btn_delete_account);
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);

        pwd = (EditText) findViewById(R.id.pwd);
        newPwd = (EditText) findViewById(R.id.newPwd);

        pwd.setVisibility(View.GONE);
        newPwd.setVisibility(View.GONE);
        changePwdConf.setVisibility(View.GONE);

        btnChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pwdVisible) {
                    pwd.setVisibility(View.VISIBLE);
                    newPwd.setVisibility(View.VISIBLE);
                    changePwdConf.setVisibility(View.VISIBLE);
                    pwdVisible = true;
                } else {
                    pwd.setVisibility(View.GONE);
                    newPwd.setVisibility(View.GONE);
                    changePwdConf.setVisibility(View.GONE);
                    pwdVisible = false;
                }
            }
        });

        changePwdConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthCredential cred = EmailAuthProvider.getCredential(
                        auth.getCurrentUser().getEmail(),
                        pwd.getText().toString().trim()
                );

                if (user != null && !newPwd.getText().toString().trim().equals("") && !pwd.getText().toString().trim().equals("")) {
                    if (newPwd.getText().toString().trim().length() < MIN_PWD_LEN) {
                        newPwd.setError(getString(R.string.minimum_pwd));
                    }
                    user.reauthenticateAndRetrieveData(cred)
                            .addOnCompleteListener(ProfileActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        pwd.setError(getString(R.string.bad_pwd));
                                    } else if(pwd.getText().toString().trim().equals(newPwd.getText().toString().trim())) {
                                        newPwd.setError(getString(R.string.same_pwd));
                                    } else {
                                        new AlertDialog.Builder(ProfileActivity.this, android.app.AlertDialog.THEME_TRADITIONAL)
                                                .setTitle(R.string.change_pwd_dialogue)
                                                .setMessage(R.string.change_pwd_logout)
                                                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener()
                                                {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        user.updatePassword(newPwd.getText().toString().trim())
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> innerTask) {
                                                                        if (innerTask.isSuccessful()) {
                                                                            Toast.makeText(ProfileActivity.this, R.string.pwd_update_success, Toast.LENGTH_SHORT).show();
                                                                            auth.signOut();
                                                                            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                                                                        } else {
                                                                            Toast.makeText(ProfileActivity.this, R.string.pwd_update_fail, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    }

                                                })
                                                .setNegativeButton(R.string.not_sure, null)
                                                .show();
                                    }
                                }
                            });
                } else if (newPwd.getText().toString().trim().equals("")) {
                    newPwd.setError("Enter password");
                } else if (pwd.getText().toString().trim().equals("")) {
                    pwd.setError("Enter password");
                }
            }
        });


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

        btnSignOut.setOnClickListener(new View.OnClickListener() {
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
                                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
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