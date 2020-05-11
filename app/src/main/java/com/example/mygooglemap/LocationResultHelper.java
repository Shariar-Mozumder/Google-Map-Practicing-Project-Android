package com.example.mygooglemap;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class LocationResultHelper {

    private Context context;
    private List<Location> mLocationList;

    public LocationResultHelper(Context context, List<Location> mLocationList) {
        this.context = context;
        this.mLocationList = mLocationList;
    }

    public String getLocationResultText(){
        StringBuilder sb=new StringBuilder();
        for(Location location:mLocationList){
            sb.append("(");
            sb.append(location.getLatitude());
            sb.append(", ");
            sb.append(location.getLongitude());
            sb.append(")");
            sb.append("\n");
        }

        return sb.toString();
    }

    private CharSequence getLocationResultTitle() {
        String result=context.getResources().getQuantityString(R.plurals.num_locations_reported,
                mLocationList.size());
        return result+" "+ DateFormat.getDateTimeInstance().format(new Date());
    }

    public void showNotification(){
        Intent notificationIntent= new Intent(context,BatchLocationActivity.class);
        //construct a task stack
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            //Construct a task stack
            TaskStackBuilder stackBuilder=TaskStackBuilder.create(context);
            //add the main Activity to the task stack as the parent
            stackBuilder.addParentStack(MainActivity.class);

            //push the content Intent onto the stack
            stackBuilder.addNextIntent(notificationIntent);

            //get a pendingIntent Containing the entire back stack

            PendingIntent notificationPendingIntent=
                    stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder notificationBuilder=null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationBuilder=new Notification.Builder(context,
                        App.CHANNEL_ID)
            .setContentTitle(getLocationResultTitle())
                        .setContentText(getLocationResultText())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setContentIntent(notificationPendingIntent);
            }

            getNotificationManager().notify();

        }
    }

    private Object getNotificationManager() {

        NotificationManager manager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }


}
