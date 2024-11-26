package com.example.maiz_final;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class clientes extends AppCompatActivity {

    private FirebaseFirestore db; // Instancia de Firestore
    private EditText inputName, inputAddress, inputEmail, inputPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes);

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Habilitar botón de retroceso
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Referenciar los campos del formulario
        inputName = findViewById(R.id.inputName);
        inputAddress = findViewById(R.id.inputAddress);
        inputEmail = findViewById(R.id.inputEmail);
        inputPhone = findViewById(R.id.inputPhone);
        Button btnRegister = findViewById(R.id.btnRegister);

        // Configurar el botón de registro
        btnRegister.setOnClickListener(view -> registerClient());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Acción del botón de retroceso
            Intent intent = new Intent(clientes.this, menu.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void registerClient() {
        // Obtener valores de los campos
        String name = inputName.getText().toString().trim();
        String address = inputAddress.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();

        // Validar campos vacíos
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone)) {
            Toast.makeText(clientes.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Referencia a la colección "clientes"
        CollectionReference clientesCollection = db.collection("clientes");

        // Verificar si los campos ya existen
        clientesCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean exists = false;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String existingName = document.getString("nombre");
                    String existingAddress = document.getString("direccion");
                    String existingEmail = document.getString("correo");
                    String existingPhone = document.getString("telefono");

                    // Verificar si alguno de los valores ya existe
                    if (name.equalsIgnoreCase(existingName) ||
                            address.equalsIgnoreCase(existingAddress) ||
                            email.equalsIgnoreCase(existingEmail) ||
                            phone.equalsIgnoreCase(existingPhone)) {
                        exists = true;
                        break;
                    }
                }

                if (exists) {
                    Toast.makeText(clientes.this, "Alguno de los campos ya existen", Toast.LENGTH_SHORT).show();
                } else {
                    // Si no existe, proceder con el registro
                    int newId = task.getResult().size() + 1; // Calcular el nuevo ID basado en el tamaño actual
                    String documentId = String.format("%03d", newId); // Formato del ID (ej. "001", "002")

                    // Crear un mapa con los datos del cliente
                    Map<String, Object> cliente = new HashMap<>();
                    cliente.put("id", documentId);
                    cliente.put("nombre", name);
                    cliente.put("direccion", address);
                    cliente.put("correo", email);
                    cliente.put("telefono", phone);

                    // Guardar los datos en Firestore
                    clientesCollection.document(documentId).set(cliente)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(clientes.this, "Cliente registrado con éxito", Toast.LENGTH_SHORT).show();
                                // Limpiar campos después del registro
                                inputName.setText("");
                                inputAddress.setText("");
                                inputEmail.setText("");
                                inputPhone.setText("");
                            })
                            .addOnFailureListener(e -> Toast.makeText(clientes.this, "Error al registrar cliente", Toast.LENGTH_SHORT).show());
                }
            } else {
                Toast.makeText(clientes.this, "Error al verificar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
