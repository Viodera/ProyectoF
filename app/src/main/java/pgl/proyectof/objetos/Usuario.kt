package pgl.proyectof.objetos

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "USUARIOS",
    foreignKeys = [
        ForeignKey(
            entity = Personaje::class,
            parentColumns = ["id"],
            childColumns = ["idPersonaje"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["idPersonaje"])]
)

data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuario: String,
    val contrasena: String,
    val idPersonaje: Long?
)


