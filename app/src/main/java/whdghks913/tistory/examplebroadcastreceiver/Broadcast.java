package whdghks913.tistory.examplebroadcastreceiver;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.telephony.SmsMessage;
import android.util.Log;

import com.sincere.kboss.LoginActivity;

public class Broadcast extends BroadcastReceiver {
    LoginActivity.SmsReceiveCallback src;

    public Broadcast () {

    }

    public Broadcast(LoginActivity.SmsReceiveCallback smsReceiveCallback) {
        src = smsReceiveCallback;
    }

    public void onReceive(Context context, Intent intent) {
/*        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            //Log.d("onReceive()","���ÿϷ�");
        }
        if (Intent.ACTION_SCREEN_ON == intent.getAction()) {
            //Log.d("onReceive()","��ũ�� ON");
        }
        if (Intent.ACTION_SCREEN_OFF == intent.getAction()) {
            //Log.d("onReceive()","��ũ�� OFF");
        }
        if ("android.provider.Telephony.SMS_RECEIVED" == intent.getAction()) {
            //Log.d("onReceive()","���ڰ� ���ŵǾ���ϴ�");

            // SMS 메시지를 파싱합니다.
            Bundle bundle = intent.getExtras();
            Object messages[] = (Object[])bundle.get("pdus");
            SmsMessage smsMessage[] = new SmsMessage[messages.length];
            
            for(int i = 0; i < messages.length; i++) {
                // PDU 포맷으로 되어 있는 메시지를 복원합니다.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String format = bundle.getString("format");
                    messages[i] = SmsMessage.createFromPdu((byte[])messages[i], format);
                }
                else {
                    smsMessage[i] = SmsMessage.createFromPdu((byte[]) messages[i]);
                }
            }

            // SMS 수신 시간 확인
            Date curDate = new Date(smsMessage[0].getTimestampMillis());

            // SMS 발신 번호 확인
            String origNumber = smsMessage[0].getOriginatingAddress();

            // SMS 메시지 확인
            String message = smsMessage[0].getMessageBody().toString();

            int start = message.lastIndexOf('[');
            int end = message.lastIndexOf(']');

            src.onReceive(message.substring(start+1, end));
        }*/
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage
                            .createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage
                            .getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String code = currentMessage.getDisplayMessageBody();

                    int start = code.lastIndexOf('[');
                    int end = code.lastIndexOf(']');

                    code = code.substring(start + 1, end);

//                    if (Util.g_handler != null
//                            && (senderNum.equals("16441903") || senderNum.equals("1644-1903"))) {
//                    if (senderNum.equals("13689706901")) {
                    if (senderNum.equals("010-4703-6688") || senderNum.equals("01047036688")) {
                        src.onReceive(code);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }
}
