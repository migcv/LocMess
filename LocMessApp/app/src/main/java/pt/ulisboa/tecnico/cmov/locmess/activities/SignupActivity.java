package pt.ulisboa.tecnico.cmov.locmess.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.services.LocationService;
import pt.ulisboa.tecnico.cmov.locmess.utils.CreateConnection;
import pt.ulisboa.tecnico.cmov.locmess.utils.SocketHandler;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.input_passwordConf) EditText _passwordConfText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTitle("Sign Up");

        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateConnection cc = new CreateConnection(getApplicationContext());
                cc.execute();
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String name = _nameText.getText().toString();
        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        String str = "";
                        try {
                            Socket s = SocketHandler.getSocket();
                            Log.d("CONNECTION", "Connection successful!");
                            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                            dout.writeUTF("SignUp;:;" + name + ";:;" + password + ";:; " + email);
                            dout.flush();
                            //dout.close();
                            DataInputStream dis = new DataInputStream(s.getInputStream());
                            str = dis.readUTF();
                            Log.d("SIGNUP", str);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(str.contains(":Username")){
                            onSignupFailedUsername();
                        }
                        else if(str.contains("Email")){
                            onSignupFailedEmail();
                        }
                        else if(str.contains("OK;:;")){
                            String[] aux = str.split(";:;");
                            SocketHandler.setToken(aux[1]);
                            SocketHandler.setUsername(name);
                            onSignupSuccess();
                        }
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        //
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);

        Intent intent = new Intent(getApplicationContext(), PostsActivity.class);
        startActivityForResult(intent, RESULT_OK);

        startService(new Intent(this, LocationService.class));
        Log.d("SERVICE", "Starting Service!");
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sign up failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public void onSignupFailedUsername() {
        Toast.makeText(getBaseContext(), "Sign up failed, Username already exists", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }
    public void onSignupFailedEmail() {
        Toast.makeText(getBaseContext(), "Sign up failed, Email already exists", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String passwordConf = _passwordConfText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (!password.equals(passwordConf)) {
            _passwordConfText.setError("the password is different");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
