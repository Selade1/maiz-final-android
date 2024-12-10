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
                    .into(ivProducto);

            // Configurar acciones de los botones
            btnAdd.setOnClickListener(v -> {
                int limite = getLimiteCantidad();
                int cantidadSeleccionada = carrito.getOrDefault(producto, 0); // Cantidad seleccionada del producto en el carrito
                int totalSeleccionados = cantidadCarrito(); // Cantidad total seleccionada en el carrito

                // Verificar el límite para Flete
                if (tipoEntrega.equals("Flete") && totalSeleccionados >= limite) {
                    Toast.makeText(itemView.getContext(), "Límite de 50 productos alcanzado para Flete.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Verificar que no exceda el stock del producto
                if (cantidadSeleccionada < producto.getStock()) {
                    carrito.put(producto, cantidadSeleccionada + 1);
                    tvCantidadProducto.setText(String.valueOf(carrito.get(producto)));
                    tvStockProducto.setText("Stock: " + (producto.getStock() - carrito.get(producto))); // Mostrar el stock restante en la interfaz
                } else {
                    Toast.makeText(itemView.getContext(), "Stock insuficiente.", Toast.LENGTH_SHORT).show();
                }
            });

            btnRemove.setOnClickListener(v -> {
                int cantidadSeleccionada = carrito.getOrDefault(producto, 0);

                if (cantidadSeleccionada > 0) {
                    carrito.put(producto, cantidadSeleccionada - 1);
                    if (carrito.get(producto) == 0) {
                        carrito.remove(producto);
                    }
                    tvCantidadProducto.setText(String.valueOf(carrito.getOrDefault(producto, 0)));
                    tvStockProducto.setText("Stock: " + (producto.getStock() - carrito.getOrDefault(producto, 0))); // Mostrar el stock restante en la interfaz
                }
            });
        }

        private int cantidadCarrito() {
            // Suma todas las cantidades seleccionadas en el carrito
            return carrito.values().stream().mapToInt(Integer::intValue).sum();
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
