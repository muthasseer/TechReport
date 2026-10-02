package com.techreport.app

import android.app.Application
import android.content.Context

object AppContextHolder {

    lateinit var context: Context

    fun init(application: Application) {
        context = application.applicationContext
    }
}
