package com.android.ermo.rakutentvapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ermo.rakutentvapp.adaptadores.RecyclerAdaptadorPeliculas;
import com.android.ermo.rakutentvapp.beans.Pelicula;
import com.android.ermo.rakutentvapp.beans.Usuario;
import com.android.ermo.rakutentvapp.datos.RakutenData;
import com.android.ermo.rakutentvapp.tools.IPGetter;
import com.android.ermo.rakutentvapp.tools.Post;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FiltroActivity extends AppCompatActivity {

    private RecyclerAdaptadorPeliculas adaptadorPeliculas;
    private RecyclerView recyclerView;

    private Button btnPeliculas;
    private Button btnBuscar;
    private Button btnPerfil;
    private Spinner spinnerBusquedaPri;
    private Spinner spinnerBusquedaSec;
    private TextView searchBar;
    private TextView tx;
    private TextView textSaludo;
    private LinearLayout layoutSearch;
    private Button hacerBusqueda;

    private final String IP_LOCAL_SERVIDOR = IPGetter.getInstance().getIP();
    private final String PATH_FOTO = "http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/images/peliculas/movieFotos/";
    private final String PATH_CARATULA = "http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/images/peliculas/movieCaratula/";

    private int buscandoPor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        tx = (TextView) findViewById(R.id.textLoggedUser);
        textSaludo = (TextView) findViewById(R.id.textSaludo);
        btnPeliculas = (Button) findViewById(R.id.btnPeliculas);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        btnPerfil = (Button) findViewById(R.id.btnPerfil);
        layoutSearch = (LinearLayout) findViewById(R.id.layoutSearch);
        hacerBusqueda = (Button) findViewById(R.id.hacerBusqueda);

        spinnerBusquedaPri = (Spinner) findViewById(R.id.spinnerBusquedaPri);
        spinnerBusquedaSec = (Spinner) findViewById(R.id.spinnerBusquedaSec);

        searchBar = (TextView) findViewById(R.id.searchBar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewPeliculas);

        getSupportActionBar().setTitle("Busca tu pelicula");

        cargarGlobalSpinner();


        if (getIntent().hasExtra("usuario") || RakutenData.getUsuario() != null) {
            Usuario usuario = null;
            if (getIntent().hasExtra("usuario")) {
                usuario = (Usuario) getIntent().getExtras().getSerializable("usuario");
            } else if (RakutenData.getUsuario() != null) {
                usuario = RakutenData.getUsuario();
            }

            textSaludo.setText("Bienvenido de nuevo ");
            tx.setText(" " + usuario.getUsername());

        } else {
            textSaludo.setText("Modo invitado");
        }

        spinnerBusquedaPri.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    //Default
                    spinnerBusquedaSec.setVisibility(View.GONE);
                } else if (position == 1) {
                    //Titulo
                    layoutSearch.setVisibility(View.VISIBLE);
                    spinnerBusquedaSec.setVisibility(View.GONE);
                } else {
                    //Resto (Spinners)
                    layoutSearch.setVisibility(View.GONE);
                    inflateSpinner(position);
                    spinnerBusquedaSec.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        hacerBusqueda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchBar.getText() != null || !searchBar.getText().equals("")) {
                    buscarTitulo(searchBar.getText().toString());
                }
            }
        });

        spinnerBusquedaSec.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    switch (buscandoPor) {
                        case 1:
                            buscarGenero(position);
                            break;

                        case 2:
                            buscarVotos(position);
                            break;
                        case 3:
                            buscarFecha(position);
                            break;
                        case 4:
                            buscarAlfTitulo(position);
                            break;
                        case 5:
                            buscarPrecio(position);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnPeliculas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ListaPeliculasActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PerfilActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);

        if (getIntent().hasExtra("usuario") || RakutenData.getUsuario() != null) {
            menu.findItem(R.id.entrar).setVisible(false);
            menu.findItem(R.id.salir).setVisible(true);
        } else {
            menu.findItem(R.id.entrar).setVisible(true);
            menu.findItem(R.id.salir).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.entrar:
            case R.id.salir:

                RakutenData.setUsuario(null);

                SharedPreferences userPreferences = getSharedPreferences("informacion", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = userPreferences.edit();

                editor.remove("userName");
                editor.remove("email");
                editor.remove("contreasena");

                editor.apply();
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }

    private void cargarGlobalSpinner() {
        final List<String> valoresSpinnerBuscar = new ArrayList<String>();
        valoresSpinnerBuscar.add("Selecciona el tipo de busqueda");
        valoresSpinnerBuscar.add("Buscar por titulo");
        valoresSpinnerBuscar.add("Buscar por géneros");
        valoresSpinnerBuscar.add("Buscar por votos");
        valoresSpinnerBuscar.add("Ordenar por estreno");
        valoresSpinnerBuscar.add("Ordenar por titulo");
        valoresSpinnerBuscar.add("Ordenar por precio");

        ArrayAdapter<String> spAdapterB = new ArrayAdapter<String>(this, R.layout.my_spinner, valoresSpinnerBuscar) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };
        spinnerBusquedaPri.setAdapter(spAdapterB);
    }

    private void inflateSpinner(int position) {

        switch (position) {
            case 2:
                buscandoPor = 1;
                final List<String> valoresSpinnerGenero = new ArrayList<String>();
                valoresSpinnerGenero.add("Selecciona un genero");
                valoresSpinnerGenero.add("Acción");
                valoresSpinnerGenero.add("Animación");
                valoresSpinnerGenero.add("Ciencia Ficción");
                valoresSpinnerGenero.add("Cine Español");
                valoresSpinnerGenero.add("Comedia");
                valoresSpinnerGenero.add("Thriller");
                valoresSpinnerGenero.add("Romántico");

                ArrayAdapter<String> spAdapterG = new ArrayAdapter<String>(this, R.layout.my_spinner, valoresSpinnerGenero) {
                    @Override
                    public boolean isEnabled(int position) {
                        return position != 0;
                    }
                };
                spinnerBusquedaSec.setAdapter(spAdapterG);
                break;


            case 3:
                buscandoPor = 2;
                final List<String> valoresSpinnerVotos = new ArrayList<String>();
                valoresSpinnerVotos.add("Selecciona el tipo de búsqueda");
                valoresSpinnerVotos.add("Más cantidad de votos");
                valoresSpinnerVotos.add("Más votadas");

                ArrayAdapter<String> spAdapterV = new ArrayAdapter<String>(this, R.layout.my_spinner, valoresSpinnerVotos) {
                    @Override
                    public boolean isEnabled(int position) {
                        return position != 0;
                    }
                };
                spinnerBusquedaSec.setAdapter(spAdapterV);
                break;
            case 4:
                buscandoPor = 3;
                final List<String> valoresSpinnerFecha = new ArrayList<String>();
                valoresSpinnerFecha.add("Selecciona el tipo de búsqueda");
                valoresSpinnerFecha.add("Más recientes");
                valoresSpinnerFecha.add("Más antiguas");

                ArrayAdapter<String> spAdapterF = new ArrayAdapter<String>(this, R.layout.my_spinner, valoresSpinnerFecha) {
                    @Override
                    public boolean isEnabled(int position) {
                        return position != 0;
                    }
                };
                spinnerBusquedaSec.setAdapter(spAdapterF);
                break;

            case 5:
                buscandoPor = 4;
                final List<String> valoresSpinnerTitulo = new ArrayList<String>();
                valoresSpinnerTitulo.add("Selecciona el tipo de búsqueda");
                valoresSpinnerTitulo.add("Ascendente A-Z");
                valoresSpinnerTitulo.add("Descendente Z-A");

                ArrayAdapter<String> spAdapterT = new ArrayAdapter<String>(this, R.layout.my_spinner, valoresSpinnerTitulo) {
                    @Override
                    public boolean isEnabled(int position) {
                        return position != 0;
                    }
                };
                spinnerBusquedaSec.setAdapter(spAdapterT);
                break;

            case 6:
                buscandoPor = 5;
                final List<String> valoresSpinnerPrecio = new ArrayList<String>();
                valoresSpinnerPrecio.add("Selecciona el tipo de búsqueda");
                valoresSpinnerPrecio.add("Más caras");
                valoresSpinnerPrecio.add("Más baratas");

                ArrayAdapter<String> spAdapterP = new ArrayAdapter<String>(this, R.layout.my_spinner, valoresSpinnerPrecio) {
                    @Override
                    public boolean isEnabled(int position) {
                        return position != 0;
                    }
                };
                spinnerBusquedaSec.setAdapter(spAdapterP);
                break;
        }

    }

    private void buscarGenero(int position) {

        HashMap<String, String> parametros = new HashMap<String, String>();

        parametros.put("ACTION", "Pelicula.listGenero");
        parametros.put("GENERO", String.valueOf(position));

        TareaSegundoPlano tarea = new TareaSegundoPlano(parametros);
        tarea.execute("http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/Controller");

    }

    private void buscarVotos(int position) {

        HashMap<String, String> parametros = new HashMap<String, String>();

        if (position == 1) {
            parametros.put("ACTION", "Pelicula.listAllMejorVotadas");
        } else if (position == 2) {
            parametros.put("ACTION", "Pelicula.listAllMasVotadas");
        }

        TareaSegundoPlano tarea = new TareaSegundoPlano(parametros);
        tarea.execute("http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/Controller");

    }

    private void buscarFecha(int position) {

        HashMap<String, String> parametros = new HashMap<String, String>();

        if (position == 1) {
            parametros.put("ACTION", "Pelicula.listAllEstrenosNuevas");
        } else if (position == 2) {
            parametros.put("ACTION", "Pelicula.listAllEstrenosViejas");
        }

        TareaSegundoPlano tarea = new TareaSegundoPlano(parametros);
        tarea.execute("http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/Controller");

    }

    private void buscarAlfTitulo(int position) {

        HashMap<String, String> parametros = new HashMap<String, String>();

        if (position == 1) {
            parametros.put("ACTION", "Pelicula.listTitulosAZ");
        } else if (position == 2) {
            parametros.put("ACTION", "Pelicula.listTitulosZA");
        }

        TareaSegundoPlano tarea = new TareaSegundoPlano(parametros);
        tarea.execute("http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/Controller");

    }

    private void buscarPrecio(int position) {

        HashMap<String, String> parametros = new HashMap<String, String>();

        if (position == 1) {
            parametros.put("ACTION", "Pelicula.listAllCaras");
        } else if (position == 2) {
            parametros.put("ACTION", "Pelicula.listAllBaratas");
        }

        TareaSegundoPlano tarea = new TareaSegundoPlano(parametros);
        tarea.execute("http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/Controller");

    }

    private void buscarTitulo(String texto) {

        HashMap<String, String> parametros = new HashMap<String, String>();

        parametros.put("ACTION", "Pelicula.filtrarNombre");
        parametros.put("NOMBRE", texto);

        TareaSegundoPlano tarea = new TareaSegundoPlano(parametros);
        tarea.execute("http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/Controller");

    }


    class TareaSegundoPlano extends AsyncTask<String, Integer, Boolean> {
        private ArrayList<Pelicula> listaPeliculas = null;
        private HashMap<String, String> parametros = null;


        public TareaSegundoPlano(HashMap<String, String> parametros) {
            this.parametros = parametros;
        }

        /*
         * doInBackground().
         * Contendrá el código principal de nuestra tarea.
         * */
        @Override
        protected Boolean doInBackground(String... params) {
            // URL
            String url_select = params[0];
            try {
                Post post = new Post();

                JSONArray result = post.getServerDataPost(parametros, url_select);
                listaPeliculas = Pelicula.getArrayListFromJSon(result);
            } catch (Exception e) {
                Log.e("log_tag", "Error in http connection " + e.toString());
                //messageUser = "Error al conectar con el servidor. ";
            }

            return true;
        }

        /*
         * onPostExecute().
         * Se ejecutará cuando finalice nuestra tarea, o dicho de otra forma,
         * tras la finalización del método doInBackground().
         * */
        @Override
        protected void onPostExecute(Boolean resp) {
            try {
                if (resp && listaPeliculas != null && listaPeliculas.size() > 0) {
                    for (Pelicula pelicula : listaPeliculas) {
                        pelicula.setCaratulaPeli(PATH_CARATULA + pelicula.getCaratulaPeli());
                        pelicula.setFotoPeli(PATH_FOTO + pelicula.getFotoPeli());
                    }
                    adaptadorPeliculas = new RecyclerAdaptadorPeliculas(getBaseContext(), listaPeliculas);
                    recyclerView.setAdapter(adaptadorPeliculas);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                } else {
                    Toast.makeText(ListaPeliculasActivity.getInstance().getBaseContext(), "No se han encontrado peliculas. ", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO: handle exception
                Log.e("log_tag", "Error parsing data " + e.toString());
            }
        }
    }
}
