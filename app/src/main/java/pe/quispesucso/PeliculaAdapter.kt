package pe.quispesucso

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import pe.quispesucso.databinding.ItemPeliculaBinding

class PeliculaAdapter(private var peliculas:MutableList<PeliculaEntity>,
                      private var listener:OnClickListener): RecyclerView.Adapter<PeliculaAdapter.ViewHolder>()
{
    private lateinit var mContext:Context

    //clase interna
    inner class ViewHolder(view:View): RecyclerView.ViewHolder(view)
    {
        //para la tarjeta
        val binding=ItemPeliculaBinding.bind(view)

        fun setListener(peliculaEntity: PeliculaEntity)
        {
            with(binding.root)
            {
                //click a la tarjeta
                setOnClickListener {
                    listener.onClick(peliculaEntity)
                }

                //click largo a la tarjeta
                setOnLongClickListener {
                    listener.onClickDelete(peliculaEntity)
                    true
                }
            }

            //click en favorite
            binding.chkFavorite.setOnClickListener {
                listener.onClickFavorite(peliculaEntity)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        mContext=parent.context
        val view=LayoutInflater.from(mContext).inflate(R.layout.item_pelicula,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val pelicula=peliculas.get(position)

        with(holder)
        {
            this.setListener(pelicula)

            //efecto en la tarjeta
            binding.tvName.text=pelicula.titulo

            binding.chkFavorite.isChecked=pelicula.isFavorite

            Glide.with(mContext)
                .load(pelicula.portadaURL)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(binding.imgPhoto)
        }
    }

    override fun getItemCount(): Int = peliculas.size

    fun addPelicula(peliculaEntity:PeliculaEntity)
    {
        this.peliculas.add(peliculaEntity)

        //refrescar cambios
        notifyItemInserted(peliculas.size-1)
    }

    fun editPelicula(peliculaEntity: PeliculaEntity)
    {
        val index=peliculas.indexOf(peliculaEntity)

        if(index!=-1)
        {
            peliculas.set(index,peliculaEntity)

            //refrescar cambios
            notifyItemChanged(index)
        }
    }

    fun removePelicula(peliculaEntity: PeliculaEntity)
    {
        val index=peliculas.indexOf(peliculaEntity)

        if(index!=-1)
        {
            peliculas.removeAt(index)

            //notificar eliminación
            notifyItemRemoved(index)
        }
    }

    fun setPeliculas(peliculasDB:MutableList<PeliculaEntity>)
    {
        this.peliculas=peliculasDB

        //refrescar cambios
        notifyDataSetChanged()
    }
}