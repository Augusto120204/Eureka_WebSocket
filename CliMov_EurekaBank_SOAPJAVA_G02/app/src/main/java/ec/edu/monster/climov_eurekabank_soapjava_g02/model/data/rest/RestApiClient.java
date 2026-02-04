package ec.edu.monster.climov_eurekabank_soapjava_g02.model.data.rest;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Cliente Retrofit para API REST del servidor Spring Boot.
 */
public class RestApiClient {
    // URL del cliente web Spring Boot (donde esta el API REST)
    // Para emulador: 10.0.2.2
    // Para dispositivo fisico: usar IP local
    private static final String BASE_URL = "http://10.0.2.2:8081/";
    
    private static IRestApiService apiService = null;
    private static String customBaseUrl = null;
    
    public static void setBaseUrl(String url) {
        customBaseUrl = url;
        apiService = null; // Forzar recreacion
    }
    
    public static String getBaseUrl() {
        return customBaseUrl != null ? customBaseUrl : BASE_URL;
    }
    
    public static IRestApiService getApiService() {
        if (apiService == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
            
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            
            apiService = retrofit.create(IRestApiService.class);
        }
        return apiService;
    }
}
