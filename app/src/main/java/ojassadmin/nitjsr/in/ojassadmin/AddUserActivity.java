package ojassadmin.nitjsr.in.ojassadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_BRANCH;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_COLLEGE;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_COLLEGE_REG_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_EMAIL;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_KIT;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_MOBILE;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_NAME;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_PAID_AMOUNT;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_PHOTO;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_RECEIVED_BY;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_TSHIRT;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_TSHIRT_SIZE;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_USERS;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_EVENT_HASH;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_IS_SOURCE_NEW_USER;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.USER_DUMMY_IMAGE;

public class AddUserActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etCollege, etBranch, etRegId;
    private Button btnAddUser;
    private Spinner spTshirtSize;
    private CheckBox cbTshirt, cbKit;
    private DatabaseReference ref;
    private ProgressDialog pd;
    private String adminEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        ref = FirebaseDatabase.getInstance().getReference(FIREBASE_REF_USERS);
        pd = new ProgressDialog(this);
        pd.setTitle("Please Wait");
        pd.setMessage("Adding new User");

        adminEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        etName = findViewById(R.id.input_name);
        etEmail = findViewById(R.id.input_email);
        etPhone = findViewById(R.id.input_mobile);
        etCollege = findViewById(R.id.input_college);
        etRegId = findViewById(R.id.input_regid);
        etBranch = findViewById(R.id.input_branch);
        btnAddUser = findViewById(R.id.btn_add_user);
        spTshirtSize = findViewById(R.id.sp_tshirt_size);
        cbTshirt = findViewById(R.id.cbTshirt);
        cbKit = findViewById(R.id.cbKit);

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateEntry()) createUser();
                else Toast.makeText(AddUserActivity.this, "Some field remaining!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean validateEntry() {
        if (TextUtils.isEmpty(etName.getText())) return false;
        if (TextUtils.isEmpty(etEmail.getText())) return false;
        if (TextUtils.isEmpty(etPhone.getText())) return false;
        if (TextUtils.isEmpty(etCollege.getText())) return false;
        if (TextUtils.isEmpty(etRegId.getText())) return false;
        if (TextUtils.isEmpty(etBranch.getText())) return false;
        return true;
    }

    private void createUser() {
        pd.show();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), "DummyPassword")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) addUserToDB(mAuth.getCurrentUser().getUid());
                        else {
                            if (pd.isShowing()) pd.dismiss();
                            Toast.makeText(AddUserActivity.this, "Account Not Created! Try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addUserToDB(String uid) {
        ref = ref.child(uid);
        ref.child(FIREBASE_REF_EMAIL).setValue(etEmail.getText().toString());
        ref.child(FIREBASE_REF_PHOTO).setValue(USER_DUMMY_IMAGE);
        ref.child(FIREBASE_REF_NAME).setValue(etName.getText().toString());
        ref.child(FIREBASE_REF_MOBILE).setValue(etPhone.getText().toString());
        ref.child(FIREBASE_REF_COLLEGE).setValue(etCollege.getText().toString());
        ref.child(FIREBASE_REF_COLLEGE_REG_ID).setValue(etRegId.getText().toString());
        ref.child(FIREBASE_REF_BRANCH).setValue(etBranch.getText().toString());
        ref.child(FIREBASE_REF_TSHIRT_SIZE).setValue(spTshirtSize.getSelectedItem().toString().split(" ")[0]);
        if (cbTshirt.isChecked()) ref.child(FIREBASE_REF_TSHIRT).setValue(adminEmail);
        if (cbKit.isChecked()) ref.child(FIREBASE_REF_KIT).setValue(adminEmail);
        Toast.makeText(this, "User successfully added!", Toast.LENGTH_SHORT).show();
        signOut();
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        new SharedPrefManager(this).setIsLoggedIn(false);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(INTENT_PARAM_IS_SOURCE_NEW_USER, true);
        startActivity(intent);
        finishAffinity();
        if (pd.isShowing()) pd.dismiss();
    }
}
