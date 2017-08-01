package swiftshop.glosoftgroup.com.shoppay.ux.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.List;

import swiftshop.glosoftgroup.com.shoppay.CONST;
import swiftshop.glosoftgroup.com.shoppay.MyApplication;
import swiftshop.glosoftgroup.com.shoppay.R;
import swiftshop.glosoftgroup.com.shoppay.entities.Shop;
import swiftshop.glosoftgroup.com.shoppay.utils.Utils;
import swiftshop.glosoftgroup.com.shoppay.ux.MainActivity;
import swiftshop.glosoftgroup.com.shoppay.ux.adapters.ShopSpinnerAdapter;
import timber.log.Timber;

//import swiftshop.glosoftgroup.com.shoppay.entities.ShopResponse;
/*
import swiftshop.glosoftgroup.com.shoppay.ux.dialogs.LicensesDialogFragment;
import swiftshop.glosoftgroup.com.shoppay.ux.dialogs.RestartDialogFragment;*/

/**
 * Fragment shows app settings and information about used open source libraries.
 * Important is possibility of changing selected shop (if more shops exist).
 */
public class SettingsFragment extends Fragment {

    private ProgressDialog progressDialog;

    /**
     * Spinner offering all available shops.
     */
    private Spinner spinShopSelection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - onCreateView", this.getClass().getSimpleName());
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        MainActivity.setActionBarTitle(getString(R.string.Settings));

        progressDialog = Utils.generateProgressDialog(getActivity(), false);

        spinShopSelection = (Spinner) view.findViewById(R.id.settings_shop_selection_spinner);

        LinearLayout licensesLayout = (LinearLayout) view.findViewById(R.id.settings_licenses_layout);
        licensesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        requestShops();
        return view;
    }

    /**
     * Load available shops from server.
     */
    private void requestShops() {
        if (progressDialog != null) progressDialog.show();
        Timber.d("request for shops made...");
    }

    /**
     * Prepare spinner with shops and pre-select already selected one.
     *
     * @param shops list of shops received from server.
     */
    private void setSpinShops(List<Shop> shops) {
        ShopSpinnerAdapter adapterLanguage = new ShopSpinnerAdapter(getActivity(), shops, false);
        spinShopSelection.setAdapter(adapterLanguage);

        int position = 0;
        for (int i = 0; i < shops.size(); i++) {
                position = i;
            }

         Timber.d("No shop selected...");

    }

    @Override
    public void onStop() {
        MyApplication.getInstance().cancelPendingRequests(CONST.SETTINGS_REQUESTS_TAG);
        if (progressDialog != null) progressDialog.cancel();
        super.onStop();
    }
}
