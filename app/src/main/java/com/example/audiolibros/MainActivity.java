package com.example.audiolibros;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.audiolibros.fragments.DetalleFragment;
import com.example.audiolibros.fragments.SelectorFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener { //agregue ese implements
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AdaptadorLibrosFiltro adaptador;
    //DrawerLayout drawer;
    //ActionBarDrawerToggle toggle;

    private AppBarLayout appBarLayout;
    private TabLayout tabs;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //-------------------fragment if
        if ((findViewById(R.id.contenedor_pequeno) != null) &&
                (getSupportFragmentManager().findFragmentById(R.id.contenedor_pequeno) == null)){
            SelectorFragment primerFragment = new SelectorFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.contenedor_pequeno, primerFragment).commit();
        }

        //-----------------
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ////////////////////////////////////////////////////////
        // Navigation Drawer
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string. drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        /*@SuppressWarnings("StatementWithEmptyBody")
        @Override



        @Override
        public void onBackPressed() {
            DrawerLayout drawer = (DrawerLayout) findViewById(
                    R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }*/

        ////////////////////////////////////////////////////////

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irUltimoVisitado(); //PARA QUE AHORA EL BOTÓN FLOTANTE ME MANDE AL ULTIMO LIBRO LEIDO

                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        //------------------------
        Aplicacion app = (Aplicacion) getApplication();
        /*recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(app.getAdaptador());
        //layoutManager = new LinearLayoutManager(this);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        */


        app.getAdaptador().setOnItemClickListener(new View.OnClickListener(){
            @Override public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Seleccionado el elemento: " + recyclerView.getChildAdapterPosition(v),Toast.LENGTH_SHORT).show();

            }
        });

        //ESTO PARA LAS PESTAÑAS
        //Pestañas
        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("Todos"));
        tabs.addTab(tabs.newTab().setText("Nuevos"));
        tabs.addTab(tabs.newTab().setText("Leidos"));
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: //Todos
                        adaptador.setNovedad(false);
                        adaptador.setLeido(false);
                        break;
                    case 1: //Nuevos
                        adaptador.setNovedad(true);
                        adaptador.setLeido(false);
                        break;
                    case 2: //Leidos
                        adaptador.setNovedad(false);
                        adaptador.setLeido(true);
                        break;
                }
                adaptador.notifyDataSetChanged();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        adaptador = ((Aplicacion) getApplicationContext()).getAdaptador();

        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);

        ///agregue metodo para que salga flechita
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

    } //aqui se cierra el metodo OnCreate()



    public void mostrarDetalle(int id) {
        DetalleFragment detalleFragment = (DetalleFragment)
                getSupportFragmentManager().findFragmentById(R.id.detalle_fragment);
        if (detalleFragment != null) {
            detalleFragment.ponInfoLibro(id);
        } else {
            DetalleFragment nuevoFragment = new DetalleFragment();
            Bundle args = new Bundle();
            args.putInt(DetalleFragment.ARG_ID_LIBRO, id);
            nuevoFragment.setArguments(args);
            FragmentTransaction transaccion = getSupportFragmentManager()
                    .beginTransaction();
            transaccion.replace(R.id.contenedor_pequeno, nuevoFragment);
            transaccion.addToBackStack(null);
            transaccion.commit();
        }

        //para almacenar el ultimo libro tomado para la toolbar
        SharedPreferences pref = getSharedPreferences("com.example.audiolibros_internal", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("ultimo", id);
        editor.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_preferencias) {
            //Toast.makeText(this, "Preferencias", Toast.LENGTH_LONG).show();

            Intent i = new Intent(this, PreferenciasActivity.class);
            startActivity(i);
            return true;
        } else if (id == R.id.menu_ultimo) {
            irUltimoVisitado();
            return true;
        /*} else if (id == R.id.menu_buscar) {
            return true;
        } else if (id == R.id.menu_acerca) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Mensaje de Acerca De");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.create().show();
            return true;*/
        }
        return super.onOptionsItemSelected(item);
    }


    public void irUltimoVisitado() {
        SharedPreferences pref = getSharedPreferences("com.example.audiolibros_internal", MODE_PRIVATE);
        int id = pref.getInt("ultimo", -1);
        if (id >= 0) {
            mostrarDetalle(id);
        } else {
            Toast.makeText(this,"Sin última vista",Toast.LENGTH_LONG).show();
        }
    }


    /////////////////////////////////////////////////DRAWER
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.nav_todos) {
            adaptador.setGenero("");
            adaptador.notifyDataSetChanged();
        } else if (id == R.id.nav_epico) {
            adaptador.setGenero(Libro.G_EPICO);
            adaptador.notifyDataSetChanged();
        } else if (id == R.id.nav_XIX) {
            adaptador.setGenero(Libro.G_S_XIX);
            adaptador.notifyDataSetChanged();
        } else if (id == R.id.nav_suspense) {
            adaptador.setGenero(Libro.G_SUSPENSE);
            adaptador.notifyDataSetChanged();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
        //////////////////////////////////////////////////////////

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(
                R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void mostrarElementos(boolean mostrar) {
        appBarLayout.setExpanded(mostrar);
        toggle.setDrawerIndicatorEnabled(mostrar);
        if (mostrar) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            tabs.setVisibility(View.VISIBLE);
        } else {
            tabs.setVisibility(View.GONE);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

}
