package com.example.arrozcomfeijao.Controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.arrozcomfeijao.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CardapioAdapter extends RecyclerView.Adapter<CardapioAdapter.ViewHolder> {

    private List<CardapioController> mCardapioControllerList;
    private Context context;
    private DatabaseReference referenciaFirebase;
    private List<CardapioController> cardapioControllers;
    private CardapioController todosprodutos;

    public CardapioAdapter(List<CardapioController> l, Context c){
        context = c;
        mCardapioControllerList = l;
    }

    @Override
    public CardapioAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lista_todos_produtos, viewGroup, false);
        return new CardapioAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CardapioAdapter.ViewHolder holder, int position) {

        final CardapioController item = mCardapioControllerList.get(position);

        cardapioControllers = new ArrayList<>();

        referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        referenciaFirebase.child("CardapioController").orderByChild("keyProduto").equalTo(item.getKeyProduto()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cardapioControllers.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    todosprodutos = postSnapshot.getValue(CardapioController.class);
                    cardapioControllers.add(todosprodutos);

                    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                    final int height = (displayMetrics.heightPixels /4);
                    final int width = (displayMetrics.widthPixels /2);

                    Picasso.get().load(todosprodutos.getUrlImagem()).resize(width, height).centerCrop().into(holder.fotoProdutoCardapio);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.txtNomeProdutoCardapio.setText(item.getNomePrato());
        holder.txtDescricaoProdutoCardapio.setText(item.getDescricao());
        holder.txtPrecoProdutoCardapio.setText(item.getPreco());
        holder.txtServeQtdProdutoCardapio.setText(item.getServeQtd());

        holder.linearLayoutProdutosCardapio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mCardapioControllerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView txtNomeProdutoCardapio;
        protected TextView txtDescricaoProdutoCardapio;
        protected TextView txtPrecoProdutoCardapio;
        protected TextView txtServeQtdProdutoCardapio;
        protected ImageView fotoProdutoCardapio;
        protected LinearLayout linearLayoutProdutosCardapio;

        public ViewHolder (View itemView){
            super(itemView);

            txtNomeProdutoCardapio = (TextView)itemView.findViewById(R.id.txtNomeProdutoCardapio);
            txtDescricaoProdutoCardapio = (TextView)itemView.findViewById(R.id.txtDescricaoProdutoCardapio);
            txtPrecoProdutoCardapio = (TextView)itemView.findViewById(R.id.txtPrecoProdutoCardapio);
            txtServeQtdProdutoCardapio = (TextView)itemView.findViewById(R.id.txtServeQtdProdutoCardapio);
            fotoProdutoCardapio = (ImageView)itemView.findViewById(R.id.fotoProdutoCardapio);
            linearLayoutProdutosCardapio = (LinearLayout)itemView.findViewById(R.id.linearLayoutProdutosCardapio);
        }
    }
}