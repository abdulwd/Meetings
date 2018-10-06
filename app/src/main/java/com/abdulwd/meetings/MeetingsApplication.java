package com.abdulwd.meetings;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.abdulwd.meetings.di.components.ApplicationComponent;
import com.abdulwd.meetings.di.components.DaggerApplicationComponent;
import com.abdulwd.meetings.di.modules.ApplicationModule;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class MeetingsApplication extends Application implements HasActivityInjector {

  private static ApplicationComponent applicationComponent;
  @Inject
  DispatchingAndroidInjector<Activity> activityInjector;

  @Override
  public AndroidInjector<Activity> activityInjector() {
    return activityInjector;
  }

  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    applicationComponent = DaggerApplicationComponent.builder()
        .applicationModule(new ApplicationModule(this))
        .build();
  }

  @Override
  public void onCreate() {
    super.onCreate();
    applicationComponent.inject(this);
  }
}
