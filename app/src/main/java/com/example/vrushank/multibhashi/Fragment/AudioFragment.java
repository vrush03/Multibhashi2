package com.example.vrushank.multibhashi.Fragment;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vrushank.multibhashi.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class AudioFragment extends Fragment {

    String file_url, downloadVersion;
    String audio = "";
    String idStr = "";
    String descStr = "";

    public AudioFragment() {
        // Required empty public constructor
    }

    MediaPlayer mediaPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio, container, false);
        TextView id, desc;
        id = (TextView) view.findViewById(R.id.id);
        desc = (TextView) view.findViewById(R.id.desc);
        audio = getArguments().getString("audio");
        idStr = getArguments().getString("id");
        descStr = getArguments().getString("desc");
        id.setText(idStr);
        desc.setText(descStr);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mediaPlayer != null) mediaPlayer.stop();
    }

    public void playSong(){
        try {
            String d = descStr.replaceAll("\\s+", "");
            String uri = "/sdcard/Multibhashi/" + d + ".aac";
            File file = new File(uri);
            if (!file.exists()) {
                String url = getArguments().getString("audio");; // your URL here
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepare(); // might take long! (for buffering, etc)
                mediaPlayer.start();
                file_url = audio;
                downloadVersion = d;
                new DownloadFileFromURL().execute(file_url);
            } else {
                Uri playUri =  Uri.parse(uri); // initialize Uri here
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(getContext(), playUri);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {


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
