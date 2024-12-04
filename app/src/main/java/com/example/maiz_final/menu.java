package com.example.maiz_final;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class menu extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView greetingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Configurar la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Referenciar el TextView del saludo
        greetingText = findViewById(R.id.greetingText);

        // Cargar datos del usuario autenticado
        cargarDatosUsuario();

        // Referenciar y configurar botones
        configurarBotones();
    }

    private void cargarDatosUsuario() {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();

            // Consultar Firestore para obtener el nombre del usuario
            db.collection("usuarios")
                    .whereEqualTo("correo", userEmail)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            String nombre = document.getString("nombre");
                            greetingText.setText("Hola " + nombre + ", ¿qué operación deseas hacer hoy?");
                        } else {
                            greetingText.setText("Hola, ¿qué operación deseas hacer hoy?");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al cargar datos del usuario.", Toast.LENGTH_SHORT).show();
                        greetingText.setText("Hola, ¿qué operación deseas hacer hoy?");
                    });
        } else {
            // Si no hay un usuario autenticado, redirigir al login
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void configurarBotones() {
        // Botón "Realizar orden"
        Button btnOrder = findViewById(R.id.btnOrder);
        btnOrder.setOnClickListener(view -> {
            Intent intent = new Intent(menu.this, orden.class);
            startActivity(intent);
        });

        // Botón "Registro de clientes"
        Button btnRegisterClients = findViewById(R.id.btnRegisterClients);
        btnRegisterClients.setOnClickListener(view -> {
            Intent intent = new Intent(menu.this, clientes.class);
            startActivity(intent);
        });

        // Botón "Ver órdenes"
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
            new AlertDialog.Builder(this)
                    .setTitle("Cerrar sesión")
                    .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(menu.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
