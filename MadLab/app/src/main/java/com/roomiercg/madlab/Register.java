package com.roomiercg.madlab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.file.attribute.AttributeView;
import java.util.HashMap;

public class Register extends AppCompatActivity {

    //variables globales
    EditText usuario, correo, contrasena;
    Button btn_register;
    FirebaseAuth auth;
    DatabaseReference reference;
    LinearLayout progress_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Inicializaciones
        usuario = findViewById(R.id.usuario);
        correo = findViewById(R.id.correo);
        contrasena = findViewById(R.id.contrasena);
        btn_register = findViewById(R.id.entrar);
        auth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_usario = usuario.getText().toString();
                String txt_correo = correo.getText().toString();
                String txt_contrasena = contrasena.getText().toString();

                if (TextUtils.isEmpty(txt_usario) || TextUtils.isEmpty(txt_correo) || TextUtils.isEmpty(txt_contrasena)) {
                    Toast.makeText(Register.this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                } else if (txt_contrasena.length() < 6) {
                    Toast.makeText(Register.this, "Minimo 6 caracteres en la contraseña", Toast.LENGTH_SHORT).show();
                } else {
                    register(txt_usario, txt_correo, txt_contrasena);
                }
            }
        });
    }

    private void register(final String usuario, String correo, String contrasena) {
        progressbar(true);
        auth.createUserWithEmailAndPassword(correo, contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String userid = firebaseUser.getUid();

                    //alamacenamos en la base de datos Users el userid del user dado de alta
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", userid);
                    hashMap.put("username", usuario);
                    hashMap.put("imageURL", "default");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(Register.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                } else {
                    Toast.makeText(Register.this, "No te has podido resgistrar con este correo o contraseña", Toast.LENGTH_SHORT).show();
                    progressbar(false);
                }
            }
        });
    }

    public void register(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
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
}
