package com.team4mhd.covandapp;

import static com.team4mhd.covandapp.util.ConfigUtils.IP_SERVIDOR;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.team4mhd.covandapp.ui.gallery.ui.login.LoginActivity;
import com.team4mhd.covandapp.util.ConfigUtils;

/**
 *  Pantalla de configuración del dispositivo.
 *
 *  @author hfunes.com
 */
public class ConfiguracionGeneralActivity extends AppCompatActivity {

    ConfigUtils config = new ConfigUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_general);

        ContentValues pares = config.getConfiguracion(this);

        if(pares != null){
            final EditText nroSerieEditText = findViewById(R.id.ipServidorEditText);
            nroSerieEditText.setText(pares.get(IP_SERVIDOR) != null? pares.get(IP_SERVIDOR).toString() : "");
        }
    }

    /**
     *  Ejecuta el grabado de la configuración en el dispositivo.
     *
     *  @author hfunes.com
     */
    public void grabarConfiguracion(View view){

        final EditText ipServidorEditText = findViewById(R.id.ipServidorEditText);
        String ipServidor = ipServidorEditText.getText().toString();

        config.grabarParConfiguracion(this, IP_SERVIDOR, ipServidor);
        Toast.makeText(ConfiguracionGeneralActivity.this, "Los datos se han guardado correctamente...", Toast.LENGTH_LONG).show();
        mostrarPantallaLogin();

        return;

    }

    /**
     * Redirecciona a la pantalla donde se seleccionará el menú configuración.
     *
     * @author hfunes
     */
    public void mostrarPantallaLogin() {
        Log.i(this.getClass().getSimpleName(), "***** Iniciando intent *****");
        Intent intent = new Intent(ConfiguracionGeneralActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}
