package com.example.maiz_final;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class OrdenesPendientesAdapter extends RecyclerView.Adapter<OrdenesPendientesAdapter.OrdenPendienteViewHolder> {

    private final List<OrdenPendiente> ordenesPendientes;

    public OrdenesPendientesAdapter(List<OrdenPendiente> ordenesPendientes) {
        this.ordenesPendientes = ordenesPendientes;
    }

    @NonNull
    @Override
    public OrdenPendienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_orden_pendiente, parent, false);
        return new OrdenPendienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdenPendienteViewHolder holder, int position) {
        OrdenPendiente orden = ordenesPendientes.get(position);

        holder.tvOrden.setText(orden.getTipo() + ": " + orden.getCliente());

        holder.itemView.setOnClickListener(v -> {
            if (holder.detalleLayout.getVisibility() == View.VISIBLE) {
                holder.detalleLayout.setVisibility(View.GONE); // Ocultar detalles
            } else {
                StringBuilder detalles = new StringBuilder();

                // Información del cliente
                detalles.append("Cliente: ").append(orden.getCliente()).append("\n");
                detalles.append("Dirección: ").append(orden.getDireccion() != null ? orden.getDireccion() : "N/A").append("\n");
                detalles.append("Teléfono: ").append(orden.getTelefono() != null ? orden.getTelefono() : "N/A").append("\n");
                detalles.append("Correo: ").append(orden.getCorreo() != null ? orden.getCorreo() : "N/A").append("\n");

                // Información específica de pedidos/envíos
                if ("Pedido".equals(orden.getTipo())) {
                    detalles.append("Tipo de Entrega: ").append(orden.getTipoEntrega()).append("\n");
                } else if ("Envío".equals(orden.getTipo())) {
                    detalles.append("Matrícula Camión: ").append(orden.getIdCamion()).append("\n");
                    detalles.append("Fecha: ").append(orden.getFecha()).append("\n");
                }

                // Información de los productos
                detalles.append("\nProductos:\n");
                for (Map<String, Object> producto : orden.getProductos()) {
                    detalles.append("- ").append(producto.get("nombre"))
                            .append(" (Cantidad: ").append(producto.get("cantidad"))
                            .append(", Precio: $").append(producto.get("precio"))
                            .append(")\n");
                }

                holder.tvDetalle.setText(detalles.toString());
                holder.detalleLayout.setVisibility(View.VISIBLE); // Mostrar detalles
            }
        });
    }

    @Override
    public int getItemCount() {
        return ordenesPendientes.size();
    }

    static class OrdenPendienteViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrden;
        TextView tvDetalle;
        LinearLayout detalleLayout;

        public OrdenPendienteViewHolder(@NonNull View itemView) {
            super(itemView);

            // Asocia las vistas del XML con las variables
            tvOrden = itemView.findViewById(R.id.tvOrden);
            tvDetalle = itemView.findViewById(R.id.tvDetalle); // ID del TextView para los detalles
            detalleLayout = itemView.findViewById(R.id.detalleLayout); // ID del layout contenedor de los detalles
        }
    }
}
