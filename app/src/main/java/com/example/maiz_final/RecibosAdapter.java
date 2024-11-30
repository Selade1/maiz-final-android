package com.example.maiz_final;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class RecibosAdapter extends RecyclerView.Adapter<RecibosAdapter.RecibosViewHolder> {

    private List<Pedido> pedidos;
    private Context context;

    public RecibosAdapter(Context context, List<Pedido> pedidos) {
        this.context = context;
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
        holder.tvProductos.setText(formatProductos(pedido.getProductos()));

        // Configurar botón "Cerrar pedido"
        holder.btnCerrarPedido.setOnClickListener(v -> {
            Intent intent = new Intent(context, CerrarPedidoActivity.class);
            intent.putExtra("pedidoId", pedido.getId());
            context.startActivity(intent);
        });

        // Configurar botón "Cancelar pedido"
        holder.btnCancelarPedido.setOnClickListener(v -> {
            Intent intent = new Intent(context, CancelarPedidoActivity.class);
            intent.putExtra("pedidoId", pedido.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public static class RecibosViewHolder extends RecyclerView.ViewHolder {
        TextView tvIdPedido, tvCliente, tvTipoEntrega, tvProductos;
        Button btnCerrarPedido, btnCancelarPedido;

        public RecibosViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIdPedido = itemView.findViewById(R.id.tvIdPedido);
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvTipoEntrega = itemView.findViewById(R.id.tvTipoEntrega);
            tvProductos = itemView.findViewById(R.id.tvProductos);
            btnCerrarPedido = itemView.findViewById(R.id.btnCerrarPedido);
            btnCancelarPedido = itemView.findViewById(R.id.btnCancelarPedido);
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
