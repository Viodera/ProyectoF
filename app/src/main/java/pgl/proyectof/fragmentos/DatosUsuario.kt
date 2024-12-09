package pgl.proyectof.fragmentos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import pgl.proyectof.R
import pgl.proyectof.baseDatos.DatabaseProvider
import pgl.proyectof.objetos.Personaje
import pgl.proyectof.objetos.Usuario
import java.util.concurrent.Executors


class DatosUsuario : Fragment() {
    private val db by lazy {
        DatabaseProvider.getInstance(requireContext())
    }
    lateinit private var etDatosUser: TextView
    lateinit private var buttGuardar: Button
    lateinit private var buttSalir: Button
    private var usuario: Usuario? = null
    private var personaje: Personaje? = null

    companion object {
        fun newInstance(idPersonaje: Long): DatosUsuario {
            val fragment = DatosUsuario()
            val args = Bundle()
            args.putLong("idPersonaje", idPersonaje)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val view = inflater.inflate(R.layout.fragment_datos_usuario, container, false)
        etDatosUser = view.findViewById(R.id.tvNombreUsuario)
        buttGuardar = view.findViewById(R.id.buttGuardar)
        buttSalir = view.findViewById(R.id.buttCerrarSesión)

        val idPersonaje = arguments?.getLong("idPersonaje") ?: 0L

        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            usuario = db.usuarioDao().getUsuarioByPersonaje(idPersonaje)
            personaje = db.personajeDao().getPersonajeById(idPersonaje)
            activity?.runOnUiThread {
                usuario?.let {
                    etDatosUser.text = usuario!!.usuario
                }
            } ?: run {
                etDatosUser.text = "Usuario no encontrado"
            }
        }

        buttGuardar.setOnClickListener {
            guardarDatos()
            Toast.makeText(requireContext(), "Guardado correctamente", Toast.LENGTH_SHORT).show()
        }

        buttSalir.setOnClickListener {
            val intent = Intent(activity, pgl.proyectof.Login::class.java)
            startActivity(intent)
        }
        return view

    }

    fun guardarDatos() {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            usuario?.let {
                db.usuarioDao().updateUsuario(usuario!!)
            }
        }
    }

}
