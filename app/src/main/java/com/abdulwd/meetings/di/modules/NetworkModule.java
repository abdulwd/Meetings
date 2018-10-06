package com.abdulwd.meetings.di.modules;

import android.support.annotation.NonNull;

import com.abdulwd.meetings.data.remote.MeetingsService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {

  private static final String BASE_URL = "http://fathomless-shelf-5846.herokuapp.com/";

  @Provides
  @Singleton
  Gson provideGson() {
    return new GsonBuilder()
        .setLenient()
        .create();
  }

  @Provides
  @Singleton
  OkHttpClient provideOkHttpClient() {
    return new OkHttpClient.Builder().build();
  }

  @Provides
  @Singleton
  Retrofit provideRetrofit(@NonNull Gson gson,
                           @NonNull OkHttpClient okHttpClient) {
    return new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        .client(okHttpClient)
        .build();
  }

  @Provides
  @Singleton
  MeetingsService provideService(Retrofit retrofit) {
    return retrofit.create(MeetingsService.class);
  }
}
