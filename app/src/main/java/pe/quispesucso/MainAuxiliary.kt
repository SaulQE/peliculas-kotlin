package pe.quispesucso

interface MainAuxiliary
{
    fun hideFabPelicula(isVisible:Boolean = false)

    fun insertPelicula(peliculaEntity: PeliculaEntity)
    fun updatePelicula(peliculaEntity: PeliculaEntity)

}