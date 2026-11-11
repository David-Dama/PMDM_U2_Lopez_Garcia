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

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private List<Noticia> noticias;

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

        NoticiaAdapter adapter = new NoticiaAdapter(noticias);
        binding.recyclerNoticias.setAdapter(adapter);

        binding.fab.setOnClickListener(view ->
                Snackbar.make(
                        binding.main,
                        "Función de añadir noticia pendiente",
                        Snackbar.LENGTH_SHORT
                ).show()
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_favorites) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}