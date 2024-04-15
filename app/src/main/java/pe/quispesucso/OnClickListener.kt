package pe.quispesucso

interface OnClickListener
{
    fun onClick(peliculaEntity: PeliculaEntity)
    fun onClickFavorite(peliculaEntity: PeliculaEntity)
    fun onClickDelete(peliculaEntity: PeliculaEntity)

}