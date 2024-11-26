package com.example.maiz_final;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
        private Button btnAdd, btnRemove;

        public CatalogoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreProducto = itemView.findViewById(R.id.tvNombreProducto);
            tvPrecioProducto = itemView.findViewById(R.id.tvPrecioProducto);
            tvStockProducto = itemView.findViewById(R.id.tvStockProducto);
            tvCantidadProducto = itemView.findViewById(R.id.tvCantidadProducto);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }

        public void bind(Producto producto) {
            // Usar el método correcto de la clase Producto
            tvNombreProducto.setText(producto.getNombre());
            tvPrecioProducto.setText("Precio: $" + producto.getPrecio());
            tvStockProducto.setText("Stock: " + producto.getStock());

            final int[] cantidad = {carrito.getOrDefault(producto, 0)};
            tvCantidadProducto.setText(String.valueOf(cantidad[0]));

            btnAdd.setOnClickListener(v -> {
                int limite = getLimiteCantidad();
                if (cantidad[0] < producto.getStock() && (limite == -1 || carrito.values().stream().mapToInt(Integer::intValue).sum() < limite)) {
                    cantidad[0]++;
                    carrito.put(producto, cantidad[0]);
                    producto.setStock(producto.getStock() - 1);
                    tvCantidadProducto.setText(String.valueOf(cantidad[0]));
                    tvStockProducto.setText("Stock: " + producto.getStock());
                } else {
                    Toast.makeText(itemView.getContext(), "Límite alcanzado para " + tipoEntrega, Toast.LENGTH_SHORT).show();
                }
            });

            btnRemove.setOnClickListener(v -> {
                if (cantidad[0] > 0) {
                    cantidad[0]--;
                    if (cantidad[0] == 0) {
                        carrito.remove(producto);
                    } else {
                        carrito.put(producto, cantidad[0]);
                    }
                    producto.setStock(producto.getStock() + 1);
                    tvCantidadProducto.setText(String.valueOf(cantidad[0]));
                    tvStockProducto.setText("Stock: " + producto.getStock());
                }
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
