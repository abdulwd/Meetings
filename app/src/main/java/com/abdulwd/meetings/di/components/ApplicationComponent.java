package com.abdulwd.meetings.di.components;

import com.abdulwd.meetings.MeetingsApplication;
import com.abdulwd.meetings.di.modules.ApplicationModule;
import com.abdulwd.meetings.di.modules.NetworkModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
    ApplicationModule.class,
    NetworkModule.class,
})
public interface ApplicationComponent {
  void inject(MeetingsApplication application);
}
