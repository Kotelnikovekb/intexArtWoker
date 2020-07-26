package ru.ekbapp.intexartwoker.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import ru.ekbapp.intexartwoker.MainActivity;
import ru.ekbapp.intexartwoker.R;

import static ru.ekbapp.intexartwoker.StartUpActivity.SESSION_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.SETTINGS_S;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService  {
    @Override
    public void onNewToken(String mToken) {
        super.onNewToken(mToken);
        Log.e("TOKEN",mToken);
        //showNotification("Ошибка отправки уведомлений","test",new Intent(getApplicationContext(),MainActivity.class),"1");


    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage){
        String action=remoteMessage.getData().get("action");
        SharedPreferences sharedPreferences=getSharedPreferences(SETTINGS_S,MODE_PRIVATE);
        if (remoteMessage.getData().get("forUser").equals(sharedPreferences.getString(SESSION_S,""))){
            showNotification(
                    remoteMessage.getData().get("title"),
                    remoteMessage.getData().get("text"),
                    new Intent(getApplicationContext(),MainActivity.class),
                    remoteMessage.getData().get("id"));
            if (!action.equals("NEW_ADS")){
                EventBus.getDefault().post("");
            }

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    void showNotification(String title,String message, Intent intent, String date){
        long[] vibrate = { 0, 100, 200, 300 };
        Log.e("TOKEN","Начало создания уведомления" );
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(R.drawable.ic_stat_name) // notification icon
                .setContentTitle(title)
                //.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.logo_1))
                .setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.s))
                .setVibrate(vibrate)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setContentText(message)
                .setAutoCancel(true);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.drawable.ic_stat_name);
            mBuilder.setColor(getResources().getColor(R.color.white));
        } else {
            mBuilder.setSmallIcon(R.drawable.logo_1);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.e("TOKEN","Этап 3: ОК" );

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // PendingIntent pi = getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mBuilder.setContentIntent(pendingIntent);


        mNotificationManager.notify(date,Integer.parseInt(date), mBuilder.build());
        Log.e("TOKEN","Уведомление создано" );

    }


}
