package com.example.todo

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ToDoDao {

    @Insert
    suspend fun insertTask(toDoModel:ToDoModel):Long

    @Query("SELECT * From ToDoModel WHERE isFinished == 0")
    fun getTask():LiveData<List<ToDoModel>>

    @Query("UPDATE ToDoModel SET isFinished = 1 WHERE id =:tid")
    fun finishTask(tid:Long)

    @Query("DELETE FROM ToDoModel WHERE id =:tid")
    fun deleteTask(tid:Long)
}