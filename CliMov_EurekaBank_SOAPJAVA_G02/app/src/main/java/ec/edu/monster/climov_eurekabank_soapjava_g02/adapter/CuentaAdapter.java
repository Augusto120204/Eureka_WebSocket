package ec.edu.monster.climov_eurekabank_soapjava_g02.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ec.edu.monster.climov_eurekabank_soapjava_g02.R;

public class CuentaAdapter extends RecyclerView.Adapter<CuentaAdapter.CuentaViewHolder> {

    private List<String> cuentas = new ArrayList<>();
    private OnCuentaClickListener listener;

    public interface OnCuentaClickListener {
        void onCuentaClick(String cuenta);
    }

    public void setOnCuentaClickListener(OnCuentaClickListener listener) {
        this.listener = listener;
    }

    public void setCuentas(List<String> cuentas) {
        this.cuentas = cuentas != null ? cuentas : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CuentaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cuenta, parent, false);
        return new CuentaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CuentaViewHolder holder, int position) {
        String cuenta = cuentas.get(position);
        holder.bind(cuenta);
    }

    @Override
    public int getItemCount() {
        return cuentas.size();
    }

    class CuentaViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView tvCuenta;

        CuentaViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tvCuenta = itemView.findViewById(R.id.tv_cuenta);
        }

        void bind(String cuenta) {
            tvCuenta.setText(cuenta);

            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCuentaClick(cuenta);
                }
            });
        }
    }
}
