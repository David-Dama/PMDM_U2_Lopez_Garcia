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
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.appcompat.app.AlertDialog;
import com.example.mistareas2.databinding.ActivityMainBinding;
import java.util.ArrayList;
import java.util.List;
import android.app.DatePickerDialog;
import android.widget.Button;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import com.google.android.material.snackbar.Snackbar;

// Practica 3: Mis tareas 2 - Completada
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

        // Crear los campos de texto
        EditText campoTitulo = new EditText(this);
        EditText campoDescripcion = new EditText(this);
        Button botonFecha = new Button(this);

        // Guardará la fecha seleccionada
        final String[] fechaLimite = {""};

        botonFecha.setText(R.string.seleccionar_fecha);

        campoTitulo.setHint(R.string.titulo_tarea_hint);
        campoDescripcion.setHint(R.string.descripcion_tarea_hint);

        campoTitulo.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        );

        campoDescripcion.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_CAP_SENTENCES |
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE
        );

        // Contenedor vertical para los dos campos
        LinearLayout contenedor = new LinearLayout(this);
        contenedor.setOrientation(LinearLayout.VERTICAL);

        int margen = (int) (24 * getResources()
                .getDisplayMetrics().density);

        contenedor.setPadding(margen, 0, margen, 0);

        contenedor.addView(campoTitulo);
        contenedor.addView(campoDescripcion);
        contenedor.addView(botonFecha);

        botonFecha.setOnClickListener(v -> {

            Calendar calendario = Calendar.getInstance();

            int anio = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog selectorFecha = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {

                        Calendar fechaSeleccionada = Calendar.getInstance();

                        fechaSeleccionada.set(
                                year,
                                month,
                                dayOfMonth
                        );

                        SimpleDateFormat formato =
                                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                        fechaLimite[0] = formato.format(
                                fechaSeleccionada.getTime()
                        );

                        botonFecha.setText(
                                getString(
                                        R.string.fecha_limite,
                                        fechaLimite[0]
                                )
                        );
                    },
                    anio,
                    mes,
                    dia
            );

            selectorFecha.show();
        });

        // Construir el diálogo
        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle(R.string.dialogo_nueva_tarea)
                .setView(contenedor)
                .setPositiveButton(R.string.boton_anadir, null)
                .setNegativeButton(R.string.boton_cancelar, null)
                .create();

        // Controlar manualmente el botón Añadir
        dialogo.setOnShowListener(v -> {

            dialogo.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(boton -> {

                        String titulo = campoTitulo.getText()
                                .toString().trim();

                        String descripcion = campoDescripcion.getText()
                                .toString().trim();

                        // Validar que el título no esté vacío
                        if (titulo.isEmpty()) {

                            campoTitulo.setError(
                                    getString(R.string.error_titulo_vacio)
                            );

                        } else {

                            // Crear la nueva tarea
                            Tarea nueva = new Tarea(
                                    titulo,
                                    descripcion,
                                    Tarea.PRIORIDAD_MEDIA
                            );

                            nueva.setFechaLimite(fechaLimite[0]);

                            // Añadirla a la lista
                            tareas.add(nueva);

                            // Notificar al adaptador
                            adapter.notifyItemInserted(tareas.size() - 1);

                            // Mostrar la última tarea
                            binding.rvTareas.scrollToPosition(
                                    tareas.size() - 1
                            );

                            dialogo.dismiss();
                        }
                    });
        });

        dialogo.show();
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
        confirmarEliminacion(pos);
        return true;
    }

        return super.onContextItemSelected(item);
    }

    private void confirmarEliminacion(int posicion) {

        // Obtener la tarea seleccionada
        Tarea tarea = tareas.get(posicion);

        // Crear diálogo de confirmación
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialogo_eliminar_titulo)
                .setMessage(R.string.dialogo_eliminar_mensaje)

                // Si confirma, eliminamos la tarea
                .setPositiveButton(R.string.boton_eliminar, (dialog, which) -> {

                    // Eliminar de la lista
                    tareas.remove(posicion);

                    // Actualizar RecyclerView
                    adapter.notifyItemRemoved(posicion);

                    // Mostrar Snackbar con opción Deshacer
                    Snackbar.make(
                            binding.main,
                            R.string.tarea_eliminada,
                            Snackbar.LENGTH_LONG
                    ).setAction(R.string.deshacer, v -> {

                        // Recuperar la tarea en su posición original
                        tareas.add(posicion, tarea);

                        // Actualizar RecyclerView
                        adapter.notifyItemInserted(posicion);

                    }).show();

                })

                // Si cancela, no hacemos nada
                .setNegativeButton(R.string.boton_cancelar, null)

                .show();
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