package com.example.maiz_final;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class CancelarPedidoActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelar_pedido);

        db = FirebaseFirestore.getInstance();

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.cancelartoolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Obtener datos del Intent
        String idOrden = getIntent().getStringExtra("idOrden");
        String cliente = getIntent().getStringExtra("cliente");
        String direccion = getIntent().getStringExtra("direccion");
        String telefono = getIntent().getStringExtra("telefono");
        String correo = getIntent().getStringExtra("correo");
        String tipo = getIntent().getStringExtra("tipo");
        String tipoEntrega = getIntent().getStringExtra("tipoEntrega");
        ArrayList<Map<String, Object>> productos = (ArrayList<Map<String, Object>>) getIntent().getSerializableExtra("productos");

        // Mostrar los detalles
        TextView tvDetalles = findViewById(R.id.tvDetalles);

        StringBuilder detalles = new StringBuilder();
        detalles.append("Cliente: ").append(cliente).append("\n")
                .append("Dirección: ").append(direccion).append("\n")
                .append("Teléfono: ").append(telefono).append("\n")
                .append("Correo: ").append(correo).append("\n")
                .append("Tipo: ").append(tipo).append("\n");

        if ("Pedido".equals(tipo)) {
            detalles.append("Tipo de Entrega: ").append(tipoEntrega).append("\n");
        }
        detalles.append("\nProductos:\n");
        if (productos != null) {
            for (Map<String, Object> producto : productos) {
                detalles.append("- ").append(producto.get("nombre"))
                        .append(" (Cantidad: ").append(producto.get("cantidad"))
                        .append(", Precio: $").append(producto.get("precio"))
                        .append(")\n");
            }
        }
        tvDetalles.setText(detalles.toString());

        // Botón Confirmar Cancelación
        Button btnConfirmarCancelar = findViewById(R.id.btnConfirmarCancelar);
        btnConfirmarCancelar.setOnClickListener(v -> cancelarPedido(idOrden, tipo));
    }

    private void cancelarPedido(String idOrden, String tipo) {
        if (idOrden == null || tipo == null) {
            Toast.makeText(this, "Datos inválidos para cancelar el pedido.", Toast.LENGTH_SHORT).show();
            return;
        }

        String collection = "Pedido".equals(tipo) ? "pedidos" : "envios";

        db.collection(collection).document(idOrden)
                .update("estado", "cancelado")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Pedido cancelado exitosamente.", Toast.LENGTH_SHORT).show();
                    finish(); // Regresar a la actividad anterior
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cancelar el pedido: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
