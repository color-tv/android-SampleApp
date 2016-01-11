package com.replay.demo;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by adamstyrc on 11/01/16.
 */

@Singleton
@Component(
        modules = TestModule.class
)
public interface TestComponent {

    void inject(Context applicationContext);
}
