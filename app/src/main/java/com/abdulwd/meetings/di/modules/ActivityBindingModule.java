package com.abdulwd.meetings.di.modules;

import com.abdulwd.meetings.di.PerActivity;
import com.abdulwd.meetings.main.MainActivity;
import com.abdulwd.meetings.schedule.ScheduleActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Dagger.Android annotation processor will create the sub-components. We also specify the modules
 * to be used by each sub-components and make Dagger.Android aware of a scope annotation
 * {@link PerActivity}.
 */

@Module
public abstract class ActivityBindingModule {
  @PerActivity
  @ContributesAndroidInjector
  public abstract MainActivity provideMainActivity();

  @PerActivity
  @ContributesAndroidInjector
  public abstract ScheduleActivity scheduleActivity();
}
