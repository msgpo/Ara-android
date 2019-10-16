/*
 * Copyright (c) 2019. Fulton Browne
 *  This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.andromeda.ara.voice;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.MicrophoneDirection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.andromeda.ara.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class VoiceMain extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO = 13;
    private static final String LOG_TAG = "v";
    private Thread recordingThread;
    MediaRecorder recorder = new MediaRecorder();
    File audiofile = null;
    TextToSpeech t1;
    boolean isRecording;
    boolean running = false;
    Boolean shouldContinue = true;
    private int recordingOffset = 0;
    private short[] recordingBuffer = new short[RECORDING_LENGTH];
    private final ReentrantLock recordingBufferLock = new ReentrantLock();
    private static final int SAMPLE_RATE = 16000;
    private static final int SAMPLE_DURATION_MS = 1000;
    private static final int RECORDING_LENGTH = SAMPLE_RATE * SAMPLE_DURATION_MS / 1000;
    int audioSource = MediaRecorder.AudioSource.MIC;
    int sampleRateInHz = 44100;
    int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    int bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);

    byte Data[] = new byte[bufferSizeInBytes];

    AudioRecord audioRecorder = new AudioRecord(audioSource,
            sampleRateInHz,
            channelConfig,
            audioFormat,
            bufferSizeInBytes);


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ActivityCompat.requestPermissions(VoiceMain.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
        requestMicrophonePermission();
        requestFilePermission();

        super.onCreate(savedInstanceState);
        startRecording();
       //AudioRecord audioRecord = new AudioRecord(MicrophoneDirection.MIC_DIRECTION_TOWARDS_USER,16000, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT,2048);

       //audioRecord.startRecording();

        setContentView(R.layout.activity_voice_main);
        Context ctx = this;
        String toSpeak = "hello, I am ara";
        //new TTS().start(getApplicationContext(), toSpeak);

        //Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();

        //String search = new DeepSpeech().run(getCacheDir()+"/main.mp3");
        //Toast.makeText(getApplicationContext(), search,Toast.LENGTH_SHORT).show();


        //String phrase = new run().run1(ctx, this);
        //Toast.makeText(ctx, phrase, Toast.LENGTH_LONG).show();
        //TODO get lat and log
        //ArrayList<RssFeedModel> toFeed = new Search().main(phrase, "0", "0");




    }

    public void back(View view) {
        if(isRecording){
            stopRecording();
        }
        else onBackPressed();

    }
    private void requestMicrophonePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);

        }


    }
    private void requestFilePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 13);
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 13);

        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == 1) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // contacts-related task you need to do.
            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(VoiceMain.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    /**private void record() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);



        // Estimate the buffer size we'll need for this device.
        int bufferSize =
                AudioRecord.getMinBufferSize(
                        SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2;
        }
        short[] audioBuffer = new short[bufferSize / 2];

        AudioRecord record =
                new AudioRecord(
                        MediaRecorder.AudioSource.DEFAULT,
                        SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize);

        if (record.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(LOG_TAG, "Audio Record can't initialize!");

            return;
        }

        record.startRecording();

        Log.v(LOG_TAG, "Start recording");

        // Loop, gathering audio data and copying it to a round-robin buffer.
        while (shouldContinue) {
            int numberRead = record.read(audioBuffer, 0, audioBuffer.length);
            int maxLength = recordingBuffer.length;
            int newRecordingOffset = recordingOffset + numberRead;
            int secondCopyLength = Math.max(0, newRecordingOffset - maxLength);
            int firstCopyLength = numberRead - secondCopyLength;
            // We store off all the data for the recognition thread to access. The ML
            // thread will copy out of this buffer into its own, while holding the
            // lock, so this should be thread safe.
            recordingBufferLock.lock();
            try {
                System.arraycopy(audioBuffer, 0, recordingBuffer, recordingOffset, firstCopyLength);
                System.arraycopy(audioBuffer, firstCopyLength, recordingBuffer, 0, secondCopyLength);
                recordingOffset = newRecordingOffset % maxLength;
            } finally {
                recordingBufferLock.unlock();
            }

        }

        record.stop();
        record.release();


    }**/
    private synchronized void startRecord() {
        if (recordingThread != null) {
            System.out.println("not null");
            return;
        }
        System.out.println("is null");
        shouldContinue = true;
        recordingThread =
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                record();
                            }
                        });
        recordingThread.start();
    }
    public void record() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
        requestFilePermission();
        //Creating file
        File dir = Environment.getExternalStorageDirectory() ;
        try {
            audiofile = File.createTempFile("sound", ".aac", dir);
        } catch (IOException e) {
            Log.e(LOG_TAG, "external storage access error");
            e.printStackTrace();
            return;
        }
        //Creating MediaRecorder and specifying audio source, output format, encoder & output format
        System.out.println("running");
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audiofile.getAbsolutePath());
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
        shouldContinue=true;
        while (shouldContinue) System.out.println("going");
        recorder.stop();
    }
    public void startRecording() {
        audioRecorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void run() {
                String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
                FileOutputStream os = null;

                try {
                    File file = new File(getDataDir(),"record.pcm");
                    os = new FileOutputStream(getDataDir()+"/record.pcm");
                    while (isRecording) {
                        audioRecorder.read(Data, 0, Data.length);
                        try {
                            assert os != null;
                            os.write(Data, 0, bufferSizeInBytes);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        assert os != null;
                        os.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }



            }
        });
        recordingThread.start();
    }

    public void stopRecording() {
        if (null != audioRecorder) {
            isRecording = false;
            audioRecorder.stop();
            audioRecorder.release();
            audioRecorder = null;
            recordingThread = null;
        }
    }

}
