package scarlet.believe.remember.home

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.transition.TransitionValues
import android.view.View
import android.view.ViewGroup
import androidx.transition.Explode

class ExplodeFadeOut : android.transition.Explode(){
    init {
        propagation = null
    }

    override fun onAppear(
        sceneRoot: ViewGroup?,
        view: View?,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator {
        val exploadAnimator =  super.onAppear(sceneRoot, view, startValues, endValues)
        val fadeInAnimator = ObjectAnimator.ofFloat(view,View.ALPHA,0f,1f)
        return animatorset(exploadAnimator,fadeInAnimator)
    }

    override fun onDisappear(
        sceneRoot: ViewGroup?,
        view: View?,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator {
        val exploadAnimator = super.onDisappear(sceneRoot, view, startValues, endValues)
        val fadeOutAnimator = ObjectAnimator.ofFloat(view,View.ALPHA,1f,0f)
        return animatorset(exploadAnimator,fadeOutAnimator)
    }

    private fun animatorset(explodeAnimator: Animator, fadeAnimator: ObjectAnimator) : AnimatorSet{
        val animatorSet = AnimatorSet()
        animatorSet.play(explodeAnimator).with(fadeAnimator)
        return animatorSet
    }

}