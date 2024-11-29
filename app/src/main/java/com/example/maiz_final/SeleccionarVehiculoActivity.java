package com.example.maiz_final;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class SeleccionarVehiculoActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerViewVehiculos;
    private VehiculosAdapter vehiculosAdapter;
    private ArrayList<String> listaVehiculos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_vehiculo);

        db = FirebaseFirestore.getInstance();
        recyclerViewVehiculos = findViewById(R.id.recyclerVehiculos);
        recyclerViewVehiculos.setLayoutManager(new LinearLayoutManager(this));

        listaVehiculos = new ArrayList<>();
        cargarVehiculos();

        // Inicializar adaptador
        vehiculosAdapter = new VehiculosAdapter(listaVehiculos, vehiculo -> {
            Toast.makeText(this, "Vehículo seleccionado: " + vehiculo, Toast.LENGTH_SHORT).show();

            // Pasar datos a la siguiente actividad
            Intent intent = new Intent(SeleccionarVehiculoActivity.this, SeleccionarFechaActivity.class);
            intent.putExtra("vehiculo", vehiculo);
            intent.putExtra("cliente", getIntent().getStringExtra("cliente"));
            intent.putExtra("productos", getIntent().getParcelableArrayListExtra("productos"));
            intent.putExtra("cantidades", getIntent().getSerializableExtra("cantidades"));
            intent.putExtra("tipoEntrega", "Flete"); // Confirmar el tipo de entrega
            intent.putExtra("esEnvio", true); // Marcar como envío
            startActivity(intent);
            finish();
        });

        recyclerViewVehiculos.setAdapter(vehiculosAdapter);

        // Botón regresar
        Button btnRegresar = findViewById(R.id.btnBack);
        btnRegresar.setOnClickListener(v -> finish());
    }

    private void cargarVehiculos() {
        db.collection("vehiculo")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaVehiculos.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getString("ID");
                        String modelo = document.getString("modelo");
                        String numeroDeSerie = document.getString("numeroDeSerie");

                        if (id != null && modelo != null && numeroDeSerie != null) {
                            String vehiculoInfo = id + " - " + modelo + " - Serie: " + numeroDeSerie;
                            listaVehiculos.add(vehiculoInfo);
                        }
                    }

                    if (listaVehiculos.isEmpty()) {
                        Toast.makeText(this, "No hay vehículos disponibles.", Toast.LENGTH_SHORT).show();
                    }

                    vehiculosAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar vehículos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
