package com.logpyx.auraconfig;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DeviceControlActivity extends AppCompatActivity{
    private final static String TAG = DeviceControlActivity.class.getSimpleName();
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private TextView mConnectionState;
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService bluetoothLeService;
    private boolean mConnected = false;
    private List<Short> decaWaveIDList = new ArrayList<>();
    public List<Short> nearDistanceList = new ArrayList<>();
    public List<Short> farDistanceList = new ArrayList<>();
    public Aura aura;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        //parte de conectar com um dispositivo, usa o mDeviceAddress
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (bluetoothLeService != null) {
                if (!bluetoothLeService.initialize()) {
                    Log.e(TAG, "Unable to initialize Bluetooth");
                    finish();
                }
                bluetoothLeService.connect(mDeviceAddress);
                aura = new Aura((SendInterface) bluetoothLeService);
                aura.setDeviceControlActivity(DeviceControlActivity.this);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetoothLeService = null;
        }

        @Override
        public void onNullBinding(ComponentName name) {
            ServiceConnection.super.onNullBinding(name);
            System.out.println("nome do pacote" + name.getPackageName());
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
            }
        }
    };

    private TextView firstIpUrlOTATextView;
    private TextView secondIpUrlOTATextView;
    private TextView thirdIpUrlOTATextView;
    private TextView fourthIpUrlOTATextView;
    private TextView portUrlOTATextView;
    private TextView xOffsetTextView;
    private TextView yOffsetTextView;
    private TextView minSecDistTextView;
    private TextView maxSecDistTextView;
    private TextView maxAlturaTextView;
    private TextView minAlturaTextView;
    private TextView auraModeTextView;
    private Spinner spinnerAuraMode;
    private TextView fixTagTextView;
    private TextView firstDecawaveIDTextView;
    private TextView secondDecawaveIDTextView;
    private TextView thirdDecawaveIDTextView;
    private TextView nearDistanceTextView;
    private TextView farDistanceTextView;
    private TextView deviceTagIdDistanceTextView;
    private TextView tagTagIdDistanceTextView;
    private TextView distanceTagIdDistanceTextView;
    private TextView setHwMajorTextView;
    private TextView setHwMinorTextView;
    private TextView setHwRevisionTextView;
    private TextView setFwMajorTextView;
    private TextView setFwMinorTextView;
    private TextView setFwRevisionTextView;
    private TextView urlBrokerTextView;
    private TextView userBrokerTextView;
    private TextView passwdBrokerTextView;
    private TextView customTopicBrokerTextView;
    private TextView logTextView;
    private Button reconectarButton;
    private Button restartButton;
    private Button otaStartButton;
    private Button otaURLButton;
    private Button dfuStartButton;
    private Button getOverCraneParamButton;
    private Button setOverCraneParamButton;
    private Button auraModeButton;
    private Button setTagButton;
    private Button versionRequestButton;
    private Button setDecawaveIDListButton;
    private Button setFarDistanceButton;
    private Button setTagIdDistanceButton;
    private Button brokerConfigButton;
    private Button userConfigButton;
    private Button passwdConfigButton;
    private Button custonTopicConfigButton;
    private Button readIncidentListButton;
    private Button readIncidentListInfoButton;
    private Button eraseIncidentList;
    private Button allInfoDeviceButton;
    String message = new String();
    @SuppressLint({"RestrictedApi", "ClickableViewAccessibility"})
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.toolbarimage);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        ((TextView) findViewById(R.id.device_name)).setText(mDeviceName);
        mConnectionState = findViewById(R.id.connection_state);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        firstIpUrlOTATextView = findViewById(R.id.firstIp_edit);
        secondIpUrlOTATextView = findViewById(R.id.secondIp_edit);
        thirdIpUrlOTATextView = findViewById(R.id.thirdIp_edit);
        fourthIpUrlOTATextView = findViewById(R.id.fourthIp_edit);
        portUrlOTATextView = findViewById(R.id.port_idit);
        xOffsetTextView = findViewById(R.id.xOffset_edit);
        yOffsetTextView = findViewById(R.id.yOffset_edit);
        minSecDistTextView = findViewById(R.id.minSecureDist_edit);
        maxSecDistTextView = findViewById(R.id.maxSecureDist_edit);
        minAlturaTextView = findViewById(R.id.minAltura_edit);
        maxAlturaTextView = findViewById(R.id.maxAltura_edit);
        //auraModeTextView = findViewById(R.id.setMode_edit);
        fixTagTextView = findViewById(R.id.fixTag_edit);
        firstDecawaveIDTextView = findViewById(R.id.firstDecawaveID_edit);
        secondDecawaveIDTextView = findViewById(R.id.secondDecawaveID_edit);
        thirdDecawaveIDTextView = findViewById(R.id.thirdDecawaveID_edit);
        nearDistanceTextView = findViewById(R.id.nearDistance_edit);
        farDistanceTextView = findViewById(R.id.farDistance_edit);
        deviceTagIdDistanceTextView = findViewById(R.id.deviceTagIdDistance_edit);
        tagTagIdDistanceTextView = findViewById(R.id.tagTagIdDistance_edit);
        distanceTagIdDistanceTextView = findViewById(R.id.distanceTagIdDistance_edit);
        setHwMajorTextView = findViewById(R.id.setHwMajor_edit);
        setHwMajorTextView.setKeyListener(null);
        setHwMinorTextView = findViewById(R.id.setHwMinor_edit);
        setHwMinorTextView.setKeyListener(null);
        setHwRevisionTextView = findViewById(R.id.setHwRevision_edit);
        setHwRevisionTextView.setKeyListener(null);
        setFwMajorTextView = findViewById(R.id.setFwMajor_edit);
        setFwMajorTextView.setKeyListener(null);
        setFwMinorTextView = findViewById(R.id.setFwMinor_edit);
        setFwMinorTextView.setKeyListener(null);
        setFwRevisionTextView = findViewById(R.id.setFwRevision_edit);
        setFwRevisionTextView.setKeyListener(null);
        urlBrokerTextView = findViewById(R.id.urlBroker_edit);
        userBrokerTextView = findViewById(R.id.userBroker_edit);
        passwdBrokerTextView = findViewById(R.id.passwdBroker_edit);
        customTopicBrokerTextView = findViewById(R.id.customTopicBroker_edit);
        logTextView = findViewById(R.id.logText_edit);

        reconectarButton = findViewById(R.id.reconnectButton);
        reconectarButton.setVisibility(View.GONE);
        otaStartButton = findViewById(R.id.otaStart);
        otaURLButton = findViewById(R.id.setOtaURL);
        dfuStartButton = findViewById(R.id.dfuStart);
        getOverCraneParamButton = findViewById(R.id.getOvercraneParam);
        setOverCraneParamButton = findViewById(R.id.setOvercraneParam);
        auraModeButton = findViewById(R.id.auraMode);
        setTagButton = findViewById(R.id.fixTag);
        versionRequestButton = findViewById(R.id.versionRequest);
        setDecawaveIDListButton = findViewById(R.id.setDecaWaveIDList);
        setFarDistanceButton = findViewById(R.id.setFarDistance);
        setFarDistanceButton = findViewById(R.id.setFarDistance);
        setTagIdDistanceButton = findViewById(R.id.setTagIdDistance);
        restartButton = findViewById(R.id.restart);
        brokerConfigButton = findViewById(R.id.setUrlBroker);
        readIncidentListButton = findViewById(R.id.readIncidentList);
        readIncidentListInfoButton = findViewById(R.id.readIncidentListInfo);
        eraseIncidentList = findViewById(R.id.eraseIncidentList);
        allInfoDeviceButton = findViewById(R.id.allInfoDevice);

        spinnerAuraMode = findViewById(R.id.auraModeSpinner);

        String[] options = getResources().getStringArray(R.array.spinner_pr);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAuraMode.setAdapter(adapter);
    }
    private final Handler handler = new Handler(Looper.getMainLooper());
    public void updateArgument(final String deviceId) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                deviceTagIdDistanceTextView.setText(deviceId);
            }
        });
    }
    public void handleReconnect(View view){
        if(!mConnected && bluetoothLeService != null){
            bluetoothLeService.connect(mDeviceAddress);
        }
    }
    public void handleRestart(View view){
        if (mConnected){
            try{
                aura.sendOpCode((byte)SampleGattAttributes.WR_ESP_RESTART, 600);
                Toast.makeText(getApplicationContext(), "Enviado comando RESTART", Toast.LENGTH_SHORT).show();
            } catch (Exception t){
                Toast.makeText(getApplicationContext(), "Erro ao enviar o comando RESTART", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void handleOtaStart(View view){
        if (mConnected){
            try{
                aura.sendOpCode((byte)SampleGattAttributes.WR_OTA_START, 600);
                Toast.makeText(getApplicationContext(), "Enviado comando OTA START", Toast.LENGTH_SHORT).show();
            } catch (Exception t){
                Toast.makeText(getApplicationContext(), "Erro ao enviar o comando OTA Start", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void handlesetOtaURL(View view){
        if (mConnected){
            int[] ip = new int[4];
            try {
                String ip1 = firstIpUrlOTATextView.getText().toString();
                String ip2 = secondIpUrlOTATextView.getText().toString();
                String ip3 = thirdIpUrlOTATextView.getText().toString();
                String ip4 = fourthIpUrlOTATextView.getText().toString();
                String port = portUrlOTATextView.getText().toString();
                if(ip1.isEmpty() || ip2.isEmpty() || ip3.isEmpty() || ip4.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Preencha todos os campos do URL.", Toast.LENGTH_LONG).show();
                }
                if (port.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Preencha o campo Porta.", Toast.LENGTH_LONG).show();
                } else {
                    ip[0] = Integer.parseInt(ip1);
                    ip[1] = Integer.parseInt(ip2);
                    ip[2] = Integer.parseInt(ip3);
                    ip[3] = Integer.parseInt(ip4);
                    aura.sendOtaUrl(ip, Integer.parseInt(port), 600);
                }
            } catch (Exception t){
                Toast.makeText(getApplicationContext(), "Erro ao enviar comando SET OTA URL.", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void handleDfuStart(View view){
        if (mConnected){
            try{
                aura.sendOpCode((byte)SampleGattAttributes.WR_DFU_START, 600);
                Toast.makeText(getApplicationContext(), "Enviado comando DFU START", Toast.LENGTH_SHORT).show();
            } catch (Exception t){
                Toast.makeText(getApplicationContext(), "Erro ao enviar o comando DFU Start", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void handleSetAuraMode(View view) {
        if (mConnected) {
            try {
                String selectedMode = spinnerAuraMode.getSelectedItem().toString();
                int modeValue;
                switch (selectedMode) {
                    case SampleGattAttributes.FORKLIFT_MODE:
                        modeValue = 0;
                        break;
                    case SampleGattAttributes.OVER_CRANE_MODE:
                        modeValue = 1;
                        break;
                    case SampleGattAttributes.ACCESS_CONTROL:
                        modeValue = 2;
                        break;
                    default:
                        Toast.makeText(getApplicationContext(),"SELECT AURA MODE!", Toast.LENGTH_LONG).show();
                        return;
                }
                aura.sendAuraMode(modeValue, 600);
                Toast.makeText(getApplicationContext(), selectedMode + " mode", Toast.LENGTH_LONG).show();
            } catch (Exception t) {
                Toast.makeText(getApplicationContext(), "Erro ao enviar comando Aura Mode", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void handleFixTag(View view) {
        if (mConnected) {
            try {
                String tagText = fixTagTextView.getText().toString();
                if (tagText.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Defina uma TAG", Toast.LENGTH_LONG).show();
                } else {
                    String tag = tagText.substring(tagText.length() - 4);
                    byte[] tagBytes = hexStringToByteArray(tag);
                    aura.sendFixTag(tagBytes, 600);
                    Toast.makeText(getApplicationContext(), "Fixed TAG " + tag, Toast.LENGTH_LONG).show();
                }
                //fixTagTextView.setText("");
            } catch (Exception t) {
                Toast.makeText(getApplicationContext(), "Erro ao enviar comando Fix TAG", Toast.LENGTH_LONG).show();
            }
        }
    }
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
    public void handleGetOvercraneParam(View view){
        if (mConnected){
            try{
                aura.sendGetOvercraneParam(600);
                Toast.makeText(getApplicationContext(), "Enviado comando GET OVER CRANE PARAM", Toast.LENGTH_SHORT).show();
            } catch (Exception t){
                Toast.makeText(getApplicationContext(), "Erro ao enviar o comando GET OVER CRANE PARAM", Toast.LENGTH_LONG).show();
            }
        }
    }
    @SuppressLint("SetTextI18n")
    public void updateOverCraneParam(int[] DataBuffer){
        if(DataBuffer != null){
            xOffsetTextView.setText(Integer.toString(((DataBuffer[1] & 0xFF) << 8) | (DataBuffer[2] & 0xFF)));
            yOffsetTextView.setText(Integer.toString(((DataBuffer[3] & 0xFF) << 8) | (DataBuffer[4] & 0xFF)));
            maxSecDistTextView.setText(Integer.toString(((DataBuffer[5] & 0xFF) << 8) | (DataBuffer[6] & 0xFF)));
            minSecDistTextView.setText(Integer.toString(((DataBuffer[7] & 0xFF) << 8) | (DataBuffer[8] & 0xFF)));
            maxAlturaTextView.setText(Integer.toString(((DataBuffer[9] & 0xFF) << 8) | (DataBuffer[10] & 0xFF)));
            minAlturaTextView.setText(Integer.toString(((DataBuffer[11] & 0xFF) << 8) | (DataBuffer[12] & 0xFF)));
        }
    }
    public void handleSetOverCraneParam(View view){
        if (mConnected){
            try{
                String xOffSet = xOffsetTextView.getText().toString();
                String yOffSet = yOffsetTextView.getText().toString();
                String maxSecDist = maxSecDistTextView.getText().toString();
                String minSecDist = minSecDistTextView.getText().toString();
                String maxAltura = maxAlturaTextView.getText().toString();
                String minAltura = minAlturaTextView.getText().toString();
                if(xOffSet.isEmpty() || yOffSet.isEmpty() || minSecDist.isEmpty() || maxSecDist.isEmpty() ||
                    minAltura.isEmpty() || maxAltura.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Preencha todos os parâmetros", Toast.LENGTH_SHORT).show();
                } else {
                    aura.sendSetOvercraneParam(Integer.parseInt(xOffSet), Integer.parseInt(yOffSet), Integer.parseInt(minSecDist), Integer.parseInt(maxSecDist),
                            Integer.parseInt(minAltura), Integer.parseInt(maxAltura), 600);
                    Toast.makeText(getApplicationContext(), "Enviado comando SET OVER CRANE PARAM", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception t){
                Toast.makeText(getApplicationContext(), "Erro ao enviar o comando SET OVER CRANE PARAM", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void handleSetDecawaveIDList(View view) {
        if (mConnected) {
            try {
                String tag1 = firstDecawaveIDTextView.getText().toString();
                String tag2 = secondDecawaveIDTextView.getText().toString();
                String tag3 = thirdDecawaveIDTextView.getText().toString();
                if (tag1.isEmpty() || tag2.isEmpty() ||tag3.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Defina as 3 TAGs Decawave", Toast.LENGTH_LONG).show();
                } else {
                    String tagList = tag1.substring(tag1.length() - 4);
                    tagList = tagList.concat(tag2.substring(tag2.length() - 4)).concat(tag3.substring(tag3.length() - 4));
                    byte[] tagBytes = hexStringToByteArray(tagList);
                    aura.sendDecawaveIDList(tagBytes, 600);
                    Toast.makeText(getApplicationContext(), "Fixed Decawave TAG List " + tagList, Toast.LENGTH_LONG).show();
                }
                //fixTagTextView.setText("");
            } catch (Exception t) {
                Toast.makeText(getApplicationContext(), "Erro ao enviar comando SET DECAWAVE TAG LIST", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void handleSetNearDistance(View view){
        if(mConnected){
            try {
                String nearDistance = nearDistanceTextView.getText().toString();
                if(nearDistance.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Defina um valor para ZONA VERMELHA", Toast.LENGTH_LONG).show();
                }
                else {
                    int distance = Integer.parseInt(nearDistance);
                    aura.sendNearFarDistance((byte)SampleGattAttributes.WR_ZONE_NEAR_DISTANCE, distance, 600);
                    Toast.makeText(getApplicationContext(), "Definido um valor para ZONA VERMELHA", Toast.LENGTH_LONG).show();
                }
            } catch (Exception t){
                Toast.makeText(getApplicationContext(), "Erro ao enviar comando SET NEAR DISTANCE", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void handleSetFarDistance(View view){
        if(mConnected){
            try {
                String farDistance = farDistanceTextView.getText().toString();
                if(farDistance.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Defina um valor para ZONA AMARELA", Toast.LENGTH_LONG).show();
                }
                else {
                    int distance = Integer.parseInt(farDistance);
                    aura.sendNearFarDistance((byte)SampleGattAttributes.WR_ZONE_FAR_DISTANCE, distance, 600);
                    Toast.makeText(getApplicationContext(), "Definido um valor para ZONA AMARELA", Toast.LENGTH_LONG).show();
                }
            } catch (Exception t){
                Toast.makeText(getApplicationContext(), "Erro ao enviar comando SET FAR DISTANCE", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void handleSetTagIdDistance(View view) {
        if (mConnected) {
            try {
                String deviceText = deviceTagIdDistanceTextView.getText().toString();
                String tagText = tagTagIdDistanceTextView.getText().toString();
                String distanceText = distanceTagIdDistanceTextView.getText().toString();

                if (deviceText.isEmpty() || tagText.isEmpty() || distanceText.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Defina os três parâmetros.", Toast.LENGTH_LONG).show();
                } else {
                    int device = Integer.parseInt(deviceText);
                    String tag = tagText.substring(tagText.length() - 4);
                    byte[] tagBytes = hexStringToByteArray(tag);
                    Log.i(TAG, "byte[0]="+tagBytes[0]+" byte[1]="+tagBytes[1]);
                    int distance = Integer.parseInt(distanceText);
                    aura.sendTagIdDistance(device, tagBytes, distance, 600);
                    Toast.makeText(getApplicationContext(), "Fixed TAG " + tag, Toast.LENGTH_LONG).show();
                }
                fixTagTextView.setText("");
            } catch (Exception t) {
                Toast.makeText(getApplicationContext(), "Erro ao enviar comando Fix TAG", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void handleVersionRequest(View view){
        if (mConnected){
            try{
                aura.sendOpCode((byte)SampleGattAttributes.RD_VERSION, 600);
                Toast.makeText(getApplicationContext(), "Enviado comando VERSION REQUEST", Toast.LENGTH_SHORT).show();
            } catch (Exception t){
                Toast.makeText(getApplicationContext(), "Erro ao enviar o comando VERSION REQUEST", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void updateVersionRequest(int[] DataBuffer){
        if(DataBuffer != null){
            setHwMajorTextView.setText(Integer.toString(DataBuffer[1]));
            setHwMinorTextView.setText(Integer.toString(DataBuffer[2]));
            setHwRevisionTextView.setText(Integer.toString(DataBuffer[3]));
            setFwMajorTextView.setText(Integer.toString(DataBuffer[4]));
            setFwMinorTextView.setText(Integer.toString(DataBuffer[5]));
            setFwRevisionTextView.setText(Integer.toString(DataBuffer[6]));
        }
    }
    public void handleSetUrlConfig(View view) {
        if (mConnected) {
            try {
                String config = urlBrokerTextView.getText().toString();
                if (!config.isEmpty()) {
                    byte[] urlBytes = config.getBytes(StandardCharsets.UTF_8);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    outputStream.write(0x00);
                    outputStream.write(urlBytes);
                    byte[] finalBytes = outputStream.toByteArray();
                    aura.sendConfigBroker(finalBytes, 600);
                } else {
                    Toast.makeText(getApplicationContext(), "Preencha o campo URL BROKER!", Toast.LENGTH_LONG).show();
                }
            } catch (Exception t) {
                Toast.makeText(getApplicationContext(), "Erro ao enviar o comando WR_BROKER_MQTT_CONFIG", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void handleSetUserConfig(View view) {
        if (mConnected) {
            try {
                String config = userBrokerTextView.getText().toString();
                if (!config.isEmpty()) {
                    byte[] urlBytes = config.getBytes(StandardCharsets.UTF_8);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    outputStream.write(0x01);
                    outputStream.write(urlBytes);
                    byte[] finalBytes = outputStream.toByteArray();
                    aura.sendConfigBroker(finalBytes, 600);
                } else {
                    Toast.makeText(getApplicationContext(), "Preencha o campo USER BROKER!", Toast.LENGTH_LONG).show();
                }
            } catch (Exception t) {
                Toast.makeText(getApplicationContext(), "Erro ao enviar o comando WR_BROKER_MQTT_CONFIG", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void handleSetPwConfig(View view) {
        if (mConnected) {
            try {
                String config = passwdBrokerTextView.getText().toString();
                if (!config.isEmpty()) {
                    byte[] urlBytes = config.getBytes(StandardCharsets.UTF_8);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    outputStream.write(0x02);
                    outputStream.write(urlBytes);
                    byte[] finalBytes = outputStream.toByteArray();
                    aura.sendConfigBroker(finalBytes, 600);
                } else {
                    Toast.makeText(getApplicationContext(), "Preencha o campo PASSWORD BROKER!", Toast.LENGTH_LONG).show();
                }
            } catch (Exception t) {
                Toast.makeText(getApplicationContext(), "Erro ao enviar o comando WR_BROKER_MQTT_CONFIG", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void handleSetCustomTopicConfig(View view) {
        if (mConnected) {
            try {
                String config = customTopicBrokerTextView.getText().toString();
                if (!config.isEmpty()) {
                    byte[] urlBytes = config.getBytes(StandardCharsets.UTF_8);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    outputStream.write(0x03);
                    outputStream.write(urlBytes);
                    byte[] finalBytes = outputStream.toByteArray();
                    aura.sendConfigBroker(finalBytes, 600);
                } else {
                    Toast.makeText(getApplicationContext(), "Preencha o campo CUSTOM TOPIC!", Toast.LENGTH_LONG).show();
                }
            } catch (Exception t) {
                Toast.makeText(getApplicationContext(), "Erro ao enviar o comando WR_BROKER_MQTT_CONFIG", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void handleReadIncidentList(View view){
        if (mConnected){
            try{
                aura.sendOpCode((byte)SampleGattAttributes.RD_INCIDENT_LIST, 600);
                Toast.makeText(getApplicationContext(), "Enviado comando RD INCIDENT LIST", Toast.LENGTH_SHORT).show();
            } catch (Exception t){
                Toast.makeText(getApplicationContext(), "Erro ao enviar o comando RD INCIDENT LIST", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void handleReadIncidentListInfo(View view){
        if (mConnected){
            try{
                aura.sendOpCode((byte)SampleGattAttributes.RD_INCIDENT_LIST_INFO, 600);
                Toast.makeText(getApplicationContext(), "Enviado comando RD INCIDENT LIST INFO", Toast.LENGTH_SHORT).show();
            } catch (Exception t){
                Toast.makeText(getApplicationContext(), "Erro ao enviar o comando RD INCIDENT LIST INFO", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void handleEraseIncidentList(View view){
        if (mConnected){
            try{
                aura.sendOpCode((byte)SampleGattAttributes.ERASE_INCIDENT_LIST, 600);
                Toast.makeText(getApplicationContext(), "Enviado comando ERASE INCIDENT LIST", Toast.LENGTH_SHORT).show();
            } catch (Exception t){
                Toast.makeText(getApplicationContext(), "Erro ao enviar o comando ERASE INCIDENT LIST", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void handleAllInfoDevices(View view){
        if (mConnected){
            try{
                aura.sendOpCode((byte)SampleGattAttributes.RD_ALL_INFO_DEVICE, 600);
                Toast.makeText(getApplicationContext(), "Enviado comando READ ALL INFO DEVICE", Toast.LENGTH_SHORT).show();
            } catch (Exception t){
                Toast.makeText(getApplicationContext(), "Erro ao enviar o comando Enviado comando READ ALL INFO DEVICE", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void updateLogText(int[] DataBuffer){
        if(DataBuffer != null){
            switch (DataBuffer[0]){
                case (int)SampleGattAttributes.RD_ALL_INFO_DEVICE:
                    message = message + "OPERATION: RD_ALL_INFO_DEVICE"
                            + "\nDecawave ID: " + Integer.toHexString(((DataBuffer[1] & 0xFF) << 8) | (DataBuffer[2] & 0xFF))
                            + "\nOffset: " + Integer.toString(((DataBuffer[3] & 0xFF) << 8) | (DataBuffer[4] & 0xFF))
                            + "\nFar Distance: " + Integer.toString(((DataBuffer[5] & 0xFF) << 8) | (DataBuffer[6] & 0xFF))
                            + "\nNear Distance: " + Integer.toString(((DataBuffer[7] & 0xFF) << 8) | (DataBuffer[8] & 0xFF))
                            + "\nMAC ADDRESS: " + Integer.toHexString(DataBuffer[9] & 0xFF) + ":" + Integer.toHexString(DataBuffer[10] & 0xFF)
                            + ":" + Integer.toHexString(DataBuffer[11] & 0xFF) + ":" + Integer.toHexString(DataBuffer[12] & 0xFF)
                            + ":" + Integer.toHexString(DataBuffer[13] & 0xFF) + ":" + Integer.toHexString(DataBuffer[14] & 0xFF) + "\n\n";
                    logTextView.setText(message);
                    break;

                case (int) SampleGattAttributes.WR_OPERATION_ERRO:
                    switch (DataBuffer[1]) {
                        case (int)SampleGattAttributes.RD_ALL_INFO_DEVICE:
                            message = message + "ERROR OPERATION: RD_ALL_INFO_DEVICE OPERATION"
                                    + "\nDevice not conected"
                                    + "\nDecawave ID: " + Integer.toHexString(((DataBuffer[2] & 0xFF) << 8) | (DataBuffer[3] & 0xFF)) + "\n\n";
                            logTextView.setText(message);
                            break;
                        case (int) SampleGattAttributes.WR_ZONE_FAR_DISTANCE:
                            int farDist = Integer.parseInt(Integer.toHexString(((DataBuffer[2] & 0xFF) << 8) | (DataBuffer[3] & 0xFF)));
                            message = message + "ERROR OPERATION: WR_ZONE_FAR_DISTANCE"
                                    + "\nFar Distance: " + farDist;
                            farDistanceTextView.setTextColor(Color.GREEN);
                            farDistanceTextView.setText(farDist);
                            break;
                        case (int) SampleGattAttributes.WR_ZONE_NEAR_DISTANCE:
                            int nearDist = Integer.parseInt(Integer.toHexString(((DataBuffer[2] & 0xFF) << 8) | (DataBuffer[3] & 0xFF)));
                            message = message + "ERROR OPERATION: WR_ZONE_NEAR_DISTANCE"
                                    + "\nNear Distance: " + nearDist;
                            nearDistanceTextView.setTextColor(Color.GREEN);
                            nearDistanceTextView.setText(nearDist);
                            break;
                            /*
                        case (int) SampleGattAttributes.WR_PERIOD_REQUEST:
                            int decaId = Integer.parseInt(Integer.toHexString(((DataBuffer[2] & 0xFF) << 8) | (DataBuffer[3] & 0xFF)));
                            message = message + "ERROR OPERATION: WR_ZONE_NEAR_DISTANCE"
                                    + "\nNear Distance: " + nearDist;
                            nearDistanceTextView.setTextColor(Color.GREEN);
                            nearDistanceTextView.setText(nearDist);
                            break;
                            */
                        default:
                            break;
                    }
                default:
                    break;
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (bluetoothLeService != null) {
            decaWaveIDList = aura.getDecaWaveIDList();
            CharSequence[] array = decaWaveIDList.toArray(new CharSequence[0]);
            ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerAuraMode.setAdapter(spinnerAdapter);
        }
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        bluetoothLeService = null;
    }
    private boolean mSoliciteInfoAtived=false;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            if(mSoliciteInfoAtived) {
                menu.findItem(R.id.action_refresh).setVisible(true);
                menu.findItem(R.id.action_solicite).setVisible(false);
            }else{
                menu.findItem(R.id.action_refresh).setVisible(false);
                menu.findItem(R.id.action_solicite).setVisible(true);
            }
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.action_refresh).setVisible(false);
            menu.findItem(R.id.action_solicite).setVisible(false);
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_disconnect:
                bluetoothLeService.disconnect();
                break;
            case R.id.action_solicite:
                aura.solicitaDeviceInfo(100);
                mSoliciteInfoAtived=true;
                invalidateOptionsMenu();
                break;
            case R.id.action_refresh:
//                if (decaWaveIDList == null) decaWaveIDList = mBluetoothLeService.getDecaWaveIDList();
//                offsetList = mBluetoothLeService.getOffsetList();
                try {
                    farDistanceList = aura.getFarDistanceList();
                    nearDistanceList = aura.getNearDistanceList();

                    invalidateOptionsMenu();
                    mSoliciteInfoAtived = false;
                }catch (Exception x){
                    x.toString();

                }
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
                if(mConnected){
                   reconectarButton.setVisibility((View.GONE));
                }
                else{
                    reconectarButton.setVisibility(View.VISIBLE);

                }
            }
        });
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
