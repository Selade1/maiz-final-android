package com.example.maiz_final;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

public class menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Configurar la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Referenciar el botón "Realizar orden"
        Button btnOrder = findViewById(R.id.btnOrder);

        // Configurar el clic en el botón
        btnOrder.setOnClickListener(view -> {
            // Redirigir a activity_orden.xml
            Intent intent = new Intent(menu.this, orden.class);
            startActivity(intent);
        });

        // Referenciar el botón "Registro de clientes"
        Button btnRegisterClients = findViewById(R.id.btnRegisterClients);

        // Configurar el clic en el botón
        btnRegisterClients.setOnClickListener(view -> {
            // Redirigir al archivo activity_clientes.xml
            Intent intent = new Intent(menu.this, clientes.class);
            startActivity(intent);
        });

        Button btnOrdenesPendientes = findViewById(R.id.btnViewOrders);
        btnOrdenesPendientes.setOnClickListener(v -> {
            Intent intent = new Intent(menu.this, OrdenesPendientesActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            // Mostrar diálogo de confirmación para logout
            new AlertDialog.Builder(this)
                    .setTitle("Cerrar sesión")
                    .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Cerrar sesión en Firebase
                            FirebaseAuth.getInstance().signOut();
                            // Redirigir al login
                            Intent intent = new Intent(menu.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
