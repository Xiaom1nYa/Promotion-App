package com.bestfree.apppromote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout google ;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG1 = "Google account";
    private static final int google_signin = 101;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        progress = findViewById(R.id.sign_progress);
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);

        if(user != null && user.isEmailVerified()){
            startActivity(new Intent(MainActivity.this , home.class));
        }

        google = findViewById(R.id.sign_in);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, google_signin);
            }
        });
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG1, "signInWithCredential:success");

                            user = auth.getCurrentUser();
                            assert user != null;
                            progress.setVisibility(View.VISIBLE);
                            db.collection("Users").document(user.getUid()).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if(documentSnapshot.exists()){
                                                progress.setVisibility(View.GONE);
                                                startActivity(new Intent(MainActivity.this , home.class));
                                            }else{
                                                Map<String, Object> ref = new HashMap<>();
                                                ref.put("id", user.getUid());
                                                ref.put("points", 0);
                                                ref.put("installs" , 0);
                                                ref.put("package" , "");
                                                ref.put("earn" , 0);
                                                db.collection("Users").document(user.getUid()).set(ref).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        progress.setVisibility(View.GONE);
                                                        startActivity(new Intent(MainActivity.this , home.class));
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progress.setVisibility(View.VISIBLE);
                                                        Toast.makeText(MainActivity.this, e.toString()
                                                                ,Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        }
                                    });
                           /* ref.put("id", user.getUid());
                            ref.put("points", 20);
                            ref.put("installs" , 0);
                            ref.put("package" , "");
                            ref.put("earn" , 0);
                            db.collection("Users").document(user.getUid()).set(ref).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progress.setVisibility(View.GONE);
                                    startActivity(new Intent(MainActivity.this , home.class));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progress.setVisibility(View.VISIBLE);
                                    Toast.makeText(MainActivity.this, e.toString()
                                            ,Toast.LENGTH_LONG).show();
                                }
                            });*/

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG1, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, task.getException().getMessage()
                                    ,Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == google_signin) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                e.printStackTrace();
                // ...
            }
        }
    }

}
