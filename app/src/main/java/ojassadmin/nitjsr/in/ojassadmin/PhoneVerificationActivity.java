package ojassadmin.nitjsr.in.ojassadmin;

import android.content.Intent;
import android.os.Build;                        
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.ui.SkinManager;
import com.facebook.accountkit.ui.UIManager;


public class PhoneVerificationActivity extends AppCompatActivity {
    public static int APP_REQUEST_COD = 999;
    private static final String TAG = "LoginActivity";
    private SharedPrefManager sharedPrefManager;
    String phoneNumberString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phoneverification);
        sharedPrefManager = new SharedPrefManager(this);
       if(sharedPrefManager.isPhoneVerified()) {
            moveToRegisterActivity();
        }
        AccountKit.initialize(getApplicationContext());
        getAccount();






    }
private void getAccount(){
    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
        @Override
        public void onSuccess(final Account account) {
            // Get Account Kit ID
            String accountKitId = account.getId();

            // Get phone number
            PhoneNumber phoneNumber = account.getPhoneNumber();
            phoneNumberString = phoneNumber.toString();

            // Surface the result to your user in an appropriate way.
            sharedPrefManager.setIsPhoneVerified(true);

            moveToRegisterActivity();
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
            // Handle Error

    });
}

    public void phoneLogin(@Nullable View view) {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder = new AccountKitConfiguration.AccountKitConfigurationBuilder(
                LoginType.PHONE, AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.CODE
        configurationBuilder.setDefaultCountryCode("IN");

        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_COD);
    }

    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_COD) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
                //showErrorActivity(loginResult.getError());
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
            } else {
                if (loginResult.getAccessToken() != null) {
                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                } else {
                    toastMessage = String.format(
                            "Success:%s...",
                            loginResult.getAuthorizationCode().substring(0,10));
                }

                // If you have an authorization code, retrieve it from
                // loginResult.getAuthorizationCode()
                // and pass it to your server and exchange it for an access token.

                // Success! Start your next activity...

                getAccount();
                sharedPrefManager.setIsPhoneVerified(true);

                moveToRegisterActivity();


            }


        }
    }
    private void moveToRegisterActivity() {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

}
