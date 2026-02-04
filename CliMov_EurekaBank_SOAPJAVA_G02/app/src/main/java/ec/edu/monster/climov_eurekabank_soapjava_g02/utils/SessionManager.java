package ec.edu.monster.climov_eurekabank_soapjava_g02.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class SessionManager {
    private static final String PREF_NAME = "EurekaSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_WS_SESSION_ID = "wsSessionId";
    private static final String KEY_CAJERO_ID = "cajeroId";
    private static final String KEY_CAJERO_NOMBRE = "cajeroNombre";
    private static final String KEY_CUENTA_SELECCIONADA = "cuentaSeleccionada";
    private static final String KEY_OPERACION_ACTUAL = "operacionActual";
    private static final String KEY_CUENTA_OPERACION = "cuentaOperacion";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String username) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

    // --- WebSocket Session ID ---
    public String getWsSessionId() {
        String sessionId = pref.getString(KEY_WS_SESSION_ID, null);
        if (sessionId == null) {
            sessionId = "session-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
            editor.putString(KEY_WS_SESSION_ID, sessionId);
            editor.commit();
        }
        return sessionId;
    }

    public void clearWsSessionId() {
        editor.remove(KEY_WS_SESSION_ID);
        editor.commit();
    }

    // --- Cajero ---
    public void setCajero(String cajeroId, String cajeroNombre) {
        editor.putString(KEY_CAJERO_ID, cajeroId);
        editor.putString(KEY_CAJERO_NOMBRE, cajeroNombre);
        editor.commit();
    }

    public String getCajeroId() {
        return pref.getString(KEY_CAJERO_ID, null);
    }

    public String getCajeroNombre() {
        return pref.getString(KEY_CAJERO_NOMBRE, null);
    }

    public boolean hasCajeroSelected() {
        return getCajeroId() != null && !getCajeroId().isEmpty();
    }

    public void clearCajero() {
        editor.remove(KEY_CAJERO_ID);
        editor.remove(KEY_CAJERO_NOMBRE);
        editor.commit();
    }

    // --- Cuenta ---
    public void setCuentaSeleccionada(String cuenta) {
        editor.putString(KEY_CUENTA_SELECCIONADA, cuenta);
        editor.commit();
    }

    public String getCuentaSeleccionada() {
        return pref.getString(KEY_CUENTA_SELECCIONADA, null);
    }

    public boolean hasCuentaSelected() {
        return getCuentaSeleccionada() != null && !getCuentaSeleccionada().isEmpty();
    }

    public void clearCuenta() {
        editor.remove(KEY_CUENTA_SELECCIONADA);
        editor.commit();
    }

    // --- Operación actual (para bloqueo) ---
    public void setOperacionActual(String operacion, String cuenta) {
        editor.putString(KEY_OPERACION_ACTUAL, operacion);
        editor.putString(KEY_CUENTA_OPERACION, cuenta);
        editor.commit();
    }

    public String getOperacionActual() {
        return pref.getString(KEY_OPERACION_ACTUAL, null);
    }

    public String getCuentaOperacion() {
        return pref.getString(KEY_CUENTA_OPERACION, null);
    }

    public void clearOperacionActual() {
        editor.remove(KEY_OPERACION_ACTUAL);
        editor.remove(KEY_CUENTA_OPERACION);
        editor.commit();
    }

    // --- Limpiar datos de sesión bancaria (pero mantener login) ---
    public void clearBankingSession() {
        editor.remove(KEY_CAJERO_ID);
        editor.remove(KEY_CAJERO_NOMBRE);
        editor.remove(KEY_CUENTA_SELECCIONADA);
        editor.remove(KEY_OPERACION_ACTUAL);
        editor.remove(KEY_CUENTA_OPERACION);
        editor.commit();
    }
}
