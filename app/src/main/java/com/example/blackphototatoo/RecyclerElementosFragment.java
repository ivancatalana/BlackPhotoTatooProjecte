package com.example.blackphototatoo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blackphototatoo.databinding.FragmentRecyclerElementosBinding;
import com.example.blackphototatoo.databinding.ViewholderElementoBinding;

import java.util.List;


public class RecyclerElementosFragment extends Fragment {

    private FragmentRecyclerElementosBinding binding;
    protected ElementosViewModel elementosViewModel;
    private NavController navController;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentRecyclerElementosBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        elementosViewModel = new ViewModelProvider(requireActivity()).get(ElementosViewModel.class);
        navController = Navigation.findNavController(view);

        binding.irANuevoElemento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_nuevoElementoFragment);
            }
        });

        ElementosAdapter elementosAdapter = new ElementosAdapter();

        binding.recyclerView.setAdapter(elementosAdapter);

        binding.recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        // obtener el array de Elementos, y pasarselo al Adaptador
  //      elementosViewModel.obtener().observe(getViewLifecycleOwner(), new Observer<List<Elemento>>() {

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.RIGHT  | ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int posicion = viewHolder.getAdapterPosition();
                Elemento elemento = elementosAdapter.obtenerElemento(posicion);
                elementosViewModel.eliminar(elemento);

            }
        }).attachToRecyclerView(binding.recyclerView);
        obtenerElementos().observe(getViewLifecycleOwner(), new Observer<List<Elemento>>() {

            @Override
            public void onChanged(List<Elemento> elementos) {
                elementosAdapter.establecerLista(elementos);
            }
        });
    }


    class ElementosAdapter extends RecyclerView.Adapter<ElementoViewHolder> {

        List<Elemento> elementos;

        @NonNull
        @Override
        public ElementoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ElementoViewHolder(ViewholderElementoBinding.inflate(getLayoutInflater(), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ElementoViewHolder holder, int position) {

            Elemento elemento = elementos.get(position);
            holder.binding.imagen.setImageResource(elemento.imagen);

            holder.binding.nombre.setText(elemento.nombre);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    elementosViewModel.seleccionar(elemento);
                    navController.navigate(R.id.action_mostrarElementoFragment);
                }
            });

        }

        @Override
        public int getItemCount() {
            return elementos != null ? elementos.size() : 0;
        }

        public void establecerLista(List<Elemento> elementos) {
            this.elementos = elementos;
            notifyDataSetChanged();
        }
        public Elemento obtenerElemento(int posicion){

            return elementos.get(posicion);
        }
    }
    class ElementoViewHolder extends RecyclerView.ViewHolder {
        private final ViewholderElementoBinding binding;

        public ElementoViewHolder(ViewholderElementoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    LiveData<List<Elemento>> obtenerElementos(){
        return elementosViewModel.obtener();
    }
}