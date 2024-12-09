package pgl.proyectof.fragmentos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import pgl.proyectof.R
import pgl.proyectof.baseDatos.DatabaseProvider
import pgl.proyectof.objetos.Personaje
import java.util.concurrent.Executors


class DatosPersonaje : Fragment() {
    private val db by lazy {
        DatabaseProvider.getInstance(requireContext())
    }
    lateinit private var etDatosPJ: TextView
    private var personaje: Personaje? = null
    lateinit private var nombreHabilidad: String

    companion object {
        fun newInstance(idPersonaje: Long): DatosPersonaje {
            val fragment = DatosPersonaje()
            val args = Bundle()
            args.putLong("idPersonaje", idPersonaje)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflar el layout
        val view = inflater.inflate(R.layout.fragment_datos_personaje, container, false)

        // Inicializar el TextView después de inflar el layout
        etDatosPJ = view.findViewById(R.id.etDatosPJ)

        // Obtener el ID del personaje de los argumentos
        val idPersonaje = arguments?.getLong("idPersonaje") ?: 0L

        // Ejecutar la consulta en un hilo secundario
        val executor = Executors.newSingleThreadExecutor()

        executor.execute {
            // Obtener el personaje desde la base de datos
            personaje = db.personajeDao().getPersonajeById(idPersonaje)
            nombreHabilidad = db.habilidadDao().getHabilidadById(personaje!!.idHabilidad).nombre

            // Verificar si el personaje se ha cargado correctamente
            activity?.runOnUiThread {
                personaje?.let {
                    // Llamar a la función para obtener los datos del personaje
                    datosPJ(it) { resultado ->
                        // Actualizar el TextView con los datos del personaje
                        etDatosPJ.text = resultado
                    }
                } ?: run {
                    // Si no se encuentra el personaje, manejar el error
                    etDatosPJ.text = "Personaje no encontrado"
                }
            }
        }

        // Devolver la vista inflada
        return view
    }

    fun datosPJ(personaje: Personaje, callback: (String) -> Unit) {
        // Simulación de la consulta para obtener los datos del personaje
        val resultado = "Clase: ${personaje.clase}\n" +
                "Nivel: ${personaje.nivel}\n" +
                "Experiencia: ${personaje.experiencia}\n" +
                "Vida: ${personaje.vida}\n" +
                "Ataque: ${personaje.ataque}\n" +
                "Defensa: ${personaje.defensa}\n" +
                "Mana: ${personaje.mana}\n" +
                "Habilidad: ${nombreHabilidad}"

        // Llamar al callback para actualizar el texto
        callback(resultado)
    }
}



