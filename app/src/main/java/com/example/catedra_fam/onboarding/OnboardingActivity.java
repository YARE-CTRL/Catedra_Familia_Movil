package com.example.catedra_fam.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.catedra_fam.LoginActivity;
import com.example.catedra_fam.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabIndicator;
    private LinearLayout indicatorContainer;
    private View[] indicators;
    private MaterialButton btnNext, btnSkip;
    private int currentPage = 0;
    private static final int TOTAL_SLIDES = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ============================================
        // MODO DESARROLLO - Para saltar onboarding
        // ============================================
        // Descomenta las siguientes 3 líneas para saltar el onboarding en desarrollo:
        // navigateToLogin();
        // return;
        // ============================================

        // Verificar si ya completó onboarding
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        if (prefs.getBoolean("onboarding_completado", false)) {
            navigateToLogin();
            return;
        }

        setContentView(R.layout.activity_onboarding);
        initViews();
        setupViewPager();
        setupListeners();
        setupBackPress();
    }

    private void setupBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (currentPage > 0) {
                    viewPager.setCurrentItem(currentPage - 1);
                } else {
                    finish();
                }
            }
        });
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPagerOnboarding);
        tabIndicator = findViewById(R.id.tabIndicator);
        indicatorContainer = findViewById(R.id.indicatorContainer);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);

        // Crear indicadores personalizados
        setupIndicators();
    }

    private void setupIndicators() {
        indicators = new View[TOTAL_SLIDES];

        for (int i = 0; i < TOTAL_SLIDES; i++) {
            indicators[i] = new View(this);

            LinearLayout.LayoutParams params;
            if (i == 0) {
                // Primer indicador activo (alargado)
                params = new LinearLayout.LayoutParams(
                    dpToPx(24), dpToPx(8)
                );
            } else {
                // Indicadores inactivos (círculos)
                params = new LinearLayout.LayoutParams(
                    dpToPx(8), dpToPx(8)
                );
            }
            params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            indicators[i].setLayoutParams(params);

            if (i == 0) {
                indicators[i].setBackgroundResource(R.drawable.indicator_active);
            } else {
                indicators[i].setBackgroundResource(R.drawable.indicator_inactive);
            }

            indicatorContainer.addView(indicators[i]);
        }
    }

    private void updateIndicators(int position) {
        for (int i = 0; i < TOTAL_SLIDES; i++) {
            LinearLayout.LayoutParams params;
            if (i == position) {
                // Indicador activo (alargado)
                params = new LinearLayout.LayoutParams(
                    dpToPx(24), dpToPx(8)
                );
                indicators[i].setBackgroundResource(R.drawable.indicator_active);
            } else {
                // Indicadores inactivos (círculos)
                params = new LinearLayout.LayoutParams(
                    dpToPx(8), dpToPx(8)
                );
                indicators[i].setBackgroundResource(R.drawable.indicator_inactive);
            }
            params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            indicators[i].setLayoutParams(params);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void setupViewPager() {
        OnboardingAdapter adapter = new OnboardingAdapter(this);
        viewPager.setAdapter(adapter);

        // Vincular TabLayout con ViewPager2
        new TabLayoutMediator(tabIndicator, viewPager, (tab, position) -> {
            // Tabs son solo indicadores visuales
        }).attach();

        // Escuchar cambios de página
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                updateButtons();
                updateIndicators(position);
            }
        });
    }

    private void setupListeners() {
        btnNext.setOnClickListener(v -> {
            if (currentPage < TOTAL_SLIDES - 1) {
                // Ir a la siguiente página
                viewPager.setCurrentItem(currentPage + 1);
            } else {
                // Última página - finalizar onboarding
                finishOnboarding();
            }
        });

        btnSkip.setOnClickListener(v -> {
            if (currentPage == TOTAL_SLIDES - 1) {
                // En última página, retroceder
                viewPager.setCurrentItem(currentPage - 1);
            } else {
                // En otras páginas, saltar onboarding
                finishOnboarding();
            }
        });
    }

    private void updateButtons() {
        if (currentPage == TOTAL_SLIDES - 1) {
            // Última página
            btnNext.setText(R.string.onboarding_comenzar);
            btnSkip.setText(R.string.onboarding_atras);
        } else {
            // Páginas intermedias
            btnNext.setText(R.string.onboarding_siguiente);
            btnSkip.setText(R.string.onboarding_saltar);
        }
    }

    private void finishOnboarding() {
        // Marcar onboarding como completado
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        prefs.edit().putBoolean("onboarding_completado", true).apply();

        // Navegar a Login
        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); // No permitir volver atrás
    }
}

