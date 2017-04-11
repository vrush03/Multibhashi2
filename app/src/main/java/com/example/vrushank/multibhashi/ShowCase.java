package com.example.vrushank.multibhashi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.vrushank.multibhashi.Model.ApiResponse;
import com.example.vrushank.multibhashi.Model.Model;
import com.example.vrushank.multibhashi.Rest.ApiClient;
import com.example.vrushank.multibhashi.Rest.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowCase extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_case);

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getData();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                List<Model> models = response.body().getData();
                for (int i = 0; i < models.size(); i++)
                    Log.d("log", models.get(i).getAudio());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }
}
