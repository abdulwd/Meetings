package com.abdulwd.meetings.di.modules;

import android.content.Context;

import com.abdulwd.meetings.MeetingsApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.android.AndroidInjectionModule;

@Module(includes = {ActivityBindingModule.class, AndroidInjectionModule.class})
public class ApplicationModule {
  private final MeetingsApplication application;

  public ApplicationModule(MeetingsApplication application) {
    this.application = application;
  }

  @Provides
  @Singleton
  Context provideApplicationContext() {
    return this.application;
  }
}
