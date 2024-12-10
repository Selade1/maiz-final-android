package com.example.maiz_final;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class orden extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerViewCatalogo;
    private CatalogoAdapter catalogoAdapter;
    private ArrayList<Producto> productosList;
    private ArrayList<String> clientesList;
    private String tipoEntregaSeleccionado;
    private String clienteSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orden);

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Habilitar la flecha de regreso
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Configurar Spinners
        configurarSpinnerTipoEntrega();
        configurarSpinnerClientes();

        // Inicializar RecyclerView
        recyclerViewCatalogo = findViewById(R.id.recyclerViewCatalogo);
        productosList = new ArrayList<>();
        catalogoAdapter = new CatalogoAdapter(productosList, "Flete"); // Tipo de entrega por defecto
        recyclerViewCatalogo.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCatalogo.setAdapter(catalogoAdapter);

        // Cargar datos de Firestore
        cargarCatalogo();

        // Configurar el botón "Realizar Pedido"
        Button btnRealizarPedido = findViewById(R.id.btnRealizarPedido);
        btnRealizarPedido.setOnClickListener(v -> realizarPedido());
    }

    private void configurarSpinnerTipoEntrega() {
        Spinner spinnerTipoEntrega = findViewById(R.id.spinnerTipoEntrega);
        String[] opciones = {"Selecciona tipo de pedido", "Flete", "En sitio", "Minorista"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoEntrega.setAdapter(adapter);

        spinnerTipoEntrega.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String nuevoTipoEntrega = opciones[position];
                if (!nuevoTipoEntrega.equals(tipoEntregaSeleccionado)) {
                    tipoEntregaSeleccionado = nuevoTipoEntrega;

                    if (tipoEntregaSeleccionado.equals("Selecciona tipo de pedido")) {
                        return;
                    }

                    // Reiniciar el carrito
                    catalogoAdapter.reiniciarCarrito();

                    // Actualizar el adaptador con el nuevo tipo de entrega
                    catalogoAdapter.setTipoEntrega(tipoEntregaSeleccionado);
                    catalogoAdapter.notifyDataSetChanged();

                    Toast.makeText(orden.this, "Tipo de pedido cambiado a " + tipoEntregaSeleccionado + ". Carrito reiniciado.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tipoEntregaSeleccionado = null;
            }
        });

        // Seleccionar el valor por defecto
        spinnerTipoEntrega.setSelection(0);
    }


    private void configurarSpinnerClientes() {
        Spinner spinnerClientes = findViewById(R.id.spinnerClientes);
        clientesList = new ArrayList<>();
        clientesList.add("Selecciona un cliente");

        db.collection("clientes").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    clientesList.add("Mostrador"); // Opción adicional
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String cliente = document.getString("nombre");
                        if (cliente != null && !cliente.isEmpty()) {
                            clientesList.add(cliente);
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(orden.this, android.R.layout.simple_spinner_item, clientesList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerClientes.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(orden.this, "Error al cargar clientes.", Toast.LENGTH_SHORT).show();
                });

        spinnerClientes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clienteSeleccionado = clientesList.get(position);

                if (tipoEntregaSeleccionado != null && tipoEntregaSeleccionado.equals("Minorista") && !clienteSeleccionado.equals("Mostrador")) {
                    // Si el tipo de pedido es "Minorista", forzar cliente a "Mostrador"
                    spinnerClientes.setSelection(clientesList.indexOf("Mostrador"));
                    clienteSeleccionado = "Mostrador";
                    Toast.makeText(orden.this, "Cliente debe ser 'Mostrador' para pedidos 'Minoristas'", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                clienteSeleccionado = null;
            }
        });

        // Seleccionar el valor por defecto
        spinnerClientes.setSelection(0);
    }

    private void cargarCatalogo() {
        db.collection("catalogo").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    productosList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String nombre = document.getString("Nombre_producto");
                        Double precio = document.getDouble("Precio");
                        Long stock = document.getLong("Stock");
                        String imageUrl = document.getString("imageUrl");

                        if (nombre != null && precio != null && stock != null && imageUrl != null) {
                            productosList.add(new Producto(nombre, precio.intValue(), stock.intValue(), imageUrl));
                        }
                    }
                    catalogoAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(orden.this, "Error al cargar el catálogo.", Toast.LENGTH_SHORT).show();
                });
    }

    private void realizarPedido() {
        if (clienteSeleccionado == null || clienteSeleccionado.equals("Selecciona un cliente")) {
            Toast.makeText(this, "Selecciona un cliente.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tipoEntregaSeleccionado == null || tipoEntregaSeleccionado.equals("Selecciona tipo de pedido")) {
            Toast.makeText(this, "Selecciona un tipo de pedido.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tipoEntregaSeleccionado.equals("Minorista") && !clienteSeleccionado.equals("Mostrador")) {
            Toast.makeText(this, "El cliente debe ser 'Mostrador' para pedidos 'Minoristas'.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si el tipo de entrega es "Flete"
        if (tipoEntregaSeleccionado.equals("Flete")) {
            // Redirigir a SeleccionarVehiculoActivity
            Intent intent = new Intent(orden.this, SeleccionarVehiculoActivity.class);
            intent.putExtra("productos", new ArrayList<>(catalogoAdapter.getCarrito().keySet())); // Productos seleccionados
            intent.putExtra("cantidades", new ArrayList<>(catalogoAdapter.getCarrito().values())); // Cantidades seleccionadas
            intent.putExtra("cliente", clienteSeleccionado); // Cliente seleccionado
            intent.putExtra("tipoEntrega", tipoEntregaSeleccionado); // Tipo de entrega
            startActivity(intent);
            return;
        }

        // Si no es "Flete", redirigir a SeleccionarFechaActivity
        Intent intent = new Intent(orden.this, SeleccionarFechaActivity.class);
        intent.putExtra("productos", new ArrayList<>(catalogoAdapter.getCarrito().keySet())); // Productos seleccionados
        intent.putExtra("cantidades", new ArrayList<>(catalogoAdapter.getCarrito().values())); // Cantidades seleccionadas
        intent.putExtra("cliente", clienteSeleccionado); // Cliente seleccionado
        intent.putExtra("tipoEntrega", tipoEntregaSeleccionado); // Tipo de entrega
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
