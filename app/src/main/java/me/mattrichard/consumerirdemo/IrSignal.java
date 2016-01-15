package me.mattrichard.consumerirdemo;

import android.hardware.ConsumerIrManager;
import android.util.Log;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Matt on 1/10/2016.
 */
public class IrSignal {

    public static final String TAG = "me.mattrichard.consumerirdemo.IrSignal";

    public enum Action {
        POWER_TOGGLE,
        POWER_ON,
        POWER_OFF,
        VOLUME_UP,
        VOLUME_DOWN,
        OTHER
    }

    @SuppressWarnings("unchecked")
    public IrSignal(InputStream file) throws YamlException {
        initialize();

        YamlReader reader = new YamlReader(new InputStreamReader(file));
        Map<String, Object> yaml = (Map<String,Object>) reader.read();

        deviceBrand = normalizeString((String)yaml.get("brand"));
        if (yaml.containsKey("model"))
            deviceModel = normalizeString((String)yaml.get("model"));
        else deviceModel = "ANY";
        deviceType = IrDeviceType.valueOf(normalizeString((String)yaml.get("device"))
                .toUpperCase());

        if (!yaml.containsKey("pronto")) {
            Log.w(TAG, "A single IrSignal is being constructed from a file containing multiple"
                       + "yaml files.");
            yaml = (Map<String, Object>) reader.read();
        }
        action = Action.valueOf(normalizeString((String)yaml.get("action")).toUpperCase());
        Object pronto = yaml.get("pronto");
        processPronto(pronto);
    }

    private IrSignal(String devBrand, String devModel, IrDeviceType devType, Action action,
                     Object pronto) {
        initialize();

        deviceBrand = devBrand;
        deviceModel = devModel;
        deviceType = devType;

        this.action = action;
        processPronto(pronto);
    }


    private static String normalizeString(String s) {
        return s.toLowerCase().replace(' ','_').trim();
    }

    public static List<IrSignal> readSignalsFromFile(InputStream file) throws YamlException {
        List<IrSignal> signals = new ArrayList<>();

        // Read first (--- delimited) YAML file in 'file'. This may or may not contain
        // an actual pattern, but it must contain device info (brand, model, type).
        YamlReader reader = new YamlReader(new InputStreamReader(file));
        Map<String, Object> yaml = (Map<String,Object>) reader.read();

        String deviceBrand, deviceModel = null;
        IrDeviceType deviceType = null;
        deviceBrand = normalizeString((String)yaml.get("brand"));
        if (yaml.containsKey("model"))
            deviceModel = normalizeString((String)yaml.get("model"));
        else deviceModel = "ANY";
        deviceType = IrDeviceType.valueOf(normalizeString((String)yaml.get("device"))
                .toUpperCase());

        // Start reading actual signal info, which may start in the first YAML file
        // (which contains device info), or in the next one.
        if (!yaml.containsKey("pronto"))
            yaml = (Map<String, Object>) reader.read();

        while (yaml.containsKey("pronto")) {
            Action action = Action.valueOf(normalizeString((String)yaml.get("action")).toUpperCase());
            Object pronto = yaml.get("pronto");
            signals.add(new IrSignal(deviceBrand, deviceModel, deviceType, action, pronto));

            yaml = (Map<String, Object>) reader.read();
        }

        return signals;
    }

    public static void setTransmitter(ConsumerIrManager CIM) {
        mCIM = CIM;
    }

    @SuppressWarnings("unchecked")
    private void processPronto(Object pronto) {
        // check if list or string
        if (pronto instanceof String)
            processOnePronto((String)pronto);
        else for (String p : (List<String>)pronto)
            processOnePronto(p);
    }

    private void processOnePronto(String pronto) {
        Scanner scanner = new Scanner(pronto);

        // Calculate transmission frequency from second number, converting from Pronto units
        // to Hz.
        scanner.next();
        long frequency = Math.round(1000000.0/(Integer.parseInt(scanner.next(), 16) * 0.241246));
        frequencies.add((int)frequency);
        scanner.next();
        scanner.next();

        // Read on/off burst durations from remaining numbers and convert them from
        // cycles to milliseconds.
        int cycleLength = Math.round(1000000.0f / frequency);
        String[] tokens = scanner.nextLine().trim().split(" ");
        int[] pattern = new int[tokens.length];
        for (int i = 0; i < pattern.length; i++)
            pattern[i] = Integer.parseInt(tokens[i], 16) * cycleLength;
        patterns.add(pattern);
    }

    public boolean isMacro() {
        return patterns.size() > 1;
    }

    public void transmit() throws RuntimeException {
        if (mCIM == null) {
            throw new RuntimeException(
                "ERROR: No transmitter provided. Cannot transmit IrSignal " + this.toString()
            );
        }

        for (int i = 0; i < patterns.size(); i++) {
            mCIM.transmit(frequencies.get(i), patterns.get(i));
        }
    }

    private void initialize() {
        patterns = new ArrayList<int[]>();
        deviceBrand = null;
        deviceModel = null;
        frequencies = new ArrayList<Integer>();
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


    static ConsumerIrManager mCIM = null;

    /* Signal data */
    List<int[]> patterns;
    List<Integer> frequencies;
    Action action;

    /* Device data */
    String deviceBrand;
    String deviceModel;
    IrDeviceType deviceType;
}
