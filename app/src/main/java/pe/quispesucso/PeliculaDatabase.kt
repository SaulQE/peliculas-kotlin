package pe.quispesucso

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(PeliculaEntity::class), version = 1)
abstract class PeliculaDatabase: RoomDatabase()
{
    // método abstracto
    abstract fun PeliculaDao(): PeliculaDao
}