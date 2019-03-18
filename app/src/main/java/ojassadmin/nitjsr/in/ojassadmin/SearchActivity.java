package ojassadmin.nitjsr.in.ojassadmin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import static android.view.View.GONE;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.HIDE_OJASS_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_SEARCH_FLAG;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_SEARCH_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_SEARCH_SRC;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SEARCH_FLAG_EMAIL;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SEARCH_FLAG_OJ_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SEARCH_FLAG_QR;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SHOW_OJASS_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SRC_SEARCH;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mOjassId,mEmail,mQRCode;
    private EditText etOjId, etEmail;
    private IntentIntegrator integrator;
    private boolean dest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mOjassId=(Button)findViewById(R.id.search_by_ojass_id);
        mEmail=(Button)findViewById(R.id.search_by_email);
        mQRCode=(Button)findViewById(R.id.btn_search_qr);
        etOjId = findViewById(R.id.et_search_oj_id);
        etEmail = findViewById(R.id.et_search_email);

        integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);

        mOjassId.setOnClickListener(this);
        mEmail.setOnClickListener(this);
        mQRCode.setOnClickListener(this);

        dest  = getIntent().getBooleanExtra(INTENT_PARAM_SEARCH_SRC, SHOW_OJASS_ID);
        if (dest) findViewById(R.id.ll_ojass_id).setVisibility(View.VISIBLE);
        else findViewById(R.id.ll_ojass_id).setVisibility(GONE);

    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.search_by_ojass_id) {
            String ojassID = etOjId.getText().toString().trim();
            if (!TextUtils.isEmpty(ojassID)) openUserDetailActivity(ojassID, SEARCH_FLAG_OJ_ID, dest);
            else showMessage("Enter OjassID");
        } else if (view.getId()==R.id.search_by_email) {
            String emailID = etEmail.getText().toString().trim();
            if (!TextUtils.isEmpty(emailID)) openUserDetailActivity(emailID, SEARCH_FLAG_EMAIL, dest);
            else showMessage("Enter email ID");
        } else if (view.getId()==R.id.btn_search_qr) {
            integrator.initiateScan();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) showMessage("Cancelled");
            else openUserDetailActivity(result.getContents(), SEARCH_FLAG_QR, dest);
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    private void openUserDetailActivity(final String ID, final int FLAG, final boolean DEST){
        Intent intent;
        if (DEST == SHOW_OJASS_ID) intent = new Intent(this,UsersDetailsActivity.class);
        else intent = new Intent(this, AdminDetailsActivity.class);
        intent.putExtra(INTENT_PARAM_SEARCH_ID, ID);
        intent.putExtra(INTENT_PARAM_SEARCH_FLAG, FLAG);
        intent.putExtra(INTENT_PARAM_SEARCH_SRC, SRC_SEARCH);
        startActivity(intent);
    }

    private void showMessage(final String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
