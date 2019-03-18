package ojassadmin.nitjsr.in.ojassadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.WatchEvent;

import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_EVENTS;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_EVENT_PARTICIPANTS;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_KIT;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_NAME;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_OJASS_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_PAID_AMOUNT;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_RECEIVED_BY;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_TSHIRT;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_TSHIRT_SIZE;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_USERS;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.TSHIRT_SIZE;

public class DBInfoActivity extends AppCompatActivity {


    private DatabaseReference userRef;
    private ProgressDialog pd;
    private static final String OLD_TIMESTAMP = "1521132371453";
    CSVWriter writer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbinfo);

        pd = new ProgressDialog(this);
        pd.setTitle("Please Wait");
        pd.setMessage("Loading...");
        pd.show();

        userRef = FirebaseDatabase.getInstance().getReference(FIREBASE_REF_USERS);
        runMasterQuery();

        findViewById(R.id.export_excel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                exportToExcel();
            }
        });

    }

    private void exportToExcel() {
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "OjassData.csv";
        String filePath = baseDir + File.separator + fileName;
        File f = new File(filePath);
        FileWriter mFileWriter;
        try {
            if (f.exists() && !f.isDirectory()) {
                mFileWriter = new FileWriter(filePath, true);
                writer = new CSVWriter(mFileWriter);
            }
            else writer = new CSVWriter(new FileWriter(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot userData : dataSnapshot.getChildren()){
                    if (userData.child(FIREBASE_REF_OJASS_ID).exists()){
                        count++;
                        String row[] = new String[2];
                        row[0] = userData.child(FIREBASE_REF_OJASS_ID).getValue().toString();
                        row[1] = userData.child(FIREBASE_REF_NAME).getValue().toString();
                        writer.writeNext(row);
                    }
                }
                //Toast.makeText(DBInfoActivity.this, ""+count, Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void runMasterQuery(){
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int paidCount = 0, paid300 = 0, paid350 = 0, paid500 = 0;
                int kitCount = 0, totalTshirtCount = 0;
                int totalAmount = 0;
                int[] tshirt = new int[6];
                int[] tshirtPaid = new int[6];
                int[] tshirtGiven = new int[6];
                for (int i = 0 ; i < 6 ; i++){
                    tshirt[i] = 0;
                    tshirtPaid[i] = 0;
                    tshirtGiven[i] = 0;
                }
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    if (data.child(FIREBASE_REF_PAID_AMOUNT).exists()) {
                        paidCount++;
                        totalAmount += Integer.parseInt(data.child(FIREBASE_REF_PAID_AMOUNT).getValue().toString());
                        if (data.child(FIREBASE_REF_PAID_AMOUNT).getValue().toString().equals("300")) paid300++;
                        else if (data.child(FIREBASE_REF_PAID_AMOUNT).getValue().toString().equals("350")) paid350++;
                        else if (data.child(FIREBASE_REF_PAID_AMOUNT).getValue().toString().equals("500")) paid500++;
                        for (int i = 0 ; i < TSHIRT_SIZE.length; i++){
                            if (data.child(FIREBASE_REF_TSHIRT_SIZE).getValue().equals(TSHIRT_SIZE[i])){
                                tshirtPaid[i]++;
                                if (data.child(FIREBASE_REF_TSHIRT).exists()) {
                                    tshirtGiven[i]++;
                                    totalTshirtCount++;
                                }
                                break;
                            }
                        }
                    }
                    for (int i = 0 ; i < TSHIRT_SIZE.length; i++){
                        if (data.child(FIREBASE_REF_TSHIRT_SIZE).getValue().equals(TSHIRT_SIZE[i])) {
                            tshirt[i]++;
                            break;
                        }
                    }
                    if (data.child(FIREBASE_REF_KIT).exists()) kitCount++;
                }
                ((TextView)findViewById(R.id.tv_info_total)).setText(""+dataSnapshot.getChildrenCount());
                ((TextView)findViewById(R.id.tv_info_xs)).setText(tshirt[0]+" - "+tshirtPaid[0]+" - "+tshirtGiven[0]);
                ((TextView)findViewById(R.id.tv_info_s)).setText(tshirt[1]+" - "+tshirtPaid[1]+" - "+tshirtGiven[1]);
                ((TextView)findViewById(R.id.tv_info_m)).setText(tshirt[2]+" - "+tshirtPaid[2]+" - "+tshirtGiven[2]);
                ((TextView)findViewById(R.id.tv_info_l)).setText(tshirt[3]+" - "+tshirtPaid[3]+" - "+tshirtGiven[3]);
                ((TextView)findViewById(R.id.tv_info_xl)).setText(tshirt[4]+" - "+tshirtPaid[4]+" - "+tshirtGiven[4]);
                ((TextView)findViewById(R.id.tv_info_xxl)).setText(tshirt[5]+" - "+tshirtPaid[5]+" - "+tshirtGiven[5]);
                ((TextView)findViewById(R.id.tv_info_paid_500)).setText(paid500 + " ( \u20B9" + (paid500 * 500) + ")");
                ((TextView)findViewById(R.id.tv_info_paid_350)).setText(paid350 + " ( \u20B9" + (paid350 * 350) + ")");
                ((TextView)findViewById(R.id.tv_info_paid_300)).setText(paid300 + " ( \u20B9" + (paid300 * 300) + ")");
                ((TextView)findViewById(R.id.tv_info_paid_total)).setText(paidCount + " ( \u20B9"+totalAmount+")");
                ((TextView)findViewById(R.id.tv_info_tshirt_given)).setText(""+totalTshirtCount);
                ((TextView)findViewById(R.id.tv_info_kit_given)).setText(""+kitCount);
                if (pd.isShowing()) pd.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*private void prepareInfo() {
        prepareTotal();
        runMasterQuery();
        prepareTshirtInfo("XS");
        prepareTshirtInfo("S");
        prepareTshirtInfo("M");
        prepareTshirtInfo("L");
        prepareTshirtInfo("XL");
        prepareTshirtInfo("XXL");
        preparePaidInfo("500");
        preparePaidInfo("350");
        preparePaidInfo("300");
        prepareTotalPaidInfo();
        prepareTshirtGivenInfo();
        prepareKitGivenInfo();
    }

    private void prepareTotal() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((TextView)findViewById(R.id.tv_info_total)).setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void prepareTshirtInfo(final String s) {
        Query query = userRef.orderByChild(FIREBASE_REF_TSHIRT_SIZE).equalTo(s);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                switch (s){
                    case "XS" : ((TextView)findViewById(R.id.tv_info_xs)).setText(""+dataSnapshot.getChildrenCount());
                        break;
                    case "S" : ((TextView)findViewById(R.id.tv_info_s)).setText(""+dataSnapshot.getChildrenCount());
                        break;
                    case "M" : ((TextView)findViewById(R.id.tv_info_m)).setText(""+dataSnapshot.getChildrenCount());
                        break;
                    case "L" : ((TextView)findViewById(R.id.tv_info_l)).setText(""+dataSnapshot.getChildrenCount());
                        break;
                    case "XL" : ((TextView)findViewById(R.id.tv_info_xl)).setText(""+dataSnapshot.getChildrenCount());
                        break;
                    case "XXL" : ((TextView)findViewById(R.id.tv_info_xxl)).setText(""+dataSnapshot.getChildrenCount());
                        break;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void preparePaidInfo(final String amount) {
        Query query = userRef.orderByChild(FIREBASE_REF_PAID_AMOUNT).equalTo(amount);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                switch (amount){
                    case "500" :
                        ((TextView)findViewById(R.id.tv_info_paid_500)).setText(count + " ( \u20B9" + (count * 500) + ")");
                        break;
                    case "350" :
                        ((TextView)findViewById(R.id.tv_info_paid_350)).setText(count + " ( \u20B9" + (count * 350) + ")");
                        break;
                    case "300" :
                        ((TextView)findViewById(R.id.tv_info_paid_300)).setText(count + " ( \u20B9" + (count * 300) + ")");
                        break;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void prepareTshirtGivenInfo() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    if (dataSnapshot1.child(FIREBASE_REF_TSHIRT).exists()) count++;
                }
                ((TextView)findViewById(R.id.tv_info_tshirt_given)).setText(""+count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void prepareKitGivenInfo() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    if (dataSnapshot1.child(FIREBASE_REF_KIT).exists()){
                        count++;
                    }
                }
                ((TextView)findViewById(R.id.tv_info_kit_given)).setText(""+count);
                if (pd.isShowing()) pd.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void prepareTotalPaidInfo() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                long totalAmount = 0l;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    if (dataSnapshot1.child(FIREBASE_REF_PAID_AMOUNT).exists()){
                        totalAmount += Long.parseLong(dataSnapshot1.child(FIREBASE_REF_PAID_AMOUNT).getValue().toString());
                        count++;
                    }
                }
                ((TextView)findViewById(R.id.tv_info_paid_total)).setText(""+count+" ( \u20B9"+totalAmount+")");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/

}
