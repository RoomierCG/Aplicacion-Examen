package com.roomiercg.madlab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    EditText correo, contrasena;
    Button btn_login;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    LinearLayout progress_layout;

    @Override
    protected void onStart() {
        //Miramos si el usuario ya se ha logeado anteriormente, si es asi lo redirijimos al MainActivity
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            Intent intent = new Intent(Login.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inicializaciones
        correo = findViewById(R.id.correoL);
        contrasena = findViewById(R.id.contrasenaL);
        btn_login = findViewById(R.id.entrar);
        auth = FirebaseAuth.getInstance();

        //Si el usuario ha dado al boton de login
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_correo = correo.getText().toString();
                String txt_contrasena = contrasena.getText().toString();

                if (TextUtils.isEmpty(txt_correo) || TextUtils.isEmpty(txt_contrasena)){
                    Toast.makeText(Login.this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                }else{

                    progressbar(true);
                    auth.signInWithEmailAndPassword(txt_correo,txt_contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }else{
                                progressbar(false);
                                Toast.makeText(Login.this, "No se ha pidido iniciar sesion", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                }
        });
    }

    public void progressbar(boolean inicio) {
        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        LayoutInflater inflater = LayoutInflater.from(this);
        int progressBar = R.layout.progressbar;
        progress_layout = (LinearLayout) inflater.inflate(progressBar, null, false);
        final ConstraintLayout contenedor = (ConstraintLayout) findViewById(R.id.backgroundR);

        progress_layout.setMinimumHeight(contenedor.getMaxHeight());
        progress_layout.setMinimumWidth(contenedor.getMaxWidth());

        if (inicio) {
            contenedor.addView(progress_layout);
        } else {
            //Solo funciona una vez
            contenedor.removeView(progress_layout);
        }
    }

    public void register(View view){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }
}
