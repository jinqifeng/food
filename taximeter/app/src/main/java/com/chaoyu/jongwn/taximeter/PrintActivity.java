package com.chaoyu.jongwn.taximeter;

import android.app.ProgressDialog;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import com.chaoyu.jongwn.taximeter.utils.util.Printer;
import com.chaoyu.jongwn.taximeter.pockdata.PocketPos;

import com.chaoyu.jongwn.taximeter.utils.util.DateUtil;
import com.chaoyu.jongwn.taximeter.utils.util.FontDefine;
import com.chaoyu.jongwn.taximeter.utils.util.Printer;
import com.chaoyu.jongwn.taximeter.utils.util.StringUtil;
import com.chaoyu.jongwn.taximeter.utils.util.Util;
import com.chaoyu.jongwn.taximeter.utils.util.DataConstants;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class PrintActivity extends AppCompatActivity implements View.OnClickListener {

    private static BluetoothSocket btsocket;

    private ProgressDialog mProgressDlg;
    private ProgressDialog mConnectingDlg;

    private BluetoothAdapter mBluetoothAdapter;

    private P25Connector mConnector;
    private Button mEnableBtn;
    private Button mPrintView;
    private Button mDisconnectBtn;
    private Button mPrintTextBtn;
    private Button mConnectBtn;
    private Spinner mDeviceSp;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        mEnableBtn			= (Button) findViewById(R.id.btn_enable);
        mBluetoothAdapter	= BluetoothAdapter.getDefaultAdapter();
        mDeviceSp 			= (Spinner) findViewById(R.id.sp_device);
        mPrintView			= (Button) findViewById(R.id.btnPreview);
        mDisconnectBtn			= (Button) findViewById(R.id.btnDisconnect);
        mConnectBtn			= (Button) findViewById(R.id.btnConnect);
        mPrintTextBtn			= (Button) findViewById(R.id.btnPrint);
        if (mBluetoothAdapter == null) {
            showUnsupported();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                showDisabled();
            } else {
                showEnabled();

                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                if (pairedDevices != null) {
                    mDeviceList.addAll(pairedDevices);

                    updateDeviceList();
                }
            }

            mProgressDlg = new ProgressDialog(this);

            mProgressDlg.setMessage("Scanning...");
            mProgressDlg.setCancelable(false);
            mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    mBluetoothAdapter.cancelDiscovery();
                }
            });

            mConnectingDlg = new ProgressDialog(this);

            mConnectingDlg.setMessage("Connecting...");
            mConnectingDlg.setCancelable(false);

            mConnector = new P25Connector(new P25Connector.P25ConnectionListener() {

                @Override
                public void onStartConnecting() {
                    mConnectingDlg.show();
                }

                @Override
                public void onConnectionSuccess() {
                    mConnectingDlg.dismiss();

                    showConnected();
                }

                @Override
                public void onConnectionFailed(String error) {
                    mConnectingDlg.dismiss();
                }

                @Override
                public void onConnectionCancelled() {
                    mConnectingDlg.dismiss();
                }

                @Override
                public void onDisconnected() {
                    showDisonnected();
                }
            });
            //enable bluetooth
            mEnableBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                    startActivityForResult(intent, 1000);
                }
            });
        }

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        registerReceiver(mReceiver, filter);
    }
    private void showConnected() {
        showToast("Connected");

        mDeviceSp.setEnabled(false);
        mConnectBtn.setEnabled(false);
        mPrintTextBtn.setEnabled(true);
        mDisconnectBtn.setEnabled(true);
        mPrintView.setEnabled(true);
    }
    private void showUnsupported() {
        showToast("Bluetooth is unsupported by this device");
        mDeviceSp.setEnabled(false);
        mConnectBtn.setEnabled(false);
        mPrintTextBtn.setEnabled(false);
        mDisconnectBtn.setEnabled(false);
        mPrintView.setEnabled(false);
    }
    private void showDisonnected() {
        showToast("Disconnected");
        mDeviceSp.setEnabled(true);
        mPrintTextBtn.setEnabled(false);
        mDisconnectBtn.setEnabled(false);
        mPrintView.setEnabled(false);
        mConnectBtn.setEnabled(true);
    }
    private void updateDeviceList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, getArray(mDeviceList));

        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        mDeviceSp.setAdapter(adapter);
        mDeviceSp.setSelection(0);
    }
    private String[] getArray(ArrayList<BluetoothDevice> data) {
        String[] list = new String[0];

        if (data == null) return list;

        int size	= data.size();
        list		= new String[size];

        for (int i = 0; i < size; i++) {
            list[i] = data.get(i).getName();
        }

        return list;
    }
    private void showEnabled() {
        showToast("Bluetooth enabled");
        mEnableBtn.setVisibility(View.GONE);
        mPrintTextBtn.setEnabled(true);
        mDisconnectBtn.setEnabled(true);
        mPrintView.setEnabled(true);
        mConnectBtn.setEnabled(true);
        mDeviceSp.setVisibility(View.VISIBLE);

    }
    private void showDisabled() {
        showToast("Bluetooth disabled");
        mEnableBtn.setVisibility(View.VISIBLE);
        mPrintTextBtn.setEnabled(false);
        mDisconnectBtn.setEnabled(false);
        mPrintView.setEnabled(false);
        mConnectBtn.setEnabled(false);
        mDeviceSp.setVisibility(View.GONE);
    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPrint:
                printText();

                break;
            case R.id.btnConnect:
                connect();
                break;
            case R.id.btnDisconnect:
                disconnect();
                break;
            case R.id.btnPreview:
                printDemoContent();
                break;

        }
    }
    private void printDemoContent(){

        /*********** print head*******/
        String receiptHead = "************************"
                + "   P25/M Test Print"+"\n"
                + "************************"
                + "\n";

        long milis		= System.currentTimeMillis();

        String date		= DateUtil.timeMilisToString(milis, "MMM dd, yyyy");
        String time		= DateUtil.timeMilisToString(milis, "hh:mm a");

        String hwDevice	= Build.MANUFACTURER;
        String hwModel	= Build.MODEL;
        String osVer	= Build.VERSION.RELEASE;
        String sdkVer	= String.valueOf(Build.VERSION.SDK_INT);

        StringBuffer receiptHeadBuffer = new StringBuffer(100);

        receiptHeadBuffer.append(receiptHead);
        receiptHeadBuffer.append(Util.nameLeftValueRightJustify(date, time, DataConstants.RECEIPT_WIDTH) + "\n");

        receiptHeadBuffer.append(Util.nameLeftValueRightJustify("Device:", hwDevice, DataConstants.RECEIPT_WIDTH) + "\n");

        receiptHeadBuffer.append(Util.nameLeftValueRightJustify("Model:",  hwModel, DataConstants.RECEIPT_WIDTH) + "\n");
        receiptHeadBuffer.append(Util.nameLeftValueRightJustify("OS ver:", osVer, DataConstants.RECEIPT_WIDTH) + "\n");
        receiptHeadBuffer.append(Util.nameLeftValueRightJustify("SDK:", sdkVer, DataConstants.RECEIPT_WIDTH));
        receiptHead = receiptHeadBuffer.toString();

        byte[] header = Printer.printfont(receiptHead + "\n", FontDefine.FONT_32PX,FontDefine.Align_CENTER,(byte)0x1A,PocketPos.LANGUAGE_ENGLISH);


        /*********** print English text*******/
        StringBuffer sb = new StringBuffer();
        for(int i=1; i<128; i++)
            sb.append((char)i);
        String content = sb.toString().trim();

        byte[] englishchartext24 			= Printer.printfont(content + "\n",FontDefine.FONT_24PX,FontDefine.Align_CENTER,(byte)0x1A,PocketPos.LANGUAGE_ENGLISH);
        byte[] englishchartext32			= Printer.printfont(content + "\n",FontDefine.FONT_32PX,FontDefine.Align_CENTER,(byte)0x1A,PocketPos.LANGUAGE_ENGLISH);
        byte[] englishchartext24underline	= Printer.printfont(content + "\n",FontDefine.FONT_24PX_UNDERLINE,FontDefine.Align_CENTER,(byte)0x1A,PocketPos.LANGUAGE_ENGLISH);

        //2D Bar Code
        byte[] barcode = StringUtil.hexStringToBytes("1d 6b 02 0d 36 39 30 31 32 33 34 35 36 37 38 39 32");


        /*********** print Tail*******/
        String receiptTail =  "Test Completed" + "\n"
                + "************************" + "\n";

        String receiptWeb =  "** www.londatiga.net ** " + "\n\n\n";

        byte[] foot = Printer.printfont(receiptTail,FontDefine.FONT_32PX,FontDefine.Align_CENTER,(byte)0x1A,PocketPos.LANGUAGE_ENGLISH);
        byte[] web	= Printer.printfont(receiptWeb,FontDefine.FONT_32PX,FontDefine.Align_CENTER,(byte)0x1A,PocketPos.LANGUAGE_ENGLISH);

        byte[] totladata =  new byte[header.length + englishchartext24.length + englishchartext32.length + englishchartext24underline.length +
                + barcode.length
                + foot.length + web.length
                ];
        int offset = 0;
        System.arraycopy(header, 0, totladata, offset, header.length);
        offset += header.length;

        System.arraycopy(englishchartext24, 0, totladata, offset, englishchartext24.length);
        offset+= englishchartext24.length;

        System.arraycopy(englishchartext32, 0, totladata, offset, englishchartext32.length);
        offset+=englishchartext32.length;

        System.arraycopy(englishchartext24underline, 0, totladata, offset, englishchartext24underline.length);
        offset+=englishchartext24underline.length;

        System.arraycopy(barcode, 0, totladata, offset, barcode.length);
        offset+=barcode.length;

        System.arraycopy(foot, 0, totladata, offset, foot.length);
        offset+=foot.length;

        System.arraycopy(web, 0, totladata, offset, web.length);
        offset+=web.length;

        byte[] senddata = PocketPos.FramePack(PocketPos.FRAME_TOF_PRINT, totladata, 0, totladata.length);

        sendData(senddata);
    }
    protected void printText() {
        String text;
        text = "test";
        byte[] line 	= Printer.printfont(text + "\n\n", FontDefine.FONT_32PX, FontDefine.Align_CENTER, (byte) 0x1A,
                PocketPos.LANGUAGE_ENGLISH);
        byte[] senddata = PocketPos.FramePack(PocketPos.FRAME_TOF_PRINT, line, 0, line.length);

        sendData(senddata);

    }
    private void sendData(byte[] bytes) {
        try {
            mConnector.sendData(bytes);
        } catch (P25ConnectionException e) {
            e.printStackTrace();
        }
    }
    protected void disconnect() {
        if (mDeviceList == null || mDeviceList.size() == 0) {
            return;
        }
        try {
            if (mConnector.isConnected()) {
                mConnector.disconnect();
                showDisonnected();
            } else {

            }
        } catch (P25ConnectionException e) {
            e.printStackTrace();
        }
    }
    protected void connect() {
        if (mDeviceList == null || mDeviceList.size() == 0) {
            return;
        }

        BluetoothDevice device = mDeviceList.get(mDeviceSp.getSelectedItemPosition());

        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            try {
                createBond(device);
            } catch (Exception e) {
                showToast("Failed to pair device");

                return;
            }
        }

        try {
            if (!mConnector.isConnected()) {
                mConnector.connect(device);
            }
        } catch (P25ConnectionException e) {
            e.printStackTrace();
        }
    }
    private void createBond(BluetoothDevice device) throws Exception {

        try {
            Class<?> cl 	= Class.forName("android.bluetooth.BluetoothDevice");
            Class<?>[] par 	= {};

            Method method 	= cl.getMethod("createBond", par);

            method.invoke(device);

        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }
    @Override
    public void onPause() {
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }

        if (mConnector != null) {
            try {
                mConnector.disconnect();
            } catch (P25ConnectionException e) {
                e.printStackTrace();
            }
        }

        super.onPause();
    }
    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state 	= intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    showEnabled();
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    showDisabled();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mDeviceList = new ArrayList<BluetoothDevice>();

                mProgressDlg.show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mProgressDlg.dismiss();

                updateDeviceList();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                mDeviceList.add(device);

                showToast("Found device " + device.getName());
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED) {
                    showToast("Paired");

                    connect();
                }
            }
        }
    };

}
