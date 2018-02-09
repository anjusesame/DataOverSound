package com.example.user.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.cast.TextTrackStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 23/1/18.
 */

public class NewActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {

    long f4836m = 0;
    private String f4838t;
    TextView playstop;
    TextView hertzz;
    View f4842d;
    ImageButton f4843e;
    TextView f4844f;
    AudioTrack audioTrack;
    boolean play_sound = false;
    boolean f4847i;
    float f4848j;
    int max_freq_range = 44100;
    byte[] bytes1;
    private int min_freq = 7616;
    private int max_freq = 24000;
    private int initial_freq = 0;
    TextView record;
    byte[] bytes;
    Context context;
    private Handler f4855r = new Handler();
    private int f4854q = 0;
    Soundify soundify;
    private AudioManager am;
    byte[] namearray;
    private AudioRecord audioRecord;
    private Thread thread;
    private boolean threadRunning = true;
    private short[] recordedData;
    List<Short> list = new ArrayList<>();
    private List<Byte> lists = new ArrayList<>();


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();

        setContentView(R.layout.activity_main);
        findViewById(R.id.main_view).setOnTouchListener(this);

        this.hertzz = (TextView) findViewById(R.id.frequency_text_view);


        record = (TextView) findViewById(R.id.record);
        this.playstop = (TextView) findViewById(R.id.play_pause_button);
        this.f4842d = findViewById(R.id.banner_wrapper);
        this.playstop.setOnClickListener(this);
        this.initial_freq = (this.max_freq - this.min_freq) / 2;
        recordedData = new short[initial_freq];
        this.hertzz.setText("" + this.initial_freq);

        this.f4844f = (TextView) findViewById(R.id.play_pause_button);
        this.f4843e = (ImageButton) findViewById(R.id.water_button);

        this.f4843e.setOnTouchListener(new C30261(this));


        soundify = new Soundify(this);
//        soundify.startListening();
//        soundify.setSoundifyListener((data) -> {
//            String stringData = Soundify.bytesToString(data);
//            Log.e("dataa",stringData.toString().trim());
//
//        });

        String datas = "hellooo";
        byte[] stringbytes=stringToBytes(datas);

        Log.e("string in bytes",stringbytes.toString());

        list = appendCommand(list, Config.START_COMMAND);
        for (byte b : stringbytes) {// Percorre todos os bytes que vão ser transmitidos
            list = appendByte(list, b);// Adiciona o valor do byte transformado em frequencia
            Log.e("list data",list.toString());
        }
        list = appendCommand(list, Config.STOP_COMMAND);// Adiciona o comando de fim da transmissao

      //  sendData(this, list);


        inicializeReceiver();
        soundify.setSoundifyListener((data) -> {
            String stringData = Soundify.bytesToString(data);
            Log.e("dataa",stringData.toString().trim());

        });


    }
    private List<Short> appendCommand(List<Short> list, short command) {
        return appendByte(list, command);
    }
    private List<Short> appendByte(List<Short> list, short byt) {
        int freq = calcFreq(byt);// Calcula a frequencia baseada no byte

        for (int i = 0; i < Config.TIME_BAND; i++) {// Percorre o tamanho de cada banda de frequencia
            double angle = 2.0 * i * freq * Math.PI / Config.SAMPLE_RATE;// Realiza o calculo do angulo da frequencia
            list.add((short) (Math.sin(angle) * Config.MAX_SIGNAL_STRENGTH));
        }


        return list;
    }
    private short calcFreq(short byt) {
        return (short) (Config.BASE_FREQ + byt * Config.FREQ_STEP);
    }
    public static short[] convertListShortToArrayShort(List<Short> list){
        int size = list.size();
        short[] data = new short[size];
        for (int i = 0; i < size; i++) {
            data[i] = list.get(i);
        }

        return data;
    }

    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 0:
            case 2:

                if (!this.f4847i) {
                    this.f4847i = true;
                    this.f4848j = motionEvent.getY();
                    break;
                }
                float y = this.f4848j - motionEvent.getY();
                this.f4848j = motionEvent.getY();
                y += (float) this.initial_freq;
                if (y <= TextTrackStyle.DEFAULT_FONT_SCALE) {
                    this.initial_freq = 1;
                } else if (y >= ((float) this.max_freq)) {
                    this.initial_freq = 25000;
                } else {
                    this.initial_freq = (int) y;
                }

                this.hertzz.setText("" + this.initial_freq);
                if (this.play_sound) {
                    play_music();
                    play();
                    break;
                }
                break;
            case 1:

                this.f4847i = false;
                break;
            case 3:

                this.f4847i = false;
                break;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_pause_button:
                Log.e("Main_Play_Clicked", "play");
                if (this.play_sound) {
                    play_music();
                    this.playstop.setText("PLAY");
                    return;
                }
                play();
                this.playstop.setText("STOP");

                return;

            default:
                return;
        }
    }

    void play() {
        this.play_sound = true;
        write_data();
        thread_write();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {


        play_music();
        super.onBackPressed();
        finish();
    }

    class C30261 implements View.OnTouchListener {
        final /* synthetic */ NewActivity f4824a;

        C30261(NewActivity mainActivity) {
            this.f4824a = mainActivity;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case 0:

                    this.f4824a.f4854q = this.f4824a.initial_freq;
                    this.f4824a.initial_freq = 165;
                    this.f4824a.play_sound = true;
                    this.f4824a.play_music();
                    this.f4824a.play();

                    break;
                case 1:

                    this.f4824a.initial_freq = this.f4824a.f4854q;
                    this.f4824a.play_sound = false;
                    this.f4824a.f4844f.setText("PLAY");
                    this.f4824a.play_music();


                    break;
                case 3:

                    this.f4824a.initial_freq = this.f4824a.f4854q;
                    this.f4824a.play_sound = false;
                    this.f4824a.play_music();
                    this.f4824a.f4844f.setText("PLAY");

                    break;
            }
            return false;
        }
    }

    public static byte[] stringToBytes(String value) {
        return value.getBytes();
    }

    class C30293 implements Runnable {
        final /* synthetic */ NewActivity newActivity;

        C30293(NewActivity mainActivity) {
            this.newActivity = mainActivity;
        }

        public void run() {
            do {
                this.newActivity.play_ultra_sonic_sound(this.newActivity.initial_freq);
            } while (this.newActivity.audioTrack != null);
        }
    }


    public synchronized void play_ultra_sonic_sound(int i) {
        if (i > 0) {
            if (this.audioTrack != null) {
                int i2 = this.max_freq_range / i;
                int i3 = i2 * i;

                Log.e("i2",i2+"");
                Log.e("i3",i3+"");



                if (this.bytes1 == null) {


                    this.bytes1 = new byte[this.max_freq_range];


                }

                this.bytes1 = FreqCalc.m6859a(this.bytes1, i2, i3);



                try {
                    if (this.audioTrack != null) {

                        this.audioTrack.play();


                    }
                    if (this.audioTrack != null) {
                          this.audioTrack.write(bytes1, 0, i3);
                      //  audioTrack.write(convertListShortToArrayShort(list), 0, list.size());

//                        int retn_value = audioTrack.write(bytes1, 0, i3);
//                        Log.e("returned value", retn_value + "");


                    }


                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }


    void play_music() {
        this.play_sound = false;
        if (this.audioTrack != null) {
            AudioTrack audioTrack = this.audioTrack;
            this.audioTrack = null;
            if (audioTrack.getPlayState() == 3) {
                audioTrack.stop();
            }
            audioTrack.release();
        }
    }

    public synchronized void write_data() {
        if (this.audioTrack == null) {
            int i = this.initial_freq;
            int i2 = this.max_freq_range / i;
            int i3 = i2 * i;
//            PrintStream printStream = System.out;
//            Log.e("stream", printStream.toString());
//            StringBuilder append = new StringBuilder().append("preparetest ").append(i2).append(" ").append(i3).append(" ").append(i).append(" ");
//            Log.e("append", append.toString());
//            AudioTrack audioTrack = this.audioTrack;
//            printStream.println(append.append(AudioTrack.getMinBufferSize(i, 4, 3)).toString());
            this.audioTrack = new AudioTrack(3, this.max_freq_range, 4, 3, i3, 1);
        }
    }

    public synchronized void thread_write() {
        new Thread(new C30293(this)).start();
    }

    //
    public void inicializeReceiver()  {
        if (audioRecord == null || audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, Config.SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, Config.AUDIO_FORMAT, AudioTrack.getMinBufferSize(Config.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT) * 4);
        }
        if (audioRecord.getState() != AudioRecord.RECORDSTATE_RECORDING) {
            audioRecord.startRecording();
        }
        if(!threadRunning){
            threadRunning = true;
        }
        if(thread == null){
            initThread();
        }
        if (thread.getState() == Thread.State.NEW) {
            thread.start();
        }
        if (thread.getState() == Thread.State.TERMINATED) {
            initThread();
            thread.start();
        }
    }
    private void initThread() {
        thread = new Thread() {
            @Override
            public void run() {
                while (threadRunning) {
                    audioRecord.read(recordedData, 0, Config.TIME_BAND);
                    short parsedData = parseRecData(recordedData);
                    if (parsedData != Config.NONSENSE_DATA) {

                        lists.add((byte) parsedData);
                    }
                }
            }
        };
    }
    private short parseRecData(short[] recordedData) {
        float[] floatData = convertArrayShortToArrayFloat(recordedData);
        short freq = calcFreq(floatData);
        short data = calcData(freq);
        Log.e("Freq: " ,+ freq + "  |  data: " + data);
        switch (data) {
            case Config.START_COMMAND:
                lists = new ArrayList<>();
                return Config.NONSENSE_DATA;
            case Config.STOP_COMMAND:
                if(!lists.isEmpty()) {
                    byte[] retByte = convertListBytesToArrayBytes(lists);
                    if (retByte != null) {
                        Soundify.soundifyListener.OnReceiveData(retByte);
                    }
                    lists = new ArrayList<>();
                }
                return Config.NONSENSE_DATA;
            default:
                if(data >= Config.STOP_COMMAND){
                    return Config.NONSENSE_DATA;
                }else{
                    return data;
                }
        }
    }

    public static byte[] convertListBytesToArrayBytes(List<Byte> list){
        int size = list.size();
        byte[] data = new byte[size];
        for (int i = 0; i < size; i++) {
            data[i] = list.get(i);
        }

        return data;
    }




    public float[] convertArrayShortToArrayFloat(short[] recordedData) {
        int size = recordedData.length;
        //  Log.e("size of recorded data",size+"");
        float[] floatData = new float[size];
        for (int i = 0; i < size; i++) {
            floatData[i] = recordedData[i];
        }

        return floatData;
    }

    static short calcFreq(float[] floatData) {
        int size = floatData.length;
        int fftSize = calcFftSize(size);
        FFT fft = new FFT(fftSize, Config.SAMPLE_RATE);
        fft.forward(floatData);
        float maxAmp = 0;
        short index = 0;
        for (short i = Config.BASE_FREQ; i < Config.BASE_FREQ + Config.FREQ_STEP * 140; i++) {
            float curAmp = fft.getFreq(i);
            if (curAmp > maxAmp) {
                maxAmp = curAmp;
                index = i;
            }
        }
        return index;
    }


    static short calcData(short freq){
        return (short) ((freq - Config.BASE_FREQ + Config.FREQ_STEP / 2) / Config.FREQ_STEP);
    }

    private static int calcFftSize(int size) {
        int count = 0;
        int i;
        for (i = 0; i < 32 && size != 0; i++) {
            if ((size & 1) == 1) {
                count++;
            }
            size >>= 1;
        }
        int r = count == 1 ? i - 1 : i;
        return 1 << r;
    }



}
