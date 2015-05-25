package developer.joao.agendaexpotec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public final class ReceptorAgenda extends BroadcastReceiver {
    public ReceptorAgenda() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent service = new Intent(context, MyAlarmService.class);
        service.putExtra("TITLE_ALERT", intent.getStringExtra("TITLE_ALERT"));
        service.putExtra("AGENDA_ALERT", intent.getSerializableExtra("AGENDA_ALERT"));
        context.startService(service);
    }
}
