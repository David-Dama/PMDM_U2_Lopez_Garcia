package com.example.mibiblioteca;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.mibiblioteca.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements LibroAdapter.OnLibroListener {

    private ActivityMainBinding binding;
    private LibroDao dao;
    private LibroAdapter adapter;

    // HILO SECUNDARIO: aquí se hace TODO el trabajo pesado con la base de datos[cite: 28]
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });

        setSupportActionBar(binding.toolbar);

        // 1. Obtener la base de datos y su DAO[cite: 28]
        dao = AppDatabase.getInstance(this).libroDao();

        // 2. Preparar la lista (RecyclerView y Adapter)[cite: 28]
        adapter = new LibroAdapter(this);
        binding.rvLibros.setLayoutManager(new LinearLayoutManager(this));
        binding.rvLibros.setAdapter(adapter);

        binding.btnAnadir.setOnClickListener(v -> anadirLibro());

        // 3. Leer lo que ya había guardado previamente[cite: 28]
        cargarLibros();
    }

    // LEER: hilo secundario -> resultado al hilo principal[cite: 29]
    private void cargarLibros() {
        executor.execute(() -> {
            // Esto se ejecuta en el HILO SECUNDARIO (no puede tocar la interfaz)[cite: 29]
            List<Libro> lista = dao.obtenerTodos();
            int leidos = dao.contarLeidos();

            // Volvemos al HILO PRINCIPAL para pintar los datos[cite: 29]
            runOnUiThread(() -> mostrar(lista, leidos));
        });
    }

    private void mostrar(List<Libro> lista, int leidos) {
        adapter.setLibros(lista);
        binding.tvVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(getString(R.string.resumen_libros, lista.size(), leidos));
        }
    }

    // AÑADIR LIBRO CON VALIDACIÓN
    private void anadirLibro() {
        String titulo = texto(binding.etTitulo.getText());
        String autor = texto(binding.etAutor.getText());
        String paginasTexto = texto(binding.etPaginas.getText());

        binding.tilTitulo.setError(null);
        binding.tilAutor.setError(null);
        binding.tilPaginas.setError(null);

        boolean correcto = true;

        if (titulo.isEmpty()) {
            binding.tilTitulo.setError(getString(R.string.error_obligatorio));
            correcto = false;
        }
        if (autor.isEmpty()) {
            binding.tilAutor.setError(getString(R.string.error_obligatorio));
            correcto = false;
        }

        int paginas = 0;
        try {
            paginas = Integer.parseInt(paginasTexto);
        } catch (NumberFormatException ignored) {
        }

        if (paginas <= 0) {
            binding.tilPaginas.setError(getString(R.string.error_paginas));
            correcto = false;
        }

        if (!correcto) return;

        Libro nuevo = new Libro(titulo, autor, paginas);

        executor.execute(() -> {
            dao.insertar(nuevo); // Trabajo en hilo secundario[cite: 30]

            runOnUiThread(() -> { // Volvemos al hilo principal para actualizar UI[cite: 30]
                binding.etTitulo.setText("");
                binding.etAutor.setText("");
                binding.etPaginas.setText("");
                binding.etTitulo.requestFocus();
                Toast.makeText(this, R.string.libro_guardado, Toast.LENGTH_SHORT).show();
                cargarLibros(); // Recargar la lista completa[cite: 30]
            });
        });
    }

    // ACTUALIZAR Y ELIMINAR (Avisos que llegan desde el adaptador)[cite: 31]
    @Override
    public void onCambiarLeido(Libro libro, boolean leido) {
        libro.leido = leido;
        executor.execute(() -> {
            dao.actualizar(libro);
            cargarLibros();
        });
    }

    @Override
    public void onEliminar(Libro libro) {
        executor.execute(() -> {
            dao.eliminar(libro);
            cargarLibros();

            runOnUiThread(() -> Snackbar.make(binding.getRoot(), R.string.libro_eliminado, Snackbar.LENGTH_LONG)
                    .setAction(R.string.deshacer, v -> executor.execute(() -> {
                        dao.insertar(libro); // Vuelve a insertar el libro con su id original[cite: 31]
                        cargarLibros();
                    }))
                    .show());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown(); // Libera el hilo al cerrar la pantalla para evitar fugas de memoria[cite: 31]
    }

    private String texto(CharSequence cs) {
        return cs == null ? "" : cs.toString().trim();
    }
}