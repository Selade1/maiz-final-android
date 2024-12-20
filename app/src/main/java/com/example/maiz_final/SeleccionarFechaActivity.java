package com.example.maiz_final;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeleccionarFechaActivity extends AppCompatActivity {

    private String fechaSeleccionada;
    private FirebaseFirestore db;

    private String formatFecha(int year, int month, int day) {
        // Formatear el mes y el día con dos dígitos
        String mes = (month < 9 ? "0" : "") + (month + 1); // Los meses empiezan desde 0, por eso sumamos 1
        String dia = (day < 10 ? "0" : "") + day;
        return year + "-" + mes + "-" + dia;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_fecha);

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Obtener datos del Intent
        String cliente = getIntent().getStringExtra("cliente");
        String vehiculo = getIntent().getStringExtra("vehiculo");
        ArrayList<Producto> productos = getIntent().getParcelableArrayListExtra("productos");
        ArrayList<Integer> cantidades = (ArrayList<Integer>) getIntent().getSerializableExtra("cantidades");
        boolean esEnvio = getIntent().getBooleanExtra("esEnvio", false);

        // Validar datos recibidos
        if (productos == null || cantidades == null || cliente == null || (esEnvio && vehiculo == null)) {
            Toast.makeText(this, "Error al cargar los datos del pedido/envío.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar el CalendarView para seleccionar la fecha
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setMinDate(System.currentTimeMillis()); // Bloquear fechas pasadas
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            fechaSeleccionada = formatFecha(year, month, dayOfMonth); // Formatear la fecha seleccionada
        });

        // Configurar el botón "Realizar orden"
        Button btnRealizarPedido = findViewById(R.id.btnRealizarPedido);
        btnRealizarPedido.setOnClickListener(v -> {
            if (fechaSeleccionada == null) {
                Toast.makeText(this, "Selecciona una fecha válida.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (esEnvio) {
                registrarEnvio(cliente, productos, cantidades, vehiculo, fechaSeleccionada);
            } else {
                registrarPedido(cliente, "En sitio", productos, cantidades, fechaSeleccionada);
            }
        });

        Button btnRegresarSeleccionVehiculo = findViewById(R.id.btnRegresarSeleccionVehiculo);
        btnRegresarSeleccionVehiculo.setOnClickListener(v -> {
            Intent intent = new Intent(SeleccionarFechaActivity.this, SeleccionarVehiculoActivity.class);
            startActivity(intent);
            finish(); // Cierra la actividad actual
        });
    }

    private void registrarPedido(String cliente, String tipoEntrega, ArrayList<Producto> productos, ArrayList<Integer> cantidades, String fecha) {
        Log.d("RegistrarPedido", "Inicio del método registrarPedido");
        db.collection("pedidos")
                .get()
                .addOnSuccessListener(snapshot -> {
                    int nextId = snapshot.size() + 1;
                    String formattedId = nextId + "_ped";

                    Map<String, Object> pedido = new HashMap<>();
                    pedido.put("id", formattedId);
                    pedido.put("nombreCliente", cliente);
                    pedido.put("tipoEntrega", tipoEntrega);
                    pedido.put("fecha", fecha);

                    List<Map<String, Object>> productosPedido = new ArrayList<>();
                    for (int i = 0; i < productos.size(); i++) {
                        Map<String, Object> productoMap = new HashMap<>();
                        productoMap.put("nombre", productos.get(i).getNombre());
                        productoMap.put("cantidad", cantidades.get(i));
                        productoMap.put("precio", productos.get(i).getPrecio());
                        productosPedido.add(productoMap);
                    }
                    pedido.put("productos", productosPedido);

                    db.collection("pedidos").document(formattedId).set(pedido)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("RegistrarPedido", "Pedido registrado con ID: " + formattedId);
                                actualizarInventario(productos, cantidades, "Pedido: " + formattedId);
                                Toast.makeText(this, "Pedido registrado con éxito.", Toast.LENGTH_SHORT).show();
                                redirigirConfirmacion(formattedId, false);
                            })
                            .addOnFailureListener(e -> {
                                Log.d("RegistrarPedido", "Error al registrar el pedido: " + e.getMessage());
                                Toast.makeText(this, "Error al registrar el pedido.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.d("RegistrarPedido", "Error al acceder a la base de datos: " + e.getMessage());
                    Toast.makeText(this, "Error al acceder a la base de datos.", Toast.LENGTH_SHORT).show();
                });
    }

    private void registrarEnvio(String cliente, ArrayList<Producto> productos, ArrayList<Integer> cantidades, String vehiculoId, String fecha) {
        Log.d("RegistrarEnvio", "Inicio del método registrarEnvio");
        db.collection("envios")
                .whereEqualTo("idCamion", vehiculoId)
                .whereEqualTo("fecha", fecha)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.size() >= 2) {
                        Log.d("RegistrarEnvio", "Límite de envíos alcanzado para el camión: " + vehiculoId);
                        Toast.makeText(this, "Este camión ya alcanzó el límite de 2 envíos para la fecha seleccionada.", Toast.LENGTH_SHORT).show();
                    } else {
                        db.collection("envios")
                                .get()
                                .addOnSuccessListener(envioSnapshot -> {
                                    int nextId = envioSnapshot.size() + 1;
                                    String formattedId = nextId + "_env";

                                    Map<String, Object> envio = new HashMap<>();
                                    envio.put("id", formattedId);
                                    envio.put("nombreCliente", cliente);
                                    envio.put("fecha", fecha);
                                    envio.put("idCamion", vehiculoId);
                                    envio.put("tipoEntrega","Flete");

                                    List<Map<String, Object>> productosEnvio = new ArrayList<>();
                                    for (int i = 0; i < productos.size(); i++) {
                                        Map<String, Object> productoMap = new HashMap<>();
                                        productoMap.put("nombre", productos.get(i).getNombre());
                                        productoMap.put("cantidad", cantidades.get(i));
                                        productoMap.put("precio", productos.get(i).getPrecio());
                                        productosEnvio.add(productoMap);
                                    }
                                    envio.put("productos", productosEnvio);

                                    db.collection("envios").document(formattedId).set(envio)
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("RegistrarEnvio", "Envío registrado con ID: " + formattedId);
                                                actualizarInventario(productos, cantidades, "Envío: " + formattedId);
                                                Toast.makeText(this, "Envío registrado con éxito.", Toast.LENGTH_SHORT).show();
                                                redirigirConfirmacion(formattedId, true);
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.d("RegistrarEnvio", "Error al registrar el envío: " + e.getMessage());
                                                Toast.makeText(this, "Error al registrar el envío: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Log.d("RegistrarEnvio", "Error al generar el ID del envío: " + e.getMessage());
                                    Toast.makeText(this, "Error al generar el ID del envío: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("RegistrarEnvio", "Error al validar límite de envíos: " + e.getMessage());
                    Toast.makeText(this, "Error al validar límite de envíos por camión: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void actualizarInventario(ArrayList<Producto> productos, ArrayList<Integer> cantidades, String contexto) {
        Log.d("ActualizarInventario", "Inicio del método actualizarInventario para: " + contexto);
        for (int i = 0; i < productos.size(); i++) {
            Producto producto = productos.get(i);
            int cantidadRestar = cantidades.get(i);

            db.collection("catalogo")
                    .whereEqualTo("Nombre_producto", producto.getNombre())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                            int stockActualFirestore = queryDocumentSnapshots.getDocuments().get(0).getLong("Stock").intValue();
                            int nuevoStock = Math.max(0, stockActualFirestore - cantidadRestar);

                            Log.d("ActualizarInventario", "Stock en Firestore: " + stockActualFirestore + " | Nuevo stock: " + nuevoStock);

                            db.collection("catalogo").document(documentId)
                                    .update("Stock", nuevoStock)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("ActualizarInventario", "Stock actualizado correctamente en Firestore.");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("ActualizarInventario", "Error al actualizar el stock: " + e.getMessage());
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ActualizarInventario", "Error al acceder al inventario: " + e.getMessage());
                    });

        }
    }





    private void redirigirConfirmacion(String id, boolean esEnvio) {
        Intent intent = new Intent(SeleccionarFechaActivity.this, ConfirmacionPedidoActivity.class);
        if (esEnvio) {
            intent.putExtra("idEnvio", id);
        } else {
            intent.putExtra("idPedido", id);
        }
        startActivity(intent);
        finish();
    }
}
