package pgl.proyectof.objetos

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "PERSONAJES")
data class Personaje(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val idUsuario: Long?,
    val clase: String,
    var nivel: Int = 1,
    var experiencia: Int = 0,
    val nombre: String,
    var vida: Int,
    var ataque: Int,
    var defensa: Int,
    var mana: Int,
    val idHabilidad: Long
):Serializable {
    override fun toString(): String {
        return "Personaje Principal:\n " +
                "Nombre=$nombre\n" +
                "Clase=$clase\n" +
                "Nivel=$nivel\n" +
                "Vida=$vida\n" +
                "Ataque=$ataque\n" +
                "Defensa=$defensa\n" +
                "Mana=$mana"
    }

}