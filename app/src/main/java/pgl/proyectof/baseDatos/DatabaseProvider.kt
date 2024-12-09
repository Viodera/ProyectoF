package pgl.proyectof.baseDatos

import android.content.Context
import android.util.Log
import androidx.room.Room

object DatabaseProvider {
    private var appDatabase: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
        if (appDatabase == null) {
            Log.d("DatabaseProvider", "Contexto recibido: $context")
            appDatabase = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "ProyectoFF.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        return appDatabase!!
    }
}
