package com.example.todo

import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.style.BackgroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toolbar
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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

        initSwipe()

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


    //Implementing swipe in recyclerview
    fun initSwipe(){
        //ItemTouchHelper.SimpleCallback(dragDirs,swipedirection)//no drag so dragDirs set as 0
        val simpleItemTouchCallback = object: ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){

            //no thing to done on move
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            //desired method for swipe
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //To get position of the element
                val position = viewHolder.layoutPosition
                //to check if directiono of swipe is left
                if(direction==ItemTouchHelper.LEFT){
                    //for left swipe delete the task

                    GlobalScope.launch(Dispatchers.IO) {db.toDoDao().deleteTask(adapter.list[position].id)  }

                }
                else if(direction == ItemTouchHelper.RIGHT){
                    //for right swipe finish the task
                    GlobalScope.launch(Dispatchers.IO) { db.toDoDao().finishTask(adapter.list[position].id) }
                }
            }

            //To draw shapes- Method overriden in object of interface ItemTouchHelper.simplecallback
            override fun onChildDraw(
                canvas: Canvas, //canvas to draw something on the view
                recyclerView: RecyclerView, //recyclerView to which the swipe call is attached.
                viewHolder: RecyclerView.ViewHolder,    //ViewHolder of attached recycler view
                dX: Float,  // direction
                dY: Float,  // direction
                actionState: Int,   //The current action being performed
                isCurrentlyActive: Boolean
            ) {
                //to check if current action state is swipe
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //getting view of the holder
                    val itemView =
                        viewHolder.itemView //to get the to-do view over which we have to draw
                    val paint = Paint() //paint ref to paint color

                    var icon: Bitmap

                    //check if direction of swipe is +ve
                    if (dX > 0) {
                        //creating bitmap for icon
                        icon = BitmapFactory.decodeResource(resources,R.drawable.ic_check_white_png) //bitmap required to draw icon
                        //assigning paint color to paint ref
                        paint.color = Color.parseColor("#388E3C")
                        //To draw rectangle
                        canvas.drawRect(
                            itemView.left.toFloat(), //to get left parameter
                            itemView.top.toFloat(), //to get top parameter
                            itemView.left.toFloat() + dX, //right parameter will be left + portion till swiped
                            itemView.bottom.toFloat(),
                            paint
                        )
                        //To draw icon
                        canvas.drawBitmap(
                            icon,
                            itemView.left.toFloat(),
                            (itemView.top.toFloat()+itemView.bottom.toFloat()-icon.height)/2,
                            paint
                        )

                    }
                    else{   //if direction of swipe is negative
                        icon = BitmapFactory.decodeResource(resources,R.drawable.ic_delete_white_png)
                        paint.color = Color.parseColor("#D32F2F")
                        //to draw rectangle
                        canvas.drawRect(
                            itemView.right.toFloat()+dX,
                            itemView.top.toFloat(),
                            itemView.right.toFloat(),
                            itemView.bottom.toFloat(),
                            paint
                        )
                        //to draw image
                        canvas.drawBitmap(icon,
                            itemView.right.toFloat()-icon.width,
                            (itemView.top.toFloat()+itemView.bottom.toFloat()-icon.height)/2,
                            paint)
                    }
                    viewHolder.itemView.translationX =dX

                } else {
                    super.onChildDraw(
                        canvas,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }

        }

        //instance of ItemTouchHelper
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        //attached recyclerview to itemmtouchhelper
        itemTouchHelper.attachToRecyclerView(toDoRv)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        //implementing search
        val item = menu?.findItem(R.id.search)

        item?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                displayToDo()
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                displayToDo()
                return true
            }

        })
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(!newText.isNullOrBlank()){
                displayToDo(newText)
                }
                return true
            }

        })
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
    private fun displayToDo(newText:String = ""){
        db.toDoDao().getTask().observe(this, Observer {
            if(!it.isNullOrEmpty()){
                list.clear()
                list.addAll(
                    it.filter { todo ->
                        todo.title.contains(newText,true)
                    }
                )
                adapter.notifyDataSetChanged()
            }
        })

    }

    fun openNewTask(view: View) {
        startActivity(Intent(this,TaskActivity::class.java))
    }
}