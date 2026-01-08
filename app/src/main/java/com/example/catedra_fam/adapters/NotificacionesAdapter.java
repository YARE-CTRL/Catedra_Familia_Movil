package com.example.catedra_fam.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catedra_fam.R;
import com.example.catedra_fam.models.Notificacion;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class NotificacionesAdapter extends RecyclerView.Adapter<NotificacionesAdapter.NotificacionViewHolder> {

    private List<Notificacion> listaNotificaciones;
    private OnNotificacionClickListener listener;

    public interface OnNotificacionClickListener {
        void onAccionClick(Notificacion notificacion);
        void onNotificacionClick(Notificacion notificacion);
    }

    public NotificacionesAdapter(List<Notificacion> listaNotificaciones, OnNotificacionClickListener listener) {
        this.listaNotificaciones = listaNotificaciones;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notificacion_card, parent, false);
        return new NotificacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacionViewHolder holder, int position) {
        holder.bind(listaNotificaciones.get(position));
    }

    @Override
    public int getItemCount() {
        return listaNotificaciones.size();
    }

    class NotificacionViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardNotificacion;
        private TextView tvIcono;
        private TextView tvTitulo;
        private TextView tvMensaje;
        private TextView tvTiempo;
        private MaterialButton btnAccion;

        public NotificacionViewHolder(@NonNull View itemView) {
            super(itemView);
            cardNotificacion = itemView.findViewById(R.id.card_notificacion);
            tvIcono = itemView.findViewById(R.id.tv_icono);
            tvTitulo = itemView.findViewById(R.id.tv_titulo);
            tvMensaje = itemView.findViewById(R.id.tv_mensaje);
            tvTiempo = itemView.findViewById(R.id.tv_tiempo);
            btnAccion = itemView.findViewById(R.id.btn_accion);
        }

        public void bind(Notificacion notificacion) {
            Context context = itemView.getContext();

            // Icono según tipo
            tvIcono.setText(notificacion.getIcono());

            // Título
            tvTitulo.setText(notificacion.getTitulo());

            // Mensaje
            tvMensaje.setText(notificacion.getMensaje());

            // Tiempo
            tvTiempo.setText(notificacion.getTiempo());

            // Texto del botón
            btnAccion.setText(notificacion.getTextoAccion());

            // Estilo según si está leída o no
            if (!notificacion.isLeida()) {
                // No leída - más prominente
                cardNotificacion.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
                cardNotificacion.setStrokeColor(ContextCompat.getColor(context, R.color.primary_light));
                cardNotificacion.setStrokeWidth(2);
                tvTitulo.setTypeface(null, Typeface.BOLD);
                tvTitulo.setTextColor(ContextCompat.getColor(context, R.color.gray_900));
            } else {
                // Leída - más sutil
                cardNotificacion.setCardBackgroundColor(ContextCompat.getColor(context, R.color.gray_50));
                cardNotificacion.setStrokeWidth(0);
                tvTitulo.setTypeface(null, Typeface.NORMAL);
                tvTitulo.setTextColor(ContextCompat.getColor(context, R.color.gray_600));
            }

            // Click en el card
            cardNotificacion.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNotificacionClick(notificacion);
                }
            });

            // Click en el botón de acción
            btnAccion.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAccionClick(notificacion);
                }
            });
        }
    }
}

