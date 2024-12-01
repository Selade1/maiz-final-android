package com.example.maiz_final;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class OrdenesPendientesActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private OrdenesPendientesAdapter adapter;
    private ArrayList<OrdenPendiente> ordenesPendientesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordenes_pendientes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerViewOrdenesPendientes);
        ordenesPendientesList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrdenesPendientesAdapter(this, ordenesPendientesList);
        recyclerView.setAdapter(adapter);

        cargarOrdenesPendientes();
    }

    private void cargarOrdenesPendientes() {
        db.collection("pedidos")
                .get()
                .addOnSuccessListener(pedidosSnapshot -> {
                    ordenesPendientesList.clear();

                    for (QueryDocumentSnapshot document : pedidosSnapshot) {
                        // Filtrar pedidos que tienen el campo "estado"
                        if (document.contains("estado")) {
                            continue; // Ignorar pedidos con "estado"
                        }

                        String clienteNombre = document.getString("nombreCliente");
                        String tipoEntrega = document.getString("tipoEntrega");
                        ArrayList<Map<String, Object>> productos = (ArrayList<Map<String, Object>>) document.get("productos");
                        String idPedido = document.getId(); // Usar el ID del documento directamente

                        if ("Mostrador".equals(clienteNombre)) {
                            OrdenPendiente pedido = new OrdenPendiente(
                                    idPedido + "_ped",
                                    "Mostrador",
                                    "Pedido",
                                    tipoEntrega,
                                    productos,
                                    null,
                                    null,
                                    "N/A",
                                    "N/A",
                                    "N/A"
                            );
                            ordenesPendientesList.add(pedido);
                        } else if (clienteNombre != null) {
                            db.collection("clientes")
                                    .whereEqualTo("nombre", clienteNombre)
                                    .get()
                                    .addOnSuccessListener(clienteSnapshot -> {
                                        if (!clienteSnapshot.isEmpty()) {
                                            QueryDocumentSnapshot clienteDoc = (QueryDocumentSnapshot) clienteSnapshot.getDocuments().get(0);
                                            String direccion = clienteDoc.getString("direccion");
                                            String telefono = clienteDoc.getString("telefono");
                                            String correo = clienteDoc.getString("correo");

                                            OrdenPendiente pedido = new OrdenPendiente(
                                                    idPedido + "_ped",
                                                    clienteNombre,
                                                    "Pedido",
                                                    tipoEntrega,
                                                    productos,
                                                    null,
                                                    null,
                                                    direccion != null ? direccion : "N/A",
                                                    telefono != null ? telefono : "N/A",
                                                    correo != null ? correo : "N/A"
                                            );
                                            ordenesPendientesList.add(pedido);
                                        }
                                        adapter.notifyDataSetChanged();
                                    });
                        }
                    }

                    cargarEnvios(); // Después de cargar los pedidos, cargar envíos
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar pedidos.", Toast.LENGTH_SHORT).show();
                });
    }

    private void cargarEnvios() {
        db.collection("envios").get()
                .addOnSuccessListener(enviosSnapshot -> {
                    for (QueryDocumentSnapshot document : enviosSnapshot) {
                        // Filtrar envíos que tienen el campo "estado"
                        if (document.contains("estado")) {
                            continue; // Ignorar envíos con "estado"
                        }

                        String clienteNombre = document.getString("nombreCliente");
                        String idCamion = document.getString("idCamion");
                        String fecha = document.getString("fecha");
                        ArrayList<Map<String, Object>> productos = (ArrayList<Map<String, Object>>) document.get("productos");
                        String idEnvio = document.getId(); // Usar el ID del documento directamente

                        if ("Mostrador".equals(clienteNombre)) {
                            OrdenPendiente envio = new OrdenPendiente(
                                    idEnvio + "_env",
                                    "Mostrador",
                                    "Envío",
                                    null,
                                    productos,
                                    idCamion,
                                    fecha,
                                    "N/A",
                                    "N/A",
                                    "N/A"
                            );
                            ordenesPendientesList.add(envio);
                        } else if (clienteNombre != null) {
                            db.collection("clientes")
                                    .whereEqualTo("nombre", clienteNombre)
                                    .get()
                                    .addOnSuccessListener(clienteSnapshot -> {
                                        if (!clienteSnapshot.isEmpty()) {
                                            QueryDocumentSnapshot clienteDoc = (QueryDocumentSnapshot) clienteSnapshot.getDocuments().get(0);
                                            String direccion = clienteDoc.getString("direccion");
                                            String telefono = clienteDoc.getString("telefono");
                                            String correo = clienteDoc.getString("correo");

                                            OrdenPendiente envio = new OrdenPendiente(
                                                    idEnvio + "_env",
                                                    clienteNombre,
                                                    "Envío",
                                                    null,
                                                    productos,
                                                    idCamion,
                                                    fecha,
                                                    direccion != null ? direccion : "N/A",
                                                    telefono != null ? telefono : "N/A",
                                                    correo != null ? correo : "N/A"
                                            );
                                            ordenesPendientesList.add(envio);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar envíos.", Toast.LENGTH_SHORT).show();
                });
    }
}
