package com.pins.infinity.utility;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

/**
 * Created by bimalchawla on 10/7/17.
 */

public final class TelephonyInfo {

    private static TelephonyInfo telephonyInfo;
    private String imeiSIM1;
    private String imeiSIM2;
    private String subscriber1;
    private String subscriber2;
    private String network1;
    private String network2;
    private String serial1;
    private String serial2;
    private String simCountry1;
    private String simCountry2;
    private boolean isSIM1Ready;
    private boolean isSIM2Ready;

    public String getImsiSIM1() {
        return imeiSIM1;
    }

    /*public static void setImsiSIM1(String imeiSIM1) {
        TelephonyInfo.imeiSIM1 = imeiSIM1;
    }*/

    public String getImsiSIM2() {
        return imeiSIM2;
    }

    /*public static void setImsiSIM2(String imeiSIM2) {
        TelephonyInfo.imeiSIM2 = imeiSIM2;
    }*/

    public boolean isSIM1Ready() {
        return isSIM1Ready;
    }

    /*public static void setSIM1Ready(boolean isSIM1Ready) {
        TelephonyInfo.isSIM1Ready = isSIM1Ready;
    }*/

    public boolean isSIM2Ready() {
        return isSIM2Ready;
    }

    /*public static void setSIM2Ready(boolean isSIM2Ready) {
        TelephonyInfo.isSIM2Ready = isSIM2Ready;
    }*/

    public String getSubscriber1() {
        return subscriber1;
    }

    public void setSubscriber1(String subscriber1) {
        this.subscriber1 = subscriber1;
    }

    public String getSubscriber2() {
        return subscriber2;
    }

    public void setSubscriber2(String subscriber2) {
        this.subscriber2 = subscriber2;
    }

    public String getNetwork1() {
        return network1;
    }

    public void setNetwork1(String network1) {
        this.network1 = network1;
    }

    public String getNetwork2() {
        return network2;
    }

    public void setNetwork2(String network2) {
        this.network2 = network2;
    }

    public String getSerial1() {
        return serial1;
    }

    public void setSerial1(String serial1) {
        this.serial1 = serial1;
    }

    public String getSerial2() {
        return serial2;
    }

    public void setSerial2(String serial2) {
        this.serial2 = serial2;
    }

    public String getSimCountry1() {
        return simCountry1;
    }

    public void setSimCountry1(String simCountry1) {
        this.simCountry1 = simCountry1;
    }

    public String getSimCountry2() {
        return simCountry2;
    }

    public void setSimCountry2(String simCountry2) {
        this.simCountry2 = simCountry2;
    }

    public boolean isDualSIM() {
        return imeiSIM2 != null;
    }

    private TelephonyInfo() {
    }

    public static TelephonyInfo getInstance(Context context){

        if(telephonyInfo == null) {

            telephonyInfo = new TelephonyInfo();

            TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));

            telephonyInfo.imeiSIM1 = telephonyManager.getDeviceId();
            telephonyInfo.imeiSIM2 = null;

            try {
                telephonyInfo.imeiSIM1 = getDeviceIdBySlot(context, "getDeviceIdGemini", 0);
                telephonyInfo.imeiSIM2 = getDeviceIdBySlot(context, "getDeviceIdGemini", 1);
            } catch (GeminiMethodNotFoundException e) {
                e.printStackTrace();

                try {
                    telephonyInfo.imeiSIM1 = getDeviceIdBySlot(context, "getDeviceId", 0);
                    telephonyInfo.imeiSIM2 = getDeviceIdBySlot(context, "getDeviceId", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                }
            }

            telephonyInfo.subscriber1 = telephonyManager.getSubscriberId();;
            telephonyInfo.subscriber2 = null;

            try {
                telephonyInfo.subscriber1 = getSubscriberIdBySlot(context, "getSubscriberIdGemini", 0);
                telephonyInfo.subscriber2 = getSubscriberIdBySlot(context, "getSubscriberIdGemini", 1);
            } catch (GeminiMethodNotFoundException e) {
                e.printStackTrace();

                try {
                    telephonyInfo.subscriber1 = getSubscriberIdBySlot(context, "getSubscriberId", 0);
                    telephonyInfo.subscriber2 = getSubscriberIdBySlot(context, "getSubscriberId", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                }
            }

            telephonyInfo.network1 = telephonyManager.getNetworkOperatorName();;
            telephonyInfo.network2 = null;

            try {
                telephonyInfo.network1 = getSubscriberIdBySlot(context, "getNetworkOperatorNameGemini", 0);
                telephonyInfo.network2 = getSubscriberIdBySlot(context, "getNetworkOperatorNameGemini", 1);
            } catch (GeminiMethodNotFoundException e) {
                e.printStackTrace();

                try {
                    telephonyInfo.network1 = getSubscriberIdBySlot(context, "getNetworkOperatorName", 0);
                    telephonyInfo.network2 = getSubscriberIdBySlot(context, "getNetworkOperatorName", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                }
            }

            telephonyInfo.simCountry1 = telephonyManager.getSimCountryIso();;
            telephonyInfo.simCountry2 = null;

            try {
                telephonyInfo.simCountry1 = getSubscriberIdBySlot(context, "getSimCountryIsoGemini", 0);
                telephonyInfo.simCountry2 = getSubscriberIdBySlot(context, "getSimCountryIsoGemini", 1);
            } catch (GeminiMethodNotFoundException e) {
                e.printStackTrace();

                try {
                    telephonyInfo.simCountry1 = getSubscriberIdBySlot(context, "getSimCountryIso", 0);
                    telephonyInfo.simCountry2 = getSubscriberIdBySlot(context, "getSimCountryIso", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                }
            }

            telephonyInfo.serial1 = telephonyManager.getSimSerialNumber();;
            telephonyInfo.serial2 = null;

            try {
                telephonyInfo.serial1 = getSubscriberIdBySlot(context, "getSimSerialNumberGemini", 0);
                telephonyInfo.serial2 = getSubscriberIdBySlot(context, "getSimSerialNumberGemini", 1);
            } catch (GeminiMethodNotFoundException e) {
                e.printStackTrace();

                try {
                    telephonyInfo.serial1 = getSubscriberIdBySlot(context, "getSimSerialNumber", 0);
                    telephonyInfo.serial2 = getSubscriberIdBySlot(context, "getSimSerialNumber", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                }
            }

            telephonyInfo.isSIM1Ready = telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
            telephonyInfo.isSIM2Ready = false;

            try {
                telephonyInfo.isSIM1Ready = getSIMStateBySlot(context, "getSimStateGemini", 0);
                telephonyInfo.isSIM2Ready = getSIMStateBySlot(context, "getSimStateGemini", 1);
            } catch (GeminiMethodNotFoundException e) {

                e.printStackTrace();

                try {
                    telephonyInfo.isSIM1Ready = getSIMStateBySlot(context, "getSimState", 0);
                    telephonyInfo.isSIM2Ready = getSIMStateBySlot(context, "getSimState", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                }
            }
        }

        return telephonyInfo;
    }

    private static String getDeviceIdBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {
        String imei = null;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try{
            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);
            if(ob_phone != null){
                imei = ob_phone.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }
        return imei;
    }

    private static String getSubscriberIdBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {
        String imei = null;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try{
            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);
            if(ob_phone != null){
                imei = ob_phone.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }
        return imei;
    }

    private static  boolean getSIMStateBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {
        boolean isReady = false;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try{
            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimStateGemini = telephonyClass.getMethod(predictedMethodName, parameter);
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimStateGemini.invoke(telephony, obParameter);
            if(ob_phone != null){
                int simState = Integer.parseInt(ob_phone.toString());
                if(simState == TelephonyManager.SIM_STATE_READY){
                    isReady = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return isReady;
    }


    private static class GeminiMethodNotFoundException extends Exception {

        private static final long serialVersionUID = -996812356902545308L;

        public GeminiMethodNotFoundException(String info) {
            super(info);
        }
    }
}