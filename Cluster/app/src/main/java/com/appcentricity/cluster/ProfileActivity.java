package com.appcentricity.cluster;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private Button btnChangePwd, changePwdConf, btnDeleteAccount, btnSignOut, btnChangeUName, changeUNameConf;

    private EditText pwd, newPwd, uName;
    private TextView uNameDisp;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage cloudStorage = FirebaseStorage.getInstance();
    private StorageReference profPicRef;
    private boolean hasProfPic = false;
    private ImageView profPic;
    private TextView uploadText;


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

        //switch to login activity if logged out
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

        //fetch username if exists, fetch if user profile pic has been uploaded
        db.document("users/" + auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                String disp_uname = getResources().getString(R.string.username_placeholder);
                if (doc.contains("userName")) {
                    disp_uname += doc.get("userName");
                } else {
                    disp_uname += auth.getUid();
                }
                //look for profpic boolean
                if (doc.contains("hasProfPic")) {
                    hasProfPic = doc.getBoolean("hasProfPic");
                }
                else
                {
                    hasProfPic = false;
                }
                uNameDisp.setText(disp_uname);
                fetchProfPic();
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

        //Change Password Button Listener
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

        //Change Password Confirmation Listener
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
                                    } else if (pwd.getText().toString().trim().equals(newPwd.getText().toString().trim())) {
                                        newPwd.setError(getString(R.string.same_pwd));
                                    } else {
                                        new AlertDialog.Builder(ProfileActivity.this, R.style.AlertDialogTheme)
                                                .setTitle(R.string.change_pwd_dialogue)
                                                .setMessage(R.string.change_pwd_logout)
                                                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
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


        //Change Username Listener
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

        //Change Username Confirmation Listener
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

        //Sign Out Button Listener
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ProfileActivity.this, R.style.AlertDialogTheme)
                        .setTitle(R.string.sign_out_dialog)
                        .setMessage(R.string.sign_out_confirm)
                        .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
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

        //Delete Account Button Listener
        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ProfileActivity.this, R.style.AlertDialogTheme)
                        .setTitle(R.string.delete_account)
                        .setMessage(R.string.delete_account_confirm)
                        .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
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
    }

    //fetchProfPic if exists, else set default image
    private void fetchProfPic() {
        profPic = findViewById(R.id.profPicView);
        if (hasProfPic){
            profPicRef = cloudStorage.getReference("users").child("thumb_"+auth.getUid());

            final long ONE_MEGABYTE = 1024 * 1024;
            profPicRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] profPicBytes) {
                    byteArrayToImageView(profPic, profPicBytes);
                }
            }).addOnFailureListener(new OnFailureListener() { //failure to get compressed profile image
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.w("ProfileActivity", "Failed to get compressed pic -- getting original instead");
                    profPicRef = cloudStorage.getReference("users").child("auth.getUid()");
                    final long ONE_MEGABYTE = 1024 * 1024;
                    profPicRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] profPicBytes) {
                            byteArrayToImageView(profPic, profPicBytes);
                        }
                    }).addOnFailureListener(new OnFailureListener() { //failure to get original profile image
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.w("ProfileActivity", "Failed to get uncompressed pic -- using default instead");

                            int defProfPic = getResources().getIdentifier("defaultuser", "drawable", getPackageName());
                            profPic.setImageResource(defProfPic); //if fail to get compressed or original profile image
                        }
                    });

                }
            });
        }
        else
        {
            //default profpic image
            int defProfPic = getResources().getIdentifier("defaultuser", "drawable", getPackageName());
            profPic.setImageResource(defProfPic);
        }


        uploadText = (TextView) findViewById(R.id.profPicViewText);

        profPic.setOnTouchListener(new View.OnTouchListener() { //on touch -- display options
                                       @Override
                                       public boolean onTouch(View v, MotionEvent touchAction) {
                                           int touchType = touchAction.getAction();
                                           switch (touchType) {
                                               case MotionEvent.ACTION_DOWN:
                                                   uploadText.setVisibility(View.VISIBLE);
                                                   break;
                                               case MotionEvent.ACTION_UP:
                                                   uploadText.setVisibility((View.INVISIBLE));
                                                   break;
                                               default:
                                                   break;
                                           }
                                           Log.d("ProfileActivity", "onTouch: Profile Image TextView");
                                           return true;
                                       }
                                   }

        );

    }

    //convert memory held byte array to image view for display
    public static void byteArrayToImageView(ImageView view, byte[] data) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        view.setImageBitmap(bitmap);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

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