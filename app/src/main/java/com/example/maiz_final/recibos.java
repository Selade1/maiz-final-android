package com.example.maiz_final;

import android.os.Bundle;

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

public class recibos extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerViewRecibos;
    private RecibosAdapter recibosAdapter;
    private ArrayList<Pedido> pedidosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recibos);

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inicializar Firestore y vistas
        db = FirebaseFirestore.getInstance();
        recyclerViewRecibos = findViewById(R.id.recyclerViewRecibos);

        // Configurar RecyclerView
        pedidosList = new ArrayList<>();
        recibosAdapter = new RecibosAdapter(pedidosList);
        recyclerViewRecibos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRecibos.setAdapter(recibosAdapter);

        // Cargar datos de Firestore
        cargarRecibos();
    }

    private void cargarRecibos() {
        db.collection("pedidos").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    pedidosList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getString("idPedido");
                        String cliente = document.getString("nombreCliente"); // Ajustado para coincidir con tu modelo
                        String tipoEntrega = document.getString("tipoEntrega");

                        // Convertir productos a List<Map<String, Object>>
                        List<Map<String, Object>> productos = new ArrayList<>();
                        List<HashMap<String, Object>> productosFirestore = (List<HashMap<String, Object>>) document.get("productos");

                        if (productosFirestore != null) {
                            for (HashMap<String, Object> producto : productosFirestore) {
                                productos.add(new HashMap<>(producto)); // Convertir a Map<String, Object>
                            }
                        }

                        // Crear un nuevo Pedido y agregarlo a la lista
                        pedidosList.add(new Pedido(id, cliente, tipoEntrega, productos));
                    }
                    recibosAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Manejar errores
                });
    }
}
