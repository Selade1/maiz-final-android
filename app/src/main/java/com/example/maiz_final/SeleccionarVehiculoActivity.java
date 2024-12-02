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

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Configurar RecyclerView
        recyclerViewVehiculos = findViewById(R.id.recyclerVehiculos);
        recyclerViewVehiculos.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar lista de vehículos
        listaVehiculos = new ArrayList<>();

        // Cargar vehículos desde Firestore
        cargarVehiculos();

        // Configurar adaptador y manejar clic en un vehículo
        vehiculosAdapter = new VehiculosAdapter(listaVehiculos, vehiculo -> {
            String vehiculoId = vehiculo.split(" ")[0]; // Extraer solo el ID del vehículo
            Intent intent = new Intent(SeleccionarVehiculoActivity.this, SeleccionarFechaActivity.class);
            intent.putExtra("vehiculo", vehiculoId); // Pasar solo el ID
            intent.putExtra("cliente", getIntent().getStringExtra("cliente"));
            intent.putExtra("productos", getIntent().getParcelableArrayListExtra("productos"));
            intent.putExtra("cantidades", getIntent().getSerializableExtra("cantidades"));
            intent.putExtra("esEnvio", true);
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
