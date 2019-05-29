package com.example.arrozcomfeijao.View;

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
import com.example.arrozcomfeijao.Controller.Usuario;
import com.example.arrozcomfeijao.Helper.RefFirebase;
import com.example.arrozcomfeijao.Model.ConfiguracaoFirebase;
import com.example.arrozcomfeijao.Helper.Preferencias;
import com.example.arrozcomfeijao.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    private FirebaseAuth autentificacao;
    private BootstrapEditText editEmailLogin;
    private BootstrapEditText editSenhaLogin;
    private BootstrapButton btnLogin;
    private Usuario usuario;
    private TextView txtAbreCadastro;
    private TextView txtRecuperarSenha;
    private AlertDialog alerta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Criação de variavéis para armazenar componentes da tela de login
        editEmailLogin =  findViewById(R.id.editEmail);
        editSenhaLogin = findViewById(R.id.editSenha);
        btnLogin =  findViewById(R.id.btnLogin);
        txtAbreCadastro = findViewById(R.id.txtAbreCadastro);
        txtRecuperarSenha = findViewById(R.id.txtRecuperarSenha);

        final EditText editTextEmail = new EditText(Login.this);
        editTextEmail.setHint("example@example.com");



        if (usuarioLogado()) {
            String UID = RefFirebase.getFirebaseAuth().getUid();
            abrirTelaPrincipal(UID);


        }


        txtAbreCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, CadastroCliente.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editEmailLogin.getText().toString().equals("") && !editSenhaLogin.getText().toString().equals("")) {
                    usuario = new Usuario();
                    usuario.setEmail(editEmailLogin.getText().toString());
                    usuario.setSenha(editSenhaLogin.getText().toString());

                    validarLogin();
                } else {
                    Toast.makeText(Login.this, "Preencha os campos de E-mail e senha", Toast.LENGTH_SHORT).show();
                }

            }
        });

        txtRecuperarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
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
                                        Toast.makeText(Login.this, "Em instantes você receberá um e-mail!", Toast.LENGTH_LONG).show();

                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(Login.this, "Falha ao enviar o e-mail", Toast.LENGTH_LONG).show();

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
                    Toast.makeText(Login.this, "Preencha o campo de e-mail!", Toast.LENGTH_LONG).show();
                }
                alerta = builder.create();
                alerta.show();
            }
        });
    }


    private void validarLogin(){
        autentificacao = RefFirebase.getFirebaseAuth();
        autentificacao.signInWithEmailAndPassword(usuario.getEmail().toString(),
                usuario.getSenha().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    abrirTelaPrincipal(usuario.getEmail());
                    Preferencias preferencias = new Preferencias(Login.this);
                    preferencias.salvarUsuarioPreferencias(usuario.getEmail(), usuario.getSenha());
                    Toast.makeText(Login.this,
                            "Login Efetuado com sucesso!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Login.this,
                            "Usuário ou senha inválidos! Tente novamente",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void abrirTelaPrincipal(String UidUser){

        DocumentReference reference = RefFirebase.getFirebaseStoreCliente().collection("Clientes").document(UidUser);
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Usuario usuario = documentSnapshot.toObject(Usuario.class);

                if (usuario.getTipo().equals("Administrador")){
                    Intent intent = new Intent( Login.this, PrincipalAdmin.class);
                    startActivity(intent);
                    finish();

                }else if (usuario.getTipo().equals("Atendente")){
                    Intent intent = new Intent( Login.this, PrincipalFuncionario.class);
                    startActivity(intent);
                    finish();

                }else if (usuario.getTipo().equals("Comum")){
                    Intent intent = new Intent( Login.this, PrincipalCliente.class);
                    startActivity(intent);
                    finish();
                }
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


}
