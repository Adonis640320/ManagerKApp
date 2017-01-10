package com.sincere.kboss.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.sincere.kboss.KbossApplication;
import com.sincere.kboss.LoginActivity;
import com.sincere.kboss.R;
import com.sincere.kboss.SplashActivity;
import com.sincere.kboss.service.ServiceParams;
import com.sincere.kboss.worker.JobListFragment;
import com.sincere.kboss.worker.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kakoapps on 2016-09-12.
 */
public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";

    int workend = 0;
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e("test", "푸쉬 도착"+remoteMessage.getData().toString());
        //$message = array("message" => "일자리 알림 도착! 빨리 근로지원해주세요.","addjob"=>"1");
        try {
//            Intent intent = new Intent(MainActivity.PushReceiver.ACTION_RECEIVE_PUSH);
//            String val = remoteMessage.getData().get("addjob");
//            intent.putExtra("push", val);
//            sendBroadcast(intent);
            Intent intent = new Intent(MainActivity.ACTION_RECEIVE_PUSH);
            if(remoteMessage.getData().containsKey("addjob")) {
                int addjob = Integer.valueOf(remoteMessage.getData().get("addjob"));
                intent.putExtra("addjob", addjob);
            }
            if(remoteMessage.getData().containsKey("workend")) {
                workend = Integer.valueOf(remoteMessage.getData().get("workend"));
                intent.putExtra("workend", workend);
            }
            LocalBroadcastManager.getInstance(getApplicationContext())
                    .sendBroadcast(intent);
        }catch(Exception e){}

        sendNotification(remoteMessage.getData().get("message"));
    }

    private void sendNotification(String messageBody) {
//        Bitmap bitmapImage = getBitmapFromURL(img.trim());
        Intent intent = null;
        KbossApplication.getInstance().setSharedPreferencesData("workend",workend);
        Log.e("test","workend:"+workend);
        intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //        intent.putExtra(KEY_INTENT_GCMTYPE, data.getString(HANDLER_KEY_TYPE));

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);//FLAG_ONE_SHOT

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(this.getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

//        if(bitmapImage != null) {
//            NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
//            style.setSummaryText(messageBody);
//            style.bigPicture(bitmapImage);
//            notificationBuilder.setStyle(style);
//        }


        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }


    public Bitmap getBitmapFromURL(String src) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(src);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally{
            if(connection!=null)connection.disconnect();
        }
    }

}
