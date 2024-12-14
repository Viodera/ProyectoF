@file:Suppress("DEPRECATION")

package pgl.proyectof

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pgl.proyectof.objetos.Personaje
import java.util.Locale

class Aventura : AppCompatActivity() {
    private val SPEECH_REQUEST_CODE = 1

    private lateinit var textoDiosa: TextView
    private lateinit var textoPJ: TextView
    private lateinit var botonContinuar: Button
    private var isPalabrasMagicas = false
    private lateinit var botonSalir: Button

    private lateinit var personaje: Personaje

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        enableEdgeToEdge()
        setContentView(R.layout.activity_aventura)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        personaje = intent.getSerializableExtra("personaje") as Personaje

        textoDiosa = findViewById(R.id.tvTextoDiosa)
        textoPJ = findViewById(R.id.tvTextoPJ)
        botonContinuar = findViewById(R.id.buttContinuar)
        botonSalir = findViewById(R.id.buttSalirAventura)
        val textoInicio = "Bienvenido a la aventura, ${personaje.nombre}.\n" +
                "Soy la Diosa de la Creación, y te he llamado aquí para que me ayudes a salvar el mundo.\n" +
                "Un malvado hechicero ha robado el Orbe de la Creación, y con él ha sumido al mundo en la oscuridad.\n" +
                "Tu misión es recuperar el Orbe y devolver la luz al mundo.\n" +
                "Voy a necesitar que te enfrentes a muchos peligros, pero sé que eres valiente y fuerte.\n" +
                "¿Estás listo para comenzar tu aventura? (Requiere nivel 2. Nivel actual: ${personaje.nivel}) "

        lifecycleScope.launch {
            delay(1000)
            Log.d("Aventura", "Iniciando el texto de la diosa")
            hablar(textoInicio, textoDiosa, 25)

            delay(15000)
            if (personaje.nivel < 2) {
                botonContinuar.isEnabled = false
                hablar("Necesito entrenar mas para continuar.", textoPJ, 50)
            } else {
                botonContinuar.isEnabled = true
                botonContinuar.isVisible = true
                val textoPJConfirmacion = buildString {
                    append("le diré a la diosa que estoy listo. ")
                    append("\"Presiona el botón para continuar y canta las palabras mágicas:\"")
                    append(" ¡Estoy listo para la aventura!")
                }
                hablar(textoPJConfirmacion, textoPJ, 50)
            }
        }
        botonContinuar.setOnClickListener {
            reconocerCancion()
        }
        botonSalir.setOnClickListener {
            finish()
        }
    }

    private fun reconocerCancion() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora para transcribir tu voz")
        }
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(
                this, "El reconocimiento de voz no está disponible",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            result?.let {
                val accion = it[0]
                cancionPJ(accion)
            }
        }
    }

    private fun cancionPJ(accion: String) {
        when {
            accion.contains("listo") -> {
                val texto = "¡Muy bien, aventurero! ¡Que comience la aventura!"
                lifecycleScope.launch {
                    hablar(texto, textoDiosa, 50)
                }
                isPalabrasMagicas = true
                lifecycleScope.launch { delay(10000) }
                if (isPalabrasMagicas) {
                    val dialog = AlertDialog.Builder(this)
                        .setTitle("DEMO")
                        .setMessage("Fin de la demostración. ¿Te ha gustado?")
                        .setPositiveButton("Me gusta") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setNegativeButton("No me gusta") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()

                    dialog.show()
                }
            }

            else -> {
                val texto = "Lo siento, no entendí lo que dijiste. ¿Podrías repetirlo?"
                lifecycleScope.launch {
                    hablar(texto, textoDiosa, 50)
                }
            }
        }
    }

    private fun hablar(texto: String, textoAHablar: TextView, delay: Long) {
        var letra = 0
        textoAHablar.text = ""
        val runnable = object : Runnable {
            override fun run() {
                if (letra < texto.length) {
                    textoAHablar.append(texto[letra].toString())
                    letra++
                    textoAHablar.postDelayed(this, delay)
                }
            }
        }
        textoAHablar.post(runnable)
    }


}