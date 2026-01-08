package com.example.catedra_fam.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catedra_fam.R;
import com.example.catedra_fam.models.Tarea;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class TareasAdapter extends RecyclerView.Adapter<TareasAdapter.TareaViewHolder> {

    private List<Tarea> listaTareas;
    private OnTareaClickListener listener;

    public interface OnTareaClickListener {
        void onTareaClick(Tarea tarea);
        void onVerDetalleClick(Tarea tarea);
    }

    public TareasAdapter(List<Tarea> listaTareas, OnTareaClickListener listener) {
        this.listaTareas = listaTareas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tarea_card, parent, false);
        return new TareaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        Tarea tarea = listaTareas.get(position);
        holder.bind(tarea);
    }

    @Override
    public int getItemCount() {
        return listaTareas.size();
    }

    public void updateList(List<Tarea> newList) {
        this.listaTareas = newList;
        notifyDataSetChanged();
    }

    class TareaViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardTarea;
        private View viewIndicador;
        private TextView tvTitulo;
        private TextView tvFecha;
        private TextView tvFrecuencia;
        private TextView tvEstado;
        private TextView tvNota;
        private MaterialButton btnAccion;

        public TareaViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTarea = itemView.findViewById(R.id.card_tarea);
            viewIndicador = itemView.findViewById(R.id.view_indicador);
            tvTitulo = itemView.findViewById(R.id.tv_titulo_tarea);
            tvFecha = itemView.findViewById(R.id.tv_fecha);
            tvFrecuencia = itemView.findViewById(R.id.tv_frecuencia);
            tvEstado = itemView.findViewById(R.id.tv_estado);
            tvNota = itemView.findViewById(R.id.tv_nota);
            btnAccion = itemView.findViewById(R.id.btn_accion);
        }

        public void bind(Tarea tarea) {
            Context context = itemView.getContext();

            // Datos básicos
            tvTitulo.setText(tarea.getTitulo());

            // Frecuencia
            String frecuencia = tarea.getFrecuencia();
            if (frecuencia != null && !frecuencia.isEmpty()) {
                tvFrecuencia.setText(frecuencia);
                tvFrecuencia.setVisibility(View.VISIBLE);
            } else {
                tvFrecuencia.setVisibility(View.GONE);
            }

            // Configurar según estado
            int colorIndicador;
            String textoFecha;
            String textoEstado;
            String textoBoton;

            switch (tarea.getEstado()) {
                case "vencida":
                    colorIndicador = ContextCompat.getColor(context, R.color.danger);
                    textoFecha = "Venció: " + tarea.getFechaVencimiento();
                    textoEstado = "VENCIDA";
                    textoBoton = "ENTREGAR YA";
                    tvEstado.setTextColor(ContextCompat.getColor(context, R.color.danger));
                    tvFecha.setTextColor(ContextCompat.getColor(context, R.color.danger));
                    cardTarea.setStrokeColor(ContextCompat.getColor(context, R.color.danger));
                    cardTarea.setStrokeWidth(2);
                    tvNota.setVisibility(View.GONE);
                    break;

                case "proxima":
                    colorIndicador = ContextCompat.getColor(context, R.color.warning);
                    textoFecha = "Vence: " + tarea.getFechaVencimiento();
                    textoEstado = "Próxima a vencer";
                    textoBoton = "VER DETALLE";
                    tvEstado.setTextColor(ContextCompat.getColor(context, R.color.warning));
                    tvFecha.setTextColor(ContextCompat.getColor(context, R.color.warning));
                    cardTarea.setStrokeColor(ContextCompat.getColor(context, R.color.warning));
                    cardTarea.setStrokeWidth(2);
                    tvNota.setVisibility(View.GONE);
                    break;

                case "pendiente":
                    colorIndicador = ContextCompat.getColor(context, R.color.info);
                    textoFecha = "Vence: " + tarea.getFechaVencimiento();
                    textoEstado = "Pendiente";
                    textoBoton = "VER DETALLE";
                    tvEstado.setTextColor(ContextCompat.getColor(context, R.color.info));
                    tvFecha.setTextColor(ContextCompat.getColor(context, R.color.gray_600));
                    cardTarea.setStrokeWidth(0);
                    tvNota.setVisibility(View.GONE);
                    break;

                case "completada":
                    colorIndicador = ContextCompat.getColor(context, R.color.secondary);
                    textoFecha = "Entregada: " + tarea.getFechaVencimiento();
                    textoEstado = "En revisión";
                    textoBoton = "VER EVIDENCIA";
                    tvEstado.setTextColor(ContextCompat.getColor(context, R.color.secondary));
                    tvFecha.setTextColor(ContextCompat.getColor(context, R.color.gray_600));
                    cardTarea.setStrokeWidth(0);
                    tvNota.setVisibility(View.GONE);
                    break;

                case "calificada":
                default:
                    colorIndicador = ContextCompat.getColor(context, R.color.success);
                    textoFecha = "Entregada: " + tarea.getFechaVencimiento();
                    textoEstado = "Calificada";
                    textoBoton = "VER NOTA";
                    tvEstado.setTextColor(ContextCompat.getColor(context, R.color.success));
                    tvFecha.setTextColor(ContextCompat.getColor(context, R.color.gray_600));
                    cardTarea.setStrokeColor(ContextCompat.getColor(context, R.color.success));
                    cardTarea.setStrokeWidth(2);

                    // Mostrar nota si existe
                    if (tarea.getNota() != null && !tarea.getNota().isEmpty()) {
                        tvNota.setVisibility(View.VISIBLE);
                        tvNota.setText("Nota: " + tarea.getNota());
                    } else {
                        tvNota.setVisibility(View.GONE);
                    }
                    break;
            }

            // Aplicar colores
            viewIndicador.setBackgroundColor(colorIndicador);
            tvFecha.setText(textoFecha);
            tvEstado.setText(textoEstado);
            btnAccion.setText(textoBoton);

            // Listeners
            btnAccion.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVerDetalleClick(tarea);
                }
            });

            cardTarea.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTareaClick(tarea);
                }
            });
        }
    }
}

