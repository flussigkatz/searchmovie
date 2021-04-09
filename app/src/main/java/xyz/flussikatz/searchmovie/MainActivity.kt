package xyz.flussikatz.searchmovie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun clickSearch (view: View) {
        Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
    }

    fun clickHistory (view: View) {
        Toast.makeText(this, "History", Toast.LENGTH_SHORT).show()
    }

    fun clickSettings (view: View) {
        Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
    }

    fun clickMarked (view: View) {
        Toast.makeText(this, "Marked", Toast.LENGTH_SHORT).show()
    }

}