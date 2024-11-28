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
                tipoEntregaSeleccionado = opciones[position];

                if (tipoEntregaSeleccionado.equals("Minorista")) {
                    // Si el tipo de pedido es "Minorista", forzar el cliente a "Mostrador"
                    Spinner spinnerClientes = findViewById(R.id.spinnerClientes);
                    int posicionMostrador = clientesList.indexOf("Mostrador");
                    if (posicionMostrador != -1) {
                        spinnerClientes.setSelection(posicionMostrador);
                        clienteSeleccionado = "Mostrador";
                        Toast.makeText(orden.this, "Cliente configurado automáticamente a 'Mostrador'", Toast.LENGTH_SHORT).show();
                    }
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
                        Long precio = document.getLong("Precio");
                        Long stock = document.getLong("Stock");

                        if (nombre != null && precio != null && stock != null) {
                            productosList.add(new Producto(nombre, precio.intValue(), stock.intValue()));
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
            // Redirigir al flujo de selección de camión y fecha
            Intent intent = new Intent(orden.this, SeleccionarVehiculoActivity.class);
            intent.putExtra("productos", new ArrayList<>(catalogoAdapter.getCarrito().keySet())); // Productos seleccionados
            intent.putExtra("cantidades", new ArrayList<>(catalogoAdapter.getCarrito().values())); // Cantidades seleccionadas
            intent.putExtra("cliente", clienteSeleccionado); // Cliente seleccionado
            startActivity(intent);
            return;
        }

        // Si no es "Flete", continuar con el registro normal del pedido
        db.collection("pedidos")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // Generar ID auto incrementable para el pedido
                    int nextId = querySnapshot.size() + 1;
                    String idPedido = String.valueOf(nextId);

                    // Crear un mapa para los datos del pedido
                    Map<String, Object> pedido = new HashMap<>();
                    pedido.put("idPedido", idPedido);
                    pedido.put("idCliente", clienteSeleccionado);
                    pedido.put("nombreCliente", clienteSeleccionado);
                    pedido.put("tipoEntrega", tipoEntregaSeleccionado);

                    // Agregar productos al pedido
                    List<Map<String, Object>> productosPedido = new ArrayList<>();
                    for (Producto producto : catalogoAdapter.getCarrito().keySet()) {
                        int cantidad = catalogoAdapter.getCarrito().get(producto);

                        // Crear un mapa con los datos del producto
                        Map<String, Object> productoMap = new HashMap<>();
                        productoMap.put("nombre", producto.getNombre());
                        productoMap.put("cantidad", cantidad);
                        productoMap.put("precio", producto.getPrecio());
                        productosPedido.add(productoMap);

                        // Actualizar el stock en Firestore
                        db.collection("catalogo").whereEqualTo("Nombre_producto", producto.getNombre())
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        int nuevoStock = producto.getStock() - cantidad;
                                        db.collection("catalogo").document(document.getId())
                                                .update("Stock", nuevoStock)
                                                .addOnSuccessListener(aVoid -> {
                                                    // Opcional: Puedes mostrar un mensaje de éxito para cada producto
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(this, "Error al actualizar el stock de " + producto.getNombre(), Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error al obtener el producto " + producto.getNombre(), Toast.LENGTH_SHORT).show();
                                });
                    }
                    pedido.put("productos", productosPedido);

                    // Guardar el pedido en Firestore
                    db.collection("pedidos").document(idPedido).set(pedido)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Pedido generado con éxito", Toast.LENGTH_SHORT).show();
                                // Redirigir a activity_recibos
                                Intent intent = new Intent(orden.this, recibos.class);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al generar el pedido", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al acceder a la base de datos", Toast.LENGTH_SHORT).show();
                });
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
