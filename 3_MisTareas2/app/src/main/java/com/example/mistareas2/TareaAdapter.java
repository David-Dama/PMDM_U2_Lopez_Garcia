package com.example.mistareas2;

import android.content.Context;
import android.graphics.Paint;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mistareas2.databinding.ItemTareaBinding;

import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {

    // Interfaz para comunicar las acciones de la fila a la MainActivity
    public interface OnTareaListener {
        void onTareaPulsada(int posicion); // Toque corto
        void onMenuTarea(View ancla, int posicion); // Botón de tres puntos
        void onCrearMenuContextual(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo, int posicion); // Pulsación larga
    }

    private final List<Tarea> tareas;
    private final OnTareaListener listener;

    public TareaAdapter(List<Tarea> tareas, OnTareaListener listener) {
        this.tareas = tareas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el layout de la celda individual usando View Binding
        ItemTareaBinding binding = ItemTareaBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TareaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        // Rellenamos la celda con los datos de la posición actual
        holder.mostrar(tareas.get(position));
    }

    @Override
    public int getItemCount() {
        return tareas.size();
    }

    // --- VIEWHOLDER ---
    public class TareaViewHolder extends RecyclerView.ViewHolder {
        private final ItemTareaBinding binding;

        public TareaViewHolder(ItemTareaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Los listeners se asignan UNA sola vez al crear la celda, no en cada bind
            binding.getRoot().setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onTareaPulsada(pos);
                }
            });

            binding.btnMenuTarea.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onMenuTarea(v, pos);
                }
            });

            binding.getRoot().setOnCreateContextMenuListener((menu, v, menuInfo) -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onCrearMenuContextual(menu, v, menuInfo, pos);
                }
            });
        }

        public void mostrar(Tarea tarea) {
            Context ctx = binding.getRoot().getContext();
            binding.tvTitulo.setText(tarea.getTitulo());
            binding.tvDescripcion.setText(tarea.getDescripcion());
            binding.cbCompletada.setChecked(tarea.isCompletada());

            // Tachar el título si está completada (¡Obligatorio el else para limpiar celdas recicladas!)[cite: 32, 33]
            int flags = binding.tvTitulo.getPaintFlags();
            if (tarea.isCompletada()) {
                binding.tvTitulo.setPaintFlags(flags | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.getRoot().setAlpha(0.6f);
            } else {
                binding.tvTitulo.setPaintFlags(flags & ~Paint.STRIKE_THRU_TEXT_FLAG);
                binding.getRoot().setAlpha(1f);
            }

            // Asignar colores y textos según la prioridad
            int texto, color;
            switch (tarea.getPrioridad()) {
                case Tarea.PRIORIDAD_ALTA:
                    texto = R.string.prioridad_alta;
                    color = R.color.prioridad_alta;
                    break;
                case Tarea.PRIORIDAD_BAJA:
                    texto = R.string.prioridad_baja;
                    color = R.color.prioridad_baja;
                    break;
                default:
                    texto = R.string.prioridad_media;
                    color = R.color.prioridad_media;
                    break;
            }
            binding.tvPrioridad.setText(texto);
            binding.tvPrioridad.setTextColor(ContextCompat.getColor(ctx, color));
        }
    }
}