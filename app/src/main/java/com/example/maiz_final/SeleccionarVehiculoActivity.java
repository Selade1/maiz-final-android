package com.example.maiz_final;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class SeleccionarVehiculoActivity extends AppCompatActivity {

    private RecyclerView recyclerVehiculos;
    private FirebaseFirestore db;
    private ArrayList<String> listaVehiculos; // IDs de vehículos
    private VehiculosAdapter adapter; // Adapter para mostrar vehículos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_vehiculo);

        // Inicializar RecyclerView
        recyclerVehiculos = findViewById(R.id.recyclerVehiculos);
        recyclerVehiculos.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Recuperar datos del Intent
        ArrayList<Producto> productos = getIntent().getParcelableArrayListExtra("productos");
        ArrayList<Integer> cantidades = (ArrayList<Integer>) getIntent().getSerializableExtra("cantidades");
        String cliente = getIntent().getStringExtra("cliente");

        // Validar datos
        if (productos == null || cantidades == null || cliente == null) {
            Toast.makeText(this, "Error al cargar los datos del pedido.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cargar vehículos desde Firestore
        listaVehiculos = new ArrayList<>();
        adapter = new VehiculosAdapter(listaVehiculos, vehiculo -> {
            // Redirigir a la actividad de selección de fecha
            Intent intent = new Intent(SeleccionarVehiculoActivity.this, SeleccionarFechaActivity.class);
            intent.putExtra("vehiculo", vehiculo); // Vehículo seleccionado
            intent.putExtra("productos", productos); // Pasar productos seleccionados
            intent.putExtra("cantidades", cantidades); // Pasar cantidades
            intent.putExtra("cliente", cliente); // Pasar cliente
            startActivity(intent);
        });
        recyclerVehiculos.setAdapter(adapter);

        cargarVehiculos();
    }

    private void cargarVehiculos() {
        db.collection("vehiculo").get().addOnSuccessListener(queryDocumentSnapshots -> {
            listaVehiculos.clear();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                String idVehiculo = document.getId(); // ID del vehículo
                listaVehiculos.add(idVehiculo);
            }
            adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos cambiaron
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al cargar vehículos.", Toast.LENGTH_SHORT).show();
        });
    }
}
