package ojassadmin.nitjsr.in.ojassadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_ACCESS_LEVEL;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_ADMIN;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_BRANCH;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_COLLEGE_REG_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_EMAIL;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_MOBILE;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_NAME;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_OJASS_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_PHOTO;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_SEARCH_FLAG;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_SEARCH_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SEARCH_FLAG_EMAIL;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SEARCH_FLAG_OJ_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SEARCH_FLAG_QR;

public class AdminDetailsActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etName, etEmail, etNumber, etBranch, etReg, etAccess;
    private Button btnUpdate;
    private ImageButton ibEdit;
    private DatabaseReference adminRef;
    private String userHashID;
    private ProgressDialog pd;
    private ImageView ivImage;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_details);

        adminRef = FirebaseDatabase.getInstance().getReference(FIREBASE_REF_ADMIN);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        pd = new ProgressDialog(this);
        pd.setMessage("Fetching User...");
        pd.setTitle("Please Wait");
        pd.setCancelable(false);

        etName = findViewById(R.id.et_admin_name);
        etEmail = findViewById(R.id.et_admin_email);
        etNumber = findViewById(R.id.et_admin_number);
        etBranch = findViewById(R.id.et_admin_branch);
        etReg = findViewById(R.id.et_admin_reg_id);
        etAccess = findViewById(R.id.et_admin_access);
        btnUpdate = findViewById(R.id.btn_admin_update);
        ibEdit = findViewById(R.id.ib_admin_edit);
        ivImage = findViewById(R.id.iv_admin_img);

        ibEdit.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

        Intent intent = getIntent();
        final String ID = intent.getStringExtra(INTENT_PARAM_SEARCH_ID);
        final int SEARCH_FLAG = intent.getIntExtra(INTENT_PARAM_SEARCH_FLAG, 0);

        prohibitEdit();
        searchUser(SEARCH_FLAG, ID);

        if (new SharedPrefManager(this).getAccessLevel() <= 3)
            ibEdit.setVisibility(View.VISIBLE);
    }

    private void checkSelf() {
        if (!TextUtils.isEmpty(userHashID) && userHashID.equals(mUser.getUid())){
            btnUpdate.setText("Log Out");
            btnUpdate.setVisibility(View.VISIBLE);
        }
    }

    private void prohibitEdit() {
        etName.setEnabled(false);
        etEmail.setEnabled(false);
        etNumber.setEnabled(false);
        etBranch.setEnabled(false);
        etReg.setEnabled(false);
        etAccess.setEnabled(false);
        btnUpdate.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if (view == ibEdit){
            enableField();
        } else if (view == btnUpdate){
            if (btnUpdate.getText().toString().equals("Update")) sendDataToServer();
            else logOut();
        }
    }

    private void logOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();
        FirebaseAuth.getInstance().signOut();
        moveToLoginPage();
    }

    private void moveToLoginPage() {
        SharedPrefManager shared = new SharedPrefManager(this);
        shared.setIsLoggedIn(false);
        shared.setIsRegistered(false);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendDataToServer() {
        adminRef.child(userHashID).child(FIREBASE_REF_NAME).setValue(etName.getText().toString());
        adminRef.child(userHashID).child(FIREBASE_REF_EMAIL).setValue(etEmail.getText().toString());
        adminRef.child(userHashID).child(FIREBASE_REF_MOBILE).setValue(etNumber.getText().toString());
        adminRef.child(userHashID).child(FIREBASE_REF_BRANCH).setValue(etBranch.getText().toString());
        adminRef.child(userHashID).child(FIREBASE_REF_COLLEGE_REG_ID).setValue(etReg.getText().toString());
        adminRef.child(userHashID).child(FIREBASE_REF_ACCESS_LEVEL).setValue(etAccess.getText().toString());
        Toast.makeText(this, "Values updated!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void enableField() {
        etName.setEnabled(true);
        etEmail.setEnabled(true);
        etNumber.setEnabled(true);
        etBranch.setEnabled(true);
        etReg.setEnabled(true);
        if (!mUser.getUid().equals(userHashID)) etAccess.setEnabled(true);
        btnUpdate.setVisibility(View.VISIBLE);
        ibEdit.setVisibility(View.GONE);
    }

    private void searchUser(int search_flag, String id) {
        pd.show();
        switch (search_flag){
            case SEARCH_FLAG_EMAIL :
                adminRef.orderByChild(FIREBASE_REF_EMAIL).equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot){
                        if (dataSnapshot.getValue() != null){
                            for (DataSnapshot child : dataSnapshot.getChildren()){
                                userHashID = child.getKey();
                                fillData(child);
                                checkSelf();
                            }
                        } else showError();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showError();
                    }
                });
                break;
            case SEARCH_FLAG_QR :
                userHashID = id;
                checkSelf();
                adminRef.child(userHashID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) fillData(dataSnapshot);
                        else showError();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showError();
                    }
                });
                break;
        }
    }

    private void fillData(DataSnapshot dataSnapshot) {
        if (pd.isShowing()) pd.dismiss();
        try {
            etName.setText(dataSnapshot.child(FIREBASE_REF_NAME).getValue().toString());
            etNumber.setText(dataSnapshot.child(FIREBASE_REF_MOBILE).getValue().toString());
            etEmail.setText(dataSnapshot.child(FIREBASE_REF_EMAIL).getValue().toString());
            etBranch.setText(dataSnapshot.child(FIREBASE_REF_BRANCH).getValue().toString());
            etReg.setText(dataSnapshot.child(FIREBASE_REF_COLLEGE_REG_ID).getValue().toString());
            etAccess.setText(dataSnapshot.child(FIREBASE_REF_ACCESS_LEVEL).getValue().toString());
            Picasso.with(this).load(dataSnapshot.child(FIREBASE_REF_PHOTO).getValue().toString()).fit().into(ivImage);
        } catch (Exception e){

        }
    }

    private void showError() {
        if (pd.isShowing()) pd.dismiss();
        Toast.makeText(this, "User Not Found!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
