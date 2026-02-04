package ec.edu.monster.climov_eurekabank_soapjava_g02.model.data.rest;

import ec.edu.monster.climov_eurekabank_soapjava_g02.model.dto.ApiResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Interface para las llamadas REST al servidor Spring Boot.
 */
public interface IRestApiService {
    
    @FormUrlEncoded
    @POST("api/estado/cajero/ocupar")
    Call<ApiResponse> ocuparCajero(
        @Field("cajeroId") String cajeroId,
        @Field("cajeroNombre") String cajeroNombre,
        @Field("sessionId") String sessionId
    );
    
    @FormUrlEncoded
    @POST("api/estado/cajero/liberar")
    Call<ApiResponse> liberarCajero(@Field("cajeroId") String cajeroId);
    
    @FormUrlEncoded
    @POST("api/estado/operacion/bloquear")
    Call<ApiResponse> bloquearOperacion(
        @Field("cuenta") String cuenta,
        @Field("operacion") String operacion,
        @Field("sessionId") String sessionId
    );
    
    @FormUrlEncoded
    @POST("api/estado/operacion/liberar")
    Call<ApiResponse> liberarOperacion(
        @Field("cuenta") String cuenta,
        @Field("operacion") String operacion,
        @Field("sessionId") String sessionId
    );
}
