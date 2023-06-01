package com.goalmaster

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application(), LifecycleObserver