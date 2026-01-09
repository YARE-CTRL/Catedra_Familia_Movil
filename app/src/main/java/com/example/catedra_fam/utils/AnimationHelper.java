package com.example.catedra_fam.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.catedra_fam.R;

/**
 * Clase helper para animaciones y transiciones
 */
public class AnimationHelper {

    /**
     * Anima un card cuando se marca como completado
     */
    public static void animarCompletado(View view, Runnable onComplete) {
        // Escala hacia abajo y luego vuelve
        view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .withEndAction(onComplete)
                            .start();
                })
                .start();
    }

    /**
     * Anima fade in de una vista
     */
    public static void fadeIn(View view) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1f)
                .setDuration(300)
                .setListener(null);
    }

    /**
     * Anima fade out de una vista
     */
    public static void fadeOut(View view) {
        view.animate()
                .alpha(0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * Slide in desde la derecha
     */
    public static void slideInRight(View view) {
        view.setTranslationX(view.getWidth());
        view.setVisibility(View.VISIBLE);
        view.animate()
                .translationX(0)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(null);
    }

    /**
     * Slide out hacia la izquierda
     */
    public static void slideOutLeft(View view) {
        view.animate()
                .translationX(-view.getWidth())
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * Animación de pulso (para notificaciones o badges)
     */
    public static void pulsar(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.2f, 1f);
        scaleX.setDuration(600);
        scaleY.setDuration(600);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        scaleX.start();
        scaleY.start();
    }

    /**
     * Animación de shake (para errores)
     */
    public static void shake(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX",
                0, 25, -25, 25, -25, 15, -15, 6, -6, 0);
        animator.setDuration(500);
        animator.start();
    }

    /**
     * Mostrar vista con slide up desde abajo
     */
    public static void slideUp(View view) {
        view.setTranslationY(view.getHeight());
        view.setVisibility(View.VISIBLE);
        view.animate()
                .translationY(0)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(null);
    }

    /**
     * Ocultar vista con slide down hacia abajo
     */
    public static void slideDown(View view) {
        view.animate()
                .translationY(view.getHeight())
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * Rotación de 360 grados (para botones de refresh)
     */
    public static void rotar360(View view) {
        view.animate()
                .rotation(360f)
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> view.setRotation(0))
                .start();
    }

    /**
     * Aplicar animación de transición al Activity
     */
    public static void aplicarTransicionSlide(Context context) {
        if (context instanceof android.app.Activity) {
            ((android.app.Activity) context).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            );
        }
    }

    /**
     * Aplicar animación de transición hacia atrás
     */
    public static void aplicarTransicionSlideBack(Context context) {
        if (context instanceof android.app.Activity) {
            ((android.app.Activity) context).overridePendingTransition(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
        }
    }

    /**
     * Aplicar animación fade
     */
    public static void aplicarTransicionFade(Context context) {
        if (context instanceof android.app.Activity) {
            ((android.app.Activity) context).overridePendingTransition(
                    R.anim.fade_in,
                    R.anim.fade_out
            );
        }
    }

    /**
     * Animar cambio de color (para indicadores de estado)
     */
    public static void animarCambioColor(View view, int colorInicial, int colorFinal) {
        ValueAnimator colorAnimation = ValueAnimator.ofArgb(colorInicial, colorFinal);
        colorAnimation.setDuration(500);
        colorAnimation.addUpdateListener(animator -> {
            view.setBackgroundColor((int) animator.getAnimatedValue());
        });
        colorAnimation.start();
    }

    /**
     * Mostrar loading con fade in
     */
    public static void mostrarLoading(View loadingView) {
        fadeIn(loadingView);
    }

    /**
     * Ocultar loading con fade out
     */
    public static void ocultarLoading(View loadingView) {
        fadeOut(loadingView);
    }

    /**
     * Animar aparición de lista (stagger animation)
     */
    public static void animarAparicionItem(View item, int position) {
        item.setAlpha(0f);
        item.setTranslationY(50f);
        item.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setStartDelay(position * 50L) // 50ms de delay por cada item
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }
}

