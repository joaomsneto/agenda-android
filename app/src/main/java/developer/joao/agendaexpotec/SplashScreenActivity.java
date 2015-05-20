package developer.joao.agendaexpotec;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.TreeMap;

import network.JsonParser;


public class SplashScreenActivity extends Activity {

    TreeMap<String, Agenda> agenda;
    public static Boolean atualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final ImageView animImageView = (ImageView) findViewById(R.id.gif_splash);
        animImageView.setBackgroundResource(R.drawable.gif_splash);
        animImageView.post(new Runnable() {
            @Override
            public void run() {
                AnimationDrawable frameAnimation =
                        (AnimationDrawable) animImageView.getBackground();
                frameAnimation.start();
            }
        });

        Agenda agendaObjeto = new Agenda(getApplicationContext());
        try {
            ConnectivityManager conectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            Boolean haConexao = conectivityManager.getActiveNetworkInfo() != null
                    && conectivityManager.getActiveNetworkInfo().isAvailable()
                    && conectivityManager.getActiveNetworkInfo().isConnected();

            if(haConexao && (atualizar == null || atualizar == true)) {
                agendaObjeto.limpar();
            }

            TreeMap<String, Agenda> listaAgenda = agendaObjeto.listar("titulo IS NOT null", "dia ASC, sala ASC, hora_inicial ASC");
            if( !listaAgenda.isEmpty() ) {
                agenda = listaAgenda;
                Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("agenda", agenda);
                mainIntent.putExtras(bundle);
                startActivity(mainIntent);
                finish();
            } else {

                if (haConexao) {
                    new PrecarregarDados().execute();
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                    alertDialog.setTitle("Não há conexão");
                    alertDialog.setMessage("No seu primeiro acesso, é necessário alguma conexão com a internet"+listaAgenda.size());
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    alertDialog.show();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class PrecarregarDados extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("http")
                      .authority("www.expotec.org.br")
                      .appendPath("infoAgendaJson.html")
                      .appendQueryParameter("auth", "batman");

            JsonParser jsonParser = new JsonParser();

            String json = jsonParser.getJSONFromUrl(uriBuilder.build().toString());

            if( json != null ) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(json);
                    JSONArray jsonArrayAgenda = jsonObject.getJSONArray("agenda");
                    int tamanhoAgenda = jsonArrayAgenda.length();
                    agenda = new TreeMap<String, Agenda>();
                    for( int i = 0; i < tamanhoAgenda; i++ ) {
                        JSONObject objetoJsonAgenda = jsonArrayAgenda.getJSONObject(i);
                        Iterator<String> keysObjetoAgenda = objetoJsonAgenda.keys();
                        JSONObject objetoAgenda = (JSONObject) objetoJsonAgenda.get(keysObjetoAgenda.next());
                        Agenda agendaTemp = new Agenda(getApplicationContext());
                        agendaTemp.setId(objetoAgenda.getInt("id"));
                        SimpleDateFormat formatterDia = new SimpleDateFormat("yyyy-MM-dd");
                        agendaTemp.setDia(formatterDia.parse(objetoAgenda.getString("dia")));
                        SimpleDateFormat formatterHora = new SimpleDateFormat("HH:mm");
                        agendaTemp.setHoraInicial(formatterHora.parse(objetoAgenda.getString("hora_inicial")));
                        agendaTemp.setHoraFinal(formatterHora.parse(objetoAgenda.getString("hora_final")));
                        agendaTemp.setSala(objetoAgenda.getString("sala"));
                        agendaTemp.setCor(objetoAgenda.getString("cor"));
                        agendaTemp.setLink(objetoAgenda.getString("link"));
                        agendaTemp.setTitulo(objetoAgenda.getString("titulo"));
                        agendaTemp.setTexto(objetoAgenda.getString("texto"));
                        agendaTemp.salvar();
                        agenda.put(formatterDia.format(agendaTemp.getDia()) + "|-|" + agendaTemp.getSala() + "|-|" + formatterHora.format(agendaTemp.getHoraInicial()), agendaTemp);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void resultado) {
            super.onPostExecute(resultado);

            Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("agenda", agenda);
            mainIntent.putExtras(bundle);
            startActivity(mainIntent);

            finish();
        }

    }
}
