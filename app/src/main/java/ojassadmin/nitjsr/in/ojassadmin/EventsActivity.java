package ojassadmin.nitjsr.in.ojassadmin;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

import java.util.HashMap;

import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_EVENTS;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_EVENT_PARTICIPANTS;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_EVENT_BRANCH;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_EVENT_HASH;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_EVENT_NAME;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_SEARCH_FLAG;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_SEARCH_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_SEARCH_SRC;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SEARCH_FLAG_EMAIL;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SEARCH_FLAG_OJ_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SEARCH_FLAG_QR;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SRC_EVENT;

public class EventsActivity extends AppCompatActivity implements View.OnClickListener{

    HashMap<String,String> utilMap;
    private Spinner spBranch, spEvent;
    private Button btnEmailID,btnOjassID,btnQRCode;
    private IntentIntegrator integrator;
    private boolean isEventSelected;
    private String selectedEventHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        btnOjassID = findViewById(R.id.search_by_ojass_id_event);
        btnEmailID = findViewById(R.id.search_by_email_event);
        btnQRCode = findViewById(R.id.search_by_qr_code_event);

        integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);

        spBranch = findViewById(R.id.sp_branch);
        spEvent = findViewById(R.id.sp_event);

        isEventSelected = false;

        ArrayAdapter<String> branchArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Constants.EVENT_BRANCH);
        spBranch.setAdapter(branchArrayAdapter);

        spBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> eventArrayAdapter = new ArrayAdapter<>(EventsActivity.this, android.R.layout.simple_spinner_dropdown_item, Constants.EventList[position]);
                spEvent.setAdapter(eventArrayAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0){
                    selectedEventHash = utilMap.get(spEvent.getSelectedItem().toString());
                    isEventSelected = true;
                    ((TextView)findViewById(R.id.tv_event_hash)).setText(selectedEventHash);
                } else isEventSelected = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnOjassID.setOnClickListener(this);
        btnEmailID.setOnClickListener(this);
        btnQRCode.setOnClickListener(this);
        findViewById(R.id.ll_total).setOnClickListener(this);
        findViewById(R.id.tv_event_hash).setOnClickListener(this);

        prepareEventHash();
    }

    private void prepareEventHash() {
        utilMap=new HashMap<>();
        utilMap.put(Constants.EventList[1][1],"-Kcm3PF37gDCX4YjBIg_");
        utilMap.put(Constants.EventList[1][2],"-KcmBMVFnXaS5sfgNu_z");
        utilMap.put(Constants.EventList[1][3],"-Kcm3TyHu9GEcstcA5x7");
        utilMap.put(Constants.EventList[1][4],"-Kcm3cChPrp5CTSf1oS9");
        utilMap.put(Constants.EventList[1][5],"-Kcm3_jbXkSGL--ilVyr");
        utilMap.put(Constants.EventList[1][6],"-Kcm3QyySHKhY8cTtkeY");
        utilMap.put(Constants.EventList[1][7],"-Kcm0lDuLd42VKCd3w95");

        utilMap.put(Constants.EventList[2][1],"-KcngWaQkr2GD_itYlji");
        utilMap.put(Constants.EventList[2][2],"-Kcm-mZZuFX7GwpywCNE");
        utilMap.put(Constants.EventList[2][3],"-Kcm2uVtqT8SY4uYz6xm");
        utilMap.put(Constants.EventList[2][4],"-Kcm2ptKrS4iz1JjPn7t");
        utilMap.put(Constants.EventList[2][5],"-Kclziq6SIXUQPBGl_Ll");
        utilMap.put(Constants.EventList[2][6],"-KclzQND0vJPzf5MYlJ8");
        utilMap.put(Constants.EventList[2][7],"-KclzfYa3_ziiS-3m_Dg");
        utilMap.put(Constants.EventList[2][8],"-Kcm_QO03nTDbadwyTAx");

        utilMap.put(Constants.EventList[3][1],"-KcOAEcqW_WTDOYTUagm");
        utilMap.put(Constants.EventList[3][2],"-KcT4hdwnW4F43TYgjZ5");
        utilMap.put(Constants.EventList[3][3],"-KcT4zKnsEM859q0XyxQ");
        utilMap.put(Constants.EventList[3][4],"-KcOEajfJpch9OTpe2pA");
        utilMap.put(Constants.EventList[3][5],"-KcT4vVO-77sj2yIIZS3");
        utilMap.put(Constants.EventList[3][6],"-KcT4ouBnqgypG2sHMDg");
        utilMap.put(Constants.EventList[3][7],"-KcT5RyDG4SZEYh9DE-W");

        utilMap.put(Constants.EventList[4][1],"-Kcm2EJ4uZ9BakHRrnYd");
        utilMap.put(Constants.EventList[4][2],"-Kcm-OYyNrcCT_a22GyS");
        utilMap.put(Constants.EventList[4][3],"-KcltEupg-EjPBO3710J");
        utilMap.put(Constants.EventList[4][4],"-KclpP0VmbuXITy-ZVjp");
        utilMap.put(Constants.EventList[4][5],"-Kclw0w8SYhwa87y5w0B");
        utilMap.put(Constants.EventList[4][6],"-Kcm1ByvxpqFsBPQX1TH");
        utilMap.put(Constants.EventList[4][7],"-KclzVQB3WsYi7_fdawG");

        utilMap.put(Constants.EventList[5][1],"-Kcm5lk33rOPCuxpcCNN");
        utilMap.put(Constants.EventList[5][2],"-Kcm5ySU37qNFR_A1yXa");
        utilMap.put(Constants.EventList[5][3],"-Kcm5uozyeH7F3EWAP5n");
        utilMap.put(Constants.EventList[5][4],"-Kcm5qeg9QWeFvcgPRvM");
        utilMap.put(Constants.EventList[5][5],"-KcqFQ-pFDndES4gOe1D");
        utilMap.put(Constants.EventList[5][6],"-Kcnv6Z7gKm-6DVjwx2d");
        utilMap.put(Constants.EventList[5][7],"-Kcm3XicUFWzFSXgunpS");

        utilMap.put(Constants.EventList[6][1],"-Kcm5CO23PiVkz38dVZt");
        utilMap.put(Constants.EventList[6][2],"-KcqkXY90l3YuT9v4vlg");
        utilMap.put(Constants.EventList[6][3],"-Kcqk3MT-BDrz1wQ0b9R");
        utilMap.put(Constants.EventList[6][4],"-KcqS7npNlIXSfm_lYux");
        utilMap.put(Constants.EventList[6][5],"-Kcm5_cN-LADSEUKCIXH");
        utilMap.put(Constants.EventList[6][6],"-KcqjDRuR9KSEVMl4Om6");

        utilMap.put(Constants.EventList[7][1],"-KcqDWNFL8F_je4RbpoL");
        utilMap.put(Constants.EventList[7][2],"-KcqCuUogfvbFNJdd6D_");
        utilMap.put(Constants.EventList[7][3],"-KcqEYZAhbyh8s_1wEg8");
        utilMap.put(Constants.EventList[7][4],"-KcqBWmKIs3eDBkLtwsB");
        utilMap.put(Constants.EventList[7][5],"-KcqE4Ygh-A6bZtrY8N_");
        utilMap.put(Constants.EventList[7][6],"-KcqDCTTvIMlf60mMBSl");

        utilMap.put(Constants.EventList[8][1],"-KcTJHpsDmkaKV4sr6UU");
        utilMap.put(Constants.EventList[8][2],"-KciNjwmSIv8XjDw-B74");
        utilMap.put(Constants.EventList[8][3],"-KcTGptq6R0yIy72Nsuk");
        utilMap.put(Constants.EventList[8][4],"-KciNBJESMx6r8Vj5nME");
        utilMap.put(Constants.EventList[8][5],"-KcTL8BlmBl3p4uHDaTV");

        utilMap.put(Constants.EventList[9][1],"-KcmaBRl0FBuXalKvqlS");
        utilMap.put(Constants.EventList[9][2],"-Kcm61EZRD4ynRWQtCA9");
        utilMap.put(Constants.EventList[9][3],"-KcmZSFgMRrVb09lV44M");
        utilMap.put(Constants.EventList[9][4],"-KcqtzYpWM1sCBIdy6Jl");
        utilMap.put(Constants.EventList[9][5],"-KcniduM7CpN86EQkM5c");

        utilMap.put(Constants.EventList[10][1],"-KcQICD7WKTDryvfgQzq");
        utilMap.put(Constants.EventList[10][2],"-KcQLaqMdmyT0-AenWfj");
        utilMap.put(Constants.EventList[10][3],"-KcQNgXtL3OzWD5KAbx9");
        utilMap.put(Constants.EventList[10][4],"-KcQQNGL1gAe0bQbFNAF");
        utilMap.put(Constants.EventList[10][5],"-KcQEcFIxgi2unQrZuw9");

        utilMap.put(Constants.EventList[11][1],"-Kcm23BmlCklhgys6Axt");
        utilMap.put(Constants.EventList[11][2],"-Kcm50DbJS_sImMiPpNg");
        utilMap.put(Constants.EventList[11][3],"-Kcm2hUfPbr-jM9xCMfG");
        utilMap.put(Constants.EventList[11][4],"-KcnoMsA-zvAMz3F5hj1");
        utilMap.put(Constants.EventList[11][5],"-Kcm2k1fQX546UWYAFw7");

        utilMap.put(Constants.EventList[12][1],"-KcQV8SJ2tHJbJ8U87Aq");
        utilMap.put(Constants.EventList[12][2],"-KcQTNIqQlHQetm7yFJ5");
        utilMap.put(Constants.EventList[12][3],"-KcQVnYXGbY-tIqNT80h");
        utilMap.put(Constants.EventList[12][4],"-KcQWT_uXRRUusPal6at");
        utilMap.put(Constants.EventList[12][5],"-KcQUPfaZLm5nr0jTT6q");

        utilMap.put(Constants.EventList[13][1],"-KcT5sw9srOHzxUuMlkk");
        utilMap.put(Constants.EventList[13][2],"-KcT5xmoxYZv3LkuPS23");
        utilMap.put(Constants.EventList[13][3],"-KcT4Y1BjB3FK8QjmFB0");

        utilMap.put(Constants.EventList[14][1],"-KcmYpaGsiyRmvZ6bLOf");
        utilMap.put(Constants.EventList[14][2],"-KchaB1wsmEZ6iGIeRmd");

        utilMap.put(Constants.EventList[15][1],"-KcT5hM_ib_WsQsTkJ1u");

        utilMap.put(Constants.EventList[16][1],"-Kcnk38AOixNn4MFRxs2");

        utilMap.put(Constants.EventList[17][1],"-Kcm2pKC1x43FLG9i7cI");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(EventsActivity.this, "Searching failed", Toast.LENGTH_LONG).show();
            } else {
                openUserDetail(result.getContents(), SEARCH_FLAG_QR);
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        if (isEventSelected){
            if (view.getId()==R.id.search_by_ojass_id_event) {
                String ojassID = ((EditText)findViewById(R.id.et_event_oj_id)).getText().toString().trim();
                if (!TextUtils.isEmpty(ojassID)) openUserDetail(ojassID, SEARCH_FLAG_OJ_ID);
                else Toast.makeText(this, "Field can't be left blank!", Toast.LENGTH_SHORT).show();
            } else if (view.getId()==R.id.search_by_email_event) {
                String emailID = ((EditText)findViewById(R.id.et_event_email_id)).getText().toString().trim();
                if (!TextUtils.isEmpty(emailID)) openUserDetail(emailID, SEARCH_FLAG_EMAIL);
                else Toast.makeText(this, "Field can't be left blank!", Toast.LENGTH_SHORT).show();
            } else if (view.getId()==R.id.search_by_qr_code_event) {
                integrator.initiateScan();
            } else if (view.getId() == R.id.ll_total) getTotalParticipants();
            else if (view.getId() == R.id.tv_event_hash) copyToCipboard(selectedEventHash);
        } else Toast.makeText(this, "Select any event", Toast.LENGTH_SHORT).show();
    }

    private void copyToCipboard(String selectedEventHash) {
        ClipboardManager cb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("label", selectedEventHash);
        cb.setPrimaryClip(data);
        Toast.makeText(this, "Copied!", Toast.LENGTH_SHORT).show();
    }

    private void openUserDetail(String ID, int FLAG){
        Intent intent=new Intent(EventsActivity.this,UsersDetailsActivity.class);
        intent.putExtra(INTENT_PARAM_SEARCH_FLAG, FLAG);
        intent.putExtra(INTENT_PARAM_SEARCH_ID, ID);
        intent.putExtra(INTENT_PARAM_SEARCH_SRC, SRC_EVENT);
        intent.putExtra(INTENT_PARAM_EVENT_NAME, spEvent.getSelectedItem().toString());
        intent.putExtra(INTENT_PARAM_EVENT_HASH, selectedEventHash);
        intent.putExtra(INTENT_PARAM_EVENT_BRANCH, spBranch.getSelectedItem().toString());
        startActivity(intent);
    }


    public void getTotalParticipants() {
        Toast.makeText(this, "Getting total Counts...", Toast.LENGTH_SHORT).show();
        DatabaseReference eventReference = FirebaseDatabase.getInstance().getReference(FIREBASE_REF_EVENTS).child(selectedEventHash).child(FIREBASE_REF_EVENT_PARTICIPANTS);
        eventReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((TextView)findViewById(R.id.tv_total_participants)).setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
