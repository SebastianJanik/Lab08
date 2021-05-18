package pollub.ism.lab08;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import pollub.ism.lab08.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ArrayAdapter<CharSequence> adapter;

    private String wybraneWarzywoNazwa = null;
    private Integer wybraneWarzywoIlosc = null;

    public enum OperacjaMagazynowa {SKLADUJ, WYDAJ};


    private BazaMagazynowa bazaDanych;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = ArrayAdapter.createFromResource(this, R.array.Asortyment, android.R.layout.simple_dropdown_item_1line);
        binding.spinner.setAdapter(adapter);

        bazaDanych = Room.databaseBuilder(getApplicationContext(), BazaMagazynowa.class, BazaMagazynowa.NAZWA_BAZY)
                .allowMainThreadQueries().build();

        binding.przyciskSkladuj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                zmienStan(OperacjaMagazynowa.SKLADUJ); // <---

            }
        });

        binding.przyciskWydaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                zmienStan(OperacjaMagazynowa.WYDAJ); // <---

            }
        });

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                wybraneWarzywoNazwa = adapter.getItem(i).toString(); // <---
                aktualizuj();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Nie będziemy implementować, ale musi być
            }
        });
        if (bazaDanych.pozycjaMagazynowaDAO().size() == 0) {
            String[] asortyment = getResources().getStringArray(R.array.Asortyment);
            for (String nazwa : asortyment) {
                PozycjaMagazynowa pozycjaMagazynowa = new PozycjaMagazynowa();
                pozycjaMagazynowa.NAME = nazwa;
                pozycjaMagazynowa.QUANTITY = 0;
                bazaDanych.pozycjaMagazynowaDAO().insert(pozycjaMagazynowa);
            }
        }

    }

    private void aktualizuj() {
        wybraneWarzywoIlosc = bazaDanych.pozycjaMagazynowaDAO().findQuantityByName(wybraneWarzywoNazwa);
        binding.tekstStanMagazynu.setText("Stan magazynu dla " + wybraneWarzywoNazwa + " wynosi: " + wybraneWarzywoIlosc);

        Historia [] dane = bazaDanych.HistoriaDAO().findLogsByName(wybraneWarzywoNazwa);

        binding.Historia.setText("");
        for(Historia tekst : dane){
            String temp = tekst.data + String.valueOf(tekst.stara_ilosc) +
                    " ---> " + String.valueOf(tekst.nowa_ilosc) + "\n";
            binding.Historia.append(temp);
        }

    }



    private void zmienStan(OperacjaMagazynowa operacja){

        Integer zmianaIlosci = null, nowaIlosc = null;

        try {
            zmianaIlosci = Integer.parseInt(binding.edycjaIlosc.getText().toString());
        }catch(NumberFormatException ex){
            return;
        }finally {
            binding.edycjaIlosc.setText("");
        }

        switch (operacja){
            case SKLADUJ: nowaIlosc = wybraneWarzywoIlosc + zmianaIlosci; break;
            case WYDAJ: nowaIlosc = wybraneWarzywoIlosc - zmianaIlosci; break;
        }


        Historia historia = new Historia();
        historia.nazwa = wybraneWarzywoNazwa;
        SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        historia.data = data.format(new Date());
        historia.stara_ilosc = wybraneWarzywoIlosc;
        historia.nowa_ilosc = nowaIlosc;
        bazaDanych.HistoriaDAO().insert(historia);
        bazaDanych.pozycjaMagazynowaDAO().updateQuantityByName(wybraneWarzywoNazwa,nowaIlosc);

        aktualizuj();
    }
}