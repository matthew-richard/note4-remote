package me.mattrichard.consumerirdemo;

import android.hardware.ConsumerIrManager;
import android.util.Log;
import android.widget.TextView;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * Created by Matt on 1/10/2016.
 */
public class IrSignal {

    public static final String TAG = "me.mattrichard.consumerirdemo.IrSignal";

    public enum Action {
        POWER_TOGGLE,
        POWER_ON,
        POWER_OFF,
        OTHER
    }

    public IrSignal(InputStream file) throws YamlException {
        // TODO: Implement IrSignal initialization from pronto file

        YamlReader reader = new YamlReader(new InputStreamReader(file));
        Map<String, Object> yaml = (Map<String,Object>) reader.read();

        deviceBrand = normalizeString((String)yaml.get("brand"));
        deviceModel = normalizeString((String)yaml.get("model"));
        deviceType = IrDeviceType.valueOf(normalizeString((String)yaml.get("device"))
                .toUpperCase());

        if (!yaml.containsKey("pronto")) {
            Log.w(TAG, "A single IrSignal is being constructed from a file containing multiple"
                       + "yaml files.");
            yaml = (Map<String, Object>) reader.read();
        }
        action = Action.valueOf(normalizeString((String)yaml.get("action")));
        Object pronto = yaml.get("pronto");
        processPronto(pronto);
    }

    private IrSignal(String devBrand, String devModel, IrDeviceType devType, Action action,
                     Object pronto) {
        deviceBrand = devBrand;
        deviceModel = devModel;
        deviceType = devType;

        this.action = action;
        processPronto(pronto);
    }


    private static String normalizeString(String s) {
        return s.toLowerCase().replace(' ','_').trim();
    }

    public static void setTransmitter(ConsumerIrManager CIM) {
        mCIM = CIM;
    }

    public static int getCycleDuration(int frequency) {
        return Math.round(1000000.0f / frequency);
    }

    private void processPronto(Object pronto) {
        // TODO: Implement processPronto()

        // check if list or string
        Log.d(IrSignal.TAG, pronto.getClass().getName());
    }

    public boolean isMacro() {
        return patterns.size() > 1;
    }

    public void transmit() throws Exception {
        if (mCIM == null) {
            throw new Exception(
                "ERROR: No transmitter provided. Cannot transmit IrSignal " + this.toString()
            );
        }

        for (int i = 0; i < patterns.size(); i++) {
            mCIM.transmit(frequencies.get(i), patterns.get(i));
        }
    }

    private void initialize() {
        patterns = null;
        deviceBrand = null;
        deviceModel = null;
        frequencies = null;
        mCIM = null;
        action = null;
        deviceType = null;
    }


    public List<int[]> getPatterns() {
        return patterns;
    }

    public int[] getPattern() {
        if (!isMacro())
            return patterns.get(0);
        else return null;
    }

    public int getFrequency() {
        return frequencies.get(0);
    }

    public Action getAction() {
        return action;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public IrDeviceType getDeviceType() {
        return deviceType;
    }


    static ConsumerIrManager mCIM;

    /* Signal data */
    List<int[]> patterns;
    List<Integer> frequencies;
    Action action;

    /* Device data */
    String deviceBrand;
    String deviceModel;
    IrDeviceType deviceType;
}
