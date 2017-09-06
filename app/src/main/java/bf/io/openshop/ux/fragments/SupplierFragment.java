package bf.io.openshop.ux.fragments;


import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.List;

import bf.io.openshop.CONST;
import bf.io.openshop.MyApplication;
import bf.io.openshop.R;
import bf.io.openshop.api.GsonRequest;
import bf.io.openshop.entities.drawerMenu.DrawerItemPage;
import bf.io.openshop.entities.suppliers.Supplier;
import bf.io.openshop.entities.suppliers.SuppliersResponse;
import bf.io.openshop.interfaces.SupplierRecyclerInterface;
import bf.io.openshop.utils.MsgUtils;
import bf.io.openshop.ux.MainActivity;
import bf.io.openshop.ux.adapters.DrawerSubmenuRecyclerAdapter;
import bf.io.openshop.ux.adapters.SupplierRecyclerAdapter;
import timber.log.Timber;

/**
 * Fragment handles the drawer menu.
 */
public class SupplierFragment extends Fragment {

    private static final int BANNERS_ID = -123;
    public static final String NULL_DRAWER_LISTENER_WTF = "Null drawer listener. WTF.";

    private ProgressBar drawerProgress;

    /**
     * Button to reload drawer menu content (used when content failed to load).
     */
    private Button drawerRetryBtn;
    /**
     * Indicates that menu is currently loading.
     */
    private boolean drawerLoading = false;

    /**
     * Listener indicating events that occurred on the menu.
     */

    // Drawer top menu fields.
    private DrawerLayout mDrawerLayout;
    private RecyclerView drawerRecycler;
    private SupplierRecyclerAdapter drawerRecyclerAdapter;

    // Drawer sub menu fields
    private LinearLayout drawerSubmenuLayout;
    private TextView drawerSubmenuTitle;
    private DrawerSubmenuRecyclerAdapter drawerSubmenuRecyclerAdapter;

    public static SupplierFragment newInstance(long id) {
        Bundle args = new Bundle();
        args.putLong("ID", id);
        SupplierFragment fragment = new SupplierFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - onCreateView", this.getClass().getSimpleName());
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_drawer, container, false);
        RelativeLayout relatively=(RelativeLayout)layout.findViewById(R.id.relatively);
        relatively.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        drawerProgress = (ProgressBar) layout.findViewById(R.id.drawer_progress);

        drawerRetryBtn = (Button) layout.findViewById(R.id.drawer_retry_btn);
        drawerRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerLoading)
                    getDrawerItems();
            }
        });

        prepareDrawerRecycler(layout);

        Button backBtn = (Button) layout.findViewById(R.id.drawer_submenu_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            private long mLastClickTime = 0;

            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000)
                    return;
                mLastClickTime = SystemClock.elapsedRealtime();
            }
        });

        getDrawerItems();

        return layout;
    }

    /**
     * Prepare drawer menu content views, adapters and listeners.
     *
     * @param view fragment base view.
     */
    private void prepareDrawerRecycler(View view) {
        drawerRecycler = (RecyclerView) view.findViewById(R.id.drawer_recycler);
        drawerRecyclerAdapter = new SupplierRecyclerAdapter(getContext(), new SupplierRecyclerInterface() {
            @Override
            public void onSuplierSelected(View v, Supplier supplier) {
                    if (supplier!= null) {
                        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                            setReenterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
                        }
                        ((MainActivity) getActivity()).onSupplierSelected(supplier);
                    } else {
                        Timber.e(new RuntimeException(), NULL_DRAWER_LISTENER_WTF);
                    }
            }
        });
        drawerRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        drawerRecycler.setHasFixedSize(true);
        drawerRecycler.setAdapter(drawerRecyclerAdapter);

    }


    private void getDrawerItems() {
        drawerLoading = true;
        drawerProgress.setVisibility(View.VISIBLE);
        drawerRetryBtn.setVisibility(View.GONE);
        drawerRecyclerAdapter.addDrawerItem(new Supplier(1,"SUPPLIERS",null));
        /*List<Supplier> navigation=new ArrayList<>();
        navigation.add(new Supplier(1,"MY ACCOUNT","http://shop.babaviz.com/assets/images/1501571582.png"));
        navigation.add(new Supplier(2,"PRODUCTS","http://shop.babaviz.com/assets/images/1501571582.png"));
        navigation.add(new Supplier(3,"SERVICES","http://shop.babaviz.com/assets/images/1501571582.png"));
        navigation.add(new Supplier(4,"ONE TAP","http://shop.babaviz.com/assets/images/1501571582.png"));
        navigation.add(new Supplier(5,"TRACK GOODS","http://shop.babaviz.com/assets/images/1501571582.png"));
        drawerRecyclerAdapter.addDrawerItemList(navigation);
        drawerRecyclerAdapter.notifyDataSetChanged();*/


        drawerLoading = false;
        if (drawerRecycler != null) drawerRecycler.setVisibility(View.VISIBLE);
        if (drawerProgress != null) drawerProgress.setVisibility(View.GONE);

        //String url = String.format(EndPoints.NAVIGATION_DRAWER, SettingsMy.getActualNonNullShop(getActivity()).getId());
        GsonRequest<SuppliersResponse> getDrawerMenu = new GsonRequest<>(Request.Method.GET,"http://android.babaviz.com/PS254/suppliers.php", null, SuppliersResponse.class, new Response.Listener<SuppliersResponse>() {
            @Override
            public void onResponse(@NonNull SuppliersResponse drawerResponse) {
                //drawerRecyclerAdapter.addDrawerItem(new Supplier(BANNERS_ID, BANNERS_ID, getString(R.string.Just_arrived)));
                drawerRecyclerAdapter.addDrawerItemList(drawerResponse.getRecords());
                drawerRecyclerAdapter.notifyDataSetChanged();


                drawerLoading = false;
                if (drawerRecycler != null) drawerRecycler.setVisibility(View.VISIBLE);
                if (drawerProgress != null) drawerProgress.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
                drawerLoading = false;
                if (drawerProgress != null) drawerProgress.setVisibility(View.GONE);
                if (drawerRetryBtn != null) drawerRetryBtn.setVisibility(View.VISIBLE);
            }
        });
        getDrawerMenu.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        getDrawerMenu.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(getDrawerMenu, CONST.DRAWER_REQUESTS_TAG);
    }


    @Override
    public void onPause() {
        // Cancellation during onPause is needed because of app restarting during changing shop.
        MyApplication.getInstance().cancelPendingRequests(CONST.DRAWER_REQUESTS_TAG);
        if (drawerLoading) {
            if (drawerProgress != null) drawerProgress.setVisibility(View.GONE);
            if (drawerRetryBtn != null) drawerRetryBtn.setVisibility(View.VISIBLE);
            drawerLoading = false;
        }
        super.onPause();
    }


    /**
     * Interface defining events initiated by {@link SupplierFragment}.
     */
    public interface FragmentDrawerListener {

        /**
         * Launch {@link GeneralCategoriesFragment}. If fragment is already launched nothing happen.
         */
        void onDrawerBannersSelected();

        /**
         * Launch {@link CategoryFragment}.
         *
         * @param Supplier object specifying selected item in the drawer.
         */
        void onSupplierSelected(Supplier Supplier);

        /**
         * Launch {@link PageFragment}, with downloadable content.
         *
         * @param drawerItemPage id of page for download and display. (Define in OpenShop server administration)
         */
        void onDrawerItemPageSelected(DrawerItemPage drawerItemPage);

        /**
         * Launch {@link AccountFragment}.
         */
        void onAccountSelected();

        /**
         * Prepare all search strings for search whisperer.
         *
         * @param navigation items for suggestions generating.
         */
        void prepareSearchSuggestions(List<Supplier> navigation);
    }
}
