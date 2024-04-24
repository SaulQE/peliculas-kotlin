package pe.quispesucso

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import pe.quispesucso.databinding.FragmentPeliculaBinding

class PeliculaFragment : Fragment()
{
    //variables globales
    private lateinit var mBinding: FragmentPeliculaBinding
    private var mActivity:MainActivity?=null

    private var isEdit:Boolean=false
    private var peliculaEntity:PeliculaEntity?=null

    //preparar diseño
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        mBinding=FragmentPeliculaBinding.inflate(inflater,container,false)
        return mBinding.root
    }

    //ciclo de vida
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        //CODE, si es para EDITAR se debe recibir bundle
        val peliculaId=arguments?.getLong("peliculaId",0)

        if(peliculaId!=null && peliculaId!=0L)
        {
            //mensaje EDITAR
            Toast.makeText(activity,"¡Editar pelicula!",Toast.LENGTH_SHORT).show()

            isEdit=true
            this.printPelicula(peliculaId)
        }
        else
        {
            //mensaje REGISTRAR
            Toast.makeText(activity,"¡Registrar pelicula!",Toast.LENGTH_SHORT).show()

            isEdit=false
            peliculaEntity= PeliculaEntity(titulo = "", anioSalida = null, genero = "", duracion = 0,
                puntuacion = 0, idioma = "", trailerURL = "", portadaURL = "", isFavorite = false)
        }

        //actividad de tipo MainActivity
        mActivity=activity as? MainActivity

        //poner flecha de retroceso en el ActionBar
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //poner titulo en el ActionBar
        mActivity?.supportActionBar?.title=
                if(isEdit) {
                    getString(R.string.title_update)
                }
                else {
                    getString(R.string.title_insert)
                }

        //poner acceso al menu_save
        setHasOptionsMenu(true)

        //evento tietPortada (cargar imagen con GLIDE)
        mBinding.tietPortada.addTextChangedListener {
            Glide.with(this).load(mBinding.tietPortada.text.toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(mBinding.ivPortada)
        }
    }

    //cuando el empieza en fragmento llamar al recurso menu_save
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
    {
        inflater.inflate(R.menu.menu_save,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //eventos del actionBar
    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when(item.itemId)
        {
            //flecha de retroceso
            android.R.id.home -> {
                //volver al principal
                mActivity?.onBackPressed()
                true
            }

            //icono guardar
            R.id.action_save -> {

                if(peliculaEntity!=null && this.validateField())
                {
                    //peliculaEntity listo para EDITAR / REGISTRAR
                    with(peliculaEntity!!)
                    {
                        titulo=mBinding.tietTitulo.text.toString().trim()
                        anioSalida=mBinding.tietAnioSalida.text.toString().toIntOrNull() ?: 0
                        genero=mBinding.tietGenero.text.toString().trim()
                        duracion=mBinding.tietDuracion.text.toString().toIntOrNull() ?: 0
                        puntuacion=mBinding.tietPuntuacion.text.toString().toIntOrNull() ?: 0
                        idioma=mBinding.tietIdioma.text.toString().trim()
                        trailerURL=mBinding.tietTrailer.text.toString().trim()
                        portadaURL=mBinding.tietPortada.text.toString().trim()
                    }

                    doAsync {

                        if(isEdit) {
                            PeliculaApp.database.PeliculaDao().update(peliculaEntity!!)
                        }
                        else {
                            peliculaEntity!!.peliculaId=PeliculaApp.database.PeliculaDao().insert(peliculaEntity!!)
                        }

                        hideKeyboard()

                        uiThread {

                            if(isEdit)
                            {
                                //actualizar la tarjeta
                                mActivity?.updatePelicula(peliculaEntity!!)

                                //mensaje
                                Snackbar.make(mBinding.root,getString(R.string.message_update),Snackbar.LENGTH_SHORT).show()
                            }
                            else
                            {
                                //insertar al carril del recyclerView
                                mActivity?.insertPelicula(peliculaEntity!!)

                                //mensaje
                                Snackbar.make(mBinding.root,getString(R.string.message_save),Snackbar.LENGTH_SHORT).show()
                            }

                            //mostrar la ventana principal
                            mActivity?.onBackPressed()
                        }
                    }
                }

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    //disvincularse de la vista
    override fun onDestroyView()
    {
        hideKeyboard()
        super.onDestroyView()
    }

    //terminar el ciclo de vida del fragmento
    override fun onDestroy()
    {
        //restablecer informacion inicial
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title=getString(R.string.app_name)

        //disvincularse del recurso menu_save
        setHasOptionsMenu(false)

        //mostrar el boton flotante fabPelicula
        mActivity?.hideFabPelicula(true)

        super.onDestroy()
    }

    //función para ocultar el teclado
    @SuppressLint("UseRequireInsteadOfGet")
    private fun hideKeyboard()
    {
        val imm=mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(view!=null)
            imm.hideSoftInputFromWindow(view!!.windowToken,0)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun printPelicula(peliculaId:Long)
    {
        doAsync {
            peliculaEntity=PeliculaApp.database.PeliculaDao().findById(peliculaId)

            uiThread {
                if(peliculaEntity!=null)
                {
                    with(mBinding)
                    {
                        tietTitulo.text=peliculaEntity!!.titulo.editable()
                        tietAnioSalida.text=peliculaEntity!!.anioSalida.toString().editable()
                        tietGenero.text=peliculaEntity!!.genero.editable()
                        tietDuracion.text=peliculaEntity!!.duracion.toString().editable()
                        tietPuntuacion.text=peliculaEntity!!.puntuacion.toString().editable()
                        tietIdioma.text=peliculaEntity!!.idioma.editable()
                        tietTrailer.text=peliculaEntity!!.trailerURL.editable()
                        tietPortada.text=peliculaEntity!!.portadaURL.editable()

                        Glide.with(activity!!)
                            .load(peliculaEntity!!.portadaURL)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .into(ivPortada)
                    }
                }
            }
        }
    }

    private fun String.editable(): Editable {
        return Editable.Factory.getInstance().newEditable(this)
    }

    private fun validateField(): Boolean
    {
        var isValid=true

        if(mBinding.tietTitulo.text.toString().trim().isEmpty())
        {
            mBinding.tilTitulo.error=getString(R.string.helper_required)

            //posicionamiento del curso
            mBinding.tilTitulo.requestFocus()

            isValid=false
        }

        if(mBinding.tietAnioSalida.text.toString().trim().isEmpty())
        {
            //marco color rojo
            mBinding.tilAnioSalida.error=getString(R.string.helper_required)

            //posicionamiento del curso
            mBinding.tilAnioSalida.requestFocus()

            isValid=false
        }

        if(mBinding.tietGenero.text.toString().trim().isEmpty())
        {
            //marco color rojo
            mBinding.tilGenero.error=getString(R.string.helper_required)

            //posicionamiento del curso
            mBinding.tilGenero.requestFocus()

            isValid=false
        }

        if(mBinding.tietDuracion.text.toString().trim().isEmpty())
        {
            //marco color rojo
            mBinding.tilDuracion.error=getString(R.string.helper_required)

            //posicionamiento del curso
            mBinding.tilDuracion.requestFocus()

            isValid=false
        }

        if(mBinding.tietPuntuacion.text.toString().trim().isEmpty())
        {
            //marco color rojo
            mBinding.tilPuntuacion.error=getString(R.string.helper_required)

            //posicionamiento del curso
            mBinding.tilPuntuacion.requestFocus()

            isValid=false
        }

        if(mBinding.tietIdioma.text.toString().trim().isEmpty())
        {
            //marco color rojo
            mBinding.tilIdioma.error=getString(R.string.helper_required)

            //posicionamiento del curso
            mBinding.tilIdioma.requestFocus()

            isValid=false
        }

        if(mBinding.tietTrailer.text.toString().trim().isEmpty())
        {
            mBinding.tilTrailer.error=getString(R.string.helper_required)

            //posicionamiento del curso
            mBinding.tietTrailer.requestFocus()

            isValid=false
        }

        if(mBinding.tietPortada.text.toString().trim().isEmpty())
        {
            //marco color rojo
            mBinding.tilPortadaURL.error=getString(R.string.helper_required)

            //posicionamiento del curso
            mBinding.tietPortada.requestFocus()

            isValid=false
        }


        return isValid
    }
}