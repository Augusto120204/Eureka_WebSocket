package ec.edu.monster.climov_eurekabank_soapjava_g02.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ec.edu.monster.climov_eurekabank_soapjava_g02.R;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods.MovimientoResponseEnvelope;

public class MovimientoAdapter extends RecyclerView.Adapter<MovimientoAdapter.MovimientoViewHolder>{
    private Context context;
    private List<MovimientoResponseEnvelope.Movimiento> movimientos;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;

    public MovimientoAdapter(Context context, List<MovimientoResponseEnvelope.Movimiento> movimientos) {
        this.context = context;
        this.movimientos = movimientos;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "EC"));
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public MovimientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movimiento, parent, false);
        return new MovimientoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovimientoViewHolder holder, int position) {
        MovimientoResponseEnvelope.Movimiento movimiento = movimientos.get(position);

        // Número de movimiento
        holder.tvNumeroMovimiento.setText(String.valueOf(movimiento.getNromov()));

        // Fecha (formatea según tu necesidad)
        try {
            Date fecha = dateFormat.parse(movimiento.getFecha());
            holder.tvFecha.setText(dateFormat.format(fecha));
        } catch (Exception e) {
            holder.tvFecha.setText(movimiento.getFecha());
        }

        // Tipo de movimiento
        holder.tvTipo.setText(movimiento.getTipo());

        // Importe
        String importeFormateado = currencyFormat.format(movimiento.getImporte());
        holder.tvImporte.setText(importeFormateado);

        // Colorear según tipo (depósito = verde, retiro = rojo)
        if (movimiento.getTipo() != null &&
                (movimiento.getTipo().toLowerCase().contains("depósito") ||
                        movimiento.getTipo().toLowerCase().contains("deposito"))) {
            holder.tvImporte.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.tvImporte.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    @Override
    public int getItemCount() {
        return movimientos != null ? movimientos.size() : 0;
    }

    public void setMovimientos(List<MovimientoResponseEnvelope.Movimiento> movimientos) {
        this.movimientos = movimientos;
        notifyDataSetChanged();
    }

    static class MovimientoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumeroMovimiento, tvFecha, tvTipo, tvImporte;

        public MovimientoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumeroMovimiento = itemView.findViewById(R.id.tv_numero_movimiento);
            tvFecha = itemView.findViewById(R.id.tv_fecha);
            tvTipo = itemView.findViewById(R.id.tv_tipo);
            tvImporte = itemView.findViewById(R.id.tv_importe);
        }
    }
}
