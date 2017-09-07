package com.example.raajesharunachalam.communityserviceconnector;

import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Activity that displays an initial welcome screen that allows the user to sign in if they have not
 * already done so using the Firebase AuthUI library.
 *
 * @author arnchlm2
 */
public class AuthActivity extends AppCompatActivity {

    private static final int SIGN_IN_CODE = 100;
    private static final String TAG = "AuthActivity";

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    Button signInButton;

    /**
     * Displays welcome screen and allows user to begin signing in or advances user to next activity
     * if already signed in.
     *
     * @param savedInstanceState A previous instance of the this Activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        //If this activity was called by signing out from another activity, display a toast
        Intent intent = getIntent();
        if (intent.hasExtra(getString(R.string.sign_out_key))) {
            String signOutMessage = getString(R.string.auth_activity_sign_out_message);
            Toast toast = Toast.makeText(this, signOutMessage, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        }

        //Checks if the user has signed in. If so, they will move to the next activity.
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //Upon successful login, adds this user to the database they are new to the app
                    final String email = user.getEmail();
                    try {
                        DatabaseOperations.addUser(email);
                    } catch (InterruptedException i) {
                        Log.d("ChooseActionActivity", getString(R.string.database_operation_interrupted));
                    }

                    Intent intent = new Intent(AuthActivity.this, ChooseActionActivity.class);
                    startActivity(intent);
                }
            }
        };

        /**
         * Allows the user to begin signing in with the Email, Google, or Facebook. The sign in flow
         * for each provider is handled by the Firebase AuthUI library.
         */
        signInButton = (Button) findViewById(R.id.auth_activity_sign_in);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                                .build(),
                        SIGN_IN_CODE);
            }
        });
    }

    /**
     * Adds the authorization sign in state listener
     */
    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * Removes the authorization sign in state listener
     */
    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    /**
     * Checks if the user canceled the sign in process midway through. If so, displays a toast
     * message to the user.
     *
     * @param requestCode An integer that identifies what activity was called for to get a result from
     * @param resultCode  An integer that signifies that state of the action
     * @param data        An Intent that holds data returned by the activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SIGN_IN_CODE && resultCode == RESULT_CANCELED) {
            String canceledSignIn = getString(R.string.canceled_sign_in_toast);
            Toast toast = Toast.makeText(AuthActivity.this, canceledSignIn, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        }
    }
}
