package edu.css.cis3334_unit12_firebase_2019;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1001;
    private TextView textViewStatus;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonGoogleLogin;
    private Button buttonCreateLogin;
    private Button buttonSignOut;
    private Button buttonStartChat;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private String accountEmail;

    /**
     * onStart gets current user from Firebase, and sees if user is currently
     * signed in. Also checks if Google user is signed in
     */
    @Override
    public void onStart(){
        super.onStart();
        //Check if user is signed in and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null){
            //User is signed in
            updateUI("User Signed In");
        }
        else {
            // User is signed out
            updateUI("User Signed Out");
        }

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) {
            accountEmail = account.getEmail();
            updateUI(account.toString());
        }
    }

    /**
     * Sets up onclick events for buttons and also gets Google Sign in client
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonGoogleLogin = (Button) findViewById(R.id.buttonGoogleLogin);
        buttonCreateLogin = (Button) findViewById(R.id.buttonCreateLogin);
        buttonSignOut = (Button) findViewById(R.id.buttonSignOut);
        buttonStartChat = findViewById(R.id.buttonStartChat);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Normal login
                if(fieldsFilled()){
                signIn(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                }
                else {
                    updateUI("You must enter an email and a password.");
                }
            }
        });

        buttonCreateLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Create account
                if (fieldsFilled()) {
                    createAccount(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                }
                else {
                    updateUI("You must enter an email and a password.");
                }
            }
        });

        buttonGoogleLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Google login
                googleSignIn();
            }
        });

        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Log out
                signOut();
            }
        });

        buttonStartChat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(accountEmail != null && !accountEmail.isEmpty()) {
                    //Starting chat
                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    intent.putExtra("email", accountEmail);
                    startActivity(intent);
                }
                else {
                    updateUI(("Please login"));
                }
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    /**
     * Creates normal user
     * @param email email of user
     * @param password password of user
     */
    private void createAccount(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI("Signed In");
                        } else {
                            // If sign in fails, display a message to the user.
                            updateUI("Not Signed In");
                        }

                        // ...
                    }
                });
    }

    /**
     * Sign in as user if correct info
     * @param email value from email field
     * @param password value from password field
     */
    private void signIn(String email, String password){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user.toString());
                            accountEmail = user.getEmail();
                        } else {
                            // If sign in fails, display a message to the user.
                            updateUI("Authentication failed");
                        }

                        // ...
                    }
                });

    }

    /**
     * Sign out currently logged in user
     */
    private void signOut () {

        mAuth.signOut();
        //mGoogleSignInClient.signOut();
        updateUI("Signed out");
        accountEmail = "";
    }

    /**
     * Open Google Account Login intent
     */
    private void googleSignIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Get results of request and tries to sign into Google account if result
     * @param requestCode request code
     * @param resultCode resulting code
     * @param data data returned
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }

    /**
     * Sign into Google account acount
     * @param completedTask Sign in task
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            accountEmail = account.getEmail();
            updateUI(account.toString());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            updateUI("not Signed In");
        }
    }

    /**
     * Update status with parameter string
     * @param msg string to set status to
     */
    private void updateUI(String msg){
        if(msg != null) {
            textViewStatus.setText(msg);
        }
    }

    /**
     * Check if email and password fields have been filled
     * @return true if both filled. false if one or both not filled
     */
    private boolean fieldsFilled(){
        return !editTextEmail.getText().toString().isEmpty() &&
                !editTextPassword.getText().toString().isEmpty();
    }



}
