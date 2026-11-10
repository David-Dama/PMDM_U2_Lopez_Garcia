package com.example.enrutamientoavanzado;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.enrutamientoavanzado.databinding.FragmentSecondBinding;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Comprobamos que nos hayan llegado argumentos de forma segura
        if (getArguments() != null) {
            // Usamos la clase generada automáticamente por Safe Args para extraer los datos
            SecondFragmentArgs args = SecondFragmentArgs.fromBundle(getArguments());
            String mensajeRecibido = args.getMensaje(); // Coincide con el nombre 'mensaje' que pusiste en el nav_graph.xml

            // 2. Mostramos el mensaje recibido en el TextView de esta pantalla
            binding.textviewSecond.setText(mensajeRecibido);
        }

        // 3. El botón que ya traía la plantilla para volver al primer fragmento
        binding.buttonSecond.setOnClickListener(v ->
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}