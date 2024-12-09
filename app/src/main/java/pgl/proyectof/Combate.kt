package pgl.proyectof

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.content.Intent
import android.media.SoundPool
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pgl.proyectof.baseDatos.DatabaseProvider
import pgl.proyectof.objetos.Habilidad
import pgl.proyectof.objetos.Monstruo
import pgl.proyectof.objetos.Personaje
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow

@Suppress("DEPRECATION")
class Combate : AppCompatActivity() {
    private val SPEECH_REQUEST_CODE = 1
    private var onSpeechResult: ((String) -> Unit)? = null
    private var onSpeechError: ((Int) -> Unit)? = null

    private lateinit var startButton: Button
    private lateinit var tvVidaPJ: TextView
    private lateinit var tvVidaMonstruo: TextView
    private lateinit var barraVidaPJ: ProgressBar
    private lateinit var barraVidaMonstruo: ProgressBar
    private lateinit var tvAccionMonstruo: TextView
    private lateinit var imagenMonstruo: ImageView

    private val db = DatabaseProvider.getInstance(this)

    lateinit private var personaje: Personaje
    private var MAX_VIDA_PJ: Int = 0
    private var manaActualPJ: Int = 0
    private var isDefensaPJ = false
    lateinit private var habilidadPj: Habilidad

    lateinit private var monstruo: Monstruo
    private var isDefensaMonstruo = false
    private var MAX_VIDA_MONSTRUO: Int = 0
    lateinit var habilidadMonstruo: Habilidad
    private var cdActual: Int = 0
    private var cdHabilidadMonstruo: Int = 0

    private lateinit var listaHabilidades: List<Habilidad>
    private var isTurnoPJ: Boolean = true

    private lateinit var soundPool: SoundPool
    private var sonidoAtaque: Int = 0
    private var sonidoDefensa: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        enableEdgeToEdge()
        setContentView(R.layout.activity_combate)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        soundPool = SoundPool.Builder().setMaxStreams(1).build()
        sonidoAtaque = soundPool.load(this@Combate, R.raw.hit, 1)
        sonidoDefensa = soundPool.load(this@Combate, R.raw.shield, 1)


        personaje = intent.getSerializableExtra("personaje") as Personaje
        MAX_VIDA_PJ = personaje.vida
        manaActualPJ = personaje.mana

        tvVidaPJ = findViewById(R.id.tvVidaPJ)
        tvVidaMonstruo = findViewById(R.id.tvVidaMonstruo)
        tvAccionMonstruo = findViewById(R.id.tvAccionMonstruo)
        startButton = findViewById(R.id.buttAccion)
        barraVidaPJ = findViewById(R.id.barraVidaPJ)
        barraVidaPJ.max = MAX_VIDA_PJ
        barraVidaMonstruo = findViewById(R.id.barraVidaMonstruo)
        imagenMonstruo = findViewById(R.id.imagenMonstruo)



        lifecycleScope.launch { habilidadPj = devolverHabilidad(personaje.idHabilidad) }
        startButton.isEnabled = false
        lifecycleScope.launch {
            listaHabilidades = withContext(Dispatchers.IO) {
                db.habilidadDao().getAllHabilidades()
            }
            startButton.isEnabled = true
        }

        tvVidaPJ.text = "Vida: ${personaje.vida}/${MAX_VIDA_PJ}"
        actualizarVida(barraVidaPJ, personaje.vida)

        startButton.setOnClickListener {
            lifecycleScope.launch {
                sacarMonstruo()
                combate()
            }
        }
    }

    private fun reconocerAccionPorVoz() {
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            val result =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            onSpeechResult?.invoke(result ?: "")
        } else {
            onSpeechError?.invoke(resultCode)
        }
    }

    private suspend fun reconocerAccionPorVozSuspend(): String {
        return withContext(Dispatchers.Main) {
            val resultado = kotlinx.coroutines.suspendCancellableCoroutine<String> { continuation ->
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
                    continuation.resumeWith(Result.failure(e))
                }
                onSpeechResult = { text ->
                    if (text.isNotBlank()) {
                        continuation.resume(text)
                    } else {
                        continuation.resumeWith(Result.failure(Exception("Comando vacío")))
                    }
                }
                onSpeechError = { error ->
                    continuation.resumeWith(Result.failure(Exception("Error en reconocimiento: $error")))
                }
            }

            return@withContext resultado
        }
    }

    private fun procesarComando(comando: String): Boolean {
        return when {
            comando.contains("ataque") || comando.contains("atacar") -> {
                ataquePJ()
                true
            }
            comando.contains("defensa") || comando.contains("defender") -> {
                defensaPJ()
                true
            }
            comando.contains("habilidad") || comando.contains("especial") -> {
                habilidadPJ()
                true
            }
            else -> false
        }
    }

    private fun ataquePJ() {
        val monstruoActual = monstruo
        Toast.makeText(this, "Tu pj está atacando!", Toast.LENGTH_SHORT).show()
        if (isDefensaMonstruo) {
            soundPool.play(sonidoDefensa, 1f, 1f, 0, 0, 1f)
            monstruoActual.vida -= danio(personaje.ataque, monstruoActual.defensa)
            isDefensaMonstruo = false
        } else {
            soundPool.play(sonidoAtaque, 1f, 1f, 0, 0, 1f)
            monstruoActual.vida -= personaje.ataque
        }
    }

    private fun defensaPJ() {
        Toast.makeText(this, "Tu pj está defendiendo!", Toast.LENGTH_SHORT).show()
        isDefensaPJ = true
    }

    private fun habilidadPJ() {
        Toast.makeText(this, "Tu pj está usando una habilidad!", Toast.LENGTH_SHORT).show()
        calculoHabilidades(habilidadPj)
    }

    private fun turnoMonstruo() {
        var accionMonstruo: Int
        cdActual++
        if (cdActual < cdHabilidadMonstruo) {
            accionMonstruo = (1..2).random()
        } else {
            accionMonstruo = (1..3).random()
        }
        when (accionMonstruo) {
            1 -> {  //ataque del monstruo
                if (isDefensaPJ) {
                    personaje.vida -= danio(monstruo!!.ataque, personaje.defensa)
                } else {
                    personaje.vida -= monstruo!!.ataque
                }
                tvAccionMonstruo.text = "El monstruo atacó"
                Toast.makeText(this@Combate, "El monstruo ataca", Toast.LENGTH_SHORT).show()
            }

            2 -> {  //defensa del monstruo
                isDefensaMonstruo = true
                tvAccionMonstruo.text = "El monstruo se defendió"
                Toast.makeText(this@Combate, "El monstruo se defiende", Toast.LENGTH_SHORT).show()
            }

            3 -> {  //habilidad del monstruo
                calculoHabilidades(habilidadMonstruo)
                Toast.makeText(this@Combate, "El monstruo usa una habilidad", Toast.LENGTH_SHORT)
                    .show()
                cdActual = 0
            }
        }
    }

    private fun danio(atacante: Int, defensor: Int): Int {
        return atacante - defensor
    }

    private suspend fun sacarMonstruo() {
        val idMonstruo = (1..5).random().toLong()
        monstruo = withContext(Dispatchers.IO) {
            db.monstruoDao().getMonstruoById(idMonstruo)
        }
        monstruo?.let {
            habilidadMonstruo = devolverHabilidad(it.idHabilidad)
            MAX_VIDA_MONSTRUO = it.vida
            barraVidaMonstruo.max = MAX_VIDA_MONSTRUO
            tvVidaMonstruo.text = "Vida: ${it.vida}/${MAX_VIDA_MONSTRUO}"
            when (monstruo.id){
                1L -> imagenMonstruo.setImageResource(R.drawable.monstruo1)
                2L -> imagenMonstruo.setImageResource(R.drawable.monstruo2)
                3L -> imagenMonstruo.setImageResource(R.drawable.monstruo3)
                4L -> imagenMonstruo.setImageResource(R.drawable.monstruo4)
                5L -> imagenMonstruo.setImageResource(R.drawable.monstruo5)
            }
        } ?: run {
            Toast.makeText(this, "Error al cargar el monstruo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sacarMonstruoManual(id: Long) {
        lifecycleScope.launch {
            monstruo = withContext(Dispatchers.IO) {
                db.monstruoDao().getMonstruoById(id)
            }
            monstruo.let {
                habilidadMonstruo = devolverHabilidad(it.idHabilidad)
                MAX_VIDA_MONSTRUO = it.vida
                barraVidaMonstruo.max = MAX_VIDA_MONSTRUO
                tvVidaMonstruo.text = "Vida: ${it.vida}/${MAX_VIDA_MONSTRUO}"
            }
        }
    }

    private suspend fun devolverHabilidad(id: Long): Habilidad {
        return withContext(Dispatchers.IO) {
            db.habilidadDao().getHabilidadById(id)
        }
    }

    private fun calculoHabilidades(habilidad: Habilidad) {
        listaHabilidades.forEach() {
            if (it.id == habilidad.id) {
                if (isTurnoPJ) {
                    when (it.id) {
                        1L -> {  //Canción curativa
                            if (manaActualPJ >= it.coste) {
                                manaActualPJ -= it.coste
                                if ((personaje.vida + it.poder) > MAX_VIDA_PJ) {
                                    personaje.vida = MAX_VIDA_PJ
                                } else {
                                    personaje.vida += it.poder
                                }

                            } else {
                                Toast.makeText(
                                    this,
                                    "No tienes suficiente maná",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        2L -> {  //Embate brutal
                            if (manaActualPJ < it.coste) {
                                Toast.makeText(
                                    this,
                                    "No tienes suficiente maná",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                manaActualPJ -= it.coste
                                if (isDefensaMonstruo) {
                                    monstruo.vida -= danio(
                                        it.poder,
                                        ceil(monstruo.defensa * 0.75).toInt()
                                    )
                                } else monstruo.vida -= it.poder
                            }
                        }

                        3L -> {  //Bola de fuego
                            if (manaActualPJ < it.coste) {
                                Toast.makeText(
                                    this,
                                    "No tienes suficiente maná",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                manaActualPJ -= it.coste
                                monstruo.vida -= it.poder
                            }
                        }
                    }
                } else {
                    Log.d("Habilidad", it.id.toString())
                    when (it.id) {
                        2L -> {  //Embate brutal
                            tvAccionMonstruo.text = "El monstruo usó embate brutal"
                            Toast.makeText(this@Combate, "Embate brutal", Toast.LENGTH_SHORT).show()
                            if (isDefensaPJ) {
                                personaje.vida -= danio(
                                    it.poder,
                                    ceil(personaje.defensa * 0.75).toInt()
                                )
                            } else personaje.vida -= it.poder
                        }

                        3L, 4L -> {  //Bola de fuego
                            tvAccionMonstruo.text = "El monstruo usó bola de fuego"
                            Toast.makeText(this@Combate, "Bola de fuego", Toast.LENGTH_SHORT).show()
                            personaje.vida -= it.poder
                        }

                        5L -> {  //Aullido intimidante
                            tvAccionMonstruo.text = "El monstruo usó aullido intimidante"
                            Toast.makeText(this@Combate, "Aullido intimidante", Toast.LENGTH_SHORT)
                                .show()
                            isTurnoPJ = !isTurnoPJ
                        }

                        6L -> {  //Llama oscura
                            tvAccionMonstruo.text = "El monstruo usó llama oscura"
                            Toast.makeText(this@Combate, "Llama oscura", Toast.LENGTH_SHORT).show()
                            personaje.vida -= it.poder
                        }
                    }
                }
            }
        }
    }

    private fun calculoNivel() {
        personaje.experiencia++
        val base = 1.2
        personaje.nivel = floor((personaje.experiencia.toDouble() / 5.0).pow(base)).toInt() + 1
    }

    fun actualizarVida(barraVida: ProgressBar, vidaRestante: Int) {
        barraVida.progress = vidaRestante
    }

    @SuppressLint("SetTextI18n")
    private suspend fun combate() {
        var turno = 1
        isTurnoPJ = true
        isDefensaPJ = false
        isDefensaMonstruo = false
        cdHabilidadMonstruo = ceil(habilidadMonstruo.coste.toDouble() / 10).toInt()
        cdActual = cdHabilidadMonstruo
        val monstruoActual = monstruo ?: run {
            Toast.makeText(this, "Monstruo no disponible", Toast.LENGTH_SHORT).show()
            return
        }
        while (personaje.vida > 0 && monstruoActual.vida > 0) {
            if (isTurnoPJ) {
                val comando = reconocerAccionPorVozSuspend()
                if (procesarComando(comando)) {
                    isDefensaMonstruo = false
                } else {
                    Toast.makeText(this, "Comando no válido. Intenta de nuevo.", Toast.LENGTH_SHORT)
                        .show()
                    continue
                }
            } else {
                turnoMonstruo()
                isDefensaPJ = false
            }
            delay(2000)
            isTurnoPJ = !isTurnoPJ
            turno++

            tvVidaPJ.text = "Vida: ${personaje.vida}/${MAX_VIDA_PJ}"
            actualizarVida(barraVidaPJ, personaje.vida)
            tvVidaMonstruo.text = "Vida: ${monstruoActual.vida}/${MAX_VIDA_MONSTRUO}"
            actualizarVida(barraVidaMonstruo, monstruoActual.vida)
        }

        if (personaje.vida <= 0) {
            Toast.makeText(this, "¡Has perdido!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "¡Has ganado!", Toast.LENGTH_SHORT).show()
            calculoNivel()
        }
        personaje.vida = MAX_VIDA_PJ
        manaActualPJ = personaje.mana
    }
}
