package com.example.audiolibros.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiolibros.AdaptadorLibros;
import com.example.audiolibros.AdaptadorLibrosFiltro;
import com.example.audiolibros.Libro;
import com.example.audiolibros.AdaptadorLibros;
import com.example.audiolibros.Aplicacion;
import com.example.audiolibros.Libro;
import com.example.audiolibros.MainActivity;
import com.example.audiolibros.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Vector;

public class SelectorFragment extends Fragment {

    private Activity actividad;
    private RecyclerView recyclerView;
    private AdaptadorLibrosFiltro adaptador; //modifique aqui
    private Vector<Libro> vectorLibros;

    @Override
    public void onAttach(Context contexto) {
        super.onAttach(contexto);
        if (contexto instanceof Activity) {
            this.actividad = (Activity) contexto;
            Aplicacion app = (Aplicacion) actividad.getApplication();
            adaptador = app.getAdaptador();
            vectorLibros = app.getVectorLibros();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflador, ViewGroup contenedor, Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.fragment_selector, contenedor, false);
        recyclerView = vista.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(actividad, 2));
        recyclerView.setAdapter(adaptador);
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(actividad, "Seleccionado el elemento: " + recyclerView.getChildAdapterPosition(v), Toast.LENGTH_SHORT).show();
                ((MainActivity) actividad).mostrarDetalle(
                        //recyclerView.getChildAdapterPosition(v));
                        (int) adaptador.getItemId(
                                recyclerView.getChildAdapterPosition(v)));
            }
        });

        adaptador.setOnItemLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(final View v) {
                final int id = recyclerView.getChildAdapterPosition(v);
                AlertDialog.Builder menu = new AlertDialog.Builder(actividad);
                CharSequence[] opciones = {"Compartir", "Borrar ", "Insertar"};
                menu.setItems(opciones, new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialog, int opcion) {
                        switch (opcion) {
                            case 0: //Compartir
                                Libro libro = vectorLibros.elementAt(id);
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("text/plain");
                                i.putExtra(Intent.EXTRA_SUBJECT, libro.titulo);
                                i.putExtra(Intent.EXTRA_TEXT, libro.urlAudio);
                                startActivity(Intent.createChooser(i, "Compartir"));
                                break;
                            case 1: //Borrar

                                Snackbar.make(v,"¿Estás seguro?", Snackbar.LENGTH_LONG).setAction("SI", new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {

                                        //vectorLibros.remove(id);
                                        adaptador.borrar(id);
                                        adaptador.notifyDataSetChanged();
                                    }
                                }).show();


                                break;
                            case 2: //Insertar
                                //vectorLibros.add(vectorLibros.elementAt(id));

                                int posicion = recyclerView.getChildLayoutPosition(v);
                                adaptador.insertar((Libro) adaptador.getItem(posicion));

                                adaptador.notifyDataSetChanged();

                                Snackbar.make(v,"Libro insertado", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("OK", new View.OnClickListener() {
                                            @Override public void onClick(View view) { }
                                        }).show(); //agregué esto (desde donde dice snackbar)
                                break;
                        }
                    }
                });
                menu.create().show();
                return true;
            }
        });

        //para que se puedan añadir nuevos items
        setHasOptionsMenu(true);
        //

        return vista;
    }

    ///////////////////////////////////////
    @Override public void onResume(){
        ((MainActivity) getActivity()).mostrarElementos(true);
        super.onResume();
    }
    ///////////////////////////////////////

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_selector, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.menu_buscar);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextChange(String query) {
                        adaptador.setBusqueda(query);
                        adaptador.notifyDataSetChanged();
                        return false;
                    }
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }
                });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_ultimo) {
            ((MainActivity) actividad).irUltimoVisitado();
            return true;
        } else if (id == R.id.menu_buscar) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
