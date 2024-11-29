package com.example.maiz_final;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class VehiculosAdapter extends RecyclerView.Adapter<VehiculosAdapter.VehiculoViewHolder> {

    private ArrayList<String> listaVehiculos;
    private OnVehiculoClickListener listener;

    public interface OnVehiculoClickListener {
        void onVehiculoClick(String vehiculo);
    }

    public VehiculosAdapter(ArrayList<String> listaVehiculos, OnVehiculoClickListener listener) {
        this.listaVehiculos = listaVehiculos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VehiculoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehiculo, parent, false);
        return new VehiculoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehiculoViewHolder holder, int position) {
        String vehiculo = listaVehiculos.get(position);
        holder.tvVehiculo.setText(vehiculo);

        holder.itemView.setOnClickListener(v -> listener.onVehiculoClick(vehiculo));
    }

    @Override
    public int getItemCount() {
        return listaVehiculos.size();
    }

    class VehiculoViewHolder extends RecyclerView.ViewHolder {
        TextView tvVehiculo;

        public VehiculoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVehiculo = itemView.findViewById(R.id.tvVehiculo);
        }
    }
}
