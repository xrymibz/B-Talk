package com.scandev;

import android.app.Application;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.ScannerNotClaimedException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.TriggerStateChangeEvent;
import com.honeywell.aidc.UnsupportedPropertyException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.HashMap;
import java.util.Map;

import static com.honeywell.aidc.BarcodeReader.TRIGGER_CONTROL_MODE_CLIENT_CONTROL;

/**
 * Created by renjicui on 16/8/26.
 */
public class S11Application extends Application {

    static private SoundPool sp;
    static private HashMap<Integer, Integer> spMap;
    static private AudioManager am;
    public static BarcodeReader barcodeReader;

    static public DisplayImageOptions options;
    private AidcManager aidcManager;
    public static boolean scanning = false;

    private static S11Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        initSoundPool();
        instance = this;

        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);

        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .showImageOnLoading(R.drawable.loading)
                .build();

        AidcManager.create(this, new AidcManager.CreatedCallback() {
            @Override
            public void onCreated(AidcManager manager) {
                aidcManager = manager;
                barcodeReader = aidcManager.createBarcodeReader();
                initBarcodeReader();

                /*** honeywell *******************************/
                try {
                    //scanning = !scanning;
                    barcodeReader.aim(scanning);
                    barcodeReader.light(scanning);
                    barcodeReader.decode(scanning);
                    scanning = !scanning;
                } catch (ScannerNotClaimedException e) {
                    e.printStackTrace();
                } catch (ScannerUnavailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onTerminate() {
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.clearDiskCache();
        imageLoader.clearMemoryCache();
        super.onTerminate();
    }

    public void initSoundPool() {
        am = (AudioManager) this.getSystemService(this.AUDIO_SERVICE);
        sp = new SoundPool(
                5,
                AudioManager.STREAM_MUSIC,
                0
        );
        spMap = new HashMap<Integer, Integer>();
        spMap.put(1, sp.load(this, R.raw.error, 1));
        spMap.put(2, sp.load(this, R.raw.didong, 1));
        spMap.put(3, sp.load(this, R.raw.error, 1));
    }

    static public void playSound(int sound, int number) {

        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volumnRatio = audioCurrentVolumn / audioMaxVolumn;
        sp.play(
                spMap.get(sound),
                volumnRatio,
                volumnRatio,
                1,
                number,
                1
        );
    }

    private void initBarcodeReader() {
        if (barcodeReader != null) {
            //barcodeReader.addBarcodeListener(this);
            try {
                barcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                        TRIGGER_CONTROL_MODE_CLIENT_CONTROL);
            } catch (UnsupportedPropertyException e) {
                e.printStackTrace();
            }
            //barcodeReader.addTriggerListener(this);
            Map<String, Object> properties = new HashMap<String, Object>();
            // Set Symbologies On/Off
            properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_GS1_128_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_QR_CODE_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_CODE_39_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_UPC_A_ENABLE, true);
            properties.put(BarcodeReader.PROPERTY_EAN_13_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_AZTEC_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_CODABAR_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_PDF_417_ENABLED, false);

            // Set Max Code 39 barcode length
            properties.put(BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH, 10);
            // Turn on center decoding
            properties.put(BarcodeReader.PROPERTY_CENTER_DECODE, true);
            // Disable bad read response, handle in onFailureEvent
            properties.put(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, false);
            // Apply the settings
            barcodeReader.setProperties(properties);
            try {
                barcodeReader.claim();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
            }
        }
    }

    public static S11Application getInstance() {
        return instance;
    }
}
