package edu.training.wearbountyhunter;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.util.Locale;
/**
 * Created by brachialste on 29/11/16.
 */

public class Home extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private static SectionsPagerAdapter mSectionsPagerAdapter;
    private Fragment[] mFragments;
    public static DBProvider oDB;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private static ViewPager mViewPager;

    public Home()
    {
        //Se inicializa el Manejador de Datos para evitar colisión de Conexiones...
        oDB = new DBProvider(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent oW = new Intent();
                oW.setClass(getApplicationContext(), AgregarActivity.class);
                startActivityForResult(oW, 0);
            }
        });

        //Se obtiene el ID del dispositivo...
        String UDIDDevice = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("[UDID]", UDIDDevice);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.menu_agregar:
                Intent oW = new Intent();
                oW.setClass(this, AgregarActivity.class);
                startActivityForResult(oW,0);
                break;
        }
        return true;
    }

    public static void UpdateLists(int index)
    {
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        UpdateLists(requestCode);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            //Se instancian los Fragments a utilizar por cada Tab...
            mFragments = new Fragment[3];
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(mFragments[position] == null)
            {
                //Si el fragment no existe se instancia...
                if (position < 2)
                {
                    //Para los 2 primeros Tabs se crea el mismo fragment con el
                    //parametro que indica el Tab...
                    mFragments[position] = new ListFragment();
                    Bundle args = new Bundle();
                    args.putInt(ListFragment.ARG_SECTION_NUMBER, position);
                    mFragments[position].setArguments(args);
                }
                else
                {
                    mFragments[position] = new AboutFragment();
                }
            }
            return mFragments[position];
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_fugitivos).toUpperCase(l);
                case 1:
                    return getString(R.string.title_capturados).toUpperCase(l);
                case 2:
                    return getString(R.string.title_acercade).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * Fragment multiuso para mostrar la lista de Fujitivos o Capturados acorde
     * al argumento indicado.
     */
    public static class ListFragment extends Fragment {
        public static final String ARG_SECTION_TEXT = "section_text";
        public static final String ARG_SECTION_NUMBER = "section_number";

        View iView;
        int iMode;

        public ListFragment() {
        }

        public void UpdateList(){
            //Datos del SQL...
            String[][] aRef = oDB.ObtenerFugitivos(iMode == 1);
            if(aRef != null)
            {
                String[] aData = new String[aRef.length];
                for(int iCnt = 0; iCnt < aRef.length; iCnt++)
                {
                    aData[iCnt] = aRef[iCnt][1];
                }
                ListView tlList = (ListView)iView.findViewById(R.id.bhlist);
                ArrayAdapter<String> aList = new ArrayAdapter<String>(getActivity(),R.layout.row_list,aData);
                tlList.setTag(aRef);
                tlList.setAdapter(aList);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            // Se hace referencia al Fragment generado por XML en los Layouts y
            // se instancia en una View...
            iView = inflater.inflate(R.layout.list_fragment, container,
                    false);
            Bundle aArgs = this.getArguments();
            iMode = aArgs.getInt(ListFragment.ARG_SECTION_NUMBER);

            ListView tlList = ((ListView) iView.findViewById(R.id.bhlist));
            /*String[] aData = new String[6];
            // Datos en HardCode...
            aData[0] = "Armando Olmos";
            aData[1] = "Guillermo Ortega";
            aData[2] = "Carlos Martinez";
            aData[3] = "Moises Rivas";
            aData[4] = "Adrian Rubiera";
            aData[5] = "Victor Medina";

            ArrayAdapter<String> aList = new ArrayAdapter<String>(
                    getActivity(), R.layout.row_list, aData);
            tlList.setAdapter(aList);*/
            UpdateList();
            // Se genera el Listener para el detalle de cada elemento...
            tlList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> aList, View vItem,
                                        int iPosition, long l) {
                    Intent oW = new Intent();
                    String[][] aDat = (String[][])aList.getTag();
                    oW.setClass(getActivity(), DetalleActivity.class);
                    oW.putExtra("title", aDat[iPosition][1]);
                    oW.putExtra("mode", iMode);
                    oW.putExtra("id", aDat[iPosition][0]);
                    getActivity().startActivityForResult(oW, iMode);
                }
            });

            return iView;
        }
    }

    /**
     * Fragment con datos acerca de la App.
     */
    public static class AboutFragment extends Fragment {
        public AboutFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Se hace referencia al Fragment generado por XML en los Layouts y
            // se instanc’a en una View...
            View iView = inflater.inflate(R.layout.about_fragment, container,
                    false);
            // Se accede a los elementos ajustables del Fragment...
            RatingBar rbApp = ((RatingBar) iView.findViewById(R.id.ratingApp));

            String sRating = "0.0"; // Variable para lectura del Rating guardado
            // en el property.
            try {
                if (System.getProperty("rating") != null) {
                    sRating = System.getProperty("rating");
                }
            } catch (Exception ex) {
            }
            if (sRating.isEmpty())
                sRating = "0.0";

            rbApp.setRating(Float.parseFloat(sRating));
            // Listener al Raiting para la actualizacion de la property...
            rbApp.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating,
                                            boolean fromUser) {
                    System.setProperty("rating", String.valueOf(rating));
                    ratingBar.setRating(rating);
                }
            });

            return iView;
        }
    }
}
