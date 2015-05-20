package developer.joao.agendaexpotec;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.ShareActionProvider;
import android.widget.TextView;


public class TextoAgendaActivity extends Activity {

    Intent mShareIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texto_agenda);

        Intent intent = getIntent();
        Bundle extra = intent.getExtras();
        String textoAgenda = extra.getString("textoAgenda");
        TextView textViewTextoAgenda = (TextView) findViewById(R.id.textoAgenda);
        Typeface fontArimo = Typeface.createFromAsset(getAssets(), "Arimo-Bold.ttf");
        textViewTextoAgenda.setTypeface(fontArimo);
        textViewTextoAgenda.setText(Html.fromHtml(textoAgenda).toString());

    }
}
