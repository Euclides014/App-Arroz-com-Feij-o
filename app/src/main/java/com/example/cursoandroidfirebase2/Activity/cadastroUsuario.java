package com.example.cursoandroidfirebase2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class cadastroUsuario extends AppCompatActivity {

    private BootstrapEditText email;
    private BootstrapEditText senha1;
    private BootstrapEditText senha2;
    private BootstrapEditText nome;
    private RadioButton rbAdmin;
    private RadioButton rbAtend;
    private BootstrapButton btnCadastrar;
    private BootstrapButton btnCancelar;
    private FirebaseAuth autenticacao;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private BootstrapEditText CPF;
    private BootstrapEditText rua;
    private BootstrapEditText numero;
    private BootstrapEditText bairro;
    private RadioButton rbFeminino;
    private RadioButton rbMasculino;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        email = (BootstrapEditText) findViewById(R.id.edtCadEmail);
        senha1 = (BootstrapEditText) findViewById(R.id.edtCadSenha1);
        senha2 = (BootstrapEditText) findViewById(R.id.edtCadSenha2);
        nome = (BootstrapEditText) findViewById(R.id.edtCadNome);
        CPF = (BootstrapEditText) findViewById(R.id.edtCadCPFUsuario);
        rua = (BootstrapEditText) findViewById(R.id.edtCadRuaUsuario);
        numero = (BootstrapEditText) findViewById(R.id.edtCadNumeroUsuario);
        bairro = (BootstrapEditText) findViewById(R.id.edtCadBairroUsuario);
        rbAdmin = (RadioButton) findViewById(R.id.rbAdmin);
        rbAtend = (RadioButton) findViewById(R.id.rbAtend);
        btnCadastrar = (BootstrapButton) findViewById(R.id.btnCadastrar);
        btnCancelar = (BootstrapButton) findViewById(R.id.btnCancela);
        rbFeminino = (RadioButton) findViewById(R.id.rbFemale);
        rbMasculino = (RadioButton) findViewById(R.id.rbMale);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (senha1.getText().toString().equals(senha2.getText().toString())){
                    usuario = new Usuario();
                    usuario.setEmail(email.getText().toString());
                    usuario.setSenha(senha1.getText().toString());
                    usuario.setNome(nome.getText().toString());
                    usuario.setCPF(CPF.getText().toString());
                    usuario.setRua(rua.getText().toString());
                    usuario.setNumero(numero.getText().toString());
                    usuario.setBairro(bairro.getText().toString());

                    if(rbAdmin.isChecked()){
                        usuario.setTipo("Administrador");
                    }else if(rbAtend.isChecked()){
                        usuario.setTipo("Atendente");
                    }else if(rbMasculino.isChecked()){
                        usuario.setSexo("Masculino");
                    }else if(rbFeminino.isChecked()){
                        usuario.setSexo("Feminino");
                    }

                    cadUsuario();
                }else{
                    Toast.makeText(cadastroUsuario.this, "As senha não se correspondem", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void cadUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(cadastroUsuario.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    insereUsuario(usuario);
                    finish();
                    //deslogar ao adicionar o usuario
                    autenticacao.signOut();
                    //para abrir a tela principal após a reautenticacao
                    abreTelaPrincipal();
                }else {

                    String erroExcecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        erroExcecao = "Digite uma senha mais forte, contendo no mínimo 8 caracteres e que contenha letras e números!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erroExcecao = "Email inválido, digite novamente!";

                    }catch (FirebaseAuthUserCollisionException e) {
                        erroExcecao = "Esse e-mail já está cadastrado";

                    }catch (Exception e) {
                        erroExcecao = "Erro ao efetuar o cadastro!";
                        e.printStackTrace();
                    }

                    Toast.makeText(cadastroUsuario.this,"Erro: " + erroExcecao, Toast.LENGTH_SHORT).show();;
                }
            }
        });
    }

    private boolean insereUsuario(Usuario usuario){
        try{
            reference = ConfiguracaoFirebase.getFirebase().child("usuarios");
            String key = reference.push().getKey();
            usuario.setKeyUsuario(key);
            reference.push().setValue(usuario);
            Toast.makeText(cadastroUsuario.this,"Usuário cadastrado com sucesso", Toast.LENGTH_SHORT).show();
            return true;
        }catch (Exception e){
            Toast.makeText(cadastroUsuario.this,"Erro ao gravar o usuário!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return  false;
        }
    }

    private void abreTelaPrincipal(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        Preferencias preferencias = new Preferencias(cadastroUsuario.this);
        autenticacao.signInWithEmailAndPassword(preferencias.getEmailUsuarioLogado(), preferencias.getSenhaUsuarioLogado()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    Intent intent = new Intent(cadastroUsuario.this, PrincipalActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(cadastroUsuario.this, "Falha", Toast.LENGTH_LONG).show();
                    autenticacao.signOut();
                    Intent intent = new Intent(cadastroUsuario.this, MainActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });
    }
}
