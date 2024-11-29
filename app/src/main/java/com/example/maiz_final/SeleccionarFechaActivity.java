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
        db.collection("envios")
                .get()
                .addOnSuccessListener(enviosSnapshot -> {
                    int nextId = enviosSnapshot.size() + 1;
                    String formattedId = nextId + "_env"; // Formato del ID

                    Map<String, Object> envio = new HashMap<>();
                    envio.put("id", formattedId);
                    envio.put("idCamion", vehiculo);
                    envio.put("fecha", fecha);
                    envio.put("cliente", cliente);

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

                                // Redirigir a pantalla de confirmación
                                Intent intent = new Intent(SeleccionarFechaActivity.this, ConfirmacionPedidoActivity.class);
                                intent.putExtra("idEnvio", formattedId);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al registrar el envío.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al acceder a la base de datos.", Toast.LENGTH_SHORT).show();
                });
    }




}
