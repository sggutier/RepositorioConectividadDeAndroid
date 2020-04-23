package net.sggutier.aplicacionconectividadwifi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListaRedesFragment extends Fragment {
    private RecyclerView mRedesRecyclerView;
    private CrimeAdapter mAdapter;
    private WifiManager mainWifiObj;
    private ReceptorDeBroadcastsDeWifi mReceptorDeBroadcasts;
    private Activity padre;
    private static int[] ICONOS_WIFI_IDS = {
            R.drawable.ic_wifi_0_barras,
            R.drawable.ic_wifi_1_barras,
            R.drawable.ic_wifi_2_barras,
            R.drawable.ic_wifi_3_barras,
            R.drawable.ic_wifi_4_barras,
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_redes, container, false);

        mRedesRecyclerView = view.findViewById(R.id.redes_recycler_view);
        mRedesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mReceptorDeBroadcasts = new ReceptorDeBroadcastsDeWifi();
        padre = getActivity();

        mainWifiObj = (WifiManager) padre.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        padre.registerReceiver(mReceptorDeBroadcasts, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private class ScanResultsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mNombreRed;
        private TextView mBSSID;
        private ScanResult mScanResult;
        private ImageView mIconoWifi;

        ScanResultsHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
            super(inflater.inflate(
                    R.layout.list_item_access_point,
                    parent,
                    false));
            itemView.setOnClickListener(this);

            mNombreRed = itemView.findViewById(R.id.nombre_red);
            mBSSID = itemView.findViewById(R.id.bssid);
            mIconoWifi = itemView.findViewById(R.id.icono_wifi);
        }

        void bind(ScanResult scanResult) {
            mScanResult = scanResult;
            mNombreRed.setText(mScanResult.SSID);
            mBSSID.setText(mScanResult.BSSID);
            int fuerzaSeñal = mainWifiObj.calculateSignalLevel(mScanResult.level, ICONOS_WIFI_IDS.length);
            mIconoWifi.setImageResource(ICONOS_WIFI_IDS[fuerzaSeñal]);
        }

        @Override
        public void onClick(View v) {
            // De momento nada, tal vez luego para conectarse a una red
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<ScanResultsHolder> {
        private List<ScanResult> mListaRedes;

        CrimeAdapter(List<ScanResult> scanResults) {
            mListaRedes = scanResults;
        }

        @NonNull
        @Override
        public ScanResultsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new ScanResultsHolder(layoutInflater, parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull ScanResultsHolder holder, int position) {
            ScanResult scanResult = mListaRedes.get(position);
            holder.bind(scanResult);
        }

        @Override
        public int getItemCount() {
            return mListaRedes.size();
        }
    }


    private class ReceptorDeBroadcastsDeWifi extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = mainWifiObj.getScanResults();

            if (mAdapter == null) {
                mAdapter = new CrimeAdapter(wifiScanList);
                mRedesRecyclerView.setAdapter(mAdapter);
            }
            else {
                mAdapter.notifyDataSetChanged();
            }
            Log.i("ElTag", "Wifi Actualizado");
        }
    }
}
