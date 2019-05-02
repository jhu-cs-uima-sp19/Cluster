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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {

    private Button btnChangePwd, changePwdConf, btnDeleteAccount, btnSignOut, btnChangeUName, changeUNameConf;

    private EditText pwd, newPwd, uName;
    private TextView uNameDisp;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private boolean pwdVisible = false;
    private boolean uNameVisible = false;

    private final int MIN_PWD_LEN = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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

        uNameDisp = (TextView) findViewById(R.id.u_name_disp);
        db.document("users/" + auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                String disp_uname = getResources().getString(R.string.username_placeholder);
                if(doc.contains("userName")) {
                    disp_uname += doc.get("userName");
                } else {
                    disp_uname += auth.getUid();
                }
                uNameDisp.setText(disp_uname);
            }
        });

        btnChangePwd = (Button) findViewById(R.id.btn_change_pwd);
        btnChangeUName = (Button) findViewById(R.id.btn_change_uName);
        changePwdConf = (Button) findViewById(R.id.btn_change_pwd_confirm);
        changeUNameConf = (Button) findViewById(R.id.btn_change_uname_confirm);
        btnDeleteAccount = (Button) findViewById(R.id.btn_delete_account);
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);

        pwd = (EditText) findViewById(R.id.pwd);
        newPwd = (EditText) findViewById(R.id.newPwd);
        uName = (EditText) findViewById(R.id.newUName);

        pwd.setVisibility(View.GONE);
        newPwd.setVisibility(View.GONE);
        changePwdConf.setVisibility(View.GONE);

        uName.setVisibility(View.GONE);
        changeUNameConf.setVisibility(View.GONE);

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
                                        new AlertDialog.Builder(ProfileActivity.this, R.style.AlertDialogTheme)
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

        btnChangeUName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!uNameVisible) {
                    uName.setVisibility(View.VISIBLE);
                    changeUNameConf.setVisibility(View.VISIBLE);
                    uNameVisible = true;
                } else {
                    uName.setVisibility(View.GONE);
                    changeUNameConf.setVisibility(View.GONE);
                    uNameVisible = false;
                }
            }
        });

        changeUNameConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String input = uName.getText().toString().trim();
                // we can come up with more complicated conditions for usernames if we want, I don't care
                if (input.length() == 0) {
                    uName.setError(getString(R.string.bad_uname));
                } else {
                    new AlertDialog.Builder(ProfileActivity.this, R.style.AlertDialogTheme)
                            .setTitle(R.string.change_uname)
                            .setMessage(R.string.change_uname_msg)
                            .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String newUserName = uName.getText().toString();
                                    Map<String, Object> updateUserName = new HashMap<>();
                                    updateUserName.put("userName", newUserName);
                                    db.document("users/" + auth.getUid()).set(updateUserName, SetOptions.merge());
                                    String updatedDispUserName = getResources().getString(R.string.username_placeholder) + input;
                                    uNameDisp.setText(updatedDispUserName);
                                    uName.setText("");
                                    uName.setVisibility(View.GONE);
                                    changeUNameConf.setVisibility(View.GONE);
                                    uNameVisible = false;
                                }
                            })
                            .setNegativeButton(R.string.not_sure, null)
                            .show();
                }
            }
        });

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ProfileActivity.this, R.style.AlertDialogTheme)
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
                new AlertDialog.Builder(ProfileActivity.this, R.style.AlertDialogTheme)
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