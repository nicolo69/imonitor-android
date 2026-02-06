package com.cerotek.imonitor.util

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.cardview.widget.CardView
import com.cerotek.imonitor.R

object BlinkAnimationHelper {
    
    private val activeAnimations = mutableMapOf<View, ValueAnimator>()
    
    /**
     * Fa lampeggiare una card in rosso
     */
    fun startBlinkingCard(cardView: CardView) {
        // Ferma animazione precedente se esiste
        stopBlinking(cardView)
        
        val colorFrom = cardView.context.getColor(R.color.surface)
        val colorTo = cardView.context.getColor(R.color.health_danger)
        
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.duration = 500 // 0.5 secondi
        colorAnimation.repeatCount = ValueAnimator.INFINITE
        colorAnimation.repeatMode = ValueAnimator.REVERSE
        
        colorAnimation.addUpdateListener { animator ->
            try {
                cardView.setCardBackgroundColor(animator.animatedValue as Int)
            } catch (e: Exception) {
                // Fallback: usa background normale
                cardView.setBackgroundColor(animator.animatedValue as Int)
            }
        }
        
        colorAnimation.start()
        activeAnimations[cardView] = colorAnimation
        
        // Log per debug
        android.util.Log.d("BlinkAnimation", "Started blinking card")
    }
    
    /**
     * Fa lampeggiare il bordo della card in rosso
     */
    fun startBlinkingBorder(cardView: CardView) {
        // Ferma animazione precedente se esiste
        stopBlinking(cardView)
        
        // Imposta background con bordo rosso
        cardView.setBackgroundResource(R.drawable.bg_card_alert_pulse)
        
        // Animazione alpha per effetto lampeggio
        val alphaAnimation = ObjectAnimator.ofFloat(cardView, "alpha", 1f, 0.5f, 1f)
        alphaAnimation.duration = 800
        alphaAnimation.repeatCount = ValueAnimator.INFINITE
        alphaAnimation.start()
        
        activeAnimations[cardView] = alphaAnimation
    }
    
    /**
     * Fa lampeggiare il testo in rosso
     */
    fun startBlinkingText(textView: android.widget.TextView) {
        // Ferma animazione precedente se esiste
        stopBlinking(textView)
        
        val colorFrom = textView.context.getColor(R.color.health_danger)
        val colorTo = Color.WHITE
        
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.duration = 600
        colorAnimation.repeatCount = ValueAnimator.INFINITE
        colorAnimation.repeatMode = ValueAnimator.REVERSE
        
        colorAnimation.addUpdateListener { animator ->
            textView.setTextColor(animator.animatedValue as Int)
        }
        
        colorAnimation.start()
        activeAnimations[textView] = colorAnimation
    }
    
    /**
     * Effetto pulsante - scala la card
     */
    fun startPulseAnimation(view: View) {
        stopBlinking(view)
        
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.05f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.05f, 1f)
        
        scaleX.duration = 800
        scaleY.duration = 800
        scaleX.repeatCount = ValueAnimator.INFINITE
        scaleY.repeatCount = ValueAnimator.INFINITE
        
        scaleX.start()
        scaleY.start()
        
        activeAnimations[view] = scaleX
    }
    
    /**
     * Effetto shake - scuote la card
     */
    fun startShakeAnimation(view: View) {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.shake)
        view.startAnimation(animation)
    }
    
    /**
     * Combinazione: lampeggio + pulsazione
     */
    fun startAlertAnimation(cardView: CardView, textView: android.widget.TextView) {
        startBlinkingCard(cardView)
        startBlinkingText(textView)
    }
    
    /**
     * Ferma tutte le animazioni su una view
     */
    fun stopBlinking(view: View) {
        activeAnimations[view]?.cancel()
        activeAnimations.remove(view)
        
        // Reset view
        view.alpha = 1f
        view.scaleX = 1f
        view.scaleY = 1f
        view.clearAnimation()
        
        // Reset colori
        if (view is CardView) {
            view.setCardBackgroundColor(view.context.getColor(R.color.surface))
        }
    }
    
    /**
     * Ferma tutte le animazioni attive
     */
    fun stopAllAnimations() {
        activeAnimations.values.forEach { it.cancel() }
        activeAnimations.clear()
    }
}
