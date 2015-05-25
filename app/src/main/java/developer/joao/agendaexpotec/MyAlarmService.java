package developer.joao.agendaexpotec;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;

import java.util.HashSet;
import java.util.Set;

public class MyAlarmService extends Service {
    NotificationManager nManager;

    public MyAlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        nManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(this.getApplicationContext(), TextoAgendaActivity.class);
        Agenda agenda = (Agenda) intent.getSerializableExtra("AGENDA_ALERT");
        intent1.putExtra("agenda", agenda);

        SharedPreferences sharedPreferences = getSharedPreferences("alarms", MODE_PRIVATE);
        Set<String> alarmsSet = sharedPreferences.getStringSet("alarms", new HashSet<String>());
        alarmsSet.remove(String.valueOf(agenda.getId()));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("alarms", alarmsSet);
        editor.commit();

        Notification notification = new Notification(R.mipmap.ic_launcher, intent.getStringExtra("TITLE_ALERT"), System.currentTimeMillis());
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity( this.getApplicationContext(),0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(this.getApplicationContext(), "Expotec - Em 5 minutos...", intent.getStringExtra("TITLE_ALERT"), pendingNotificationIntent);

        nManager.notify(0, notification);

        Vibrator v = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(2000);

        Uri alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone ringtoneAlarm = RingtoneManager.getRingtone(getApplicationContext(), alarmTone);
        ringtoneAlarm.play();


    }
}
