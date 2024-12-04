package com.example.maiz_final;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class CatalogoAdapter extends RecyclerView.Adapter<CatalogoAdapter.CatalogoViewHolder> {

    private ArrayList<Producto> productos;
    private HashMap<Producto, Integer> carrito;
    private String tipoEntrega; // Tipo de entrega seleccionado

    public CatalogoAdapter(ArrayList<Producto> productos, String tipoEntrega) {
        this.productos = productos;
        this.carrito = new HashMap<>();
        this.tipoEntrega = tipoEntrega;
    }

    public HashMap<Producto, Integer> getCarrito() {
        return carrito;
    }

    @NonNull
    @Override
    public CatalogoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalogo, parent, false);
        return new CatalogoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogoViewHolder holder, int position) {
        Producto producto = productos.get(position);
        holder.bind(producto);
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    class CatalogoViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNombreProducto, tvPrecioProducto, tvStockProducto, tvCantidadProducto;
        private ImageButton btnAdd, btnRemove;
        private ImageView ivProducto; // Imagen del producto

        public CatalogoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreProducto = itemView.findViewById(R.id.tvNombreProducto);
            tvPrecioProducto = itemView.findViewById(R.id.tvPrecioProducto);
            tvStockProducto = itemView.findViewById(R.id.tvStockProducto);
            tvCantidadProducto = itemView.findViewById(R.id.tvCantidadProducto);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            ivProducto = itemView.findViewById(R.id.ivProducto); // Agregado: ImageView para la imagen del producto
        }

        public void bind(Producto producto) {
            // Configurar los datos del producto
            tvNombreProducto.setText(producto.getNombre());
            tvPrecioProducto.setText("Precio: $" + producto.getPrecio());
            tvStockProducto.setText("Stock: " + producto.getStock());
            tvCantidadProducto.setText(String.valueOf(carrito.getOrDefault(producto, 0)));

            // Cargar la imagen del producto usando Glide
            Glide.with(itemView.getContext())
                    .load(producto.getImageUrl()) // URL de la imagen desde Firestore
                    //.placeholder(R.drawable.placeholder_image) // Imagen de carga (opcional)
                    //.error(R.drawable.error_image) // Imagen en caso de error (opcional)
                    .into(ivProducto);

            // Configurar acciones de los botones
            btnAdd.setOnClickListener(v -> {
                int limite = getLimiteCantidad();
                if (cantidadCarrito() < limite || limite == -1) {
                    if (carrito.getOrDefault(producto, 0) < producto.getStock()) {
                        carrito.put(producto, carrito.getOrDefault(producto, 0) + 1);
                        producto.setStock(producto.getStock() - 1);
                        tvCantidadProducto.setText(String.valueOf(carrito.get(producto)));
                        tvStockProducto.setText("Stock: " + producto.getStock());

                        // Actualizar el stock en Firestore
                        actualizarStockEnFirestore(producto);
                    } else {
                        Toast.makeText(itemView.getContext(), "Stock insuficiente.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(itemView.getContext(), "Límite alcanzado para " + tipoEntrega, Toast.LENGTH_SHORT).show();
                }
            });

            btnRemove.setOnClickListener(v -> {
                if (carrito.containsKey(producto) && carrito.get(producto) > 0) {
                    carrito.put(producto, carrito.get(producto) - 1);
                    if (carrito.get(producto) == 0) {
                        carrito.remove(producto);
                    }
                    producto.setStock(producto.getStock() + 1);
                    tvCantidadProducto.setText(String.valueOf(carrito.getOrDefault(producto, 0)));
                    tvStockProducto.setText("Stock: " + producto.getStock());

                    // Actualizar el stock en Firestore
                    actualizarStockEnFirestore(producto);
                }
            });
        }

        private int cantidadCarrito() {
            return carrito.values().stream().mapToInt(Integer::intValue).sum();
        }

        private void actualizarStockEnFirestore(Producto producto) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("catalogo")
                    .whereEqualTo("Nombre_producto", producto.getNombre()) // Usar el nombre del producto como identificador
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                            db.collection("catalogo").document(documentId)
                                    .update("Stock", producto.getStock()) // Actualizar el stock en Firestore
                                    .addOnSuccessListener(aVoid -> {
                                        // Éxito: Stock actualizado en Firestore
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(ivProducto.getContext(), "Error al actualizar el stock.", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ivProducto.getContext(), "Error al acceder a Firestore.", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private int getLimiteCantidad() {
        switch (tipoEntrega) {
            case "Flete":
                return 50; // Máximo 50 productos para "Flete"
            case "Minorista":
                return 5; // Máximo 5 productos para "Minorista"
            case "En sitio":
                return -1; // Sin límite para "En sitio"
            default:
                return -1; // Sin límite por defecto
        }
    }
}
