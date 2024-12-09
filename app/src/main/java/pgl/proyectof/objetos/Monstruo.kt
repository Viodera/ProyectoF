package pgl.proyectof.objetos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MONSTRUOS")
data class Monstruo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    var vida: Int,
    val ataque: Int,
    val defensa: Int,
    val idHabilidad: Long
)
