package developer.joao.agendaexpotec;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    SecoesPagerAdapter mSecoesPagerAdapter;
    ViewPager mViewPager;
    ActionBar mActionBar;
    static ArrayList<String> datas = new ArrayList<String>();
    static ArrayList<String> salas = new ArrayList<String>();
    static String diaEscolhido;
    static Map<String, List<Agenda>> listaDeAgendas = new HashMap<String, List<Agenda>>();
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        Map<String, Agenda> agenda = (HashMap<String, Agenda>) intent.getSerializableExtra("agenda");

        if( agenda != null && savedInstanceState == null ) {
            for (Map.Entry<String, Agenda> entry : agenda.entrySet()) {
                Agenda agendaUn = entry.getValue();
                String[] explode = entry.getKey().split("\\|\\-\\|");
                datas.add(explode[0]);
                salas.add(explode[1]);

                if (listaDeAgendas.containsKey(explode[0] + "|-|" + explode[1])) {
                    listaDeAgendas.get(explode[0] + "|-|" + explode[1]).add(agendaUn);
                } else {
                    List<Agenda> tempList = new ArrayList<Agenda>();
                    tempList.add(agendaUn);
                    listaDeAgendas.put(explode[0] + "|-|" + explode[1], tempList);
                }
            }

            Collections.sort(datas);
            diaEscolhido = datas.get(0);
        }

        Set<String> datasTemp = new HashSet<String>(datas);
        datas.clear();
        Iterator<String> datasIterator = datasTemp.iterator();
        while( datasIterator.hasNext() ) {
            datas.add(datasIterator.next());
        }

        Set<String> salasTemp = new HashSet<String>(salas);
        salas.clear();
        Iterator<String> salasIterator = salasTemp.iterator();
        while( salasIterator.hasNext() ) {
            salas.add(salasIterator.next());
        }

        Collections.sort(salas, new Comparator<String>() {
            public int compare(String str1, String str2) {
                int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
                if (res == 0) {
                    res = str1.compareTo(str2);
                }
                return res;
            }
        });

        mSecoesPagerAdapter = new SecoesPagerAdapter(getSupportFragmentManager());
        final ActionBar actionBar = getActionBar();

        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSecoesPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mSecoesPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSecoesPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        mActionBar = actionBar;

        mDrawerList = (ListView)findViewById(R.id.navList);
        DrawerLayout mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        addDrawerItems(mDrawer);
    }

    private void addDrawerItems(final DrawerLayout mDrawer) {
        String[] osArray = salas.toArray(new String[salas.size()]);
        for( int i = 0; i < osArray.length; i++ ) {
            osArray[i] = osArray[i].toLowerCase();
        }
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mViewPager.setCurrentItem(position);
                mDrawer.closeDrawer(mDrawerList);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        listaDeAgendas.clear();
        SplashScreenActivity.atualizar = false;
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        TextView tv = (TextView) getLayoutInflater().inflate(R.layout.textview_texto_dia_escolhido, mViewPager).findViewById(R.id.texto_dia_escolhido);
        SimpleDateFormat formatterDia = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatterDiaFormatada = new SimpleDateFormat("dd/MM/yyyy");
        String diaEscolhidoFormatado = null;
        try {
            diaEscolhidoFormatado = formatterDiaFormatada.format(formatterDia.parse(diaEscolhido));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tv.setText(diaEscolhidoFormatado + "  ");
        menu.add(0, 0, 1, diaEscolhidoFormatado).setActionView(tv).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_calendar) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Escolha o dia");
            ArrayList<String> datasFormatadas = (ArrayList<String>) datas.clone();
            SimpleDateFormat formatterDia = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatterDiaFormatada = new SimpleDateFormat("dd/MM/yyyy");
            Collections.sort(datasFormatadas);
            int tamanhoArrayDatas = datasFormatadas.size();
            final String[] dias = new String[tamanhoArrayDatas];
            Date dia = null;
            for(int i = 0; i < tamanhoArrayDatas; i++) {
                try {
                    dia = formatterDia.parse(datasFormatadas.get(i));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dias[i] = formatterDiaFormatada.format(dia);
            }
            dialog.setItems(dias, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SimpleDateFormat formatterDia = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat formatterDiaFormatada = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        diaEscolhido = formatterDia.format(formatterDiaFormatada.parse(dias[which]));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mSecoesPagerAdapter.notifyDataSetChanged();
                    mViewPager.setAdapter(mSecoesPagerAdapter);
                    TextView tv = (TextView) findViewById(R.id.texto_dia_escolhido);
                    tv.setText(dias[which]);
                    mActionBar.selectTab(mActionBar.getTabAt(0));
                }
            });
            dialog.show();
            return true;
        } else if( id == R.id.action_sobre ) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Sobre");
            View view = getLayoutInflater().inflate(R.layout.mensagem_sobre, null);
            TextView textView3 = (TextView) view.findViewById(R.id.text3_sobre);
            final int[] vezesClicadas = {0};
            textView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vezesClicadas[0]++;
                    if( vezesClicadas[0] % 5 == 0 ) {
                        Toast.makeText(getApplicationContext(), "Dedico esse primeiro aplicativo à minha família, pelo apoio, e em especial à minha esposa, pela grande companheira e guerreira que ela é. Te amo Anna Rayssa! <3", Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "Dedico esse primeiro aplicativo à minha família, pelo apoio, e em especial à minha esposa, pela grande companheira e guerreira que ela é. Te amo Anna Rayssa! <3", Toast.LENGTH_LONG).show();
                    }
                }
            });
            dialog.setView(view);
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    public static class SecoesPagerAdapter extends FragmentPagerAdapter {

        public SecoesPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                default:
                    // The other sections of the app are dummy placeholders.
                    Fragment fragment = new SalasFragment();
                    Bundle args = new Bundle();
                    args.putInt(SalasFragment.ARG_SECTION_NUMBER, i);
                    fragment.setArguments(args);
                    return fragment;
            }
        }

        @Override
        public int getCount() {
            return salas.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return salas.get(position);
        }
    }


    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class SalasFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_section_salas, container, false);
            Bundle args = getArguments();
            ListView listaAgenda = (ListView) rootView.findViewById(R.id.listaAgenda);
            if( !listaDeAgendas.containsKey(diaEscolhido+"|-|"+salas.get((Integer) args.get(ARG_SECTION_NUMBER))) ) {
                listaDeAgendas.put(diaEscolhido+"|-|"+salas.get((Integer) args.get(ARG_SECTION_NUMBER)), new ArrayList<Agenda>());
            }

            List<Agenda> listaAgendaTemp = listaDeAgendas.get(diaEscolhido+"|-|"+salas.get((Integer) args.get(ARG_SECTION_NUMBER)));

            Collections.sort(listaAgendaTemp);

            listaAgenda.setAdapter(new ListaAgendaAdapter(this.getActivity().getApplicationContext(), listaAgendaTemp));

            return rootView;
        }
    }
}
