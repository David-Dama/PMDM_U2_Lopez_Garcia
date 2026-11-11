package com.example.noticiasfp;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noticiasfp.databinding.ItemNoticiaBinding;

import java.util.List;

public class NoticiaAdapter extends RecyclerView.Adapter<NoticiaAdapter.NoticiaViewHolder> {

    private final List<Noticia> noticias;

    public NoticiaAdapter(List<Noticia> noticias) {
        this.noticias = noticias;
    }

    @NonNull
    @Override
    public NoticiaViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        ItemNoticiaBinding binding = ItemNoticiaBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new NoticiaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull NoticiaViewHolder holder, int position) {

        Noticia noticia = noticias.get(position);

        holder.binding.tvTituloNoticia.setText(noticia.getTitulo());
        holder.binding.tvDescripcionNoticia.setText(noticia.getDescripcion());
        holder.binding.tvFechaNoticia.setText(noticia.getFecha());
        holder.binding.chipCategoria.setText(noticia.getCategoria());
        holder.binding.ivNoticia.setImageResource(noticia.getImagen());

        // Actualizar el texto según el estado de favorito.
        holder.binding.btnFavorita.setText(
                noticia.isFavorita()
                        ? "Quitar de favoritas"
                        : "Añadir a favoritas"
        );

        // Gestionar el clic sobre el botón.
        holder.binding.btnFavorita.setOnClickListener(view -> {

            noticia.setFavorita(!noticia.isFavorita());

            holder.binding.btnFavorita.setText(
                    noticia.isFavorita()
                            ? "Quitar de favoritas"
                            : "Añadir a favoritas"
            );

        });
    }

    @Override
    public int getItemCount() {
        return noticias.size();
    }

    public void actualizarNoticias(List<Noticia> nuevasNoticias) {
        noticias.clear();
        noticias.addAll(nuevasNoticias);
        notifyDataSetChanged();
    }

    static class NoticiaViewHolder extends RecyclerView.ViewHolder {
        private final ItemNoticiaBinding binding;

        public NoticiaViewHolder(ItemNoticiaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
