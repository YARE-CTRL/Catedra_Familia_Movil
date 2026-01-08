package com.example.catedra_fam.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catedra_fam.R;
import com.example.catedra_fam.models.Hijo;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class HijosAdapter extends RecyclerView.Adapter<HijosAdapter.HijoViewHolder> {

    private List<Hijo> listaHijos;
    private OnHijoClickListener listener;

    public interface OnHijoClickListener {
        void onHijoClick(Hijo hijo);
    }

    public HijosAdapter(List<Hijo> listaHijos, OnHijoClickListener listener) {
        this.listaHijos = listaHijos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HijoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hijo_card, parent, false);
        return new HijoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HijoViewHolder holder, int position) {
        Hijo hijo = listaHijos.get(position);
        holder.bind(hijo);
    }

    @Override
    public int getItemCount() {
        return listaHijos.size();
    }

    class HijoViewHolder extends RecyclerView.ViewHolder {
        private TextView tvIniciales;
        private TextView tvNombre;
        private TextView tvCurso;
        private TextView tvPendientes;
        private TextView tvCompletadas;
        private TextView tvCalificadas;
        private TextView tvProximaTarea;
        private View viewIndicador;
        private MaterialButton btnVerTareas;
        private CardView cardHijo;

        public HijoViewHolder(@NonNull View itemView) {
            super(itemView);
            cardHijo = itemView.findViewById(R.id.card_hijo);
            tvIniciales = itemView.findViewById(R.id.tv_iniciales);
            tvNombre = itemView.findViewById(R.id.tv_nombre_hijo);
            tvCurso = itemView.findViewById(R.id.tv_curso);
            tvPendientes = itemView.findViewById(R.id.tv_pendientes);
            tvCompletadas = itemView.findViewById(R.id.tv_completadas);
            tvCalificadas = itemView.findViewById(R.id.tv_calificadas);
            tvProximaTarea = itemView.findViewById(R.id.tv_proxima_tarea);
            viewIndicador = itemView.findViewById(R.id.view_indicador);
            btnVerTareas = itemView.findViewById(R.id.btn_ver_tareas);
        }

        public void bind(Hijo hijo) {
            Context context = itemView.getContext();

            // Datos básicos
            tvIniciales.setText(hijo.getIniciales());
            tvNombre.setText(hijo.getNombreCompleto());
            tvCurso.setText(hijo.getCursoNombre());

            // Estadísticas
            tvPendientes.setText(hijo.getTareasPendientes() + " pendientes");
            tvCompletadas.setText(hijo.getTareasCompletadas() + " completadas");
            tvCalificadas.setText(hijo.getTareasCalificadas() + " calificadas");

            // Próxima tarea
            if (hijo.getProximaTarea() != null && !hijo.getProximaTarea().isEmpty()) {
                tvProximaTarea.setVisibility(View.VISIBLE);
                tvProximaTarea.setText("📅 " + hijo.getProximaTarea() + " - " + hijo.getFechaProximaTarea());
            } else {
                tvProximaTarea.setVisibility(View.GONE);
            }

            // Color según estado
            int colorIndicador;
            switch (hijo.getEstado()) {
                case "con_vencidas":
                    colorIndicador = ContextCompat.getColor(context, R.color.danger);
                    tvPendientes.setTextColor(ContextCompat.getColor(context, R.color.danger));
                    break;
                case "con_pendientes_urgentes":
                case "con_pendientes":
                    colorIndicador = ContextCompat.getColor(context, R.color.warning);
                    tvPendientes.setTextColor(ContextCompat.getColor(context, R.color.warning));
                    break;
                case "al_dia":
                default:
                    colorIndicador = ContextCompat.getColor(context, R.color.success);
                    tvPendientes.setTextColor(ContextCompat.getColor(context, R.color.success));
                    tvPendientes.setText("✓ Al día");
                    break;
            }

            if (viewIndicador != null) {
                viewIndicador.setBackgroundColor(colorIndicador);
            }

            // Click en botón
            btnVerTareas.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onHijoClick(hijo);
                }
            });

            // Click en card
            cardHijo.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onHijoClick(hijo);
                }
            });
        }
    }
}

