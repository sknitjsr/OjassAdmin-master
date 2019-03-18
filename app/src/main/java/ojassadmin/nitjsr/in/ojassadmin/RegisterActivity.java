package ojassadmin.nitjsr.in.ojassadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.PhoneNumber;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_ACCESS_LEVEL;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_BRANCH;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_COLLEGE_REG_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_EMAIL;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_MOBILE;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_NAME;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_PHOTO;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText adminMobile,adminBranch,adminRegId;
    private Button mRegister;
    private TextView adminName,adminEmail;
    private DatabaseReference rootReference;
    private ProgressDialog progressDialog;
    private FirebaseUser user;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        sharedPrefManager = new SharedPrefManager(this);
       if(sharedPrefManager.isRegistered())
        {
           moveToMainActivity();
        }

        rootReference = FirebaseDatabase.getInstance().getReference().child("Admins");

        user = FirebaseAuth.getInstance().getCurrentUser();

        adminName = (TextView)findViewById(R.id.admin_name);
        adminEmail = (TextView)findViewById(R.id.admin_email);
        mRegister = (Button)findViewById(R.id.btn_register);
        adminMobile = (EditText)findViewById(R.id.admin_mobile);
        adminBranch = (EditText)findViewById(R.id.admin_branch);
        adminRegId = (EditText)findViewById(R.id.admin_reg_id);

        progressDialog = new ProgressDialog(this);

        adminName.setText(user.getDisplayName());
        adminEmail.setText(user.getEmail());



        mRegister.setOnClickListener(this);


            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(final Account account) {

                    String accountKitId = account.getId();


                    PhoneNumber phoneNumber = account.getPhoneNumber();
                  String  phoneNumberString = phoneNumber.toString();
                    adminMobile.setText(phoneNumberString);
                    adminMobile.setEnabled(false);

                }

                @Override
                public void onError(final AccountKitError error) {
                    Log.e("AccountKit",error.toString());
                    Toast.makeText(
                            getApplicationContext(),
                            error.toString(),
                            Toast.LENGTH_LONG)
                            .show();
                }
            });


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_register) registerAdmin();
    }

    private void registerAdmin() {

        String inputMobile = adminMobile.getText().toString().trim();
        String inputBranch = adminBranch.getText().toString().trim();
        String inputRegID = adminRegId.getText().toString().trim();

        if (!TextUtils.isEmpty(inputMobile) && !TextUtils.isEmpty(inputBranch) && !TextUtils.isEmpty(inputRegID)) {
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            String uid = user.getUid();
            rootReference.child(uid).child(FIREBASE_REF_NAME).setValue(user.getDisplayName());
            rootReference.child(uid).child(FIREBASE_REF_EMAIL).setValue(user.getEmail());
            rootReference.child(uid).child(FIREBASE_REF_MOBILE).setValue(inputMobile);
            rootReference.child(uid).child(FIREBASE_REF_BRANCH).setValue(inputBranch);
            rootReference.child(uid).child(FIREBASE_REF_COLLEGE_REG_ID).setValue(inputRegID);
            rootReference.child(uid).child(FIREBASE_REF_ACCESS_LEVEL).setValue("3");
            rootReference.child(uid).child(FIREBASE_REF_PHOTO).setValue(user.getPhotoUrl().toString());

            sharedPrefManager.setIsRegistered(true);

            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Fields can't be left blank!", Toast.LENGTH_SHORT).show();
        }
    }

    private void moveToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
