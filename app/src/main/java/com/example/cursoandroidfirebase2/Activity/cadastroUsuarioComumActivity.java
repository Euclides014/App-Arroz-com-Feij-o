package com.example.cursoandroidfirebase2.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class cadastroUsuarioComumActivity extends AppCompatActivity {

    private BootstrapEditText email;
    private BootstrapEditText senha1;
    private BootstrapEditText senha2;
    private BootstrapEditText nome;
    private BootstrapEditText CPF;
    private BootstrapEditText rua;
    private BootstrapEditText numero;
    private BootstrapEditText bairro;
    private RadioButton rbFeminino;
    private RadioButton rbMasculino;
    private BootstrapButton btnCadastrar;
    private BootstrapButton btnCancelar;
    private FirebaseAuth autenticacao;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario_comum);

        email = (BootstrapEditText) findViewById(R.id.edtCadEmail);
        CPF = (BootstrapEditText) findViewById(R.id.edtCadCPF);
        rua = (BootstrapEditText) findViewById(R.id.edtCadRua);
        numero = (BootstrapEditText) findViewById(R.id.edtCadNumero);
        bairro = (BootstrapEditText) findViewById(R.id.edtCadBairro);
        senha1 = (BootstrapEditText) findViewById(R.id.edtCadSenha1);
        senha2 = (BootstrapEditText) findViewById(R.id.edtCadSenha2);
        nome = (BootstrapEditText) findViewById(R.id.edtCadNome);
        rbFeminino = (RadioButton) findViewById(R.id.rbFeminino);
        rbMasculino = (RadioButton) findViewById(R.id.rbMasculino);
        btnCadastrar = (BootstrapButton) findViewById(R.id.btnCadastrar);
        btnCancelar = (BootstrapButton) findViewById(R.id.btnCancela);

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
                    usuario.setTipo("Comum");

                    if(rbFeminino.isChecked()){
                        usuario.setSexo("Feminino");
                    }else if(rbMasculino.isChecked()){
                        usuario.setSexo("Masculino");
                    }

                    cadUsuario();
                }else{
                    Toast.makeText(cadastroUsuarioComumActivity.this, "As senha não se correspondem", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void cadUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(cadastroUsuarioComumActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    insereUsuario(usuario);

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

                    Toast.makeText(cadastroUsuarioComumActivity.this,"Erro: " + erroExcecao, Toast.LENGTH_SHORT).show();;
                }
            }
        });
    }

    private boolean insereUsuario(Usuario usuario){
        try{
            reference = ConfiguracaoFirebase.getFirebase().child("usuarios");
            String key = reference.push().getKey();
            usuario.setKeyUsuario(key);
            reference.child(key).setValue(usuario);
            Toast.makeText(cadastroUsuarioComumActivity.this,"Usuário cadastrado com sucesso", Toast.LENGTH_SHORT).show();
            abriLoginUsuario();
            return true;
        }catch (Exception e){
            Toast.makeText(cadastroUsuarioComumActivity.this,"Erro ao gravar o usuário!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return  false;
        }
    }

    private void abriLoginUsuario(){

        Intent intent = new Intent(cadastroUsuarioComumActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}

