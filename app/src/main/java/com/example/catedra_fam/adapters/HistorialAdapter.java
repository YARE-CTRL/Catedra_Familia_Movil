package com.example.catedra_fam.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catedra_fam.R;
import com.example.catedra_fam.models.Entrega;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.EntregaViewHolder> {

    private List<Entrega> listaEntregas;
    private OnEntregaClickListener listener;

    public interface OnEntregaClickListener {
        void onVerEvidenciaClick(Entrega entrega);
    }

    public HistorialAdapter(List<Entrega> listaEntregas, OnEntregaClickListener listener) {
        this.listaEntregas = listaEntregas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EntregaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historial_card, parent, false);
        return new EntregaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntregaViewHolder holder, int position) {
        holder.bind(listaEntregas.get(position));
    }

    @Override
    public int getItemCount() {
        return listaEntregas.size();
    }

    class EntregaViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardEntrega;
        private View viewIndicador;
        private TextView tvIcono;
        private TextView tvTitulo;
        private TextView tvFechaEntrega;
        private TextView tvCalificacion;
        private LinearLayout layoutFeedback;
        private TextView tvFeedback;
        private MaterialButton btnVerEvidencia;

        public EntregaViewHolder(@NonNull View itemView) {
            super(itemView);
            cardEntrega = itemView.findViewById(R.id.card_entrega);
            viewIndicador = itemView.findViewById(R.id.view_indicador);
            tvIcono = itemView.findViewById(R.id.tv_icono);
            tvTitulo = itemView.findViewById(R.id.tv_titulo);
            tvFechaEntrega = itemView.findViewById(R.id.tv_fecha_entrega);
            tvCalificacion = itemView.findViewById(R.id.tv_calificacion);
            layoutFeedback = itemView.findViewById(R.id.layout_feedback);
            tvFeedback = itemView.findViewById(R.id.tv_feedback);
            btnVerEvidencia = itemView.findViewById(R.id.btn_ver_evidencia);
        }

        public void bind(Entrega entrega) {
            Context context = itemView.getContext();

            // Título
            tvTitulo.setText(entrega.getTareaTitulo());

            // Fecha de entrega
            tvFechaEntrega.setText("Entregada: " + formatearFecha(entrega.getFechaEntrega()));

            // Estado e indicadores según estado
            switch (entrega.getEstado()) {
                case "calificada":
                    tvIcono.setText("✅");
                    viewIndicador.setBackgroundColor(ContextCompat.getColor(context, R.color.success));

                    // Mostrar calificación
                    tvCalificacion.setVisibility(View.VISIBLE);
                    String textoCalificacion = "Calificación: " + entrega.getNotaCualitativa()
                            + " (" + entrega.getNota() + ")";
                    tvCalificacion.setText(textoCalificacion);
                    tvCalificacion.setTextColor(ContextCompat.getColor(context, R.color.success));

                    // Mostrar feedback si existe
                    if (entrega.getFeedback() != null && !entrega.getFeedback().isEmpty()) {
                        layoutFeedback.setVisibility(View.VISIBLE);
                        tvFeedback.setText("\"" + entrega.getFeedback() + "\"");
                    } else {
                        layoutFeedback.setVisibility(View.GONE);
                    }
                    break;

                case "en_revision":
                    tvIcono.setText("🟣");
                    viewIndicador.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_soft));
                    tvCalificacion.setVisibility(View.VISIBLE);
                    tvCalificacion.setText("Estado: En revisión");
                    tvCalificacion.setTextColor(ContextCompat.getColor(context, R.color.purple_soft));
                    layoutFeedback.setVisibility(View.GONE);
                    break;

                case "enviada":
                default:
                    tvIcono.setText("⏳");
                    viewIndicador.setBackgroundColor(ContextCompat.getColor(context, R.color.info));
                    tvCalificacion.setVisibility(View.VISIBLE);
                    tvCalificacion.setText("Estado: Enviada");
                    tvCalificacion.setTextColor(ContextCompat.getColor(context, R.color.info));
                    layoutFeedback.setVisibility(View.GONE);
                    break;
            }

            // Botón ver evidencia
            btnVerEvidencia.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVerEvidenciaClick(entrega);
                }
            });
        }

        private String formatearFecha(String fechaISO) {
            try {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date fecha = isoFormat.parse(fechaISO);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("es", "ES"));
                return outputFormat.format(fecha);
            } catch (ParseException e) {
                return fechaISO;
            }
        }
    }
}

