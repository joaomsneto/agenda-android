package developer.joao.agendaexpotec;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

import database.DBHelper;

/**
 * Created by joao on 12/04/15.
 */
public class Agenda implements Serializable, Comparable {
    private int id;
    private Date dia;
    private Date hora_inicial;
    private Date hora_final;
    private String sala;
    private String link;
    private String cor;
    private String titulo;
    private String texto;

    private static Context context;
    private static final String TABLE_NAME = "agenda";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDia() {
        return dia;
    }

    public void setDia(Date dia) {
        this.dia = dia;
    }

    public Date getHoraInicial() {
        return hora_inicial;
    }

    public void setHoraInicial(Date hora_inicial) {
        this.hora_inicial = hora_inicial;
    }

    public Date getHoraFinal() {
        return hora_final;
    }

    public void setHoraFinal(Date hora_final) {
        this.hora_final = hora_final;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Agenda( Context context) {
        this.context = context;
    }

    public void selecionarPorCondicao(String condicao) throws Exception {
        Cursor cursor = null;

        SQLiteDatabase sqlLite = new DBHelper(this.context).getReadableDatabase();

        String where = condicao;

        String[] colunas = new String[] { "id",
                                          "dia",
                                          "hora_inicial",
                                          "hora_final",
                                          "sala",
                                          "cor",
                                          "link",
                                          "titulo",
                                          "texto" };

        String argumentos[] = new String[] { };

        cursor = sqlLite.query(TABLE_NAME, colunas, where, argumentos, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            this.setId(cursor.getInt(cursor.getColumnIndex("id")));
            SimpleDateFormat formatterDia = new SimpleDateFormat("yyyy-MM-dd");
            this.setDia(formatterDia.parse(cursor.getString(cursor.getColumnIndex("dia"))));
            SimpleDateFormat formatterHora = new SimpleDateFormat("HH:mm");
            this.setHoraInicial(formatterHora.parse(cursor.getString(cursor.getColumnIndex("hora_inicial"))));
            this.setHoraFinal(formatterHora.parse(cursor.getString(cursor.getColumnIndex("hora_final"))));
            this.setSala(cursor.getString(cursor.getColumnIndex("sala")));
            this.setCor(cursor.getString(cursor.getColumnIndex("cor")));
            this.setLink(cursor.getString(cursor.getColumnIndex("link")));
            this.setTitulo(cursor.getString(cursor.getColumnIndex("titulo")));
            this.setTexto(cursor.getString(cursor.getColumnIndex("texto")));

        }

        if (cursor != null)
            cursor.close();

    }

    public TreeMap<String, Agenda> listar(String condicao, String orderBy) throws Exception {

        Cursor cursor = null;

        SQLiteDatabase sqlLite = new DBHelper(this.context).getReadableDatabase();
        String where = condicao;
        String[] colunas = new String[] { "id",
                "dia",
                "hora_inicial",
                "hora_final",
                "sala",
                "cor",
                "link",
                "titulo",
                "texto" };

        cursor = sqlLite.query(TABLE_NAME, colunas, condicao, null, null, null, orderBy);
        Log.v("tamanhoCursor", String.valueOf(cursor.getCount()));
        TreeMap<String, Agenda> listaAgendas = new TreeMap<String, Agenda>();
        while( cursor.moveToNext() ) {
            Agenda agenda = new Agenda(this.context);
            agenda.setId(cursor.getInt(cursor.getColumnIndex("id")));
            SimpleDateFormat formatterDia = new SimpleDateFormat("yyyy-MM-dd");
            agenda.setDia(formatterDia.parse(cursor.getString(cursor.getColumnIndex("dia"))));
            SimpleDateFormat formatterHora = new SimpleDateFormat("HH:mm");
            agenda.setHoraInicial(formatterHora.parse(cursor.getString(cursor.getColumnIndex("hora_inicial"))));
            agenda.setHoraFinal(formatterHora.parse(cursor.getString(cursor.getColumnIndex("hora_final"))));
            agenda.setSala(cursor.getString(cursor.getColumnIndex("sala")));
            agenda.setCor(cursor.getString(cursor.getColumnIndex("cor")));
            agenda.setLink(cursor.getString(cursor.getColumnIndex("link")));
            agenda.setTitulo(cursor.getString(cursor.getColumnIndex("titulo")));
            agenda.setTexto(cursor.getString(cursor.getColumnIndex("texto")));
            listaAgendas.put(formatterDia.format(agenda.getDia())+"|-|"+agenda.getSala()+"|-|"+formatterHora.format(agenda.getHoraInicial()), agenda);
        }

        if (cursor != null)
            cursor.close();

        return listaAgendas;
    }

    public long salvar() throws Exception {
        SQLiteDatabase sqlLite = new DBHelper(context).getWritableDatabase();

        ContentValues content = new ContentValues();

        content.put("id", this.getId());
        SimpleDateFormat formatterDia = new SimpleDateFormat("yyyy-MM-dd");
        content.put("dia", formatterDia.format(this.getDia()));
        SimpleDateFormat formatterHora = new SimpleDateFormat("HH:mm");
        content.put("hora_inicial", formatterHora.format(this.getHoraInicial()));
        content.put("hora_final", formatterHora.format(this.getHoraFinal()));
        content.put("sala", this.getSala());
        content.put("cor", this.getCor());
        content.put("link", this.getLink());
        content.put("titulo", this.getTitulo());
        content.put("texto", this.getTexto());

        return sqlLite.insert(this.TABLE_NAME, null, content);
    }

    @Override
    public int compareTo(Object another) {
        if( another.getClass() == this.getClass() ) {
            Agenda anotherAgenda = (Agenda) another;
            if (this.getDia().compareTo(anotherAgenda.getDia()) == 0) {
                return this.getHoraInicial().compareTo(anotherAgenda.getHoraInicial());
            } else {
                return this.getDia().compareTo(anotherAgenda.getDia());
            }
        }

        return -1;
    }

    public void limpar() {
        SQLiteDatabase sqlLite = new DBHelper(context).getWritableDatabase();
        sqlLite.execSQL("DELETE FROM "+this.TABLE_NAME);
    }
}
