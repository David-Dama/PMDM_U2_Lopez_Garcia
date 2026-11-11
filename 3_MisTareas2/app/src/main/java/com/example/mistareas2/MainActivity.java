package com.example.mistareas2;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.text.InputType;
import androidx.appcompat.app.AlertDialog;

import com.example.mistareas2.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TareaAdapter.OnTareaListener {

    private ActivityMainBinding binding;
    private final List<Tarea> tareas = new ArrayList<>();
    private TareaAdapter adapter;

    private boolean vistaCuadricula = false;
    private int posicionSeleccionada = RecyclerView.NO_POSITION;
    private int contador = 1;

    // Preferencias del usuario
    private SharedPreferences preferencias;
    private String nombreUsuario;

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

        // La Toolbar hace de barra de la app: aquí se mostrará el menú de opciones[cite: 36]
        setSupportActionBar(binding.toolbar);

        // Recuperar preferencias guardadas
        preferencias = getSharedPreferences("ajustes_mis_tareas", MODE_PRIVATE);

        nombreUsuario = preferencias.getString("nombre_usuario", "");

        vistaCuadricula = preferencias.getBoolean("vista_cuadricula", false);

        // 1) Datos, 2) Adaptador, 3) RecyclerView + LayoutManager[cite: 36]
        cargarTareasDeEjemplo();
        adapter = new TareaAdapter(tareas, this);
        binding.rvTareas.setAdapter(adapter);
        aplicarLayoutManager();

        // Botón flotante para añadir tareas
        binding.fab.setOnClickListener(v -> anadirTarea());

        // Solicitar nombre únicamente en el primer arranque
        if (nombreUsuario.isEmpty()) {
            mostrarDialogoNombre();
        } else {
            actualizarTitulo();
        }
    }

    private void cargarTareasDeEjemplo() {
        tareas.add(new Tarea("Repasar layouts", "LinearLayout, ConstraintLayout y FrameLayout", Tarea.PRIORIDAD_MEDIA));
        tareas.add(new Tarea("Practicar RecyclerView", "Adapter + ViewHolder", Tarea.PRIORIDAD_ALTA));
        tareas.add(new Tarea("Probar los menús", "Opciones, contextual y popup", Tarea.PRIORIDAD_ALTA));
        tareas.add(new Tarea("Leer sobre Material Design", "CardView y FAB", Tarea.PRIORIDAD_BAJA));
        tareas.add(new Tarea("Entregar la práctica", "Subir el zip a la plataforma", Tarea.PRIORIDAD_MEDIA));

        for (int i = 1; i <= 15; i++) { // Muchas tareas para comprobar el reciclaje al desplazar[cite: 37]
            tareas.add(new Tarea("Tarea de prueba " + i, "Desplaza la lista hacia abajo", Tarea.PRIORIDAD_BAJA));
        }
    }

    // El Layout Manager decide CÓMO se colocan las filas: lista vertical o cuadrícula[cite: 37]
    private void aplicarLayoutManager() {
        if (vistaCuadricula) {
            binding.rvTareas.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            binding.rvTareas.setLayoutManager(new LinearLayoutManager(this));
        }
        invalidateOptionsMenu(); // Vuelve a llamar a onPrepareOptionsMenu para cambiar el icono
    }

    private void anadirTarea() {
        Tarea nueva = new Tarea(
                getString(R.string.nueva_tarea, contador++),
                getString(R.string.nueva_tarea_descripcion),
                Tarea.PRIORIDAD_MEDIA
        );
        tareas.add(nueva);
        adapter.notifyItemInserted(tareas.size() - 1);
        binding.rvTareas.scrollToPosition(tareas.size() - 1);
    }

    // --- MENÚ DE OPCIONES (Barra superior) ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_cambiar_vista);
        if (vistaCuadricula) {
            item.setIcon(R.drawable.ic_view_list);
            item.setTitle(R.string.ver_lista);
        } else {
            item.setIcon(R.drawable.ic_grid_view);
            item.setTitle(R.string.ver_cuadricula);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_cambiar_vista) {

            vistaCuadricula = !vistaCuadricula;

            preferencias.edit()
                    .putBoolean("vista_cuadricula", vistaCuadricula)
                    .apply();

            aplicarLayoutManager();

            return true;
        } else if (id == R.id.action_borrar_completadas) {
            borrarCompletadas();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void borrarCompletadas() {
        int borradas = 0;
        // Recorremos al revés para que los índices no se desplacen al borrar[cite: 38]
        for (int i = tareas.size() - 1; i >= 0; i--) {
            if (tareas.get(i).isCompletada()) {
                tareas.remove(i);
                adapter.notifyItemRemoved(i);
                borradas++;
            }
        }
        Toast.makeText(this, getString(R.string.tareas_borradas, borradas), Toast.LENGTH_SHORT).show();
    }

    // --- EVENTOS DE CADA FILA (Interfaz del adaptador) ---

    // Toque corto: marcar/desmarcar como completada[cite: 38]
    @Override
    public void onTareaPulsada(int posicion) {
        Tarea t = tareas.get(posicion);
        t.setCompletada(!t.isCompletada());
        adapter.notifyItemChanged(posicion); // Solo se redibuja esa fila[cite: 38]
    }

    // Botón de tres puntos: MENÚ POPUP para cambiar la prioridad[cite: 38]
    @Override
    public void onMenuTarea(View ancla, int posicion) {
        PopupMenu popup = new PopupMenu(this, ancla);
        popup.getMenuInflater().inflate(R.menu.menu_popup_prioridad, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            Tarea t = tareas.get(posicion);

            if (id == R.id.prioridad_alta) {
                t.setPrioridad(Tarea.PRIORIDAD_ALTA);
            } else if (id == R.id.prioridad_media) {
                t.setPrioridad(Tarea.PRIORIDAD_MEDIA);
            } else if (id == R.id.prioridad_baja) {
                t.setPrioridad(Tarea.PRIORIDAD_BAJA);
            } else {
                return false;
            }

            adapter.notifyItemChanged(posicion);
            return true;
        });

        popup.show();
    }

    // --- MENÚ CONTEXTUAL (Pulsación larga) ---
    @Override
    public void onCrearMenuContextual(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo, int posicion) {
        posicionSeleccionada = posicion; // Recordamos sobre qué tarea se ha pulsado[cite: 42]
        getMenuInflater().inflate(R.menu.menu_contextual_tarea, menu);
        menu.setHeaderTitle(tareas.get(posicion).getTitulo());
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int pos = posicionSeleccionada;
        if (pos == RecyclerView.NO_POSITION || pos >= tareas.size()) {
            return super.onContextItemSelected(item);
        }

        int id = item.getItemId();
        if (id == R.id.action_duplicar) {
            Tarea original = tareas.get(pos);
            Tarea copia = new Tarea(
                    getString(R.string.copia_de, original.getTitulo()),
                    original.getDescripcion(),
                    original.getPrioridad()
            );
            tareas.add(pos + 1, copia);
            adapter.notifyItemInserted(pos + 1);
            return true;
        } else if (id == R.id.action_eliminar) {
            tareas.remove(pos);
            adapter.notifyItemRemoved(pos);
            return true;
        }

        return super.onContextItemSelected(item);
    }

    private void mostrarDialogoNombre() {

        EditText campoNombre = new EditText(this);

        campoNombre.setHint(R.string.dialogo_nombre_hint);
        campoNombre.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle(R.string.dialogo_nombre_titulo)
                .setMessage(R.string.dialogo_nombre_mensaje)
                .setView(campoNombre)
                .setCancelable(false)
                .setPositiveButton(R.string.dialogo_aceptar, null)
                .create();

        dialogo.setOnShowListener(v -> {

            dialogo.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(boton -> {

                        String nombre = campoNombre.getText()
                                .toString().trim();

                        if (nombre.isEmpty()) {
                            campoNombre.setError(
                                    getString(R.string.error_nombre_vacio)
                            );
                        } else {

                            nombreUsuario = nombre;

                            preferencias.edit()
                                    .putString("nombre_usuario", nombreUsuario)
                                    .apply();

                            actualizarTitulo();

                            dialogo.dismiss();
                        }
                    });
        });

        dialogo.show();
    }

    private void actualizarTitulo() {

        binding.toolbar.setSubtitle(
                getString(R.string.tareas_de, nombreUsuario)
        );
    }
}