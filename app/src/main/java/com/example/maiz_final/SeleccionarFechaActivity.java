package com.example.maiz_final;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
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
        String vehiculo = getIntent().getStringExtra("vehiculo");
        ArrayList<Producto> productos = getIntent().getParcelableArrayListExtra("productos");
        ArrayList<Integer> cantidades = (ArrayList<Integer>) getIntent().getSerializableExtra("cantidades");
        String cliente = getIntent().getStringExtra("cliente");

        // Validar datos
        if (vehiculo == null || productos == null || cantidades == null || cliente == null) {
            Toast.makeText(this, "Error al cargar los datos del pedido.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Mostrar vehículo seleccionado
        TextView tvCamionSeleccionado = findViewById(R.id.tvCamionSeleccionado);
        tvCamionSeleccionado.setText("Camión seleccionado: " + vehiculo);

        // Configurar calendario
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setMinDate(System.currentTimeMillis() + (24 * 60 * 60 * 1000)); // Desde mañana
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            fechaSeleccionada = year + "-" + (month + 1) + "-" + dayOfMonth;
        });

        // Configurar botón "Realizar Pedido"
        Button btnRealizarPedido = findViewById(R.id.btnRealizarPedido);
        btnRealizarPedido.setOnClickListener(v -> {
            if (fechaSeleccionada == null) {
                Toast.makeText(this, "Selecciona una fecha válida.", Toast.LENGTH_SHORT).show();
                return;
            }

            registrarPedido(vehiculo, productos, cantidades, cliente, fechaSeleccionada);
        });
    }

    private void registrarPedido(String vehiculo, ArrayList<Producto> productos, ArrayList<Integer> cantidades, String cliente, String fecha) {
        // Verificar pedidos existentes para este vehículo en la fecha seleccionada
        db.collection("envios")
                .whereEqualTo("idCamion", vehiculo)
                .whereEqualTo("fecha", fecha)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.size() >= 2) {
                        Toast.makeText(this, "El vehículo ya tiene 2 pedidos para esta fecha.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Obtener el último ID para calcular el siguiente ID de manera segura
                        db.collection("envios")
                                .orderBy("idPedido", com.google.firebase.firestore.Query.Direction.DESCENDING)
                                .limit(1)
                                .get()
                                .addOnSuccessListener(snapshot -> {
                                    int nextId = 1; // Por defecto, el primer ID es 1
                                    if (!snapshot.isEmpty()) {
                                        nextId = snapshot.getDocuments().get(0).getLong("idPedido").intValue() + 1;
                                    }

                                    // Crear un nuevo pedido
                                    Map<String, Object> pedido = new HashMap<>();
                                    pedido.put("idPedido", nextId);
                                    pedido.put("idCamion", vehiculo);
                                    pedido.put("fecha", fecha);
                                    pedido.put("cliente", cliente);

                                    List<Map<String, Object>> productosPedido = new ArrayList<>();
                                    for (int i = 0; i < productos.size(); i++) {
                                        Map<String, Object> productoMap = new HashMap<>();
                                        productoMap.put("nombre", productos.get(i).getNombre());
                                        productoMap.put("cantidad", cantidades.get(i));
                                        productoMap.put("precio", productos.get(i).getPrecio());
                                        productosPedido.add(productoMap);
                                    }
                                    pedido.put("productos", productosPedido);

                                    // Guardar el pedido en la colección "envios"
                                    db.collection("envios").document(String.valueOf(nextId)).set(pedido)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(this, "Pedido de flete registrado con éxito.", Toast.LENGTH_SHORT).show();

                                                // Redirigir a pantalla de confirmación
                                                Intent intent = new Intent(SeleccionarFechaActivity.this, ConfirmacionPedidoActivity.class);
                                                startActivity(intent);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "Error al registrar el pedido.", Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error al obtener el último ID.", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al verificar los pedidos del vehículo.", Toast.LENGTH_SHORT).show();
                });
    }


}
