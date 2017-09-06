package bf.io.openshop.ux.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import bf.io.openshop.CONST;
import bf.io.openshop.MyApplication;
import bf.io.openshop.R;
import bf.io.openshop.SettingsMy;
import bf.io.openshop.api.EndPoints;
import bf.io.openshop.api.GsonRequest;
import bf.io.openshop.entities.Banner;
import bf.io.openshop.entities.BannersResponse;
import bf.io.openshop.entities.Metadata;
import bf.io.openshop.interfaces.BannersRecyclerInterface;
import bf.io.openshop.listeners.OnSingleClickListener;
import bf.io.openshop.utils.EndlessRecyclerScrollListener;
import bf.io.openshop.utils.MsgUtils;
import bf.io.openshop.utils.Utils;
import bf.io.openshop.ux.MainActivity;
import bf.io.openshop.ux.adapters.GeneralCategoriesRecyclerAdapter;
import timber.log.Timber;

/**
 * Provides "welcome" screen customizable from web administration. Often contains banners with sales or best products.
 */
public class GeneralCategoriesFragment extends Fragment {

    private ProgressDialog progressDialog;

    // content related fields.
    private RecyclerView bannersRecycler;
    private GeneralCategoriesRecyclerAdapter generalCategoriesRecyclerAdapter;
    private EndlessRecyclerScrollListener endlessRecyclerScrollListener;
    private Metadata bannersMetadata;

    /**
     * Indicates if user data should be loaded from server or from memory.
     */
    private boolean mAlreadyLoaded = false;

    /**
     * Holds reference for empty view. Show to user when no data loaded.
     */
    private View emptyContent;

    public GeneralCategoriesFragment() {
    }

    private boolean isSupplierCats=false;
    private String supplier;

    @SuppressLint("ValidFragment")
    public GeneralCategoriesFragment(boolean isSupplierCats,String supplier) {
        //this();
        this.isSupplierCats = isSupplierCats;
        this.supplier=supplier;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - OnCreateView", this.getClass().getSimpleName());
        MainActivity.setActionBarTitle(getString(R.string.Just_arrived));
        View view = inflater.inflate(R.layout.fragment_banners, container, false);
        progressDialog = Utils.generateProgressDialog(getActivity(), false);
        if(isSupplierCats){
            ((TextView)view.findViewById(R.id.header_lbl)).setText(supplier+" Categories");
        }
        prepareEmptyContent(view);
        // Don't reload data when return from backStack. Reload if a new instance was created or data was empty.
        if ((savedInstanceState == null && !mAlreadyLoaded) || generalCategoriesRecyclerAdapter == null || generalCategoriesRecyclerAdapter.isEmpty()) {
            Timber.d("Reloading banners.");
            mAlreadyLoaded = true;

            // Prepare views and listeners
            prepareContentViews(view, true);
            loadBanners(null);
        } else {
            Timber.d("Banners already loaded.");
            prepareContentViews(view, false);
            // Already loaded
        }

        return view;
    }

    /**
     * Prepares views and listeners associated with content.
     *
     * @param view       fragment root view.
     * @param freshStart indicates when everything should be recreated.
     */
    private void prepareContentViews(View view, boolean freshStart) {
        bannersRecycler = (RecyclerView) view.findViewById(R.id.banners_recycler);
        if (freshStart) {
            generalCategoriesRecyclerAdapter = new GeneralCategoriesRecyclerAdapter(getActivity(), new BannersRecyclerInterface() {
                @Override
                public void onBannerSelected(Banner banner) {
                    Activity activity = getActivity();
                    if (activity instanceof MainActivity && !isSupplierCats) {
                        ((MainActivity) activity).onBannerSelected(banner);
                    }else if(activity instanceof MainActivity && isSupplierCats){
                        ((MainActivity) activity).OnSupplierCategorySelected(banner);
                    }
                }
            });
        }
        //LinearLayoutManager layoutManager = new LinearLayoutManager(bannersRecycler.getContext());
        GridLayoutManager layoutManager=new GridLayoutManager(bannersRecycler.getContext(),2);
        bannersRecycler.setLayoutManager(layoutManager);
        bannersRecycler.setItemAnimator(new DefaultItemAnimator());
        bannersRecycler.setHasFixedSize(true);
        bannersRecycler.setAdapter(generalCategoriesRecyclerAdapter);
        endlessRecyclerScrollListener = new EndlessRecyclerScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                if (bannersMetadata != null && bannersMetadata.getLinks() != null && bannersMetadata.getLinks().getNext() != null) {
                    loadBanners(bannersMetadata.getLinks().getNext());
                } else {
                    Timber.d("CustomLoadMoreDataFromApi NO MORE DATA");
                }
            }
        };
        bannersRecycler.addOnScrollListener(endlessRecyclerScrollListener);
    }

    /**
     * Prepares views and listeners associated with empty content. Visible only when no content loads.
     *
     * @param view fragment root view.
     */
    private void prepareEmptyContent(View view) {
        emptyContent = view.findViewById(R.id.banners_empty);
        TextView emptyContentAction = (TextView) view.findViewById(R.id.banners_empty_action);
        emptyContentAction.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                // Just open drawer menu.
                Activity activity = getActivity();
                if (activity instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) activity;
                    if (mainActivity.drawerFragment != null)
                        mainActivity.drawerFragment.toggleDrawerMenu();
                }
            }
        });
    }

    /**
     * Endless content loader. Should be used after views inflated.
     *
     * @param url null for fresh load. Otherwise use URLs from response metadata.
     */
    private void loadBanners(String url) {
        progressDialog.show();
        if (url == null) {
            generalCategoriesRecyclerAdapter.clear();
            url = String.format(EndPoints.BANNERS, SettingsMy.getActualNonNullShop(getActivity()).getId());
        }
        GsonRequest<BannersResponse> getBannersRequest = new GsonRequest<>(Request.Method.GET, "http://android.babaviz.com/PS254/home_cats.php", null, BannersResponse.class,
                new Response.Listener<BannersResponse>() {
                    @Override
                    public void onResponse(@NonNull BannersResponse response) {
                        Timber.d("response: %s", response.toString());
                        bannersMetadata = response.getMetadata();
                        generalCategoriesRecyclerAdapter.addBanners(response.getRecords());

                        if (generalCategoriesRecyclerAdapter.getItemCount() > 0) {
                            emptyContent.setVisibility(View.INVISIBLE);
                            bannersRecycler.setVisibility(View.VISIBLE);
                        }else if(isSupplierCats && generalCategoriesRecyclerAdapter.getItemCount()==0){
                            //load products fragment on the main activity
                        } else {
                            emptyContent.setVisibility(View.VISIBLE);
                            bannersRecycler.setVisibility(View.INVISIBLE);
                        }

                        progressDialog.cancel();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null) progressDialog.cancel();
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });
        getBannersRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        getBannersRequest.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(getBannersRequest, CONST.BANNER_REQUESTS_TAG);
    }

    @Override
    public void onStop() {
        if (progressDialog != null) {
            // Hide progress dialog if exist.
            if (progressDialog.isShowing() && endlessRecyclerScrollListener != null) {
                // Fragment stopped during loading data. Allow new loading on return.
                endlessRecyclerScrollListener.resetLoading();
            }
            progressDialog.cancel();
        }
        MyApplication.getInstance().cancelPendingRequests(CONST.BANNER_REQUESTS_TAG);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (bannersRecycler != null) bannersRecycler.clearOnScrollListeners();
        super.onDestroyView();
    }
}
