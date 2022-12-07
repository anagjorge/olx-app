package com.cursoandroid.olxapp.api;

import com.cursoandroid.olxapp.model.NotificacaoDados;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificacaoService {

    @Headers({
            "Authorization:key=AAAA7PRhs78:APA91bHOuBlvUkYfB0u-rCjYGHRq1WZde9N2hVOJFcB8UXtyIPqCqSF-ifG1VNgyeTTSv_Y2muXKbYk19MLaFXmNBOSk8voCf1I8CNQm4xlgtncvgUtpSqr181CUfRni53sYYJ9w1nmf",
            "Content-Type:application/json"

    })
    @POST("send")
    Call<NotificacaoDados> salvarNotificacao(@Body NotificacaoDados notificacaoDados);
}
