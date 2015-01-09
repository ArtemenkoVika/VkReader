package com.example.admin.vkreader.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.admin.vkreader.R;
import com.example.admin.vkreader.activity.NotificationActivityStart;
import com.example.admin.vkreader.activity.ResultNotificationActivity;
import com.example.admin.vkreader.async_task.ParseTask;
import com.example.admin.vkreader.patterns.Singleton;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class UpdateService extends Service {
    public static final int mStartId = 001;
    public static final int mUpdateId = 002;
    private NotificationManager manager;
    private PendingIntent resultPendingIntent;
    private PendingIntent pendingIntentStart;
    private Singleton singleton = Singleton.getInstance();
    private Timer mTimer = new Timer();
    private MyTimerTask mMyTimerTask = new MyTimerTask();

    @Override
    public void onCreate() {
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent(this,
                ResultNotificationActivity.class);
        resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                resultIntent, 0);

        Intent startIntent = new Intent(this, NotificationActivityStart.class);
        pendingIntentStart = PendingIntent.getActivity(getApplicationContext(), 0, startIntent, 0);
        if (singleton.count == 1) {
            showNotification(getResources().getString(R.string.ticker),
                    getResources().getString(R.string.contentTitle),
                    getResources().getString(R.string.contentText), mStartId,
                    pendingIntentStart);

            Toast.makeText(this, getResources().getString(R.string.service_started),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mTimer.schedule(mMyTimerTask, AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mTimer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void showNotification(String ticker, String contentTitle, String contentText, int id,
                                 PendingIntent pendingIntent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.portfolio)
                .setAutoCancel(true)
                .setTicker(ticker)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setDefaults(Notification.DEFAULT_ALL)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.portfolio));
        manager.notify(id, mBuilder.build());
    }

    class MyTimerTask extends TimerTask {
        ParseTask parseTask;
        final Handler uiHandler = new Handler();

        @Override
        public void run() {
            if (isOnline()) {
                parseTask = new ParseTask(getResources().getString(R.string.url2));
                parseTask.execute();
            } else {
                UpdateService.this.stopSelf();
                return;
            }
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!singleton.isDataBase())
                            singleton.getArrayAdapter().addAll(parseTask.get());
                    } catch (InterruptedException e) {
                        System.out.println(e + " - in UpdateService");
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        System.out.println(e + " - in UpdateService");
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        System.out.println(e + " - in UpdateService");
                        e.printStackTrace();
                    }
                    manager.cancel(mUpdateId);
                    showNotification(getResources().getString(R.string.ticker2),
                            getResources().getString(R.string.contentTitle2),
                            getResources().getString(R.string.contentText2), mUpdateId,
                            resultPendingIntent);
                    Toast.makeText(UpdateService.this, getResources().getString(R.string.app_updated),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() == null) {
            return false;
        } else return connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
