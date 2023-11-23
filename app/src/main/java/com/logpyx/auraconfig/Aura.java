package com.logpyx.auraconfig;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Aura extends Protocol implements ReceiveInterface{
    public List<Short> decaWaveIDList = new ArrayList<>();
    public List<Short> offsetList = new ArrayList<>();
    public List<Short> nearDistanceList = new ArrayList<>();
    public List<Short> farDistanceList = new ArrayList<>();

    private Handler handler = new Handler();

    public Aura(SendInterface sendInterface){
        super(sendInterface);
    }

    public DeviceControlActivity deviceControlActivity;
    public void setDeviceControlActivity(DeviceControlActivity activity) {
        this.deviceControlActivity = activity;
    }

    @Override
    void processCommand(int[] DataBuffer) {
        for (int i=0; i<DataBufferSize; i++){
            if(DataBuffer[i]<0){
                DataBuffer[i]+=256;
            }
            Log.i(TAG, "DataBuffer["+i+"]="+Integer.toString(DataBuffer[i]));
        }
        switch (DataBuffer[0]){
            case SampleGattAttributes.RD_REQUEST_CONNECTION:
                sendDeviceID((byte)DataBuffer[1],100);
                break;
            case SampleGattAttributes.RD_ALL_INFO_DEVICE:
                updateLog(DataBuffer);
                break;

            case SampleGattAttributes.RD_VERSION:
                if(DataBufferSize == 7){
                    updateRdRevision(DataBuffer);
                }
                break;

            case SampleGattAttributes.WR_GET_OVERCRANE_PARAM:
                if(DataBufferSize == 13) {
                    updateGetOvercraneParam(DataBuffer);
                }
                break;

            default:
                break;
        }
    }

    public void updateLog(int[] data){
        if (deviceControlActivity != null) {
            deviceControlActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    deviceControlActivity.updateLogText(data);
                }
            });
        }
    }

    public void updateRdRevision(int[] data){
        if (deviceControlActivity != null) {
            deviceControlActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    deviceControlActivity.updateVersionRequest(data);
                }
            });
        }
    }

    public void updateGetOvercraneParam(int[] data){
        if (deviceControlActivity != null) {
            deviceControlActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    deviceControlActivity.updateOverCraneParam(data);
                }
            });
        }
    }

    /*
    Função: solicitaDeviceInfo()
    Descrição: Envia um comando de solicitação para retorna dados dos disposivos conectados.
    @param: long delay -> Atraso do envio
    @return:    int length -> Quantidade de dados enviados (Nao funcional)
    Funcionamento
    Envio: solicitaDeviceInfo()-> (AURA Processa Comando e retorna dados disponiveis)
    Recepção: onCharacteristicChanged()-> extratCommand() -> ProcessComando
    */
    public int solicitaDeviceInfo(long delay){
        // limpa a lista para receber novos IDs
        decaWaveIDList.clear();
        byte data[] ={(byte)SampleGattAttributes.RD_ALL_INFO_DEVICE};
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                sendCommand((byte)SampleGattAttributes.RD_ALL_INFO_DEVICE,data,0);
            }
        };
        handler.postDelayed(runnable,delay);
        Log.i(TAG,"Solicita Device Info");
        return 0;
    }

    /*
    Função: sendOffsetCommand(int decaWaveId, int offset, long delay)
    Descrição: Envia um comando para configurar o offset do periférico baseado no decaWaveID
    @param: int decaWaveId-> Identificador do periférico
    @return:    int offset -> Valor do offset em mm
    Funcionamento
    Envio: solicitaDeviceInfo()-> (AURA Processa Comando e retorna dados disponiveis)
    Recepção: onCharacteristicChanged()-> extratCommand() -> ProcessComando
    */
    public int sendDeviceID(byte id,long delay){
        byte data[] ={(byte)(id)};
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                sendCommand((byte)SampleGattAttributes.RD_REQUEST_CONNECTION,data,1);
            }
        };
        handler.postDelayed(runnable,delay);
        return 0;
    }

    public int sendOpCode(byte opcode, long delay){
        byte data[] ={(byte)opcode};
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                sendCommand(opcode,data,0);
            }
        };
        handler.postDelayed(runnable,delay);
        return 0;
    }

    public int sendOtaUrl(int[] ip, int port, long delay){
        byte data[] ={(byte)ip[0],
                (byte)ip[1],
                (byte)ip[2],
                (byte)ip[3],
                (byte)(port>>8),
                (byte)port};
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                sendCommand((byte)SampleGattAttributes.WR_URL_HTTPS_SERVER_OTA,data,6);
            }
        };
        handler.postDelayed(runnable,delay);
        return 0;
    }

    public int sendGetOvercraneParam(long delay){
        byte data[] ={(byte)SampleGattAttributes.WR_GET_OVERCRANE_PARAM};
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                sendCommand((byte)SampleGattAttributes.WR_GET_OVERCRANE_PARAM,data,0);
            }
        };
        handler.postDelayed(runnable,delay);
        return 0;
    }

    public int sendSetOvercraneParam(int xOffSet, int yOffSet, int minSecDist, int maxSecDist, int minAltura, int maxAltura, long delay){
        byte data[] ={(byte)(xOffSet>>8),
                (byte)xOffSet,
                (byte)(yOffSet>>8),
                (byte)yOffSet,
                (byte)(maxSecDist>>8),
                (byte)maxSecDist,
                (byte)(minSecDist>>8),
                (byte)minSecDist,
                (byte)(maxAltura>>8),
                (byte)maxAltura,
                (byte)(minAltura>>8),
                (byte)minAltura};

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                sendCommand((byte)SampleGattAttributes.WR_SET_OVERCRANE_PARAM,data,12);
            }
        };
        handler.postDelayed(runnable,delay);
        return 0;
    }

    public int sendAuraMode(int mode, long delay){
        byte data[] ={(byte)mode};
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                sendCommand((byte)SampleGattAttributes.WR_SET_OVERCRANE_MODE,data,1);
            }
        };
        handler.postDelayed(runnable,delay);
        return 0;
    }

    public int sendFixTag(byte[] tag, long delay) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                sendCommand((byte) SampleGattAttributes.WR_SET_FIX_TAG, tag, tag.length);
            }
        };
        handler.postDelayed(runnable, delay);
        return 0;
    }

    public int sendDecawaveIDList(byte[] tagList, long delay) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                sendCommand((byte) SampleGattAttributes.WR_DECA_ID_LIST, tagList, tagList.length);
            }
        };
        handler.postDelayed(runnable, delay);
        return 0;
    }

    public int sendNearFarDistance(byte opcode, int distance, long delay){
        byte data[] ={(byte)255,
                (byte)255,
                (byte)(distance>>8),
                (byte)distance};
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                sendCommand(opcode,data,4);
            }
        };
        handler.postDelayed(runnable,delay);
        return 0;
    }

    public int sendTagIdDistance(int device, byte[] tag, int distance, long delay){
        byte data[] ={(byte)255,
                (byte)255,
                (byte)tag[0],
                (byte)tag[1],
                (byte)(distance>>8),
                (byte)distance,
                (byte)device};
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                sendCommand((byte)SampleGattAttributes.WR_TAG_ID_DISTANCE,data,7);
            }
        };
        handler.postDelayed(runnable,delay);
        return 0;
    }

    public int sendConfigBroker(byte[] brokerConfig, long delay) {

        for (int i=0; i<brokerConfig.length; i++){
            if(brokerConfig[i]<0){
                brokerConfig[i]+=256;
            }
            Log.i(TAG, "brokerConfig["+i+"]="+Integer.toString(brokerConfig[i]));
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                sendCommand((byte) SampleGattAttributes.WR_BROKER_MQTT_CONFIG, brokerConfig, brokerConfig.length);
            }
        };
        handler.postDelayed(runnable, delay);
        return 0;
    }


    public List<Short> getDecaWaveIDList(){
        return this.decaWaveIDList;
    }
    public List<Short> getOffsetList(){
        return this.offsetList;
    }
    public List<Short> getFarDistanceList(){
        return this.farDistanceList;
    }
    public List<Short> getNearDistanceList(){
        return this.nearDistanceList;
    }

    @Override
    public void extractMessage(int newData) {
        extractCommand(newData);
    }
}
