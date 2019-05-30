package com.example.arrozcomfeijao.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;

public class CadastroFuncionario extends AppCompatActivity {

    private FirebaseAuth currentUser;
    private BootstrapEditText email;
    private BootstrapEditText senha1;
    private BootstrapEditText senha2;
    private BootstrapEditText nome;
    private RadioButton rbGerente;
    private RadioButton rbGarcon;
    private BootstrapButton btnCadastrar;
    private BootstrapButton btnCancelar;
    private FirebaseAuth autenticacao;
    private FirebaseFirestore reference;
    private BootstrapEditText CPF;
    private BootstrapEditText rua;
    private BootstrapEditText numero;
    private BootstrapEditText bairro;
    private RadioButton rbCozinheiro;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_funcionario);

        currentUser = FirebaseAuth.getInstance();
        email = findViewById(R.id.edtCadEmail);
        senha1 = findViewById(R.id.edtCadSenha1);
        senha2 = findViewById(R.id.edtCadSenha2);
        nome = findViewById(R.id.edtCadNome);
        CPF = findViewById(R.id.edtCadCPFUsuario);
        rua = findViewById(R.id.edtCadRuaUsuario);
        numero = findViewById(R.id.edtCadNumeroUsuario);
        bairro = findViewById(R.id.edtCadBairroUsuario);
        rbGerente = findViewById(R.id.rbGerente);
        rbGarcon = findViewById(R.id.rbGarcon);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        btnCancelar = findViewById(R.id.btnCancela);
        rbCozinheiro = findViewById(R.id.rbCozinheiro);

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

                    if(rbGerente.isChecked()){
                        usuario.setTipo("Gerente");
                    }else if(rbGarcon.isChecked()) {
                        usuario.setTipo("Garcon");
                    }else if(rbCozinheiro.isChecked()){
                        usuario.setSexo("Cozinheiro");
                    }

                    cadUsuario();
                }else{
                    Toast.makeText(CadastroFuncionario.this, "As senha não se correspondem", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void cadUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroFuncionario.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //insereUsuario(usuario);
                    reference = RefFirebase.getFirebaseStore();
                    String UID = RefFirebase.getFirebaseAuth().getUid();
                    reference.collection("usuarios").document(UID).set(usuario).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CadastroFuncionario.this,"Funcionário cadstrado com sucesso ", Toast.LENGTH_SHORT).show();;
                        }
                    });


                    Intent intent = new Intent(getApplicationContext(), PrincipalGerente.class);
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

                    Toast.makeText(CadastroFuncionario.this,"Erro: " + erroExcecao, Toast.LENGTH_SHORT).show();;
                }

            }
        });
    }

    private void abreTelaPrincipal(){
        final FirebaseAuth auth = RefFirebase.getFirebaseAuth();
        Preferencias preferencias = new Preferencias(CadastroFuncionario.this);
        auth.signInWithEmailAndPassword(preferencias.getEmailUsuarioLogado(), preferencias.getSenhaUsuarioLogado()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    Intent intent = new Intent(CadastroFuncionario.this, PrincipalGerente.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(CadastroFuncionario.this, "Falha", Toast.LENGTH_LONG).show();
                    auth.signOut();
                    Intent intent = new Intent(CadastroFuncionario.this, Login.class);
                    finish();
                    startActivity(intent);
                }
            }
        });
    }
}
