package com.example.maiz_final;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class RecibosAdapter extends RecyclerView.Adapter<RecibosAdapter.RecibosViewHolder> {

    private List<Pedido> pedidos;

    public RecibosAdapter(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    @NonNull
    @Override
    public RecibosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recibo, parent, false);
        return new RecibosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecibosViewHolder holder, int position) {
        Pedido pedido = pedidos.get(position);
        holder.tvIdPedido.setText("Pedido ID: " + pedido.getId());
        holder.tvCliente.setText("Cliente: " + pedido.getCliente());
        holder.tvTipoEntrega.setText("Tipo de entrega: " + pedido.getTipoEntrega());

        // Formatear los productos
        holder.tvProductos.setText(formatProductos(pedido.getProductos()));
    }


    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public static class RecibosViewHolder extends RecyclerView.ViewHolder {
        TextView tvIdPedido, tvCliente, tvTipoEntrega, tvProductos;

        public RecibosViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIdPedido = itemView.findViewById(R.id.tvIdPedido); // Verifica que exista en item_recibo.xml
            tvCliente = itemView.findViewById(R.id.tvCliente); // Verifica que exista en item_recibo.xml
            tvTipoEntrega = itemView.findViewById(R.id.tvTipoEntrega); // Verifica que exista en item_recibo.xml
            tvProductos = itemView.findViewById(R.id.tvProductos); // Verifica que exista en item_recibo.xml
        }
    }


    private String formatProductos(List<Map<String, Object>> productos) {
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> producto : productos) {
            String nombre = (String) producto.get("nombre");
            Long cantidad = (Long) producto.get("cantidad");
            sb.append(nombre).append(" (").append(cantidad).append(")").append("\n");
        }
        return sb.toString();
    }
}
