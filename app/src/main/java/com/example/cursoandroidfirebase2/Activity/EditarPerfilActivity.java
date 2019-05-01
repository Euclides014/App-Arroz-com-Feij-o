package com.example.cursoandroidfirebase2.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.cursoandroidfirebase2.Classes.Usuario;
import com.example.cursoandroidfirebase2.DAO.ConfiguracaoFirebase;
import com.example.cursoandroidfirebase2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class EditarPerfilActivity extends AppCompatActivity {

    private BootstrapEditText editNome;
    private BootstrapEditText editCPF;
    private BootstrapEditText editLogradouro;
    private BootstrapEditText editNumero;
    private BootstrapEditText editBairro;
    private BootstrapEditText editSenha1;
    private BootstrapEditText editSenha2;

    private BootstrapButton btnGravar;
    private BootstrapButton btnCancel;

    private String txtorigem = "";
    private String txtnome = "";
    private String txtcpf = "";
    private String txtlogradouro = "";
    private String txtnumero = "";
    private String txtbairro = "";
    private String txtemail = "";
    private String txtsexo ="";
    private String txttipousuario = "";
    private String txtkeyUsuario = "";

    private DatabaseReference reference;
    private FirebaseAuth autenticacao;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        editNome = (BootstrapEditText) findViewById(R.id.edtEditNome);
        editCPF = (BootstrapEditText) findViewById(R.id.edtEditCPF);
        editLogradouro = (BootstrapEditText) findViewById(R.id.edtEditLogradouro);
        editNumero = (BootstrapEditText) findViewById(R.id.edtEditNumero);
        editBairro = (BootstrapEditText) findViewById(R.id.edtEditBairro);
        editSenha1 = (BootstrapEditText) findViewById(R.id.edtEditSenha1);
        editSenha2 = (BootstrapEditText) findViewById(R.id.edtEditSenha2);

        btnGravar = (BootstrapButton) findViewById(R.id.btnGravar);
        btnCancel = (BootstrapButton) findViewById(R.id.btnCancel);

        Intent intent = getIntent();
        Bundle bundle= intent.getExtras();
        txtorigem = bundle.getString("origem");
        if(txtorigem.equals("editarUsuario")) {
            txtnome = bundle.getString("nome");
            txtcpf = bundle.getString("cpf");
            txtemail = bundle.getString("email");
            txtsexo = bundle.getString("sexo");
            txttipousuario = bundle.getString("tipoUsuario");
            txtlogradouro = bundle.getString("logradouro");
            txtnumero = bundle.getString("numero");
            txtbairro = bundle.getString("bairro");
            txtkeyUsuario = bundle.getString("keyUsuario");

            editNome.setText(txtnome.toString());
            editCPF.setText(txtcpf.toString());
            editLogradouro.setText(txtlogradouro.toString());
            editNumero.setText(txtnumero.toString());
            editBairro.setText(txtbairro.toString());

        }

        btnGravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editSenha1.getText().toString().equals(editSenha2.getText().toString())) {

                    Usuario usuario = new Usuario();
                    usuario.setNome(editNome.getText().toString());
                    usuario.setCPF(editCPF.getText().toString());
                    usuario.setRua(editLogradouro.getText().toString());
                    usuario.setNumero(editNumero.getText().toString());
                    usuario.setBairro(editBairro.getText().toString());
                    usuario.setKeyUsuario(txtkeyUsuario);
                    usuario.setSenha(editSenha1.getText().toString());
                    usuario.setEmail(txtemail.toString());
                    usuario.setSexo(txtsexo.toString());
                    usuario.setTipo(txttipousuario.toString());

                    atualizarDados(usuario);
                }else{
                    Toast.makeText(EditarPerfilActivity.this, "As senhas não conferem!", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void abrirTelaPrincipal(){
        Intent intent = new Intent(EditarPerfilActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean atualizarDados(final Usuario usuario){

        btnGravar.setEnabled(false);
        try{
            reference = ConfiguracaoFirebase.getFirebase().child("usuarios");
            atualizarSenha(usuario.getSenha());
            reference.child(txtkeyUsuario).setValue(usuario);
            Toast.makeText(EditarPerfilActivity.this, "Dados alterados com sucesso!", Toast.LENGTH_LONG).show();
            abrirTelaPrincipal();

        }catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    private void atualizarSenha(String senhaNova){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updatePassword(senhaNova)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            Log.d("NOVA_SENHA_ATUALIZADA", "Senha atualizada com sucesso");
                    }
                });
    }
}
