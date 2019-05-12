package com.example.arrozcomfeijao.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.arrozcomfeijao.R;
import com.google.firebase.auth.FirebaseAuth;

public class PrincipalCliente extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_cliente);

        autenticacao = FirebaseAuth.getInstance();

        getWindow().setBackgroundDrawable(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();
        getMenuInflater().inflate(R.menu.menu_cliente, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_fazer_pedidos){

        }else if (id == R.id.action_sair_comum){
            deslogarUsuario();
        }else if (id == R.id.action_ver_cardapio_comum) {
            chamarCardapio();
        }else if (id == R.id.action_ver_meu_perfil) {
            vermeuperfil();
        }
        return super.onOptionsItemSelected(item);
    }

    private void chamarCardapio(){
        Intent intent = new Intent(PrincipalCliente.this, Cardapio.class);
        startActivity(intent);
        finish();
    }

    private void vermeuperfil(){
        Intent intent = new Intent(PrincipalCliente.this, MeuPerfil.class);
        startActivity(intent);
        finish();
    }

    private void deslogarUsuario(){
        autenticacao.signOut();
        Intent intent = new Intent(PrincipalCliente.this, Login.class);
        startActivity(intent);
        finish();
    }
}
