  package swiftshop.glosoftgroup.com.shoppay.ux.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import swiftshop.glosoftgroup.com.shoppay.CONST;
import swiftshop.glosoftgroup.com.shoppay.MyApplication;
import swiftshop.glosoftgroup.com.shoppay.R;
import swiftshop.glosoftgroup.com.shoppay.api.EndPoints;
import swiftshop.glosoftgroup.com.shoppay.api.GsonRequest;
import swiftshop.glosoftgroup.com.shoppay.entities.Metadata;
import swiftshop.glosoftgroup.com.shoppay.entities.merchants.MerchantCategoriesResponse;
import swiftshop.glosoftgroup.com.shoppay.entities.merchants.MerchantsCategories;
import swiftshop.glosoftgroup.com.shoppay.interfaces.MerchantCategoriesInterface;
import swiftshop.glosoftgroup.com.shoppay.listeners.OnSingleClickListener;
import swiftshop.glosoftgroup.com.shoppay.utils.EndlessRecyclerScrollListener;
import swiftshop.glosoftgroup.com.shoppay.utils.MsgUtils;
import swiftshop.glosoftgroup.com.shoppay.utils.Utils;
import swiftshop.glosoftgroup.com.shoppay.ux.MainActivity;
import swiftshop.glosoftgroup.com.shoppay.ux.adapters.MerchantCategoryRecyclerAdapter;
import timber.log.Timber;

/**
 * Created by admin on 12/31/2016.
 */

public class MerchantCategoryFragment extends Fragment {

    private ProgressDialog progressDialog;

    // content related fields.
    private RecyclerView mcategoriesRecycler;
    private MerchantCategoryRecyclerAdapter merchantCategoryRecyclerAdapter;
    private EndlessRecyclerScrollListener endlessRecyclerScrollListener;
    private Metadata mcategoriesMetadata;

    /**
     * Indicates if user data should be loaded from server or from memory.
     */
    private boolean mAlreadyLoaded = false;

    private RecyclerView merchantCategoryRecycler;

    /**
     * Holds reference for empty view. Show to user when no data loaded.
     */
    private View emptyContent;


    public View onCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - OnCreateView", this.getClass().getSimpleName());
        super.onCreate(savedInstanceState);
        MainActivity.setActionBarTitle("Merchant Categories");

//        View view = inflater.inflate(R.layout.fragment_merchant_categories, container, false);
        View view = inflater.inflate(R.layout.fragment_activity_gridview, container, false);
        progressDialog = Utils.generateProgressDialog(getActivity(), false);

        prepareEmptyContent(view);
        // Don't reload data when return from backStack. Reload if a new instance was created or data was empty.
        if ((savedInstanceState == null && !mAlreadyLoaded) || merchantCategoryRecyclerAdapter == null
                || merchantCategoryRecyclerAdapter.isEmpty()) {
            Timber.d("Reloading merchant categories...");
            mAlreadyLoaded = true;

            // Prepare views and listeners
            prepareContentViews(view, true);
            loadMoreCategories(null);
        } else {
            Timber.d("merchant Categories already loaded.");
            prepareContentViews(view, false);
            // Already loaded
        }

        return view;
    }

    private void prepareContentViews(View view, boolean freshStart) {
        merchantCategoryRecycler = (RecyclerView) view.findViewById(R.id.merchantsCategory_recycler);
        if (freshStart) {
            merchantCategoryRecyclerAdapter = new MerchantCategoryRecyclerAdapter(getActivity(), new MerchantCategoriesInterface() {
                @Override
                public void onMerchantCategorySelected(MerchantsCategories mc) {
                    Activity activity = getActivity();
                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).onMerchantCategorySelected(mc);
                    }
                }
            });
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(merchantCategoryRecycler.getContext());
        merchantCategoryRecycler.setLayoutManager(layoutManager);
        merchantCategoryRecycler.setItemAnimator(new DefaultItemAnimator());
        merchantCategoryRecycler.setHasFixedSize(true);
        merchantCategoryRecycler.setAdapter(merchantCategoryRecyclerAdapter);
        endlessRecyclerScrollListener = new EndlessRecyclerScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                if (mcategoriesMetadata != null && mcategoriesMetadata.getLinks() != null && mcategoriesMetadata.getLinks().getNext() != null) {
                    loadMoreCategories(mcategoriesMetadata.getLinks().getNext());
                } else {
                    Timber.d("CustomLoadMoreDataFromApi NO MORE DATA");
                }
            }
        };
        merchantCategoryRecycler.addOnScrollListener(endlessRecyclerScrollListener);
    }

        /**
         * Prepares views and listeners associated with empty content. Visible only when no content loads.
         *
         * @param view fragment root view.
         */
        private void prepareEmptyContent(View view) {
            emptyContent = view.findViewById(R.id.banners_empty);
            TextView emptyContentAction = (TextView) view.findViewById(R.id.banners_empty_action);
            emptyContentAction.setOnClickListener(new OnSingleClickListener(){
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

    private void loadMoreCategories(String url) {
        Timber.d("loadMoreCat visited");
        progressDialog.show();
        if (url == null) {
            merchantCategoryRecyclerAdapter.clear();
            url = String.format(EndPoints.MERCHANT_BANNERS);
            Timber.d("url null loaded " + url);
        }
        GsonRequest<MerchantCategoriesResponse> getMerchantCatRequest = new GsonRequest<>(Request.Method.GET, url, null, MerchantCategoriesResponse.class,
                new Response.Listener<MerchantCategoriesResponse>() {
                    @Override
                    public void onResponse(@NonNull MerchantCategoriesResponse response) {
                        Timber.d("loadMoreCat responses below");
                        Timber.d("response: %s", response.toString());
                        mcategoriesMetadata = response.getMetadata();
                        merchantCategoryRecyclerAdapter.addMerchantCategories(response.getRecords());

                        if (merchantCategoryRecyclerAdapter.getItemCount() > 0) {
                            emptyContent.setVisibility(View.INVISIBLE);
                            merchantCategoryRecycler.setVisibility(View.VISIBLE);
                        } else {
                            emptyContent.setVisibility(View.VISIBLE);
                            merchantCategoryRecycler.setVisibility(View.INVISIBLE);
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

        getMerchantCatRequest.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(getMerchantCatRequest, CONST.BANNER_REQUESTS_TAG);
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
        if (merchantCategoryRecycler != null) merchantCategoryRecycler.clearOnScrollListeners();
        super.onDestroyView();
    }
}
