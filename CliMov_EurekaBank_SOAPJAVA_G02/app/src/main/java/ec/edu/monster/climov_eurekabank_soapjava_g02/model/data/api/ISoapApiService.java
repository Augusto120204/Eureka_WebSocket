package ec.edu.monster.climov_eurekabank_soapjava_g02.model.data.api;

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
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ISoapApiService {
    String SERVICE_PATH = "WSEureka";

    @Headers({
            "Content-Type: text/xml; charset=utf-8",
            "SOAPAction: \"\""
    })

    @POST(SERVICE_PATH)
    Call<LoginResponseEnvelope> login(@Body LoginRequestEnvelope requestEnvelope);
    @POST(SERVICE_PATH)
    Call<RetiroResponseEnvelope> retiro(@Body RetiroRequestEnvelope requestEnvelope);
    @POST(SERVICE_PATH)
    Call<DepositoResponseEnvelope> deposito(@Body DepositoRequestEnvelope requestEnvelope);
    @POST(SERVICE_PATH)
    Call<MovimientoResponseEnvelope> traerMovimientos(@Body MovimientoRequestEnvelope requestEnvelope);
    @POST(SERVICE_PATH)
    Call<MontoResponseEnvelope> traerMonto(@Body MontoRequestEnvelope requestEnvelope);
    @POST(SERVICE_PATH)
    Call<TransferenciaResponseEnvelope> transferencia(@Body TransferenciaRequestEnvelope requestEnvelope);
    @POST(SERVICE_PATH)
    Call<EmpleadosResponseEnvelope> traerEmpleados(@Body EmpleadosRequestEnvelope requestEnvelope);
    @POST(SERVICE_PATH)
    Call<CuentasResponseEnvelope> traerCuentas(@Body CuentasRequestEnvelope requestEnvelope);
}