# Android_Firebase_Email-Password_Reset-Update
App allows to reset-update the email and password

## SignIn.java

#### previous code
```
EditText email, password;
TextView newUser;
Button signButton;

String userEmail, userPass;

FirebaseAuth auth;

auth = FirebaseAuth.getInstance();

email = findViewById(R.id.editTextTextEmail);
password = findViewById(R.id.editTextTextPassword);
signButton = findViewById(R.id.signInButton);
newUser = findViewById(R.id.newUserTextView);

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
    }
```

```
    /*
    if user is already logged in no need to ask them again to login
     */
    @Override
    protected void onStart() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            Toast.makeText(this, "You are already registered :)", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignIn.this, MainActivity.class));
            finish();
        }

        super.onStart();
    }
```

#### added new feature code
```
TextView forgotPassword;

forgotPassword = findViewById(R.id.forgotPassTextView);


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
```

## MainActivity.java

#### previous code
```
Button logoutButton, deleteUserButton;

FirebaseUser user;

logoutButton = findViewById(R.id.logoutButton);
deleteUserButton = findViewById(R.id.deleteButton);

user = FirebaseAuth.getInstance().getCurrentUser();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, SignIn.class));
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "User is NUll...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                                        Toast.makeText(MainActivity.this, "User Deleted", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();

                                        startActivity(new Intent(MainActivity.this, SignIn.class));
                                        finish();
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
        });
```

#### added new feature code
```
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
```
