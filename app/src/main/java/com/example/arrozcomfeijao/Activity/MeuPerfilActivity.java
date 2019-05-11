package com.example.arrozcomfeijao.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.example.arrozcomfeijao.Classes.Usuario;
import com.example.arrozcomfeijao.DAO.ConfiguracaoFirebase;
import com.example.arrozcomfeijao.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MeuPerfilActivity extends AppCompatActivity {

    private TextView txtNome;
    private TextView txtCPF;
    private TextView txtEmail;
    private TextView txtEndereco;
    private TextView txtSexo;
    private TextView txttipoUsuario;
    private BootstrapButton btnEditar;
    private BootstrapButton btnCancelar;
    private BootstrapButton btnExcluir;

    private FirebaseAuth autenticacao;
    private DatabaseReference reference;


    private String txtorigem = "";
    private String txtnome = "";
    private String txtcpf = "";
    private String txtlogradouro = "";
    private String txtnumero = "";
    private String txtbairro = "";
    private String txtkeyUsuario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meu_perfil);

        autenticacao = FirebaseAuth.getInstance();
        reference = ConfiguracaoFirebase.getFirebase();

        txtNome = (TextView) findViewById(R.id.txtNomeUsuario);
        txtCPF = (TextView) findViewById(R.id.txtCPF);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtEndereco = (TextView) findViewById(R.id.txtEndereco);
        txtSexo = (TextView) findViewById(R.id.txtSexo);
        txttipoUsuario = (TextView) findViewById(R.id.txttipoUsuario);

        btnEditar = (BootstrapButton) findViewById(R.id.btnEditar);
        btnCancelar = (BootstrapButton) findViewById(R.id.btnCancelar);
        btnExcluir = (BootstrapButton) findViewById(R.id.btnExcluir);

        String emailUsuarioLogado = autenticacao.getCurrentUser().getEmail();

        reference.child("usuarios").orderByChild("email").equalTo(emailUsuarioLogado).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Usuario usuario = postSnapshot.getValue(Usuario.class);

                    txtNome.setText(usuario.getNome());
                    txtCPF.setText(usuario.getCPF());
                    txtEmail.setText(usuario.getEmail());
                    txtEndereco.setText(usuario.getRua() + " " + usuario.getNumero() + " " + usuario.getBairro());
                    txtSexo.setText(usuario.getSexo());
                    txttipoUsuario.setText(usuario.getTipo());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editarPerfilUsuario();

            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelar();
            }
        });

        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDialogConfirmaExclusao();
            }
        });

    }

    private void cancelar() {
        Intent intent = new Intent(MeuPerfilActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void excluirUsuario() {
        String emailUsuarioLogado = autenticacao.getCurrentUser().getEmail();
        reference = ConfiguracaoFirebase.getFirebase();
        reference.child("usuarios").orderByChild("email").equalTo(emailUsuarioLogado).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final Usuario usuario = postSnapshot.getValue(Usuario.class);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Usuario_Excluido", "User Deleted");
                                        Toast.makeText(MeuPerfilActivity.this, "O usuário foi excluido!", Toast.LENGTH_LONG).show();
                                        reference = ConfiguracaoFirebase.getFirebase();
                                        reference.child("usuarios").child(usuario.getKeyUsuario()).removeValue();

                                        autenticacao.signOut();
                                        abrirTelaLogin();

                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void abrirTelaLogin(){
        Intent intent = new Intent(MeuPerfilActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void abrirDialogConfirmaExclusao(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.alert_personalizado_excluir);
        final BootstrapButton btnSim = (BootstrapButton)dialog.findViewById(R.id.btnSim);
        final BootstrapButton btnNao = (BootstrapButton)dialog.findViewById(R.id.btnNao);

        btnSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                excluirUsuario();
                dialog.dismiss();
            }
        });

        btnNao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTelaLogin();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void editarPerfilUsuario(){
        String emailUsuarioLogado = autenticacao.getCurrentUser().getEmail();
        reference = ConfiguracaoFirebase.getFirebase();

        reference = ConfiguracaoFirebase.getFirebase();
        reference.child("usuarios").orderByChild("email").equalTo(emailUsuarioLogado).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Usuario usuario = postSnapshot.getValue(Usuario.class);

                    final Intent intent = new Intent(MeuPerfilActivity.this, EditarPerfilActivity.class);
                    final Bundle bundle = new Bundle();
                    bundle.putString("origem", "editarUsuario");
                    bundle.putString("nome", usuario.getNome());
                    bundle.putString("cpf", usuario.getCPF());
                    bundle.putString("sexo", usuario.getSexo());
                    bundle.putString("logradouro", usuario.getRua());
                    bundle.putString("numero", usuario.getNumero());
                    bundle.putString("bairro", usuario.getBairro());
                    bundle.putString("keyUsuario", usuario.getKeyUsuario());
                    bundle.putString("email", usuario.getEmail());
                    bundle.putString("tipoUsuario", usuario.getTipo());

                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
