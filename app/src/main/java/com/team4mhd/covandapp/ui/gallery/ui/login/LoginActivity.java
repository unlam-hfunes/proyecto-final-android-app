package com.team4mhd.covandapp.ui.gallery.ui.login;

import static com.team4mhd.covandapp.util.ConfigUtils.IP_SERVIDOR;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.team4mhd.covandapp.ConfiguracionGeneralActivity;
import com.team4mhd.covandapp.MainActivity;
import com.team4mhd.covandapp.R;
import com.team4mhd.covandapp.databinding.ActivityLoginBinding;
import com.team4mhd.covandapp.util.ConfigUtils;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class LoginActivity extends AppCompatActivity {

    public static final int MY_DEFAULT_TIMEOUT = 15000;
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    int huevoPascuaCount = 0;

    ConfigUtils config = new ConfigUtils();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                //loginViewModel.login(usernameEditText.getText().toString(), passwordEditText.getText().toString());

                //login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                mostrarPantallaPrincipal();
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        //Abro la aplicación
        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
        //myIntent.putExtra("key", value);
        LoginActivity.this.startActivity(myIntent);
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    public void login(String username, String password) {

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        String ipServidor = "";
        ContentValues pares = config.getConfiguracion(this);

        if(pares != null)
            ipServidor = pares.get(IP_SERVIDOR) != null? pares.get(IP_SERVIDOR).toString() : "";

        if(pares == null || "".equals(ipServidor))
            Toast.makeText(getApplicationContext(), "Debe configurar la IP del servidor para poder autenticarse...", Toast.LENGTH_LONG).show();


        String url = "https://" + ipServidor + "/Usuarios/LoginMobile";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("LoginActivity", response.toString());
                        String welcome = getString(R.string.welcome) + "ELMER";
                        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
                        //Abro la aplicación
                        mostrarPantallaPrincipal();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("LoginActivity", error.toString());
                        String welcome = "Ha ocurrido un error, vuelva a intentarlo...";
                        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
                        Intent myIntent = new Intent(LoginActivity.this, LoginActivity.class);
                        LoginActivity.this.startActivity(myIntent);
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("UserName", username);
                params.put("PasswordHash", password);
                return params;
            }
        };

        strRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(strRequest);

    }

    /**
     * Redirecciona a la pantalla donde se seleccionará el modo de conexión.
     *
     * @author hfunes
     */
    public void mostrarPantallaPrincipal() {
        Log.i(this.getClass().getSimpleName(), "***** Iniciando intent *****");
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        //myIntent.putExtra("key", value);
        //LoginActivity.this.startActivity(myIntent);
        startActivity(intent);
    }

    /**
     * Redirecciona a la pantalla donde se seleccionará el modo de conexión.
     *
     * @author hfunes
     */
    public void mostrarPantallaConfiguracion() {
        Log.i(this.getClass().getSimpleName(), "***** Iniciando intent *****");
        Intent intent = new Intent(LoginActivity.this, ConfiguracionGeneralActivity.class);
        startActivity(intent);
    }

    /**
     * Redirecciona a la pantalla donde se ingresará la url del server.
     *
     * @author hfunes
     */
    public void mostrarPantallaConfiguracion(View view) {
        mostrarPantallaConfiguracion();
    }

    public void activarHuevoPascua(View view){
        huevoPascuaCount++;
        if(huevoPascuaCount == 5){
            mostrarPantallaConfiguracion();
        }
    }
}
