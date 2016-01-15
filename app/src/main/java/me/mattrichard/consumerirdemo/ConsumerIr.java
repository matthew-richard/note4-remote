/*
 * Copyright (C) 20013The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.mattrichard.consumerirdemo;
// Need the following import to get access to the app resources, since this
// class is in a sub-package.
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.hardware.ConsumerIrManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;

import com.esotericsoftware.yamlbeans.YamlException;

import java.io.InputStreamReader;
import java.util.List;
//import com.example.android.apis.R;
/**
 * App that transmit an IR code
 *
 * <p>This demonstrates the {@link android.hardware.ConsumerIrManager android.hardware.ConsumerIrManager} class.
 *
 * <h4>Demo</h4>
 * Hardware / Consumer IR
 *
 * <h4>Source files</h4>
 * <table class="LinkTable">
 *         <tr>
 *             <td>src/com.example.android.apis/hardware/ConsumerIr.java</td>
 *             <td>Consumer IR demo</td>
 *         </tr>
 *         <tr>
 *             <td>res/any/layout/consumer_ir.xml</td>
 *             <td>Defines contents of the screen</td>
 *         </tr>
 * </table>
 */
public class ConsumerIr extends Activity {
    private static final String TAG = "ConsumerIrTest";
    TextView mFreqsText;
    ConsumerIrManager mCIR;
    List<IrSignal> mSignals;

    /**
     * Initialization of the Activity after it is first created.  Must at least
     * call {@link android.app.Activity#setContentView setContentView()} to
     * describe what is to be displayed in the screen.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Be sure to call the super class.
        super.onCreate(savedInstanceState);

        // Get a reference to the ConsumerIrManager
        mCIR = (ConsumerIrManager)getSystemService(Context.CONSUMER_IR_SERVICE);
        if (!mCIR.hasIrEmitter()) {
            Log.e(TAG, "No IR Emitter found\n");
            return;
        }

        // Tell IrSignal what device to transmit on
        IrSignal.setTransmitter(mCIR);

        // Read list of signals from raw resource file
        try {
            mSignals = IrSignal.readSignalsFromFile(getResources().openRawResource(R.raw.samsung));
        } catch (YamlException e) {
            Log.e(ConsumerIr.TAG, "ERROR: Invalid YAML signals file");
        }

        // See assets/res/any/layout/consumer_ir.xml for this
        // view layout definition, which is being set here as
        // the content of our screen.
        setContentView(R.layout.consumer_ir);
        // Set the OnClickListener for the button so we see when it's pressed.
        findViewById(R.id.send_button).setOnClickListener(mSendClickListener);
        findViewById(R.id.get_freqs_button).setOnClickListener(mGetFreqsClickListener);
        mFreqsText = (TextView) findViewById(R.id.freqs_text);


    }

    View.OnClickListener mSendClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            for (IrSignal signal : mSignals) {
                IrSignal.Action action = null;

                try {
                    action = IrSignal.Action.valueOf((
                            (EditText)findViewById(R.id.command_field)).getText()
                            .toString().trim().toUpperCase().replace(" ","_"));
                } catch (Exception e) {}

                if (signal.action == action)
                    signal.transmit();
            }

            // A pattern of alternating series of carrier on and off periods measured in
            // microseconds.
            /*int[] samsung = {0x00a9 * 25, 0x00a8 * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0040 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x0702 * 25, 0x00a9 * 25, 0x00a8 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0e6e * 25};
            int[] sanyo = {0x0155 * 25, 0x00aa * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x0015 * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x003f * 25, 0x0015 * 25, 0x05f9 * 25, 0x0155 * 25, 0x0057 * 25, 0x0015 * 25, 0x0e30 * 25};
            int[] xbox_a = {0x0062 * 28, 0x0020 * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x0020 * 28, 0x000F * 28, 0x0020 * 28, 0x002F * 28, 0x0020 * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x0020 * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x0020 * 28, 0x0020 * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x0020 * 28, 0x0020 * 28, 0x0020 * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x0020 * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x0020 * 28, 0x000F * 28, 0x000F * 28, 0x000F * 28, 0x0981 * 28};
            int[] xbox_on = {0x0060 * 28, 0x0020 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0020 * 28, 0x0010 * 28, 0x0020 * 28, 0x0031 * 28, 0x0020 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0020 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0020 * 28, 0x0020 * 28, 0x0020 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0010 * 28, 0x0020 * 28, 0x0020 * 28, 0x0020 * 28, 0x0020 * 28, 0x0020 * 28, 0x0020 * 28, 0x0010 * 28, 0x09C1 * 28};
            int[][] patterns = {samsung, sanyo, xbox_a, xbox_on};

            for (int[] pattern : patterns) {
                mCIR.transmit(38000, pattern);
            }*/
            /* xbox */ //int[] pattern = {0x0060 * 25, 0x0020 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0020 * 25, 0x0010 * 25, 0x0020 * 25, 0x0030 * 25, 0x0020 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0020 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0020 * 25, 0x0020 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0020 * 25, 0x0020 * 25, 0x0020 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x0020 * 25, 0x0010 * 25, 0x0010 * 25, 0x0020 * 25, 0x0010 * 25, 0x0010 * 25, 0x0010 * 25, 0x09AC * 25};
            // transmit the pattern at 38.4KHz
            //mCIR.transmit(38000, pattern);

            //for (int[] pattern : patterns)


            //int freq = 38000;
            //int period = 28;//int 28 = (int) (1000000.0 / freq);

            //mCIR.transmit(freq, xbox_a);
            //mCIR.transmit(freq, xbox_on);
        }
    };

    View.OnClickListener mGetFreqsClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            StringBuilder b = new StringBuilder();
            if (!mCIR.hasIrEmitter()) {
                mFreqsText.setText("No IR Emitter found!");
                Log.e(TAG, "No IR Emitter found!\n");
                return;
            }
            // Get the available carrier frequency ranges
            ConsumerIrManager.CarrierFrequencyRange[] freqs = mCIR.getCarrierFrequencies();
            b.append("IR Carrier Frequencies:\n");
            for (ConsumerIrManager.CarrierFrequencyRange range : freqs) {
                b.append(String.format("    %d - %d\n", range.getMinFrequency(),
                        range.getMaxFrequency()));
            }
            mFreqsText.setText(b.toString());
        }
    };
}