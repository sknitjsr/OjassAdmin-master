package ojassadmin.nitjsr.in.ojassadmin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener{

    private DatabaseReference eventReference=FirebaseDatabase.getInstance().getReference("tempEvent");
    private DatabaseReference userParticipatedEventsReference=FirebaseDatabase.getInstance().getReference("Users");
    private DatabaseReference userDataRef= FirebaseDatabase.getInstance().getReference("Users");
    private FirebaseAuth firebaseAuth;

    private Bundle bundle;
    private String ID;
    private String Number;

    private Button btnAdd;
    private TextView userName,userBranch,userRegID;
    private String[] arr;
    private Spinner spinner;
    private String eventSelected;
    private String eventKey;
    private String userID=null;
    private String userOjassID=null;
    private String userEmailID=null;

    HashMap<String,String> hashMap=new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        firebaseAuth=FirebaseAuth.getInstance();

        bundle=getIntent().getExtras();
        ID=bundle.getString("ID");
        Number=bundle.getString("Number");

        userName=(TextView)findViewById(R.id.user_name);
        userBranch=(TextView)findViewById(R.id.user_branch);
        userRegID=(TextView)findViewById(R.id.user_reg_id);

        hashMap.keySet().toArray();
        hashMap.values().toArray();

        btnAdd=(Button)findViewById(R.id.btn_add_events);
        spinner = findViewById(R.id.spinner_events);


        DatabaseReference event=FirebaseDatabase.getInstance().getReference("tempEvent");
        event.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arr = new String[(int) dataSnapshot.getChildrenCount()];
                int currIndex = 0;
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    hashMap.put(dataSnapshot1.child("eventNAme").getValue().toString(),dataSnapshot1.getKey());
                    arr[currIndex] = dataSnapshot1.child("eventNAme").getValue().toString();
                    currIndex++;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(DetailsActivity.this,
                        android.R.layout.simple_list_item_1, arr);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position>0)
                {
                    eventSelected=parent.getItemAtPosition(position).toString();
                    eventKey=hashMap.get(eventSelected);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (Number.equals("1"))
        {
            userDataRef.orderByChild("ojassID").equalTo(ID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child: dataSnapshot.getChildren())
                    {
                        userID=child.getKey();
                        userEmailID=child.child("email").getValue().toString();
                        userOjassID=child.child("ojassID").getValue().toString();
                        userName.setText(child.child("name").getValue().toString());
                        userBranch.setText(child.child("branch").getValue().toString());
                        userRegID.setText(child.child("regID").getValue().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        if (Number.equals("2"))
        {
            userDataRef.orderByChild("email").equalTo(ID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child: dataSnapshot.getChildren())
                    {
                        userID=child.getKey();
                        userEmailID=child.child("email").getValue().toString();
                        userOjassID=child.child("ojassID").getValue().toString();
                        userName.setText(child.child("name").getValue().toString());
                        userBranch.setText(child.child("branch").getValue().toString());
                        userRegID.setText(child.child("regID").getValue().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        if (Number.equals("3"))
        {
            userID=ID;
            userDataRef.child(ID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userEmailID=dataSnapshot.child("email").getValue().toString();
                    userOjassID=dataSnapshot.child("ojassID").getValue().toString();
                    userName.setText(dataSnapshot.child("name").getValue().toString());
                    userBranch.setText(dataSnapshot.child("branch").getValue().toString());
                    userRegID.setText(dataSnapshot.child("regID").getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        btnAdd.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (!userID.equals(null))
        {
            userParticipatedEventsReference.child(userID).child("events").child(eventKey).child("participatedEvent").setValue(eventSelected);
            userParticipatedEventsReference.child(userID).child("events").child(eventKey).child("eventResult").setValue("Not declared");
            eventReference.child(eventKey).child("Participants").child(userID).child("email").setValue(userEmailID);
            eventReference.child(eventKey).child("Participants").child(userID).child("ojassID").setValue(userOjassID);
        }
    }
}
