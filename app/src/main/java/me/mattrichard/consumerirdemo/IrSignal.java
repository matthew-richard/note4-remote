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

    public enum Action {
        POWER_TOGGLE,
        POWER_ON,
        POWER_OFF,
        OTHER
    }

    public IrSignal(InputStream file) throws YamlException {
        // TODO: Implement IrSignal initialization from pronto file

        Map yaml = (Map) new YamlReader(new InputStreamReader(file)).read();

        Log.d("blah blah blah", "ACTION:" + (String)yaml.get("action"));
        Log.d("blah blah blah", "PRONTO:" + (String)yaml.get("pronto"));
    }

    private IrSignal(List file, int subsignal_index) {
        // TODO: Implement Ir subsignal initialization from pronto file
    }

    public int getCycleDuration() {
        return Math.round(1000000.0f / getFrequency());
    }

    public boolean isMacro() {
        return subsignals != null;
    }

    public void transmit() throws Exception {
        if (mCIM == null) {
            throw new Exception(
                "ERROR: No transmitter provided. Cannot transmit IrSignal " + this.toString()
            );
        }

        if (!isMacro()) {
            int[] pattern_in_ms = pattern.clone();
            for (int i = 0; i < pattern.length; i++)
                pattern_in_ms[i] *= getCycleDuration();
            mCIM.transmit(getFrequency(), pattern_in_ms);
        } else {
            for (IrSignal signal : subsignals)
                signal.transmit();
        }
    }

    public static void setTransmitter(ConsumerIrManager CIM) {
        mCIM = CIM;
    }

    private void initialize() {
        pattern = null;
        subsignals = null;
        deviceBrand = null;
        deviceModel = null;
        frequency = 0;
        mCIM = null;
        action = null;
        deviceType = null;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public int[] getPattern() {
        return pattern;
    }

    public int getFrequency() {
        return frequency;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public IrSignal[] getSubsignals() {
        return subsignals;
    }

    public IrDeviceType getDeviceType() {
        return deviceType;
    }


    static ConsumerIrManager mCIM;

    /* Signal data */
    int[] pattern;
    int frequency;
    Action action;
    IrSignal[] subsignals;

    /* Device data */
    String deviceBrand;
    String deviceModel;
    IrDeviceType deviceType;
}
