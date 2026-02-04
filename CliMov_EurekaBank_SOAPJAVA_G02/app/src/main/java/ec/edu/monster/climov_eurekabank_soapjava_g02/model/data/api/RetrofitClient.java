package ec.edu.monster.climov_eurekabank_soapjava_g02.model.data.api;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import okhttp3.logging.HttpLoggingInterceptor;

public class RetrofitClient {
    // URL BASE - IMPORTANTE: Cambiar según tu configuración
    // Para emulador Android Studio: usa 10.0.2.2
    // Para dispositivo físico: usa tu IP local (ejemplo: 192.168.1.10)
    private static final String BASE_URL = "http://10.0.2.2:8080/WS_EUREKABANK_SoapJava_G02/";
    private static ISoapApiService apiService = null;

    public static ISoapApiService getApiService() {
        if (apiService == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(new SoapConverterFactory())
                    .build();

            apiService = retrofit.create(ISoapApiService.class);
        }
        return apiService;
    }
}