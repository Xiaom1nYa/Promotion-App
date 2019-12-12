package com.bestfree.apppromote;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class home extends AppCompatActivity {

    private ProgressBar progress;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recycle;
    private List<each_app_class> meach_app = new ArrayList<>();
    private long mExitTime = 0;
    private recycle_adapter each_recycle;
    Random rand = new Random();
    private ImageView user_image;
    private PublisherAdView banner;
    private String ref = "";
    private Button download;
    private String userid;
    TextView earn;
    private String user_id;
    InterstitialAd mInterstitialAd;
    private TextView skip;
    private List<String> list = new ArrayList<String>();
    private FirebaseFunctions mFunctions;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);

        checkFirstRun();


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId('Adid');
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        skip = findViewById(R.id.skip);
        download = findViewById(R.id.download);
        banner = findViewById(R.id.home_ad);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        banner.loadAd(adRequest);
        ImageView ask = findViewById(R.id.ask);
        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(home.this, "You must wait until fully installed , then go back here and see your earn" , Toast.LENGTH_SHORT).show();

            }
        });

        earn =findViewById(R.id.earn);
        db.collection("Users").document(user.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getString("email") == null){
                    Map<String, Object> data = new HashMap<>();
                    data.put("email", user.getEmail());

                    db.collection("Users").document(user.getUid())
                            .set(data, SetOptions.merge());
                }
                earn.setText(String.valueOf(documentSnapshot.getLong("earn").intValue()));
            }
        });

        TextView page = findViewById(R.id.your_page);
        page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rand.nextInt(4) == 1) {
                    if(mInterstitialAd.isLoaded())
                        mInterstitialAd.show();
                }
                startActivity(new Intent(home.this, person.class));
            }
        });

        progress = findViewById(R.id.home_progress);
        user_image = findViewById(R.id.user);
        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rand.nextInt(4) == 1) {
                    if(mInterstitialAd.isLoaded())
                        mInterstitialAd.show();
                }
                    startActivity(new Intent(home.this, person.class));


            }
        });


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (meach_app.size() == 0) {
                    Toast.makeText(home.this, "No More App Today", Toast.LENGTH_SHORT).show();

                }else {
                    //Toast.makeText(home.this, meach_app.get(0).getApp_name(), Toast.LENGTH_SHORT).show();
                    list.add(meach_app.get(0).getApp_name());
                    recycle.removeAllViews();
                    meach_app.clear();
                    getApp();


                  /*  recycle.setHasFixedSize(true);
                    RecyclerViewNoBugLinearLayoutManager linearLayoutManager = new RecyclerViewNoBugLinearLayoutManager(home.this);
                    //linearLayoutManager.setStackFromEnd(true) ;
                    recycle.setLayoutManager(linearLayoutManager);
                    each_recycle = new recycle_adapter(meach_app , home.this);
                    recycle.setAdapter(each_recycle);
*/
                }
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ref.equals("")) {
                    try {
                        v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + ref)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + ref)));
                    }
                }
            }
        });

        recycle = findViewById(R.id.recycle);

        if(recycle != null)
            getApp();

        /*if(meach_app != null){
            recycle.setHasFixedSize(true);
            recycle.setLayoutManager(new LinearLayoutManager(home.this));
            each_recycle = new recycle_adapter(meach_app, home.this);
            recycle.setAdapter(each_recycle);
        }*/


    }

    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageGids(packagename);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void getApp(){
        progress.setVisibility(View.VISIBLE);
        db.collection("Apps").limit(20).orderBy("time" , Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (final QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    if(!document.exists()){
                        Toast.makeText(home.this, "No app yet", Toast.LENGTH_SHORT).show();
                        progress.setVisibility(View.GONE);
                    }else {
                        user_id = document.getString("id");
                        db.collection("Users").document(user_id)
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot document1) {
                                if(document1.getDouble("points") > 0) {
                                    db.collection("Users").document(user.getUid())
                                            .collection("Installed").document(document.getString("package"))
                                            .get().addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progress.setVisibility(View.GONE);
                                            Toast.makeText(home.this, e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                progress.setVisibility(View.GONE);
                                            } else {
                                                String name = document.getString("name");
                                                String pack = document.getString("package");
                                                progress.setVisibility(View.GONE);
                                                PackageManager pm = getPackageManager();
                                                if(isPackageInstalled(pack , pm) == false && !list.contains(name)){
                                                    meach_app.add(new each_app_class(pack, name));
                                                        //Toast.makeText(home.this, document.getString("name") , Toast.LENGTH_SHORT).show();

                                                    //meach_app.add(new each_app_class(document.getString("package"), document.getString("name")));
                                                    if(meach_app.size() == 1)
                                                        ref = document.getString("package");
                                                    recycle.setHasFixedSize(true);
                                                    RecyclerViewNoBugLinearLayoutManager linearLayoutManager = new RecyclerViewNoBugLinearLayoutManager(home.this);
                                                    //linearLayoutManager.setStackFromEnd(true) ;
                                                    recycle.setLayoutManager(linearLayoutManager);
                                                    each_recycle = new recycle_adapter(meach_app , home.this);
                                                    recycle.setAdapter(each_recycle);
                                                    //recycle.scrollToPosition(meach_app.size() + 1);
                                                }
                                            }
                                        }
                                    });
                                }else{
                                    db.collection("Apps").document(Objects.requireNonNull(document.getString("package")))
                                            .update("time" , FieldValue.serverTimestamp());
                                }
                            }
                        });
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progress.setVisibility(View.GONE);
                Toast.makeText(home.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {

                Toast.makeText(this, "One more tap to exit", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();

            } else {
                Intent home=new Intent(Intent.ACTION_MAIN);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);

            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    public void onResume() {
        super.onResume();
        TextView earn = findViewById(R.id.earn);
        ProgressBar progress = findViewById(R.id.home_progress);
        PackageManager pm = getPackageManager();
        if(!ref.equals("") && isPackageInstalled(ref , pm) == true){
            Toast.makeText(this, "Succeed!", Toast.LENGTH_SHORT).show();



            progress.setVisibility(View.VISIBLE);
            int get = Integer.parseInt(earn.getText().toString());
            get++;
            earn.setText(String.valueOf(get));
            db.collection("Apps").document(ref).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot document) {
                    userid = document.getString("id");
                    assert userid != null;

                    db.collection("Users").document(userid)
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            double point = documentSnapshot.getDouble("points");
                            int installs = documentSnapshot.getLong("installs").intValue();
                            point--;
                            installs++;

                            mFunctions = FirebaseFunctions.getInstance();
                            Map<String, Object> data = new HashMap<>();
                            data.put("email", documentSnapshot.getString("email"));
                            mFunctions
                                    .getHttpsCallable("sendMail")
                                    .call(data)
                                    .addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                                            if (!task.isSuccessful()) {

                                            }
                                        }
                                    });

                            db.collection("Users").document(userid)
                                    .update("points" , point
                                            ,"installs" , installs);
                        }
                    });
                }
            });


            db.collection("Users").document(user.getUid())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    ProgressBar progress = findViewById(R.id.home_progress);
                    progress.setVisibility(View.GONE);
                    Map<String, Object> ma = new HashMap<>();
                    ma.put("id", user.getUid());
                    db.collection("Users").document(user.getUid())
                            .collection("Installed").document(ref).set(ma);
                    Double point = documentSnapshot.getDouble("points");
                    int earn = documentSnapshot.getLong("earn").intValue();
                    point++;
                    earn++;
                    db.collection("Users").document(user.getUid())
                            .update("points" , point
                            ,"earn" , earn);
                    ref = "";
                    if(recycle != null){
                        recycle.removeAllViews();
                        meach_app.clear();
                        getApp();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ProgressBar progress = findViewById(R.id.home_progress);
                    progress.setVisibility(View.GONE);
                }
            });
        }
    }

    private void checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run


        } else if (savedVersionCode == DOESNT_EXIST) {

            // TODO This is a new install (or the user cleared the shared preferences)
            Toast.makeText(home.this, "You must wait until installed , then go back here and see your earn" , Toast.LENGTH_SHORT).show();

        } else if (currentVersionCode > savedVersionCode) {

            // TODO This is an upgrade

        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    public class RecyclerViewNoBugLinearLayoutManager extends LinearLayoutManager {
        public RecyclerViewNoBugLinearLayoutManager(Context context) {
            super( context );
        }

        public RecyclerViewNoBugLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super( context, orientation, reverseLayout );
        }

        public RecyclerViewNoBugLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super( context, attrs, defStyleAttr, defStyleRes );
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                //try catch一下
                super.onLayoutChildren( recycler, state );
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        }
    }

}
