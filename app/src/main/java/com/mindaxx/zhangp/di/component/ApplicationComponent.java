package com.mindaxx.zhangp.di.component;

import android.content.Context;

import com.mindaxx.zhangp.di.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    Context exposeContext();
}
