package com.savia.camaguey

import android.app.Application
import androidx.room.Room
import com.savia.camaguey.data.local.SaviaDatabase
import com.savia.camaguey.data.local.SeedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import timber.log.Timber
import java.io.File

class SaviaApplication : Application() {

    companion object {
        lateinit var database: SaviaDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Configuration.getInstance().apply {
            userAgentValue = packageName
            osmdroidBasePath = File(cacheDir, "osmdroid")
            osmdroidTileCache = File(osmdroidBasePath, "tile")
        }
        database = Room.databaseBuilder(
            applicationContext,
            SaviaDatabase::class.java,
            "savia_database"
        )
            .fallbackToDestructiveMigration()
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            SeedData.populateDatabase(database)
        }
    }
}
