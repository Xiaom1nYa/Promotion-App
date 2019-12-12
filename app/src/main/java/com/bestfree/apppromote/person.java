package com.bestfree.apppromote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class person extends AppCompatActivity {

    private RelativeLayout watch_ad;
    private TextView points;
    private TextView installs;
    private Button add_app;
    private ImageView my_app_image;
    private TextView delete;
    private TextView back;
    private RewardedAd rewardedAd;
    private Uri photouri;
    private double point_int;
    private ProgressBar person_progress;
    ProgressBar pro;
    ImageView icon;
    String string_package_name;
    String pa = "";
    Dialog skip_dialog;
    InterstitialAd mInterstitialAd;
    private static final int PICK_IMAGE_REQUEST = 2;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference = storage.getReference();
    private FirebaseUser user = auth.getCurrentUser();
    Random rand = new Random();
    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId('Adid);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });
        person_progress = findViewById(R.id.person_progress);
        watch_ad = findViewById(R.id.watch_ad);
        points = findViewById(R.id.points);
        installs = findViewById(R.id.installs);
        add_app = findViewById(R.id.add_app);
        my_app_image = findViewById(R.id.my_app_image);
        delete = findViewById(R.id.delete);
        back = findViewById(R.id.back);


        TextView rate = findViewById(R.id.rate);
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        try{
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getPackageName())));
                        }
                        catch (ActivityNotFoundException e){
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName())));
                        }
                    }


        });

        TextView share_app = findViewById(R.id.copy);
        share_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label" , "https://play.google.com/store/apps/details?id="+getPackageName());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(person.this, "app link copied !"
                        ,Toast.LENGTH_LONG).show();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rewardedAd = new RewardedAd(this,
                'Adid');

        getinfo();

        add_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (point_int >= 5) {
                    skip_dialog = new Dialog(person.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
                    skip_dialog.setContentView(R.layout.add_dialog);

                    Button confirm = skip_dialog.findViewById(R.id.confirm);
                    final EditText app_name = skip_dialog.findViewById(R.id.name_dialog);
                    final EditText link = skip_dialog.findViewById(R.id.link_dialog);
                    icon = skip_dialog.findViewById(R.id.icon_dialog);
                    pro = skip_dialog.findViewById(R.id.progress_dialog);

                    icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openFile();
                        }
                    });

                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (app_name.getText().toString().length() != 0 && link.getText().toString().length() != 0
                                    && photouri != null) {
                                if (link.getText().toString().contains("https://play.google.com/store/apps/details?id=")) {
                                    string_package_name = link.getText().toString();
                                    string_package_name = string_package_name.replace("https://play.google.com/store/apps/details?id=", "");
                                    pro.setVisibility(View.VISIBLE);
                                    Map<String, Object> ref = new HashMap<>();
                                    assert user != null;
                                    ref.put("id", user.getUid());
                                    ref.put("link", link.getText().toString());
                                    ref.put("name", app_name.getText().toString());
                                    ref.put("package", string_package_name);
                                    ref.put("time", FieldValue.serverTimestamp());
                                    db.collection("Apps").document(string_package_name).set(ref).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pro.setVisibility(View.GONE);
                                            Toast.makeText(person.this, e.toString()
                                                    , Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    storageReference.child(string_package_name).putFile(photouri)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Glide.with(getApplicationContext()).load(photouri).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(my_app_image);
                                                    skip_dialog.dismiss();
                                                    add_app.setVisibility(View.GONE);
                                                    pro.setVisibility(View.GONE);
                                                }
                                            });
                                    db.collection("Users").document(user.getUid())
                                            .update("package", string_package_name);
                                    if (mInterstitialAd.isLoaded())
                                        mInterstitialAd.show();
                                } else {
                                    Toast.makeText(person.this, "Please upload google play link !"
                                            , Toast.LENGTH_LONG).show();
                                }

                            } else {
                                Toast.makeText(person.this, "Please fulfill your information "
                                        , Toast.LENGTH_LONG).show();
                            }
                        }

                    });

                    skip_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    skip_dialog.show();
                    Window window = skip_dialog.getWindow();
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                }else{
                    Toast.makeText(person.this, "Earn At Least 5 Points To Add Your App"
                            , Toast.LENGTH_LONG).show();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pa.equals("")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(person.this);
                    dialog.setTitle("Alert")
                            .setMessage("Are You Sure To Delete This App ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    db.collection("Apps").document(pa)
                                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot document) {
                                            db.collection("Apps").document(document.getId()).delete();
                                            db.collection("Users").document(user.getUid())
                                                    .update("package" , "");
                                            add_app.setVisibility(View.VISIBLE);
                                            if(mInterstitialAd.isLoaded())
                                                mInterstitialAd.show();
                                        }

                                    });

                                }
                            });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialoginterface, int which) {
                            dialoginterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();

                }
            }
        });

        watch_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog = new AlertDialog.Builder(person.this)
//set icon
                        .setIcon(R.drawable.coin)
//set titl
                        .setTitle("Get Reward")
//set message
                        .setMessage("Watch Ad To Get 0.5 Points")
//set positive button
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //set what would happen when positive button is clicked
                                dialogInterface.dismiss();
                                person_progress.setVisibility(View.VISIBLE);
                                RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
                                    @Override
                                    public void onRewardedAdLoaded() {
                                        // Ad successfully loaded.
                                        RewardedAdCallback adCallback = new RewardedAdCallback() {
                                            @Override
                                            public void onRewardedAdOpened() {
                                                // Ad opened.
                                                person_progress.setVisibility(View.GONE);
                                            }

                                            @Override
                                            public void onRewardedAdClosed() {
                                                // Ad closed.
                                                rewardedAd = new RewardedAd(person.this,
                                                        "");
                                            }

                                            @Override
                                            public void onUserEarnedReward(@NonNull RewardItem reward) {
                                                // User earned reward.
                                                point_int =  point_int + 0.5;
                                                points.setText("Points : " + String.valueOf(point_int));
                                                db.collection("Users").document(user.getUid()).update(
                                                        "points" , point_int
                                                );
                                                Toast.makeText(person.this, "You get 0.5 installs points !"
                                                        , Toast.LENGTH_LONG).show();
                                                rewardedAd = new RewardedAd(person.this,
                                                        "");
                                            }

                                            @Override
                                            public void onRewardedAdFailedToShow(int errorCode) {
                                                // Ad failed to display
                                            }
                                        };
                                        rewardedAd.show(person.this , adCallback);
                                    }

                                    @Override
                                    public void onRewardedAdFailedToLoad(int errorCode) {
                                        // Ad failed to load.


                                    }
                                };
                                rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);

                            }
                        })
//set negative button
                        .setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //set what should happen when negative button is clicked
                                dialogInterface.dismiss();
                            }
                        })
                        .show();

            }
        });
    }



    private void openFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        icon = skip_dialog.findViewById(R.id.icon_dialog);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            photouri = data.getData();

            AlertDialog.Builder dialog = new AlertDialog.Builder(person.this);
            dialog.setTitle("Picture ")
                    .setMessage("Do you want to choose this picture >")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Glide.with(getApplicationContext()).load(photouri).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(icon);

                        }
                    });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialoginterface, int which) {
                    dialoginterface.dismiss();
                }
            });
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        }
    }

    private void getinfo(){
        db.collection("Users").document(user.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        person_progress.setVisibility(View.GONE);
                        point_int = documentSnapshot.getDouble("points");
                        installs.setText("Get Installs : " + String.valueOf(documentSnapshot.getLong("installs").intValue()));
                        points.setText("Points : " + point_int);
                        pa = documentSnapshot.getString("package");
                        if(documentSnapshot.getString("package").equals("")){
                            add_app.setVisibility(View.VISIBLE);
                        }
                        if(!documentSnapshot.getString("package").equals("")) {
                            storageReference.child(documentSnapshot.getString("package")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Glide.with(getApplicationContext()).load(uri.toString()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(my_app_image);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors

                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(person.this, e.toString()
                        ,Toast.LENGTH_LONG).show();
            }
        });
    }
}
