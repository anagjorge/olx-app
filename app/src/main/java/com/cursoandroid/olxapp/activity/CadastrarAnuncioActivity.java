package com.cursoandroid.olxapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.cursoandroid.olxapp.R;
import com.cursoandroid.olxapp.helper.ConfiguracaoFirebase;
import com.cursoandroid.olxapp.helper.Permissoes;
import com.cursoandroid.olxapp.model.Anuncio;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskara.widget.MaskEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class CadastrarAnuncioActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_titulo, et_descricao;
    private CurrencyEditText et_valor;
    private MaskEditText et_telefone;
    private Spinner sp_estado, sp_categoria;
    private ImageView img1, img2, img3;
    private Anuncio anuncio;
    private StorageReference storage;
    private AlertDialog dialog;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private List<String> listaFotosSelecionadas = new ArrayList<>();
    private List<String> listaURLFotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        storage = ConfiguracaoFirebase.getFirebaseStorage();

        //validar permissões
        Permissoes.validarPermissoes(permissoes, this, 1);

        inicializarComponentes();
        carregarDadosSpinner();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_cadastro1:
                escolherImgaem(1);
                break;

            case R.id.img_cadastro2:
                escolherImgaem(2);
                break;

            case R.id.img_cadastro3:
                escolherImgaem(3);
                break;

        }

    }

    public void escolherImgaem(int requestCode) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            if (requestCode == 1) {
                img1.setImageURI(imagemSelecionada);
            } else if (requestCode == 2) {
                img2.setImageURI(imagemSelecionada);
            } else if (requestCode == 3) {
                img3.setImageURI(imagemSelecionada);
            }
            listaFotosSelecionadas.add(caminhoImagem);
        }
    }

    private void carregarDadosSpinner() {
        /*String[] estados = new String[]{
                "SP", "MT"
        };*/

        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                estados);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_estado.setAdapter(adapter);

        String[] categorias = getResources().getStringArray(R.array.categorias);
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                categorias);
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_categoria.setAdapter(adapterCategoria);
    }

    private void inicializarComponentes() {
        et_titulo = findViewById(R.id.et_titulo);
        et_descricao = findViewById(R.id.et_descricao);
        et_valor = findViewById(R.id.et_valor);
        et_telefone = findViewById(R.id.et_telefone);
        img1 = findViewById(R.id.img_cadastro1);
        img2 = findViewById(R.id.img_cadastro2);
        img3 = findViewById(R.id.img_cadastro3);
        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        sp_categoria = findViewById(R.id.sp_categoria);
        sp_estado = findViewById(R.id.sp_estado);


        //Configura localidade para pt
        Locale locale = new Locale("pt", "BR");
        et_valor.setLocale(locale);
    }

    private Anuncio configurarAnuncio() {
        String estado = sp_estado.getSelectedItem().toString();
        String categoria = sp_categoria.getSelectedItem().toString();
        String titulo = et_titulo.getText().toString();
        String valor = et_valor.getText().toString();
        String telefone = et_telefone.getText().toString();
        String descricao = et_descricao.getText().toString();

        Anuncio anuncio = new Anuncio();
        anuncio.setEstado(estado);
        anuncio.setCategoria(categoria);
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);
        anuncio.setTelefone(telefone);
        anuncio.setDescricao(descricao);

        return anuncio;
    }

    public void salvarAnuncio() {
        //String valor = et_valor.getHintString(); recuperar o valor digitado
        //String valor = et_valor.getRawValue(); //recuperar o valor digitado
        //String telefone = et_valor.getUnMasked(); //recuperar o valor sem a mascara
        //String telefone = et_valor.getText().ToString(); //recuperar o valor com a mascara
        //Log.d("salvar", "salvarAnuncio");

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando anúncio")
                .setCancelable(false)
                .build();
        dialog.show();


        /* Salvar imagem no Storage */
        for (int i=0; i < listaFotosSelecionadas.size(); i++){
            String urlImagem = listaFotosSelecionadas.get(i);
            int tamanhoLista = listaFotosSelecionadas.size();
            salvarFotoStorage(urlImagem, tamanhoLista, i );
        }

    }

    private void salvarFotoStorage(String urlString, int totalFotos, int contador) {
        StorageReference imagemAnuncio = storage.child("imagens")
                .child("anuncios")
                .child( anuncio.getIdAnuncio() )
                .child("imagem"+contador);

        UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imagemAnuncio.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri url = task.getResult();
                        String urlConvertida = url.toString();
                        listaURLFotos.add(urlConvertida);

                        if(totalFotos == listaURLFotos.size()){
                            anuncio.setFotos(listaURLFotos);
                            anuncio.salvar();

                            dialog.dismiss();
                            finish();
                        }

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                exibirMensagemErro("Falha ao fazer upload");
                Log.i("INFO", "Falha ao fazer upload: " + e.getMessage());
            }
        });


    }

    public void validarDadosAnuncio(View view) {
        anuncio = configurarAnuncio();
        String valor = String.valueOf(et_valor.getRawValue());
        //String fone = "";
        /*if (et_telefone.getUnMasked() != null) {
            fone = et_telefone.getUnMasked();
        }*/


        if (listaFotosSelecionadas.size() != 0) {
            if (!anuncio.getEstado().isEmpty()) {
                if (!anuncio.getCategoria().isEmpty()) {
                    if (!anuncio.getTitulo().isEmpty()) {
                        if (!valor.isEmpty() && !valor.equals("0")) {
                            if (!anuncio.getTelefone().isEmpty() ) { //&& fone.length() >= 10
                                if (!anuncio.getDescricao().isEmpty()) {
                                    salvarAnuncio();
                                } else {
                                    exibirMensagemErro("preencha o campo descrição");
                                }
                            } else {
                                exibirMensagemErro("preencha o campo telefone, digite ao menos 10 números");
                            }
                        } else {
                            exibirMensagemErro("preencha o campo valor");
                        }
                    } else {
                        exibirMensagemErro("preencha o campo título");
                    }
                } else {
                    exibirMensagemErro("preencha o campo categoria");
                }
            } else {
                exibirMensagemErro("preencha o campo estado");
            }
        } else {
            exibirMensagemErro("Selecione ao menos uma foto");
        }
    }

    private void exibirMensagemErro(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }


}