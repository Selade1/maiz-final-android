package com.example.maiz_final;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CancelarPedidoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelar_pedido);

        String idPedido = getIntent().getStringExtra("idPedido");

        TextView tvMensaje = findViewById(R.id.tvMensaje);
        tvMensaje.setText("Cancelando el pedido con ID: " + idPedido);
    }
}
