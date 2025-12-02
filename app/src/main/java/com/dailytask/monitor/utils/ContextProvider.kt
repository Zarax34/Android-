package com.dailytask.monitor.utils

import android.app.Application
import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContextProvider @Inject constructor(
    private val application: Application
) {
    fun getContext(): Context = application.applicationContext
    fun getApplication(): Application = application
}