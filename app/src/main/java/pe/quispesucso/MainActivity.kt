package pe.quispesucso

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import pe.quispesucso.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnClickListener, MainAuxiliary
{
    //para la vista principal
    private lateinit var mBinding:ActivityMainBinding

    private lateinit var mAdapter:PeliculaAdapter
    private lateinit var mGridLayout:GridLayoutManager

    //arrancador
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        //vinculación con las vistas
        mBinding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        //evento fabComercio (botón flotante)
        mBinding.fabPelicula.setOnClickListener {

            //lanzar fragmento
            this.launchFragment()
        }

        //cargar la configuración del RecyclerView
        this.loadRecyclerView()
    }

    //evento click en una de las tarjetas
    override fun onClick(peliculaEntity: PeliculaEntity)
    {
        //llave - valor
        val bundle=Bundle()
        bundle.putLong("peliculaId",peliculaEntity.peliculaId)

        //lanzar fragmento
        this.launchFragment(bundle)
    }

    override fun onClickFavorite(peliculaEntity: PeliculaEntity)
    {
        peliculaEntity.isFavorite=!peliculaEntity.isFavorite

        doAsync {
            //actualizar en la BD
            PeliculaApp.database.PeliculaDao().update(peliculaEntity)

            //actualizar en la vista
            uiThread {
                //actualizar en la vista
                mAdapter.editPelicula(peliculaEntity)
            }
        }
    }

    override fun onClickDelete(peliculaEntity: PeliculaEntity)
    {
        val items=arrayOf("Eliminar","Ir al sitio web")

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_options)
            .setItems(items,DialogInterface.OnClickListener { dialogInterface, i ->
                when(i) {
                    0 -> this.confirmDelete(peliculaEntity)
                    1 -> this.callWebsite(peliculaEntity.trailerURL)
                }
            }).show()
    }

    private fun loadRecyclerView()
    {
        //empieza con lista vacía
        mAdapter=PeliculaAdapter(mutableListOf(),this)

        //1 contenido por cada fila
        mGridLayout= GridLayoutManager(this,1)

        mBinding.recyclerView.apply {
            setHasFixedSize(true) //tamaño fijo
            adapter=mAdapter //asignar mAdapter
            layoutManager=mGridLayout //asignar mGridLayout
        }

        //load comercios de la BD
        doAsync {
            val peliculasDB=PeliculaApp.database.PeliculaDao().findAll()

            uiThread {
                mAdapter.setPeliculas(peliculasDB)
            }
        }
    }

    override fun hideFabPelicula(isVisible: Boolean)
    {
        if(isVisible)
            mBinding.fabPelicula.show()
        else
            mBinding.fabPelicula.hide()
    }

    override fun insertPelicula(peliculaEntity: PeliculaEntity) {
        mAdapter.addPelicula(peliculaEntity)
    }

    override fun updatePelicula(peliculaEntity: PeliculaEntity) {
        mAdapter.editPelicula(peliculaEntity)
    }

    //lanzar fragmento
    private fun launchFragment(bundle:Bundle?=null)
    {
        //controles
        val fragment=PeliculaFragment()

        //para EDITAR
        //enviar argumento bundle
        if(bundle!=null) {
            fragment.arguments=bundle
        }

        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()

        //lanzar fragmento
        fragmentTransaction.add(R.id.containerMain,fragment)

        fragmentTransaction.addToBackStack(null)

        fragmentTransaction.commit()

        //ocultar
        this.hideFabPelicula()
    }

    public fun confirmDelete(peliculaEntity: PeliculaEntity)
    {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete)
            .setPositiveButton(R.string.dialog_delete_confirm,
                DialogInterface.OnClickListener { dialogInterface, i ->

                    doAsync {

                        //borrar de la BD
                        PeliculaApp.database.PeliculaDao().delete(peliculaEntity)

                        uiThread {

                            //borrar del recyclerView
                            mAdapter.removePelicula(peliculaEntity)
                        }
                    }
                })
            .setNegativeButton(R.string.dialog_delete_cancel,null)
            .show()
    }


    public fun callWebsite(website:String)
    {
        val call=Intent().apply {
            action=Intent.ACTION_VIEW //accion de llamar paginas web
            data=Uri.parse(website)
        }

        //llamar al aplicativo de google chrome
        startActivity(call)
    }
}