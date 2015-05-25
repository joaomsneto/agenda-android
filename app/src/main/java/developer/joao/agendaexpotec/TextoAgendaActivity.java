package developer.joao.agendaexpotec;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class TextoAgendaActivity extends Activity {

    int id;
    String hora;
    String titulo;
    Agenda agenda;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texto_agenda);

        Intent intent = getIntent();
        agenda = (Agenda) intent.getSerializableExtra("agenda");
        id = agenda.getId();
        titulo = agenda.getTitulo();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy_HH:mm");
        Date dataCompleta = new Date();
//        dataCompleta.setDate(agenda.getDia().getDay());
//        dataCompleta.setMonth(agenda.getDia().getMonth());
//        dataCompleta.setYear(agenda.getDia().getYear());
//        dataCompleta.setHours(agenda.getHoraInicial().getHours());
//        dataCompleta.setMinutes(agenda.getHoraInicial().getMinutes());
        dataCompleta.setDate(24);
        dataCompleta.setMonth(4);
        dataCompleta.setYear(115);
        dataCompleta.setHours(17);
        dataCompleta.setMinutes(22);
        hora = simpleDateFormat.format(dataCompleta);
        TextView textViewTextoAgenda = (TextView) findViewById(R.id.textoAgenda);
        Typeface fontArimo = Typeface.createFromAsset(getAssets(), "Arimo-Bold.ttf");
        textViewTextoAgenda.setTypeface(fontArimo);
        textViewTextoAgenda.setText(Html.fromHtml(agenda.getTexto()).toString());

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_texto_agenda, menu);
        this.menu = menu;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy_HH:mm");
        try {
            if( simpleDateFormat.parse(hora).compareTo(Calendar.getInstance().getTime()) > 0 ) {
                SharedPreferences sharedPreferences = getSharedPreferences("alarms", MODE_PRIVATE);
                Set<String> alarmsSet = sharedPreferences.getStringSet("alarms", new HashSet<String>());
                if (alarmsSet.contains(String.valueOf(id))) {
                    menu.add(0, 0, 1, "Desmarcar Alarme").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                } else {
                    menu.add(0, 1, 1, "Marcar Alarme").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idMenuItem = item.getItemId();

        if( idMenuItem == android.R.id.home ) {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            return true;
        } else if( idMenuItem == 1) {
            SimpleDateFormat desformatterHora = new SimpleDateFormat("dd-MM-yyyy_HH:mm");
            Date dataFormatada = null;
            try {
                dataFormatada = desformatterHora.parse(hora);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if( dataFormatada.compareTo(Calendar.getInstance().getTime()) > 0 ) {

                Intent intent = new Intent(TextoAgendaActivity.this, ReceptorAgenda.class);
                intent.putExtra("ID_ALERT", id);
                intent.putExtra("TIME_ALERT", hora);
                intent.putExtra("TITLE_ALERT", titulo);
                intent.putExtra("AGENDA_ALERT", agenda);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(TextoAgendaActivity.this, id, intent, PendingIntent.FLAG_ONE_SHOT);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dataFormatada);
                Toast.makeText(getApplicationContext(), "Hora Certa" + dataFormatada.toString(), Toast.LENGTH_LONG).show();
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                SharedPreferences sharedPreferences = getSharedPreferences("alarms", MODE_PRIVATE);
                Set<String> alarmsSet = sharedPreferences.getStringSet("alarms", new HashSet<String>());
                alarmsSet.add(String.valueOf(id));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putStringSet("alarms", alarmsSet);
                editor.commit();

                menu.removeItem(1);
                menu.add(0, 0, 1, "Desmarcar Alarme").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            } else {
                Toast.makeText(this, "Evento já iniciado.", Toast.LENGTH_LONG).show();
                menu.removeItem(1);
            }
        } else if( idMenuItem == 0 ) {
            SimpleDateFormat desformatterHora = new SimpleDateFormat("dd-MM-yyyy_HH:mm");
            Date dataFormatada = null;
            try {
                dataFormatada = desformatterHora.parse(hora);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if( dataFormatada.compareTo(Calendar.getInstance().getTime()) > 0 ) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                Intent intent = new Intent(TextoAgendaActivity.this, ReceptorAgenda.class);
                intent.putExtra("ID_ALERT", id);
                intent.putExtra("TIME_ALERT", hora);
                intent.putExtra("TITLE_ALERT", titulo);
                intent.putExtra("AGENDA_ALERT", agenda);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(TextoAgendaActivity.this, id, intent, PendingIntent.FLAG_ONE_SHOT);

                alarmManager.cancel(pendingIntent);

                SharedPreferences sharedPreferences = getSharedPreferences("alarms", MODE_PRIVATE);
                Set<String> alarmsSet = sharedPreferences.getStringSet("alarms", new HashSet<String>());
                alarmsSet.remove(String.valueOf(id));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putStringSet("alarms", alarmsSet);
                editor.commit();

                menu.removeItem(0);
                menu.add(0, 1, 1, "Marcar Alarme").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            } else {
                Toast.makeText(this, "Evento já iniciado.", Toast.LENGTH_LONG).show();
                menu.removeItem(0);
            }
        }

        return false;
    }
}
