package com.example.maiz_final;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerViewOrdenesPendientes);
        ordenesPendientesList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrdenesPendientesAdapter(ordenesPendientesList);
        recyclerView.setAdapter(adapter);

        cargarOrdenesPendientes();
    }

    private void cargarOrdenesPendientes() {
        db.collection("pedidos").get()
                .addOnSuccessListener(pedidosSnapshot -> {
                    ordenesPendientesList.clear(); // Limpiar la lista antes de agregar nuevos datos

                    for (QueryDocumentSnapshot document : pedidosSnapshot) {
                        String clienteNombre = document.getString("nombreCliente"); // Usar el nombre del cliente directamente
                        String tipoEntrega = document.getString("tipoEntrega");
                        ArrayList<Map<String, Object>> productos = (ArrayList<Map<String, Object>>) document.get("productos");
                        String idPedido = document.getId() + "_ped"; // Agregar sufijo _ped

                        if ("Mostrador".equals(clienteNombre)) {
                            // Manejar directamente los pedidos de "Mostrador"
                            OrdenPendiente pedido = new OrdenPendiente(
                                    idPedido,
                                    "Mostrador",
                                    "Pedido",
                                    tipoEntrega,
                                    productos,
                                    null,
                                    null,
                                    "N/A", // Dirección
                                    "N/A", // Teléfono
                                    "N/A"  // Correo
                            );
                            ordenesPendientesList.add(pedido);
                        } else if (clienteNombre != null) {
                            // Buscar datos del cliente en la colección "clientes"
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
                                                    idPedido,
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

                    // Cargar envíos después de pedidos
                    cargarEnvios();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar pedidos.", Toast.LENGTH_SHORT).show();
                });
    }

    private void cargarEnvios() {
        db.collection("envios").get()
                .addOnSuccessListener(enviosSnapshot -> {
                    for (QueryDocumentSnapshot document : enviosSnapshot) {
                        String clienteNombre = document.getString("cliente");
                        String idCamion = document.getString("idCamion");
                        String fecha = document.getString("fecha");
                        ArrayList<Map<String, Object>> productos = (ArrayList<Map<String, Object>>) document.get("productos");
                        String idEnvio = document.getId() + "_env"; // Agregar sufijo _env

                        if ("Mostrador".equals(clienteNombre)) {
                            // Manejar directamente los envíos de "Mostrador"
                            OrdenPendiente envio = new OrdenPendiente(
                                    idEnvio,
                                    "Mostrador",
                                    "Envío",
                                    null,
                                    productos,
                                    idCamion,
                                    fecha,
                                    "N/A", // Dirección
                                    "N/A", // Teléfono
                                    "N/A"  // Correo
                            );
                            ordenesPendientesList.add(envio);
                        } else if (clienteNombre != null) {
                            // Buscar datos del cliente en la colección "clientes"
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
                                                    idEnvio,
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
                                        }
                                        adapter.notifyDataSetChanged();
                                    });
                        }
                    }
                    adapter.notifyDataSetChanged(); // Actualizar el adaptador al final
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar envíos.", Toast.LENGTH_SHORT).show();
                });
    }




}
