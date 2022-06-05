package com.example.firebase_email_signinup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button logoutButton, deleteUserButton;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logoutButton = findViewById(R.id.logoutButton);
        deleteUserButton = findViewById(R.id.deleteButton);

        user = FirebaseAuth.getInstance().getCurrentUser();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutTheUser();
            }
        });

        deleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTheUser();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.change_email_item) {

            //inflate the custom view
            View customView = getLayoutInflater().inflate(R.layout.new_email, null);

            new AlertDialog.Builder(this)
                    .setTitle("Change Email")
                    .setView(customView)
                    .setCancelable(false)
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            EditText newEmailEditText = customView.findViewById(R.id.newEmailEditText);
                            String newUserEmail = newEmailEditText.getText().toString();

                            if (newUserEmail.equals("")) {
                                newEmailEditText.setError("Can't Be Empty");
                                return;
                            }

                            //change the email
                            user.updateEmail(newUserEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(MainActivity.this, "Email Updated to :: " + newUserEmail, Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                    else
                                        Toast.makeText(MainActivity.this, "Email Unchanged", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "Email Unchanged", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }).create().show();

        } else if (item.getItemId() == R.id.update_password_item) {

            //inflate the custom view
            View customView = getLayoutInflater().inflate(R.layout.new_password, null);

            new AlertDialog.Builder(this)
                    .setTitle("Update Password")
                    .setView(customView)
                    .setCancelable(false)
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            EditText newPass = customView.findViewById(R.id.newPass);
                            EditText newConfirmPass = customView.findViewById(R.id.newConfirmPass);

                            String newUserPass = newPass.getText().toString();
                            String newUserConfirmPass = newConfirmPass.getText().toString();

                            if (newUserPass.equals("")) {
                                newPass.setError("Can't Be Empty");
                                return;
                            }

                            if (newUserConfirmPass.equals("")) {
                                newConfirmPass.setError("Can't Be Empty");
                                return;
                            }

                            if (!newUserConfirmPass.equals(newUserPass)) {
                                newConfirmPass.setError("Password Doesn't match");
                                return;
                            }

                            //Update the password (here user has already logged in
                            user.updatePassword(newUserPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    } else
                                        Toast.makeText(MainActivity.this, "Password Unchanged", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Password Unchanged", Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    //Check email is verified
    @Override
    protected void onStart() {

        if (!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
            Toast.makeText(this, "Please Verify Email", Toast.LENGTH_SHORT).show();
        }

        super.onStart();
    }

    /**
     * Method will logout the user
     */
    private void logoutTheUser() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, SignIn.class));
            finish();
        } else {
            Toast.makeText(MainActivity.this, "User is NUll...", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method will delete the user
     */
    private void deleteTheUser() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Delete Yourself!")
                .setMessage("Are you sure you want to deregister yourself?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        //prepare the progress dialog
                        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("Deleting user");
                        progressDialog.setTitle("Please Wait!");
                        progressDialog.show();

                        //delete the user
                        FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Toast.makeText(MainActivity.this, "User Deleted", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();

                                    startActivity(new Intent(MainActivity.this, SignIn.class));
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "User Doesn't Exist", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}
