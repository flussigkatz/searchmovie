package xyz.flussikatz.searchmovie

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
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


        val animatorSet = AnimatorSet()
        val posterAnim1 = ObjectAnimator.ofFloat(posters, View.SCALE_X, 0f, 1F)
        val posterAnim2 = ObjectAnimator.ofFloat(posters, View.SCALE_Y, 0f, 1F)
        val animationUpdateListener = object: Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                text1.alpha = 1f
                text1.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, R.anim.text_anim))
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }

        }
//        val textAnim = ObjectAnimator.ofFloat(text1, View.ALPHA, 0f, 1f)

        animatorSet.playTogether(posterAnim1,posterAnim2)
        animatorSet.interpolator = OvershootInterpolator()
        animatorSet.startDelay = 500
        animatorSet.addListener(animationUpdateListener)
        animatorSet.setDuration(1000).start()

    }
}