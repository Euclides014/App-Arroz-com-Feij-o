package com.example.arrozcomfeijao.View;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.example.arrozcomfeijao.Model.ConfiguracaoFirebase;
import com.example.arrozcomfeijao.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class UploadFoto extends AppCompatActivity {

    private BootstrapButton btnUpload;
    private BootstrapButton btnCancelar;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference referenciaFirebase;
    private FirebaseAuth autenticacao;
    private ImageView imageView;
    private String emailUsuarioLogado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_foto);

        storageReference = ConfiguracaoFirebase.getFirebaseStorageReference();
        autenticacao =  ConfiguracaoFirebase.getFirebaseAuth();
        emailUsuarioLogado = autenticacao.getCurrentUser().getEmail();

        imageView = (ImageView) findViewById(R.id.imagemCadFotoPerfil);

        carregarImagemPadrao();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult( Intent.createChooser(intent, "Selecione uma imagem"), 123);
            }
        });

        btnUpload = (BootstrapButton)findViewById(R.id.btnUpload);
        btnCancelar = (BootstrapButton)findViewById(R.id.btnCancelar);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastroFotoPerfil();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chamarTelaPrincipal();
            }
        });

    }

    private void cadastroFotoPerfil(){

        StorageReference montaImagemReferencia = storageReference.child("fotoPerfilUsuario/"+ emailUsuarioLogado + ".jpg");
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();

        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,30,byteArray);
        byte [] data = byteArray.toByteArray();
        UploadTask uploadTask = montaImagemReferencia.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                carregarImagemPadrao();
                Toast.makeText(UploadFoto.this, "Foto adicionada com sucesso!", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        final int heigth = 300;
        final int width = 300;
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == 123) {
                Uri imagemSelecionada = data.getData();
                Picasso.get().load(imagemSelecionada.toString()).resize(width, heigth).centerCrop().into(imageView);
            }
        }
    }

    private void carregarImagemPadrao(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl("gs://cursoandroidfirebase-17737.appspot.com/fotoPerfilUsuario/" + emailUsuarioLogado + ".jpg");

        final int heigth = 300;
        final int width = 300;
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri.toString()).resize(width,heigth).centerCrop().into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void chamarTelaPrincipal(){
        Intent intent = new Intent(UploadFoto.this, PrincipalFuncionario.class);
        startActivity(intent);
        finish();
    }



}
