package ojassadmin.nitjsr.in.ojassadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.PhoneNumber;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;

import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.CountryCodeSpinner;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.ui.SkinManager;
import com.facebook.accountkit.ui.UIManager;



import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_ACCESS_LEVEL;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_ADMIN;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_IS_SOURCE_NEW_USER;


public class LoginActivity extends AppCompatActivity {
    public static int APP_REQUEST_CODE = 99;

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "LoginActivity";
    private ProgressDialog pd;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);


        sharedPrefManager = new SharedPrefManager(this);


        if (sharedPrefManager.isLoggedIn()) {

            moveToPhoneVerificationActivity();
       }

        Picasso.with(this).load(R.drawable.login_bg).fit().into((ImageView)findViewById(R.id.login_screen));

        mAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);
        pd.setTitle("Hang On");
        pd.setMessage("Connecting you to Mothership...");
        pd.setCancelable(false);



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.btn_signIN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        if (getIntent().getBooleanExtra(INTENT_PARAM_IS_SOURCE_NEW_USER, false)) signIn();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            pd.show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e){
                Log.d(TAG, "Google Sign in failed. Reason: " + e.getMessage());
                if (pd.isShowing()) pd.dismiss();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            sharedPrefManager.setIsLoggedIn(true);
                            isRegisteredUser();
                        } else {
                            if (pd.isShowing()) pd.dismiss();
                            Log.d(TAG,"Authentication failed. Reason: "+ task.getException());
                            Toast.makeText(LoginActivity.this, "LoginActivity Failed. Reason: "+ task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void moveToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void moveToRegisterActivity() {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    private void moveToPhoneVerificationActivity() {
        startActivity(new Intent(this, PhoneVerificationActivity.class));
        finish();
    }

    private void isRegisteredUser() {
        final String fName = mAuth.getCurrentUser().getDisplayName().split(" ")[0];
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(FIREBASE_REF_ADMIN).child(mAuth.getCurrentUser().getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    sharedPrefManager.setIsRegistered(true);
                    int accessLevel = Integer.parseInt(dataSnapshot.child(FIREBASE_REF_ACCESS_LEVEL).getValue().toString());
                    sharedPrefManager.setAccessLevel(accessLevel);
                   //moveToMainActivity();
                    moveToPhoneVerificationActivity();
                    Toast.makeText(LoginActivity.this, "Welcome to Ojass Space Voyage Dashboard! "+fName, Toast.LENGTH_LONG).show();
                } else {
                    sharedPrefManager.setIsRegistered(false);
                    sharedPrefManager.setAccessLevel(3);
                    moveToPhoneVerificationActivity();
                    Toast.makeText(LoginActivity.this, "Hey "+fName+"! Let us know you better.", Toast.LENGTH_LONG).show();
                }
                if (pd.isShowing()) pd.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (pd.isShowing()) pd.dismiss();
    }


}
