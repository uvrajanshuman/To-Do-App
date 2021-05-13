package com.example.todo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.room.RoomDatabase
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

const val DB_NAME = "ToDo.db"

class TaskActivity : AppCompatActivity(),View.OnClickListener {

    //To hold the value of date, month and year.
    lateinit var calendar:Calendar
    lateinit var dateEdtTxt : TextView
    lateinit var timeEdtTxt : TextView
    lateinit var timeInputLayout : TextInputLayout
    lateinit var spinner:Spinner
    lateinit var savebtn:Button
    lateinit var titleInputLayout:TextInputLayout
    lateinit var taskInputLayout:TextInputLayout

    //list of labels
    private val labels = arrayListOf("Personal","Business","Shopping","Medicine")

    val db by lazy { AppDatabase.getDatabase(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        dateEdtTxt  = findViewById(R.id.dateEdt)
        dateEdtTxt.setOnClickListener(this)
        timeInputLayout = findViewById(R.id.timeInputLay)
        timeEdtTxt = findViewById(R.id.timeEdt)
        timeEdtTxt.setOnClickListener(this)
        spinner = findViewById(R.id.spinnerCategory)
        savebtn = findViewById(R.id.saveTaskBtn)
        savebtn.setOnClickListener(this)
        titleInputLayout = findViewById(R.id.titleInputLay)
        taskInputLayout = findViewById(R.id.taskInputLay)


        setupSpinner()

    }

    private fun setupSpinner() {
        val labelAdapter = ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,labels)
        labels.sort()
        spinner.adapter = labelAdapter
    }

    //same onClickListener implementation for multiple touchs
    override fun onClick(view:View){
        when(view.id){
            R.id.dateEdt -> setDate()
            R.id.timeEdt -> setTime()
            R.id.saveTaskBtn -> saveToDO()

        }
    }

    private fun saveToDO() {
        //TODO: validate inputs
        val category = spinner.selectedItem.toString()
        val task = taskInputLayout.editText?.text.toString()
        val title = titleInputLayout.editText?.text.toString()
        GlobalScope.launch(Dispatchers.IO) {
            var id = db.toDoDao().insertTask(
                ToDoModel(
                title,
                task,
                category,
                calendar.time.time,
                calendar.time.time
            )
            )
        }
        finish()
    }

    private fun setDate(){
            calendar = Calendar.getInstance()

            var dateSetListener = object :DatePickerDialog.OnDateSetListener{
                    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMoth: Int) {
                        calendar.set(year,month,dayOfMoth)
                        updateDate()
                    }
                }

            var datePickerDialog:DatePickerDialog = DatePickerDialog(this,dateSetListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))
                    //To set minimim selectable date as today.
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()

        }

    private fun updateDate() {
        //setting up the text
        val dateFormat = "EEE, MMM d, ''yy"
        val simpleDateFormat=SimpleDateFormat(dateFormat)
        val text = simpleDateFormat.format(calendar.time)
        dateEdtTxt.text = text
        //Making time layout visible
        timeInputLayout.visibility = View.VISIBLE
    }

    private fun setTime(){
        var onTimeSetListener = object : TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(var1: TimePicker, hour: Int, minute: Int){
                calendar.set(Calendar.HOUR_OF_DAY,hour)
                calendar.set(Calendar.MINUTE,minute)
                updateTime()
            }
        }
        val timepickerDialog:TimePickerDialog = TimePickerDialog(this,
            onTimeSetListener,Calendar.HOUR_OF_DAY,Calendar.MINUTE,false)
        timepickerDialog.show()
    }

    private fun updateTime() {
        val timeformat = "h:mm a"
        val simpleDateFormat:SimpleDateFormat = SimpleDateFormat(timeformat)
        val timetext = simpleDateFormat.format(calendar.time)
        timeEdtTxt.text = timetext

    }

}

