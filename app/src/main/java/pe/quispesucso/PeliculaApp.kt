package pe.quispesucso

import android.app.Application
import androidx.room.Room

class PeliculaApp: Application()
{
    //para acceder a la base de datos desde cualquier punto
    companion object {
        lateinit var database: PeliculaDatabase
    }

    //para crear una instancia de la base de datos
    override fun onCreate()
    {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            PeliculaDatabase::class.java,
            "PeliculaDatabase"
        ).build()
    }
}