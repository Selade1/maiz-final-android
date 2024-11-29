package com.example.maiz_final;

import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_fecha);

        db = FirebaseFirestore.getInstance();

        // Recuperar datos del Intent
        String cliente = getIntent().getStringExtra("cliente");
        String tipoEntrega = getIntent().getStringExtra("tipoEntrega");
        ArrayList<Producto> productos = getIntent().getParcelableArrayListExtra("productos");
        ArrayList<Integer> cantidades = (ArrayList<Integer>) getIntent().getSerializableExtra("cantidades");
        boolean esEnvio = getIntent().getBooleanExtra("esEnvio", false);
        String vehiculo = getIntent().getStringExtra("vehiculo");

        // Validar datos recibidos
        if (productos == null || cantidades == null || cliente == null || tipoEntrega == null || (esEnvio && vehiculo == null)) {
            Toast.makeText(this, "Error al cargar los datos del pedido/envío.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar el calendario
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setMinDate(System.currentTimeMillis() + (24 * 60 * 60 * 1000));
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            fechaSeleccionada = year + "-" + (month + 1) + "-" + dayOfMonth;
        });

        // Configurar el botón "Registrar"
        Button btnRegistrar = findViewById(R.id.btnRealizarPedido);
        btnRegistrar.setOnClickListener(v -> {
            if (fechaSeleccionada == null) {
                Toast.makeText(this, "Selecciona una fecha válida.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (esEnvio) {
                registrarEnvio(cliente, productos, cantidades, vehiculo, fechaSeleccionada);
            } else {
                registrarPedido(cliente, tipoEntrega, productos, cantidades, fechaSeleccionada);
            }
        });
    }

    private void registrarPedido(String cliente, String tipoEntrega, ArrayList<Producto> productos, ArrayList<Integer> cantidades, String fecha) {
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
                                Toast.makeText(this, "Pedido registrado con éxito.", Toast.LENGTH_SHORT).show();
                                redirigirConfirmacion(formattedId, false);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al registrar el pedido.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al acceder a la base de datos.", Toast.LENGTH_SHORT).show();
                });
    }

    private void registrarEnvio(String cliente, ArrayList<Producto> productos, ArrayList<Integer> cantidades, String vehiculo, String fecha) {
        db.collection("envios")
                .get()
                .addOnSuccessListener(snapshot -> {
                    int nextId = snapshot.size() + 1;
                    String formattedId = nextId + "_env";

                    Map<String, Object> envio = new HashMap<>();
                    envio.put("id", formattedId);
                    envio.put("cliente", cliente);
                    envio.put("idCamion", vehiculo);
                    envio.put("fecha", fecha);

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
                                Toast.makeText(this, "Envío registrado con éxito.", Toast.LENGTH_SHORT).show();
                                redirigirConfirmacion(formattedId, true);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al registrar el envío.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al acceder a la base de datos.", Toast.LENGTH_SHORT).show();
                });
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
