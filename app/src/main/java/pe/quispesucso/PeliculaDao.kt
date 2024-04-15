package pe.quispesucso

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PeliculaDao
{

    @Insert
    fun insert(peliculaEntity: PeliculaEntity): Long

    @Update
    fun update(peliculaEntity: PeliculaEntity)

    @Delete
    fun delete(peliculaEntity: PeliculaEntity)

    @Query("select * from PeliculaTable where peliculaId=:peliculaId")
    fun findById(peliculaId:Long): PeliculaEntity

    @Query("select * from PeliculaTable")
    fun findAll(): MutableList<PeliculaEntity>

}