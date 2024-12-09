package pgl.proyectof.objetos

import androidx.room.Embedded
import androidx.room.Relation

data class UsuarioConPersonaje(
    @Embedded val usuario: Usuario, // Incluye todos los campos del Usuario
    @Relation(
        parentColumn = "idPersonaje", // Columna en Usuario que apunta al personaje
        entityColumn = "id"           // Columna en Personaje que corresponde al ID
    )
    val personaje: Personaje?         // Objeto Personaje asociado
)
