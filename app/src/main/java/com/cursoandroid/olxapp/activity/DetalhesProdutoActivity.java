package com.cursoandroid.olxapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cursoandroid.olxapp.R;
import com.cursoandroid.olxapp.model.Anuncio;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class DetalhesProdutoActivity extends AppCompatActivity {

    private CarouselView carouselView;
    private TextView tv_titulo, tv_descricao, tv_estado, tv_valor;
    private Anuncio anuncioSelecionado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_produto);

        getSupportActionBar().setTitle("Detalhe produto");

        inicializarComponentes();

        //Recupera anúncio para exibição em detalhes
        anuncioSelecionado = (Anuncio) getIntent().getSerializableExtra("anuncioSelecionado");

        if( anuncioSelecionado != null ){

            tv_titulo.setText( anuncioSelecionado.getTitulo() );
            tv_descricao.setText( anuncioSelecionado.getDescricao() );
            tv_estado.setText( anuncioSelecionado.getEstado() );
            tv_valor.setText( anuncioSelecionado.getValor());

            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    String urlString = anuncioSelecionado.getFotos().get( position );
                    Picasso.get().load(urlString).into(imageView);
                }
            };

            carouselView.setPageCount( anuncioSelecionado.getFotos().size() );
            carouselView.setImageListener( imageListener );

        }
    }

    public void visualizarTelefone(View view){
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", anuncioSelecionado.getTelefone(), null ));
        startActivity( i );

    }

    private void inicializarComponentes(){
        carouselView = findViewById(R.id.cr);
        tv_titulo = findViewById(R.id.tv_titulo_detalhe);
        tv_descricao = findViewById(R.id.tv_descicao_detalhe);
        tv_estado = findViewById(R.id.tv_estado_detalhe);
        tv_valor = findViewById(R.id.tv_valor_detalhe);
    }


}