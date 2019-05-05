package com.example.cursoandroidfirebase2.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.cursoandroidfirebase2.Classes.Usuario;
import com.example.cursoandroidfirebase2.DAO.ConfiguracaoFirebase;
import com.example.cursoandroidfirebase2.Helper.Preferencias;
import com.example.cursoandroidfirebase2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autentificacao;
    private BootstrapEditText editEmailLogin;
    private BootstrapEditText editSenhaLogin;
    private BootstrapButton btnLogin;
    private Usuario usuario;
    private TextView txtAbreCadastro;
    private TextView txtRecuperarSenha;
    private AlertDialog alerta;

    private DatabaseReference referenciaFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        editEmailLogin = (BootstrapEditText) findViewById(R.id.editEmail);
        editSenhaLogin = (BootstrapEditText) findViewById(R.id.editSenha);
        btnLogin = (BootstrapButton) findViewById(R.id.btnLogin);
        txtAbreCadastro = (TextView) findViewById(R.id.txtAbreCadastro);
        txtRecuperarSenha = (TextView) findViewById(R.id.txtRecuperarSenha);

        final EditText editTextEmail = new EditText(MainActivity.this);
        editTextEmail.setHint("example@example.com");

        referenciaFirebase = FirebaseDatabase.getInstance().getReference();

        autentificacao = FirebaseAuth.getInstance();

        permission();

        if (usuarioLogado()) {

            String email = autentificacao.getCurrentUser().getEmail().toString();
            abrirTelaPrincipal(email);
            finish();

        } else {

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!editEmailLogin.getText().toString().equals("") && !editSenhaLogin.getText().toString().equals("")) {
                        usuario = new Usuario();
                        usuario.setEmail(editEmailLogin.getText().toString());
                        usuario.setSenha(editSenhaLogin.getText().toString());

                        validarLogin();
                    } else {
                        Toast.makeText(MainActivity.this, "Preencha os campos de E-mail e senha", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        txtAbreCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, cadastroUsuarioComumActivity.class);
                startActivity(intent);
                finish();
            }
        });

        txtRecuperarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(false);
                builder.setTitle("Recuperar senha");
                builder.setMessage("Informe o seu e-mail");
                builder.setView(editTextEmail);

                if(!editTextEmail.getText().equals("")){
                    builder.setPositiveButton("Recuperar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            autentificacao = FirebaseAuth.getInstance();
                            String emailRecuperar = editTextEmail.getText().toString();
                            autentificacao.sendPasswordResetEmail(emailRecuperar).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(MainActivity.this, "Em instantes você receberá um e-mail!", Toast.LENGTH_LONG).show();

                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(MainActivity.this, "Falha ao enviar o e-mail", Toast.LENGTH_LONG).show();

                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                    });

                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    });
                }else{
                    Toast.makeText(MainActivity.this, "Preencha o campo de e-mail!", Toast.LENGTH_LONG).show();
                }
                alerta = builder.create();
                alerta.show();
            }
        });
    }


    private void validarLogin(){
        autentificacao = ConfiguracaoFirebase.getFirebaseAuth();
        autentificacao.signInWithEmailAndPassword(usuario.getEmail().toString(),
                usuario.getSenha().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    abrirTelaPrincipal(usuario.getEmail());
                    Preferencias preferencias = new Preferencias(MainActivity.this);
                    preferencias.salvarUsuarioPreferencias(usuario.getEmail(), usuario.getSenha());
                    Toast.makeText(MainActivity.this,
                            "Login Efetuado com sucesso!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Usuário ou senha inválidos! Tente novamente",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void abrirTelaPrincipal(String emailUsuario){

        String email = autentificacao.getCurrentUser().getEmail().toString();

        referenciaFirebase.child("usuarios").orderByChild("email").equalTo(email.toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String tipoUsuarioEmail = postSnapshot.child("tipo").getValue().toString();

                    if (tipoUsuarioEmail.equals("Administrador")){
                        Intent intent = new Intent( MainActivity.this, PrincipalActivity.class);
                        startActivity(intent);
                        finish();

                    }else if (tipoUsuarioEmail.equals("Atendente")){
                        Intent intent = new Intent( MainActivity.this, PrincipalActivityAtendente.class);
                        startActivity(intent);
                        finish();

                    }else if (tipoUsuarioEmail.equals("Comum")){
                        Intent intent = new Intent( MainActivity.this, PrincipalActivityComum.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public Boolean usuarioLogado(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            return true;
        }else{
            return false;
        }
    }

    public void abrirNovaActivity (Intent intent){
        startActivity(intent);
        finish();
    }

    public void permission(){
        int PERMISSION_ALL = 1;
        String [] PERMISSSION = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, PERMISSSION, PERMISSION_ALL);
    }
}
