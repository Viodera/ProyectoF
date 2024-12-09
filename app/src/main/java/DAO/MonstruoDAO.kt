package DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import pgl.proyectof.objetos.Monstruo

@Dao
interface MonstruoDao {
    @Insert
     fun insert(monstruo: Monstruo)

    @Query("SELECT * FROM MONSTRUOS")
     fun getAllMonstruos(): List<Monstruo>

    @Query("SELECT * FROM MONSTRUOS WHERE id = :id")
     fun getMonstruoById(id: Long): Monstruo

    @Query("DELETE FROM MONSTRUOS WHERE id = :id")
     fun deleteMonstruoById(id: Long)

    @Query("SELECT COUNT(*) FROM Monstruos")
    fun countMonstruos(): Int
}