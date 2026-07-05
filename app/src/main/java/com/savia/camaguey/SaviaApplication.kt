package com.savia.camaguey

import android.app.Application
import com.savia.camaguey.data.local.SaviaDatabase
import timber.log.Timber

/**
 * SaviaApplication
 * Inicializa Room, Timber, y ejecuta seed data.
 */
class SaviaApplication : Application() {

    lateinit var database: SaviaDatabase
        private set

    override fun onCreate() {
        super.onCreate()

        // Plant Timber for logging (only in debug)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Initialize Room database
        database = SaviaDatabase.getInstance(this)

        // Execute seed data (async, once)
        database.seedDatabaseAsync(this)

        Timber.i("SaviaApplication inicializado")
    }
}
