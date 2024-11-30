package com.example.maiz_final;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CerrarPedidoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cerrar_pedido);

        String idPedido = getIntent().getStringExtra("idPedido");

        TextView tvMensaje = findViewById(R.id.tvMensaje);
        tvMensaje.setText("Cerrando el pedido con ID: " + idPedido);
    }
}
