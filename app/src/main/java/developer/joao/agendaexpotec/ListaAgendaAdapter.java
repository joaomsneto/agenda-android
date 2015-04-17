package developer.joao.agendaexpotec;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by joao on 13/04/15.
 */
public class ListaAgendaAdapter extends ArrayAdapter<Agenda> {

    private Context context;
    private List<Agenda> agendas = null;

    public ListaAgendaAdapter(Context context, List<Agenda> agendas) {
        super(context, 0, agendas);
        this.agendas = agendas;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Agenda agenda = agendas.get(position);

        if( convertView == null )
            convertView = LayoutInflater.from(context).inflate(R.layout.item_sala, null);

        if( agenda.getCor() != null ) {
            int cor = Color.parseColor("#" + agenda.getCor());
//            GradientDrawable drawable = new GradientDrawable();
//            drawable.setBounds(0, 0,0, 10);
//            drawable.setShape(GradientDrawable.RECTANGLE);
//            drawable.setStroke(3, cor);
//            Drawable[] layers = new Drawable[1];
//            layers[0] = drawable;
//            LayerDrawable layerList = new LayerDrawable(layers);
            LayerDrawable layerList = (LayerDrawable) context.getResources().getDrawable(R.drawable.border_bottom_item_sala);
            GradientDrawable itemBorda = (GradientDrawable) layerList.findDrawableByLayerId(R.id.border_item_sala);
            itemBorda.setColor(cor);
            convertView.setBackgroundDrawable(layerList);
        }

        TextView tituloAgenda = (TextView) convertView.findViewById(R.id.titulo_agenda);
        tituloAgenda.setText(agenda.getTitulo());

        SimpleDateFormat formatterHora = new SimpleDateFormat("HH:mm");

        TextView horaInicialAgenda = (TextView) convertView.findViewById(R.id.hora_inicial_agenda);
        String horaInicial = formatterHora.format(agenda.getHoraInicial());
        horaInicialAgenda.setText(horaInicial);

        TextView horaFinalAgenda = (TextView) convertView.findViewById(R.id.hora_final_agenda);
        String horaFinal = formatterHora.format(agenda.getHoraFinal());
        horaFinalAgenda.setText(horaFinal);

        return convertView;
    }
}
