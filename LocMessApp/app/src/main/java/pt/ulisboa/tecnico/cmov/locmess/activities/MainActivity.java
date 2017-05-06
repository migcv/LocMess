package pt.ulisboa.tecnico.cmov.locmess.activities;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pt.ulisboa.tecnico.cmov.locmess.services.LocationService;
import pt.ulisboa.tecnico.cmov.locmess.utils.CreateConnection;
import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.utils.SocketHandler;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @InjectView(R.id.input_username)
    EditText _usernameText;
    @InjectView(R.id.input_password)
    EditText _passwordText;
    @InjectView(R.id.btn_login)
    Button _loginButton;
    @InjectView(R.id.link_signup)
    TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        this.setTitle("Login");

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CreateConnection cc = new CreateConnection(getApplicationContext());
                cc.execute();
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        //_loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();


        final String username = _usernameText.getText().toString();
        final String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

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
                            dout.writeUTF("Login;:;" + username + ";:;" + password);
                            dout.flush();
                            //dout.close();
                            DataInputStream dis = new DataInputStream(s.getInputStream());
                            str = dis.readUTF();
                            Log.d("SIGNIN", str);

                            if (str.startsWith("OK;:;")) {
                                Log.d("LOGIN", "Success");
                                String[] aux = str.split(";:;");
                                Log.d("TOKEN", aux[1]);
                                SocketHandler.setToken(aux[1]);
                                SocketHandler.setUsername(username);
                                onLoginSuccess();
                            } else {
                                Log.d("LOGIN", "Failed");
                                onLoginFailed();
                            }
                        } catch (Exception e) {
                            Log.d("CONNECTION", "Error socket null!");
                            Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                        // On complete call either onLoginSuccess or onLoginFailed
                        //onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                Intent intent = new Intent(getApplicationContext(), PostsActivity.class);
                startActivityForResult(intent, RESULT_OK);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(), PostsActivity.class);
        startActivityForResult(intent, RESULT_OK);

        startService(new Intent(this, LocationService.class));
        Log.d("SERVICE", "Starting Service!");
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty() || username.length() < 4) {
            _usernameText.setError("enter a valid email address");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        return valid;
    }
}






