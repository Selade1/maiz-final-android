package com.example.maiz_final;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ConfirmacionPedidoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacion);

        TextView tvConfirmacion = findViewById(R.id.tvConfirmacion);

        String idPedido = getIntent().getStringExtra("idPedido");
        String idEnvio = getIntent().getStringExtra("idEnvio");

        Button btnMenu = findViewById(R.id.btnRegresarMenu);
        Button btnPed = findViewById(R.id.btnOtroPedido);

        // Redirigir al menú principal
        btnMenu.setOnClickListener(view -> {
            Intent intent = new Intent(ConfirmacionPedidoActivity.this, menu.class);
            startActivity(intent);
            finish(); // Finalizar la actividad actual
        });

        // Redirigir para realizar otro pedido
        btnPed.setOnClickListener(view -> {
            Intent intent = new Intent(ConfirmacionPedidoActivity.this, orden.class);
            startActivity(intent);
            finish(); // Finalizar la actividad actual
        });

        // Mostrar el mensaje de confirmación
        if (idPedido != null) {
            tvConfirmacion.setText("Pedido registrado exitosamente con ID: " + idPedido);
        } else if (idEnvio != null) {
            tvConfirmacion.setText("Envío registrado exitosamente con ID: " + idEnvio);
        } else {
            tvConfirmacion.setText("Error: No se encontró información del registro.");
        }
    }
}
