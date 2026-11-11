package com.example.noticiasfp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import com.example.noticiasfp.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.app.ActionBarDrawerToggle;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private List<Noticia> noticias;
    private ActionBarDrawerToggle drawerToggle;
    private NoticiaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (view, insets) -> {
            Insets systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            view.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );

            return insets;
        });

        setSupportActionBar(binding.toolbar);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        binding.navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_portada) {

                binding.toolbar.setTitle("Portada");
                adapter.actualizarNoticias(noticias);

            } else if (id == R.id.nav_tecnologia) {

                binding.toolbar.setTitle("Tecnología");
                filtrarNoticias("Tecnología");

            } else if (id == R.id.nav_economia) {

                binding.toolbar.setTitle("Economía");
                filtrarNoticias("Economía");

            } else if (id == R.id.nav_educacion) {

                binding.toolbar.setTitle("Educación");
                filtrarNoticias("Educación");

            } else if (id == R.id.nav_favoritas) {

                binding.toolbar.setTitle("Mis favoritas");

            }

            binding.drawerLayout.closeDrawers();

            return true;
        });

        noticias = new ArrayList<>();

        noticias.add(new Noticia(
                "La inteligencia artificial transforma el desarrollo de software",
                "Las nuevas herramientas de IA están cambiando la forma de programar y desarrollar aplicaciones.",
                "Tecnología",
                "11/11/2026",
                R.drawable.noticia_ia
        ));

        noticias.add(new Noticia(
                "España impulsa nuevos proyectos de ciberseguridad",
                "Las empresas tecnológicas refuerzan sus sistemas para proteger sus infraestructuras digitales.",
                "Tecnología",
                "10/11/2026",
                R.drawable.noticia_ciberseguridad
        ));

        noticias.add(new Noticia(
                "Nuevas tendencias en el mercado laboral",
                "El sector tecnológico continúa demandando profesionales especializados en desarrollo y seguridad.",
                "Economía",
                "09/11/2026",
                R.drawable.noticia_economia
        ));

        noticias.add(new Noticia(
                "La educación digital gana protagonismo",
                "Los centros educativos incorporan nuevas herramientas para mejorar el aprendizaje.",
                "Educación",
                "08/11/2026",
                R.drawable.noticia_educacion
        ));

        binding.recyclerNoticias.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new NoticiaAdapter(new ArrayList<>(noticias));
        binding.recyclerNoticias.setAdapter(adapter);

        binding.fab.setOnClickListener(view ->
                Snackbar.make(
                        binding.main,
                        "Función de añadir noticia pendiente",
                        Snackbar.LENGTH_SHORT
                ).show()
        );
    }
    private void filtrarNoticias(String categoria) {

        List<Noticia> noticiasFiltradas = new ArrayList<>();

        for (Noticia noticia : noticias) {

            if (noticia.getCategoria().equals(categoria)) {
                noticiasFiltradas.add(noticia);
            }
        }

        adapter.actualizarNoticias(noticiasFiltradas);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();

        if (id == R.id.action_favorites) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}