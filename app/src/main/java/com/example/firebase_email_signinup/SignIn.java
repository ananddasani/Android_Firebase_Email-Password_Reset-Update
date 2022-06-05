package com.example.firebase_email_signinup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {

    EditText email, password;
    TextView newUser, forgotPassword;
    Button signButton;

    String userEmail, userPass;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.editTextTextEmail);
        password = findViewById(R.id.editTextTextPassword);
        signButton = findViewById(R.id.signInButton);
        newUser = findViewById(R.id.newUserTextView);
        forgotPassword = findViewById(R.id.forgotPassTextView);

        //new user textview clicked
        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, SignUp.class));
                finish();
            }
        });

        //sign button clicked
        signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate the email, password inserted or not
                userEmail = email.getText().toString();
                userPass = password.getText().toString();

                if (userEmail.equals("")) {
                    email.setError("Email is required");
                    return;
                }

                if (userPass.equals("")) {
                    password.setError("Password is required");
                    return;
                }

                //Email password given sign in the user
                auth.signInWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //user entered correct credential move to home
                            Toast.makeText(SignIn.this, "user verified", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignIn.this, MainActivity.class));
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignIn.this, "Invalid Credential :: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //forgot password when clicked
        forgotPassword.setOnClickListener(new View.OnClickListener() {

            View customView = getLayoutInflater().inflate(R.layout.forgot_pass, null);

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SignIn.this)
                        .setTitle("Reset Password")
                        .setMessage("Enter registered email on which you will get reset password link.")
                        .setView(customView)
                        .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText forgotPassEmail = customView.findViewById(R.id.forgotPasswodEmail);
                                String userVerifiedEmail = forgotPassword.getText().toString();

                                if (userVerifiedEmail.equals("")) {
                                    forgotPassEmail.setError("Required Email");
                                    Toast.makeText(SignIn.this, "Password Unchanged", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                //send the password reset link
                                FirebaseAuth.getInstance().sendPasswordResetEmail(userVerifiedEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SignIn.this, "Reset link sent to email", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        } else
                                            Toast.makeText(SignIn.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignIn.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(SignIn.this, "Password Unchanged", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });
    }

    /*
    if user is already logged in no need to ask them again to login
     */
    @Override
    protected void onStart() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Toast.makeText(this, "You are already registered :)", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignIn.this, MainActivity.class));
            finish();
        }

        super.onStart();
    }
}
