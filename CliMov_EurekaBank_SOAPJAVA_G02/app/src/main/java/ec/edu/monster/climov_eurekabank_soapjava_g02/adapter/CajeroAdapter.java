package ec.edu.monster.climov_eurekabank_soapjava_g02.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ec.edu.monster.climov_eurekabank_soapjava_g02.R;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods.EmpleadosResponseEnvelope;

public class CajeroAdapter extends RecyclerView.Adapter<CajeroAdapter.CajeroViewHolder> {

    private List<EmpleadosResponseEnvelope.Empleado> empleados = new ArrayList<>();
    private Set<String> cajerosOcupados = new HashSet<>();
    private OnCajeroClickListener listener;

    public interface OnCajeroClickListener {
        void onCajeroClick(EmpleadosResponseEnvelope.Empleado empleado);
    }

    public void setOnCajeroClickListener(OnCajeroClickListener listener) {
        this.listener = listener;
    }

    public void setEmpleados(List<EmpleadosResponseEnvelope.Empleado> empleados) {
        this.empleados = empleados != null ? empleados : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setCajerosOcupados(Set<String> cajerosOcupados) {
        this.cajerosOcupados = cajerosOcupados != null ? cajerosOcupados : new HashSet<>();
        notifyDataSetChanged();
    }

    public void actualizarCajeroOcupado(String cajeroId, boolean ocupado) {
        if (ocupado) {
            cajerosOcupados.add(cajeroId);
        } else {
            cajerosOcupados.remove(cajeroId);
        }
        // Encontrar la posicion y notificar
        for (int i = 0; i < empleados.size(); i++) {
            if (empleados.get(i).getCodigo().equals(cajeroId)) {
                notifyItemChanged(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public CajeroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cajero, parent, false);
        return new CajeroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CajeroViewHolder holder, int position) {
        EmpleadosResponseEnvelope.Empleado empleado = empleados.get(position);
        boolean ocupado = cajerosOcupados.contains(empleado.getCodigo());
        holder.bind(empleado, ocupado);
    }

    @Override
    public int getItemCount() {
        return empleados.size();
    }

    class CajeroViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView tvNombre;
        private final TextView tvCodigo;
        private final TextView tvCiudad;
        private final TextView tvEstado;
        private final FrameLayout iconContainer;

        CajeroViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvCodigo = itemView.findViewById(R.id.tv_codigo);
            tvCiudad = itemView.findViewById(R.id.tv_ciudad);
            tvEstado = itemView.findViewById(R.id.tv_estado);
            iconContainer = itemView.findViewById(R.id.icon_container);
        }

        void bind(EmpleadosResponseEnvelope.Empleado empleado, boolean ocupado) {
            tvNombre.setText(empleado.getNombreCompleto());
            tvCodigo.setText("Cajero " + empleado.getCodigo());
            tvCiudad.setText(empleado.getCiudad());

            if (ocupado) {
                tvEstado.setText("Ocupado");
                tvEstado.setBackgroundResource(R.drawable.status_ocupado);
                tvEstado.setTextColor(0xFFdc2626);
                cardView.setAlpha(0.6f);
                cardView.setClickable(false);
            } else {
                tvEstado.setText("Disponible");
                tvEstado.setBackgroundResource(R.drawable.status_disponible);
                tvEstado.setTextColor(0xFF15803d);
                cardView.setAlpha(1.0f);
                cardView.setClickable(true);
            }

            cardView.setOnClickListener(v -> {
                if (!ocupado && listener != null) {
                    listener.onCajeroClick(empleado);
                }
            });
        }
    }
}
