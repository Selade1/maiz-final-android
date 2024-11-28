package com.example.maiz_final;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ConfirmacionPedidoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacion);

        // Botón para realizar otro pedido
        Button btnOtroPedido = findViewById(R.id.btnOtroPedido);
        btnOtroPedido.setOnClickListener(v -> {
            Intent intent = new Intent(ConfirmacionPedidoActivity.this, orden.class); // Regresa al menú principal
            startActivity(intent);
            finish();
        });

        // Botón para regresar al menú
        // Código para el botón "Regresar al menú"
        Button btnRegresarMenu = findViewById(R.id.btnRegresarMenu);
        btnRegresarMenu.setOnClickListener(v -> {
            Intent intent = new Intent(ConfirmacionPedidoActivity.this, menu.class); // Cambia MainActivity por la actividad que deseas
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Asegura que sea la pantalla principal
            startActivity(intent);
            finish();
        });

    }
}