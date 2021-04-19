package xyz.flussikatz.searchmovie

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        materialToolbar.setNavigationOnClickListener {
        }

        materialToolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.settings -> {Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        bottomToolBar.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.search -> {Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.history -> {Toast.makeText(this, "History", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.marked -> {Toast.makeText(this, "Marked", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }


        val posterAnim = ObjectAnimator.ofFloat(lotr1, View.X, 0F, -500F)
                posterAnim.setDuration(30000).start()

        val posterAnim2 = ObjectAnimator.ofFloat(spisok, View.TRANSLATION_X,  -500F)
        posterAnim2.setDuration(30000).start()

    }
}