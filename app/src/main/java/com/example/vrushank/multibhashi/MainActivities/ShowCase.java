package com.example.vrushank.multibhashi.MainActivities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.example.vrushank.multibhashi.Adapter.AudioAdapter;
import com.example.vrushank.multibhashi.AudioPresenter;
import com.example.vrushank.multibhashi.Fragment.AudioFragment;
import com.example.vrushank.multibhashi.Interfaces.ItemClickDownload;
import com.example.vrushank.multibhashi.Interfaces.ItemClickFragmentChange;
import com.example.vrushank.multibhashi.Model.ApiResponse;
import com.example.vrushank.multibhashi.Model.Model;
import com.example.vrushank.multibhashi.R;
import com.example.vrushank.multibhashi.Rest.ApiClient;
import com.example.vrushank.multibhashi.Rest.ApiInterface;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowCase extends AppCompatActivity implements AudioPresenter, ItemClickFragmentChange, ItemClickDownload {

    AudioAdapter adapter;
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    int i = 0;

    String downloadVersion;
    int size = 0;
    Model audioList[];
    private static String file_url;

    private AudioPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_case);
        File file = new File("/sdcard/Multibhashi");
        if (!file.exists())
            file.mkdir();
        recyclerView = (RecyclerView) findViewById(R.id.audioRecyclerView);
        manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();


        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getData();
        call.enqueue(new Callback<ApiResponse>() {

            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                List<Model> models = response.body().getData();

                audioList = new Model[models.size()];
                for (int i = 0; i < models.size(); i++) {
                    audioList[i] = new Model(models.get(i).getItemid(), models.get(i).getDesc(), models.get(i).getAudio());
                    //Log.d("log", models.get(i).getItemid() + " " + models.get(i).getDesc() + " " + models.get(i).getAudio());
                }
                size = models.size();
                i = 0;

                if (i + 1 < size) {
                    String d = audioList[i + 1].getDesc().replaceAll("\\s+", "");
                    String uri = "/sdcard/Multibhashi/" + d + ".aac";
                    File file = new File(uri);
                    if (!file.exists()) {
                        file_url = audioList[i + 1].getAudio();
                        new DownloadFileFromURL().execute(file_url);
                        downloadVersion = d;
                    }
                }
                //fragmentTransaction.replace(R.id.fragmentContainer, newInstance(audioList[0]), "Item Id " + audioList[0].getItemid()).commit();
                changeFragment(audioList[0]);
                loadSong(audioList[0]);
                //Log.d("Change Fragment", "called");
                adapter = new AudioAdapter(getApplicationContext(), audioList, ShowCase.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });

        final GestureDetector mGestureDetector = new GestureDetector(ShowCase.this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });


        LinearLayout next = (LinearLayout) findViewById(R.id.nextLayout);
        next.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                //Log.d("Next", "On Click");
                i++;
                if (i < size) {
                    if (i + 1 < size) {
                        String d = audioList[i + 1].getDesc().replaceAll("\\s+", "");
                        String uri = "/sdcard/Multibhashi/" + d + ".aac";
                        File file = new File(uri);
                        if (!file.exists()) {
                            file_url = audioList[i + 1].getAudio();
                            downloadFile(file_url);
                            downloadVersion = d;
                        }
                    }
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack("fragment");
                    AudioFragment fragment = newInstance(audioList[i]);
                    playSong(fragment);
                    fragmentTransaction.replace(R.id.fragmentContainer, fragment, "Item Id " + audioList[i].getItemid());
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                } else {
                    Snackbar.make(getCurrentFocus(), "There are no more Tracks", Snackbar.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
    public void downloadFile (String file_url) {
        new DownloadFileFromURL().execute(file_url);
    }

    public static AudioFragment newInstance(Model audio) {
        AudioFragment audioFragment = new AudioFragment();
        Bundle args = new Bundle();
        args.putString("audio", audio.getAudio());
        args.putString("id", audio.getItemid());
        args.putString("desc", audio.getDesc());

        audioFragment.setArguments(args);

        return audioFragment;
    }

    @Override
    public void changeFragment(Model model) {
        if (fragmentManager == null) return;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack("fragment");

        AudioFragment fragment = newInstance(model);
        fragmentTransaction.replace(R.id.fragmentContainer, fragment, "Item Id " + model.getItemid());
        playSong(fragment);
        Log.d("Play song", "called");
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }

    @Override
    public void loadSong(Model nextItem) {
        String d = nextItem.getDesc().replaceAll("\\s+", "");
        String uri = "/sdcard/Multibhashi/" + d + ".aac";
        File file = new File(uri);
        if (!file.exists()) {
            String file_url = nextItem.getAudio();

            new DownloadFileFromURL().execute(file_url);
            downloadVersion = d;
        }
    }

    @Override
    public void playSong(AudioFragment fragment) {
        if (fragment != null){
            fragment.playSong();
        }
    }


    public class DownloadFileFromURL extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                OutputStream output = new FileOutputStream("/sdcard/Multibhashi/" + downloadVersion + ".aac");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // setting progress percentage

        }

        @Override
        protected void onPostExecute(String file_url) {


        }

    }
}

