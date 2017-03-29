package ca.beogotechnologies.deliverymanager_mobileapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import ca.beogotechnologies.deliverymanager_mobileapp.R;
import ca.beogotechnologies.deliverymanager_mobileapp.asyncTasks.UserInfoTask;
import ca.beogotechnologies.deliverymanager_mobileapp.domain.User;

import static ca.beogotechnologies.deliverymanager_mobileapp.util.LoginUtil.CURRENT_USER_EMAIL;
import static ca.beogotechnologies.deliverymanager_mobileapp.util.LoginUtil.CURRENT_USER_FIRST_NAME;
import static ca.beogotechnologies.deliverymanager_mobileapp.util.LoginUtil.CURRENT_USER_ID;
import static ca.beogotechnologies.deliverymanager_mobileapp.util.LoginUtil.CURRENT_USER_LAST_NAME;
import static ca.beogotechnologies.deliverymanager_mobileapp.util.LoginUtil.CURRENT_USER_PASSWORD;
import static ca.beogotechnologies.deliverymanager_mobileapp.util.LoginUtil.CURRENT_USER_PHONE;
import static ca.beogotechnologies.deliverymanager_mobileapp.util.LoginUtil.CURRENT_USER_USER_NAME;
import static ca.beogotechnologies.deliverymanager_mobileapp.util.LoginUtil.MyPREFERENCES;
import static ca.beogotechnologies.deliverymanager_mobileapp.util.LoginUtil.USER_SESSION_ID;

public class UserInfoActivity extends AppCompatActivity implements UserInfoTask.AsyncResponse {
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmail;
    private EditText mPhone;
    private EditText mUserName;
    private EditText mPassword;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        initViews();

        setData();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setData() {
        //get user session id in shared preferences
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String firstName = sharedpreferences.getString(CURRENT_USER_FIRST_NAME, "");
        String lastName = sharedpreferences.getString(CURRENT_USER_LAST_NAME, "");
        String email = sharedpreferences.getString(CURRENT_USER_EMAIL, "");
        String phone = sharedpreferences.getString(CURRENT_USER_PHONE, "");
        String userName = sharedpreferences.getString(CURRENT_USER_USER_NAME, "");
        String password = sharedpreferences.getString(CURRENT_USER_PASSWORD, "");

        mFirstName.setText(firstName);
        mLastName.setText(lastName);
        mEmail.setText(email);
        mPhone.setText(phone);
        mUserName.setText(userName);
        mPassword.setText(password);
    }

    private void initViews() {
        mFirstName = (EditText) findViewById(R.id.txtFirstName);
        mLastName = (EditText) findViewById(R.id.txtLastName);
        mEmail = (EditText) findViewById(R.id.txtEmail);
        mPhone = (EditText) findViewById(R.id.txtPhone);
        mUserName = (EditText) findViewById(R.id.txtUserName);
        mPassword = (EditText) findViewById(R.id.txtPassword);
    }

    public void updateUserInfo(View view) {
        if (validateData())
        {
            SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            String userSessionId = sharedpreferences.getString(USER_SESSION_ID, "");
            String userId = sharedpreferences.getString(CURRENT_USER_ID, "");

            String userName = mUserName.getText().toString();
            String password = mPassword.getText().toString();
            String email = mEmail.getText().toString();
            String phone = mPhone.getText().toString();

            UserInfoTask task = new UserInfoTask(this);
            task.execute(userSessionId, userId, email, phone, userName, password);
        }
    }

    public void onCancel(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private boolean validateData() {
        boolean isValid = true;

        String email = mEmail.getText().toString();
        String userName = mUserName.getText().toString();
        String password = mPassword.getText().toString();

        if (userName.isEmpty())
        {
            mUserName.setError("Le champs ne doit pas etre vide");
            isValid = false;
        }
        if (password.isEmpty())
        {
            mPassword.setError("Le champs ne doit pas etre vide");
            isValid = false;
        }
        if (!email.isEmpty() && !email.contains("@"))
        {
            mEmail.setError("Email invalide");
            isValid = false;
        }

        return isValid;
    }

    @Override
    public void processFinish(String result) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), "Bien enregistre", Snackbar.LENGTH_LONG);
        snackbar.show();
//        Toast.makeText(getApplicationContext(), , Toast.LENGTH_LONG).show();

        try
        {

            JSONObject jsonObject = new JSONObject(result);

            SharedPreferences preferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            //update user info in shared preferences
            User user = new Gson().fromJson(jsonObject.get("user").toString(), User.class);
            editor.putString(CURRENT_USER_PHONE, user.getPhone());
            editor.putString(CURRENT_USER_EMAIL, user.getEmail());
            editor.putString(CURRENT_USER_USER_NAME, user.getUserName());
            editor.putString(CURRENT_USER_PASSWORD, user.getPassword());

            editor.apply();
        }
        catch (JSONException ex)
        {

        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, 0);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("UserInfo Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
