package pe.quispesucso

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PeliculaTable")
data class PeliculaEntity(
    @PrimaryKey(autoGenerate=true)
    var peliculaId: Long = 0,
    var titulo: String = "",
    var anioSalida: Int? = null,
    var genero: String = "",
    var duracion: Int = 0,
    var puntuacion: Int = 0,
    var idioma: String = "",
    var trailerURL: String = "",
    var portadaURL: String,
    var isFavorite: Boolean = false
)
{
    //evitar duplicidad  para peliculaId
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PeliculaEntity

        if (peliculaId != other.peliculaId) return false

        return true
    }

    //evitar duplicidad  para peliculaId
    override fun hashCode(): Int {
        return peliculaId.hashCode()
    }
}