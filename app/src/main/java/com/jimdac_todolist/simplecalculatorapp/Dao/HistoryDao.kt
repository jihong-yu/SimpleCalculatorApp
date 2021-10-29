package com.jimdac_todolist.simplecalculatorapp.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.jimdac_todolist.simplecalculatorapp.model.History

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history")
    fun getAll(): List<History>

    @Insert
    fun insertHistory(history:History)

    @Delete
    fun delete(history: History)

    @Query("DELETE FROM history")
    fun deleteAll()
}