package ojassadmin.nitjsr.in.ojassadmin;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_NOTIFICATIONS;

public class NotificationActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etTitle, etBody;
    private Spinner spChannel;
    private Button btnSend;
    private DatabaseReference notiRef;
    private RequestQueue queue;
    private static final String VOLLEY_TAG = "VolleyTag";
    private static final String FCM_KEY = "AAAAX90eYv8:APA91bG_JJUSsjVJfntkCsVDGn-_0oecmrV4QX-fOeqP2WZr6R8bSlUX8_4NyAlg6ElfzqYqQSkK-ctRZ4zHh21ziPDpqR6wSl_w4A4k_a9GdyvN5B3--qNeUI6zn80HOkNrKgLg5irD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        queue  =Volley.newRequestQueue(this);

        btnSend = findViewById(R.id.btn_send);
        etTitle = findViewById(R.id.et_noti_title);
        etBody = findViewById(R.id.et_noti_body);
        spChannel = findViewById(R.id.sp_noti_channel);

        notiRef = FirebaseDatabase.getInstance().getReference(FIREBASE_REF_NOTIFICATIONS);

        btnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btnSend){
            sendPushNotification();
        }
    }

    void sendPushNotification() {
        String topic = spChannel.getSelectedItem().toString();
        if(!TextUtils.isEmpty(etTitle.getText()) && !TextUtils.isEmpty(etBody.getText())) {
            FeedModel d = new FeedModel(etTitle.getText().toString(), etBody.getText().toString());
            notiRef.child(topic).child(""+System.currentTimeMillis()/1000).setValue(d);
            Toast.makeText(getApplication(),"Notification Sent",Toast.LENGTH_SHORT).show();
            pushToDevice(topic, etTitle.getText().toString(), etBody.getText().toString());
            etTitle.setText("");
            etBody.setText("");
        } else {
            Toast.makeText(getApplication(),"Field can't be left blank!",Toast.LENGTH_SHORT).show();
        }
    }

    private void pushToDevice(String topic, String title, String body) {
        try {

            JSONObject data=new JSONObject();
            data.put("body",title+"\n"+body);
            data.put("title",topic);

            JSONObject feilds = new JSONObject();
            feilds.put("to","/topics/"+topic);
            feilds.put("notification",data);

            final String requestBody = feilds.toString();
            final String url="https://fcm.googleapis.com/fcm/send";
            StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header=new HashMap<>();
                    header.put("Authorization","key="+FCM_KEY);
                    header.put("Content-Type","application/json");
                    return header;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        uee.printStackTrace();
                        return null;
                    }
                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

            };
            stringRequest.addMarker(VOLLEY_TAG);
            queue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        queue.cancelAll(VOLLEY_TAG);
    }
}
