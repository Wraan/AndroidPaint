package com.wran.androidpaint

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import yuku.ambilwarna.AmbilWarnaDialog
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.brush_width_layout.view.*


class MainActivity : AppCompatActivity() {

    val REQUEST_WRITE_EXTERNAL = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        var metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        paintView.init(metrics)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.clear -> {
                showClearConfirmationDialog()
                return true
            }
            R.id.lineWidth -> {
                showLineWidthSetterDialog()
                return true
            }
            R.id.screenShot -> {
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_WRITE_EXTERNAL
                    )
                }
                showFileSavingDialog()
                return true
            }
            R.id.color -> {
                showColorPickerDialog()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private var clearDialogClickListener: DialogInterface.OnClickListener =
        DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    paintView.clear()
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                }
            }
        }

    private fun showClearConfirmationDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure?").setPositiveButton("Yes", clearDialogClickListener)
            .setNegativeButton("No", clearDialogClickListener).show()
    }

    private fun showLineWidthSetterDialog(){
        val popDialog = AlertDialog.Builder(this)

        val inflater = LayoutInflater.from(this@MainActivity)
        val seekView = inflater.inflate(R.layout.brush_width_layout, null)

        seekView.brushWidthSeekText.text = "Current width: " + paintView.getBrushWidth()

        seekView.brushWidthSeek.max = 100
        seekView.brushWidthSeek.progress = paintView.getBrushWidth()
        seekView.brushWidthSeek.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                seekView.brushWidthSeekText.text = "Current width : $i"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

        popDialog.setTitle("Please select brush width")
        popDialog.setView(seekView)
        popDialog.setPositiveButton("OK") { dialog, _ ->
            run {
                dialog.dismiss()
                paintView.setBrushWidth(seekView.brushWidthSeek.progress)
            }
        }
        popDialog.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        popDialog.create()
        popDialog.show()

    }

    private fun showFileSavingDialog(){
        var fileName: String
        val builder = AlertDialog.Builder(this)
        builder.setTitle("File name")

        val input = EditText(this)

        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton(
            "OK"
        ) {
                _, _ -> fileName = input.text.toString()
            paintView.saveImage(fileName)
            Toast.makeText(this,"Save completed",Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }

        builder.show()
    }

    private fun showColorPickerDialog(){
        val colorPicker = AmbilWarnaDialog(this, paintView.getBrushColor(), object : AmbilWarnaDialog.OnAmbilWarnaListener {
            override fun onCancel(dialog: AmbilWarnaDialog) {
            }

            override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                paintView.setBrushColor(color)
            }
        })
        colorPicker.dialog.setTitle("Select color")
        colorPicker.show()
    }


}
