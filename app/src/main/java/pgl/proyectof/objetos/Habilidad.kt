package pgl.proyectof.objetos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "HABILIDADES")
data class Habilidad(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val descripcion: String,
    val poder: Int,
    val coste: Int
)

/*
val listaHabilidades = listOf(
    Habilidad(id = 1, nombre = "Canción curativa", descripcion = "Recupera un porcentaje de la vida del usuario.", poder = 20, coste = 20),
    Habilidad(id = 2, nombre = "Embate Brutal", descripcion = "Ataque pesado que reduce la defensa del enemigo.", poder = 25, coste = 15),
    Habilidad(id = 3, nombre = "Bola de Fuego", descripcion = "Inflige daño mágico.", poder = 20, coste = 15),
    Habilidad(id = 4, nombre = "Golpe Rápido", descripcion = "Un ataque veloz que ignora la defensa enemiga.", poder = 15, coste = 10),
    Habilidad(id = 5, nombre = "Aullido Intimidante", descripcion = "Reduce el ataque del enemigo.", poder = 0, coste = 10),
    Habilidad(id = 6, nombre = "Llama Oscura", descripcion = "Inflige daño mágico con un alto poder destructivo.", poder = 30, coste = 25)
)
*/