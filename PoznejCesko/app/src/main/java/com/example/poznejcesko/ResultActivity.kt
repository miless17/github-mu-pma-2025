package com.example.poznejcesko

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val score = intent.getIntExtra(EXTRA_SCORE, 0)
        val stars = intent.getIntExtra(EXTRA_STARS, 0)

        val scoreTextView: TextView = findViewById(R.id.scoreResultText)
        val continueButton: MaterialButton = findViewById(R.id.continueButton)
        
        val star1: ImageView = findViewById(R.id.star1)
        val star2: ImageView = findViewById(R.id.star2)
        val star3: ImageView = findViewById(R.id.star3)
        val starViews = listOf(star1, star2, star3)

        scoreTextView.text = score.toString()

        // Spustíme animaci hvězd s mírným zpožděním
        animateStars(starViews, stars)

        continueButton.setOnClickListener {
            finish()
        }
    }

    private fun animateStars(stars: List<ImageView>, earnedCount: Int) {
        val goldColor = Color.parseColor("#FFD700")
        
        stars.forEachIndexed { index, star ->
            // Výchozí stav - zmenšené a šedé
            star.scaleX = 0f
            star.scaleY = 0f
            
            if (index < earnedCount) {
                // Animace pro získané hvězdy
                star.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(300L * (index + 1)) // Postupně jedna po druhé
                    .setDuration(500L)
                    .setInterpolator(OvershootInterpolator()) // Pružný efekt
                    .withStartAction {
                        star.imageTintList = ColorStateList.valueOf(goldColor)
                    }
                    .start()
            } else {
                // Pro nezískané hvězdy jen mírné zobrazení v šedé
                star.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(300L * (index + 1))
                    .setDuration(300L)
                    .withStartAction {
                        star.imageTintList = ColorStateList.valueOf(Color.LTGRAY)
                        star.alpha = 0.5f
                    }
                    .start()
            }
        }
    }

    companion object {
        const val EXTRA_SCORE = "com.example.poznejcesko.SCORE"
        const val EXTRA_STARS = "com.example.poznejcesko.STARS"
    }
}
