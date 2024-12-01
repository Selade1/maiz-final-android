package com.example.maiz_final;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrdenesPendientesAdapter extends RecyclerView.Adapter<OrdenesPendientesAdapter.OrdenPendienteViewHolder> {

    private final Context context;
    private final List<OrdenPendiente> ordenesPendientes;

    public OrdenesPendientesAdapter(Context context, List<OrdenPendiente> ordenesPendientes) {
        this.context = context;
        this.ordenesPendientes = ordenesPendientes;
    }

    @NonNull
    @Override
    public OrdenPendienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_orden_pendiente_expandible, parent, false);
        return new OrdenPendienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdenPendienteViewHolder holder, int position) {
        OrdenPendiente orden = ordenesPendientes.get(position);

        holder.tvOrden.setText(orden.getTipo() + ": " + orden.getCliente() + " (" + orden.getId() + ")");

        holder.itemView.setOnClickListener(v -> {
            if (holder.detalleLayout.getVisibility() == View.VISIBLE) {
                holder.detalleLayout.setVisibility(View.GONE);
            } else {
                StringBuilder detalles = new StringBuilder();
                detalles.append("Cliente: ").append(orden.getCliente()).append("\n");
                detalles.append("Dirección: ").append(orden.getDireccion() != null ? orden.getDireccion() : "N/A").append("\n");
                detalles.append("Teléfono: ").append(orden.getTelefono() != null ? orden.getTelefono() : "N/A").append("\n");
                detalles.append("Correo: ").append(orden.getCorreo() != null ? orden.getCorreo() : "N/A").append("\n");

                if ("Pedido".equals(orden.getTipo())) {
                    detalles.append("Tipo de Entrega: ").append(orden.getTipoEntrega()).append("\n");
                } else if ("Envío".equals(orden.getTipo())) {
                    detalles.append("Matrícula Camión: ").append(orden.getIdCamion()).append("\n");
                    detalles.append("Fecha: ").append(orden.getFecha()).append("\n");
                }

                detalles.append("\nProductos:\n");
                for (Map<String, Object> producto : orden.getProductos()) {
                    detalles.append("- ").append(producto.get("nombre"))
                            .append(" (Cantidad: ").append(producto.get("cantidad"))
                            .append(", Precio: $").append(producto.get("precio"))
                            .append(")\n");
                }

                holder.tvDetalle.setText(detalles.toString());
                holder.detalleLayout.setVisibility(View.VISIBLE);
            }
        });

        holder.btnCerrar.setOnClickListener(v -> {
            Intent intent = new Intent(context, CerrarPedidoActivity.class);
            intent.putExtra("idOrden", orden.getId());
            intent.putExtra("cliente", orden.getCliente());
            intent.putExtra("direccion", orden.getDireccion());
            intent.putExtra("telefono", orden.getTelefono());
            intent.putExtra("correo", orden.getCorreo());
            intent.putExtra("tipo", orden.getTipo());
            intent.putExtra("tipoEntrega", orden.getTipoEntrega());
            intent.putExtra("idCamion", orden.getIdCamion());
            intent.putExtra("fecha", orden.getFecha());
            intent.putExtra("productos", new ArrayList<>(orden.getProductos())); // Convertir a ArrayList para serialización
            context.startActivity(intent);
        });

        holder.btnCancelar.setOnClickListener(v -> {
            Intent intent = new Intent(context, CancelarPedidoActivity.class);
            intent.putExtra("idOrden", orden.getId());
            intent.putExtra("cliente", orden.getCliente());
            intent.putExtra("direccion", orden.getDireccion());
            intent.putExtra("telefono", orden.getTelefono());
            intent.putExtra("correo", orden.getCorreo());
            intent.putExtra("tipo", orden.getTipo());
            intent.putExtra("tipoEntrega", orden.getTipoEntrega());
            intent.putExtra("idCamion", orden.getIdCamion());
            intent.putExtra("fecha", orden.getFecha());
            intent.putExtra("productos", new ArrayList<>(orden.getProductos())); // Convertir a ArrayList para serialización
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return ordenesPendientes.size();
    }

    static class OrdenPendienteViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrden, tvDetalle;
        LinearLayout detalleLayout;
        Button btnCerrar, btnCancelar;

        public OrdenPendienteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrden = itemView.findViewById(R.id.tvOrden);
            tvDetalle = itemView.findViewById(R.id.tvDetalles);
            detalleLayout = itemView.findViewById(R.id.containerDetalles);
            btnCerrar = itemView.findViewById(R.id.btnCerrarPedido);
            btnCancelar = itemView.findViewById(R.id.btnCancelarPedido);
        }
    }
}
