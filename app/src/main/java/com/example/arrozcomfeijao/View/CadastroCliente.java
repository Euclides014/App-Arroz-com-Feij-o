package com.example.arrozcomfeijao.View;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.arrozcomfeijao.Controller.Usuario;
import com.example.arrozcomfeijao.Helper.RefFirebase;
import com.example.arrozcomfeijao.Model.ConfiguracaoFirebase;
import com.example.arrozcomfeijao.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;


public class CadastroCliente extends AppCompatActivity {

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
    private FirebaseFirestore reference;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_cliente);

        email =  findViewById(R.id.edtCadEmail);
        CPF = findViewById(R.id.edtCadCPF);
        rua = findViewById(R.id.edtCadRua);
        numero = findViewById(R.id.edtCadNumero);
        bairro = findViewById(R.id.edtCadBairro);
        senha1 = findViewById(R.id.edtCadSenha1);
        senha2 = findViewById(R.id.edtCadSenha2);
        nome = findViewById(R.id.edtCadNome);
        rbFeminino = findViewById(R.id.rbFeminino);
        rbMasculino = findViewById(R.id.rbMasculino);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        btnCancelar = findViewById(R.id.btnCancela);

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
                    usuario.setTipo("Cliente");

                    if(rbFeminino.isChecked()){
                        usuario.setSexo("Feminino");
                    }else if(rbMasculino.isChecked()){
                        usuario.setSexo("Masculino");
                    }

                    cadUsuario();
                }else{
                    Toast.makeText(CadastroCliente.this, "As senha não se correspondem", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void cadUsuario(){
        autenticacao = RefFirebase.getFirebaseAuth();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroCliente.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //insereUsuario(usuario);
                    reference = RefFirebase.getFirebaseStore();
                    String UID = autenticacao.getUid();
                    reference.collection("usuarios").document(UID).set(usuario).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CadastroCliente.this,"Cliente cadstrado com sucesso ", Toast.LENGTH_SHORT).show();;
                        }
                    });

                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();


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

                    Toast.makeText(CadastroCliente.this,"Erro: " + erroExcecao, Toast.LENGTH_SHORT).show();;
                }
            }
        });
    }


}

