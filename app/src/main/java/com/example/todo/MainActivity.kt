package com.example.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.style.BackgroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

//Int array for colors on recyclr view
lateinit var color:IntArray

class MainActivity : AppCompatActivity() {

    val db by lazy {
        AppDatabase.getDatabase(this)
    }
    lateinit var toDoRv: RecyclerView

    var list = arrayListOf<ToDoModel>()
    var adapter = ToDoAdapter(list)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        color = resources.getIntArray(R.array.random_color)

        toDoRv = findViewById(R.id.todoRv)
        toDoRv.adapter = adapter
        toDoRv.layoutManager = LinearLayoutManager(this)

        db.toDoDao().getTask().observe(this, Observer {
            if(!it.isNullOrEmpty()){
                list.clear()
                list.addAll(it)
                adapter.notifyDataSetChanged()
            }
            else{
                list.clear()
                adapter.notifyDataSetChanged()
            }
        })


        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.history ->{
                startActivity(Intent(this,HistoryActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun openNewTask(view: View) {
        startActivity(Intent(this,TaskActivity::class.java))
    }
}