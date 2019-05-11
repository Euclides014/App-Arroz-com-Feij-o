package com.example.arrozcomfeijao.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.arrozcomfeijao.R;
import com.google.firebase.auth.FirebaseAuth;

public class PrincipalActivityAtendente extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_atendente);

        autenticacao = FirebaseAuth.getInstance();

        getWindow().setBackgroundDrawable(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();
        getMenuInflater().inflate(R.menu.menu_atend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cad_foto_perfil_atend){
            uploadFotoPerfil();
        }else if (id == R.id.action_sair_atend){
            deslogarUsuario();
        }else if (id == R.id.action_ver_meu_perfil){
            verMeuPerfil();
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadFotoPerfil(){
        Intent intent = new Intent(PrincipalActivityAtendente.this, UploadFotoActivity.class);
        startActivity(intent);

    }

    private void verMeuPerfil(){
        Intent intent = new Intent(PrincipalActivityAtendente.this, MeuPerfilActivity.class);
        startActivity(intent);
        finish();
    }

    private void deslogarUsuario(){
        autenticacao.signOut();
        Intent intent = new Intent(PrincipalActivityAtendente.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
