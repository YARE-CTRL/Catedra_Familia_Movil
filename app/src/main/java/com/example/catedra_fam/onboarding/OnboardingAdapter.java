package com.example.catedra_fam.onboarding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.catedra_fam.R;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private final Context context;
    private static final int TOTAL_SLIDES = 4;

    // Datos de cada slide
    private final String[] titles = {
        "Bienvenido a\nPARCHANDO JUNTOS",
        "¿Qué es Cátedra\nde Familia?",
        "¿Cómo funciona?",
        "¡Funciona sin internet!"
    };

    private final String[] descriptions = {
        "Fortalece los lazos familiares a través de actividades semanales",
        "Programa donde los docentes asignan tareas familiares\n\nEjemplos:\n• Lectura en familia\n• Juegos de mesa\n• Conversaciones sobre valores",
        "1️⃣ Recibes tareas del docente\n2️⃣ Realizas la actividad con tus hijos\n3️⃣ Subes fotos y escribes qué hicieron\n4️⃣ El docente califica y va al boletín",
        "✅ Ves tareas sin conexión\n✅ Escribes evidencias que se envían después\n✅ Consultas calificaciones guardadas\n\n📱 Ideal para zonas rurales"
    };

    // Íconos simples para cada slide
    private final int[] icons = {
        R.drawable.pjj,  // Slide 1: Bienvenida
        R.drawable.pjj,  // Slide 2: Qué es
        R.drawable.pjj,  // Slide 3: Cómo funciona
        R.drawable.pjj   // Slide 4: Offline
    };

    public OnboardingAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_onboarding_slide, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.tvTitle.setText(titles[position]);
        holder.tvDescription.setText(descriptions[position]);
        holder.ivIcon.setImageResource(icons[position]);
    }

    @Override
    public int getItemCount() {
        return TOTAL_SLIDES;
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvDescription;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}

