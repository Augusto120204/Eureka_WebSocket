package ec.edu.monster.climov_eurekabank_soapjava_g02.model.data.api;

import static ec.edu.monster.climov_eurekabank_soapjava_g02.utils.SoapFaultManager.parseSoapFault;
import java.util.List;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods.CuentasRequestEnvelope;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods.CuentasResponseEnvelope;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods.DepositoRequestEnvelope;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods.DepositoResponseEnvelope;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods.EmpleadosRequestEnvelope;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods.EmpleadosResponseEnvelope;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods.LoginRequestEnvelope;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods.LoginResponseEnvelope;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods.MontoRequestEnvelope;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods.MontoResponseEnvelope;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods.MovimientoRequestEnvelope;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods.MovimientoResponseEnvelope;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods.RetiroRequestEnvelope;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods.RetiroResponseEnvelope;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods.TransferenciaRequestEnvelope;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods.TransferenciaResponseEnvelope;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SoapApiService {
    private final ISoapApiService apiService;

    public SoapApiService() {
        this.apiService = RetrofitClient.getApiService();
    }

    public interface SimpleCallBack{
        void onResult(boolean success, String resultMessage);
    }

    public interface MontoCallBack{
        void onResult(boolean success, Double monto, String resultMessage);
    }

    public interface MovimientosCallBack{
        void onResult(boolean success, List<MovimientoResponseEnvelope.Movimiento> movimientos, String resultMessage);
    }

    public void performLogin(String user, String password, final SimpleCallBack callback) {
        LoginRequestEnvelope requestEnvelope = new LoginRequestEnvelope(user, password);

        apiService.login(requestEnvelope).enqueue(new Callback<LoginResponseEnvelope>() {
            @Override
            public void onResponse(Call<LoginResponseEnvelope> call, Response<LoginResponseEnvelope> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        int result = response.body().body.loginResponse.result;
                        if(result == 1){
                            callback.onResult(true, null);
                        } else if (result == -1) {
                            callback.onResult(false, "Usuario no registrado/activo");
                        } else{
                            callback.onResult(false, "Error en el proceso de login. Server Cod: "+result);
                        }
                    } catch (Exception e) {
                        callback.onResult(false, "Error parsing response: " + e.getMessage());
                    }
                } else {
                    callback.onResult(false, "Error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponseEnvelope> call, Throwable t) {
                callback.onResult(false, "Conexión fallida: " + t.getMessage());
            }
        });
    }

    public void performRetiro(String cuenta, double importe, final SimpleCallBack callback) {
        RetiroRequestEnvelope requestEnvelope = new RetiroRequestEnvelope(cuenta, importe);

        apiService.retiro(requestEnvelope).enqueue(new Callback<RetiroResponseEnvelope>() {
            @Override
            public void onResponse(Call<RetiroResponseEnvelope> call, Response<RetiroResponseEnvelope> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Usar getResult() que ya tiene las validaciones internas
                    int resultado = (int) response.body().getResult();

                    if (resultado == 1) {
                        // Éxito
                        callback.onResult(true, "Retiro realizado con éxito");
                    } else {
                        // 0 = estructura incorrecta/vacía, otro número = código de error del servidor
                        callback.onResult(false, resultado == 0 ?
                                "Respuesta inválida del servidor" :
                                "Retiro no completado. Código: " + resultado);
                    }
                } else {
                    // El servidor envió un Fault
                    String errorMsg = parseSoapFault(response.errorBody());
                    callback.onResult(false, errorMsg);
                }
            }

            @Override
            public void onFailure(Call<RetiroResponseEnvelope> call, Throwable t) {
                callback.onResult(false, "Conexión fallida: " + t.getMessage());
            }
        });
    }
    public void performDeposito(String cuenta, double importe, final SimpleCallBack callback) {
        DepositoRequestEnvelope requestEnvelope = new DepositoRequestEnvelope(cuenta, importe);

        apiService.deposito(requestEnvelope).enqueue(new Callback<DepositoResponseEnvelope>() {
            @Override
            public void onResponse(Call<DepositoResponseEnvelope> call, Response<DepositoResponseEnvelope> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Usar getResult() que ya tiene las validaciones internas
                    int resultado = (int) response.body().getResult();

                    if (resultado == 1) {
                        // Éxito
                        callback.onResult(true, "Depósito realizado con éxito");
                    } else {
                        // 0 = estructura incorrecta/vacía, otro número = código de error del servidor
                        callback.onResult(false, resultado == 0 ?
                                "Respuesta inválida del servidor" :
                                "Depósito no completado. Código: " + resultado);
                    }
                } else {
                    // El servidor envió un Fault
                    String errorMsg = parseSoapFault(response.errorBody());
                    callback.onResult(false, errorMsg);
                }
            }

            @Override
            public void onFailure(Call<DepositoResponseEnvelope> call, Throwable t) {
                callback.onResult(false, "Conexión fallida: " + t.getMessage());
            }
        });
    }

    public void performTraerMonto(String cuenta, final MontoCallBack callback) {
        MontoRequestEnvelope montoRequestEnvelope = new MontoRequestEnvelope(cuenta);
        MovimientoRequestEnvelope movimientoRequestEnvelope = new MovimientoRequestEnvelope(cuenta);

        apiService.traerMonto(montoRequestEnvelope).enqueue(new Callback<MontoResponseEnvelope>() {
            @Override
            public void onResponse(Call<MontoResponseEnvelope> call, Response<MontoResponseEnvelope> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getBody() != null && response.body().getBody().getMontoData() != null) {
                        double resultado = response.body().getResult();
                        callback.onResult(true, resultado, "Consulta de monto exitosa");
                    }else{
                        callback.onResult(false, null,"Respuesta inválida del servidor");
                    }
                } else {
                    // El servidor envió un Fault
                    String errorMsg = parseSoapFault(response.errorBody());
                    callback.onResult(false, null, errorMsg);
                }
            }

            @Override
            public void onFailure(Call<MontoResponseEnvelope> call, Throwable t) {
                callback.onResult(false, null,"Conexión fallida: " + t.getMessage());
            }
        });
    }

    public void performTraerMovimientos(String cuenta, final MovimientosCallBack callback) {
        MovimientoRequestEnvelope movimientoRequestEnvelope = new MovimientoRequestEnvelope(cuenta);

        apiService.traerMovimientos(movimientoRequestEnvelope).enqueue(new Callback<MovimientoResponseEnvelope>() {
            @Override
            public void onResponse(Call<MovimientoResponseEnvelope> call, Response<MovimientoResponseEnvelope> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getBody() != null && response.body().getBody().getMovimientoData() != null) {
                        List<MovimientoResponseEnvelope.Movimiento> resultado = response.body().getMovimientos();
                        callback.onResult(true, resultado, "Consulta de movimientos exitosa");
                    }else{
                        callback.onResult(false, null,"Respuesta inválida del servidor");
                    }
                } else {
                    // El servidor envió un Fault
                    String errorMsg = parseSoapFault(response.errorBody());
                    callback.onResult(false, null, errorMsg);
                }
            }

            @Override
            public void onFailure(Call<MovimientoResponseEnvelope> call, Throwable t) {
                callback.onResult(false, null,"Conexión fallida: " + t.getMessage());
            }
        });
    }

    public void performTransferencia(String cuentaOrigen, String cuentaDestino,double importe, final SimpleCallBack callback) {
        TransferenciaRequestEnvelope requestEnvelope = new TransferenciaRequestEnvelope(cuentaOrigen, cuentaDestino, importe);

        apiService.transferencia(requestEnvelope).enqueue(new Callback<TransferenciaResponseEnvelope>() {
            @Override
            public void onResponse(Call<TransferenciaResponseEnvelope> call, Response<TransferenciaResponseEnvelope> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Usar getResult() que ya tiene las validaciones internas
                    int resultado = (int) response.body().getResult();

                    if (resultado == 1) {
                        // Éxito
                        callback.onResult(true, "Transferencia realizado con éxito");
                    } else {
                        // 0 = estructura incorrecta/vacía, otro número = código de error del servidor
                        callback.onResult(false, resultado == 0 ?
                                "Respuesta inválida del servidor" :
                                "Transferencia no completado. Código: " + resultado);
                    }
                } else {
                    // El servidor envió un Fault
                    String errorMsg = parseSoapFault(response.errorBody());
                    callback.onResult(false, errorMsg);
                }
            }

            @Override
            public void onFailure(Call<TransferenciaResponseEnvelope> call, Throwable t) {
                callback.onResult(false, "Conexión fallida: " + t.getMessage());
            }
        });
    }

    // Callback para empleados
    public interface EmpleadosCallBack {
        void onResult(boolean success, List<EmpleadosResponseEnvelope.Empleado> empleados, String resultMessage);
    }

    // Callback para cuentas
    public interface CuentasCallBack {
        void onResult(boolean success, List<String> cuentas, String resultMessage);
    }

    public void performTraerEmpleados(final EmpleadosCallBack callback) {
        EmpleadosRequestEnvelope requestEnvelope = new EmpleadosRequestEnvelope();

        apiService.traerEmpleados(requestEnvelope).enqueue(new Callback<EmpleadosResponseEnvelope>() {
            @Override
            public void onResponse(Call<EmpleadosResponseEnvelope> call, Response<EmpleadosResponseEnvelope> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EmpleadosResponseEnvelope.Empleado> empleados = response.body().getEmpleados();
                    if (empleados != null) {
                        callback.onResult(true, empleados, "Consulta de empleados exitosa");
                    } else {
                        callback.onResult(false, null, "Respuesta invalida del servidor");
                    }
                } else {
                    String errorMsg = parseSoapFault(response.errorBody());
                    callback.onResult(false, null, errorMsg);
                }
            }

            @Override
            public void onFailure(Call<EmpleadosResponseEnvelope> call, Throwable t) {
                callback.onResult(false, null, "Conexion fallida: " + t.getMessage());
            }
        });
    }

    public void performTraerCuentas(final CuentasCallBack callback) {
        CuentasRequestEnvelope requestEnvelope = new CuentasRequestEnvelope();

        apiService.traerCuentas(requestEnvelope).enqueue(new Callback<CuentasResponseEnvelope>() {
            @Override
            public void onResponse(Call<CuentasResponseEnvelope> call, Response<CuentasResponseEnvelope> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> cuentas = response.body().getCuentas();
                    if (cuentas != null) {
                        callback.onResult(true, cuentas, "Consulta de cuentas exitosa");
                    } else {
                        callback.onResult(false, null, "Respuesta invalida del servidor");
                    }
                } else {
                    String errorMsg = parseSoapFault(response.errorBody());
                    callback.onResult(false, null, errorMsg);
                }
            }

            @Override
            public void onFailure(Call<CuentasResponseEnvelope> call, Throwable t) {
                callback.onResult(false, null, "Conexion fallida: " + t.getMessage());
            }
        });
    }
}
