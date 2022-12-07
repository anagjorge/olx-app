package com.cursoandroid.olxapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cursoandroid.olxapp.R;
import com.cursoandroid.olxapp.api.NotificacaoService;
import com.cursoandroid.olxapp.model.Notificacao;
import com.cursoandroid.olxapp.model.NotificacaoDados;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificacoesActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private String urlBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacoes);

        urlBase = "https://fcm.googleapis.com/fcm/";
        retrofit = new Retrofit.Builder()
                .baseUrl(urlBase)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FirebaseMessaging.getInstance().subscribeToTopic("carros");
    }

    public void enviarNotificacao(View view){
        String to = "/topics/carros";
        Notificacao notificacao = new Notificacao("Título da notificação!!", "Corpo da notificação");
        NotificacaoDados notificacaoDados = new NotificacaoDados(to, notificacao);

        NotificacaoService service = retrofit.create(NotificacaoService.class);
        Call<NotificacaoDados> call = service.salvarNotificacao(notificacaoDados);
        call.enqueue(new Callback<NotificacaoDados>() {
            @Override
            public void onResponse(Call<NotificacaoDados> call, Response<NotificacaoDados> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "codigo" + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NotificacaoDados> call, Throwable t) {

            }
        });
    }


    public void recuperarToken(View view){


    }
}