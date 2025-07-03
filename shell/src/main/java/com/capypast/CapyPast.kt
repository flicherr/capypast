package com.capypast

import android.app.Application
import com.capypast.di.capypastModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class CapyPast : Application() {
	override fun onCreate() {
		super.onCreate()

		startKoin {
			androidContext(this@CapyPast)
			modules(capypastModule)
		}
	}
}