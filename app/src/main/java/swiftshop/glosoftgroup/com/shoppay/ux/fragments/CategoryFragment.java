package swiftshop.glosoftgroup.com.shoppay.ux.fragments;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import swiftshop.glosoftgroup.com.shoppay.CONST;
import swiftshop.glosoftgroup.com.shoppay.MyApplication;
import swiftshop.glosoftgroup.com.shoppay.R;
import swiftshop.glosoftgroup.com.shoppay.entities.Metadata;
import swiftshop.glosoftgroup.com.shoppay.entities.drawerMenu.DrawerItemCategory;
import swiftshop.glosoftgroup.com.shoppay.listeners.OnSingleClickListener;
import swiftshop.glosoftgroup.com.shoppay.utils.MsgUtils;
import swiftshop.glosoftgroup.com.shoppay.ux.MainActivity;
import timber.log.Timber;

/*import swiftshop.glosoftgroup.com.shoppay.entities.filtr.Filters;
import swiftshop.glosoftgroup.com.shoppay.entities.SortItem;
import swiftshop.glosoftgroup.com.shoppay.entities.product.Product;
import swiftshop.glosoftgroup.com.shoppay.entities.product.ProductListResponse;
import swiftshop.glosoftgroup.com.shoppay.interfaces.CategoryRecyclerInterface;
import swiftshop.glosoftgroup.com.shoppay.interfaces.FilterDialogInterface;
import swiftshop.glosoftgroup.com.shoppay.listeners.OnSingleClickListener;
import swiftshop.glosoftgroup.com.shoppay.utils.Analytics;
import swiftshop.glosoftgroup.com.shoppay.utils.EndlessRecyclerScrollListener;

import swiftshop.glosoftgroup.com.shoppay.utils.RecyclerMarginDecorator;

import swiftshop.glosoftgroup.com.shoppay.ux.adapters.ProductsRecyclerAdapter;
import swiftshop.glosoftgroup.com.shoppay.ux.adapters.SortSpinnerAdapter;
import swiftshop.glosoftgroup.com.shoppay.ux.dialogs.FilterDialogFragment;*/

/**
 * Fragment handles various types of product lists.
 * Also allows displaying the search results.
 */
public class CategoryFragment extends Fragment {

    private static final String TYPE = "type";
    private static final String CATEGORY_NAME = "categoryName";
    private static final String CATEGORY_ID = "categoryId";
    private static final String SEARCH_QUERY = "search_query";

    /**
     * Prevent the sort selection callback during initialization.
     */
    private boolean firstTimeSort = true;

    private View loadMoreProgress;

    private long categoryId;
    private String categoryType;

    /**
     * Search string. The value is set only if the fragment is launched in order to searching.
     */
    private String searchQuery = null;

    /**
     * Request metadata containing URLs for endlessScroll.
     */
    private Metadata productsMetadata;

    private ImageSwitcher switchLayoutManager;
    private Spinner sortSpinner;

    // Content specific
    private TextView emptyContentView;
    private RecyclerView productsRecycler;
    private GridLayoutManager productsRecyclerLayoutManager;
    //private ProductsRecyclerAdapter productsRecyclerAdapter;
    //private EndlessRecyclerScrollListener endlessRecyclerScrollListener;

    // Filters parameters
    //private Filters filters;
    private String filterParameters = null;
    private ImageView filterButton;

    // Properties used to restore previous state
    private int toolbarOffset = -1;
    private boolean isList = false;


    /**
     * Show product list defined by parameters.
     *
     * @param categoryId id of product category.
     * @param name       name of product list.
     * @param type       type of product list.
     * @return new fragment instance.
     */
    public static CategoryFragment newInstance(long categoryId, String name, String type) {
        Bundle args = new Bundle();
        args.putLong(CATEGORY_ID, categoryId);
        args.putString(CATEGORY_NAME, name);
        args.putString(TYPE, type);
        args.putString(SEARCH_QUERY, null);

        CategoryFragment fragment = new CategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Show product list populated from drawer menu.
     *
     * @param drawerItemCategory corresponding drawer menu item.
     * @return new fragment instance.
     */
    public static CategoryFragment newInstance(DrawerItemCategory drawerItemCategory) {
        if (drawerItemCategory != null)
            return newInstance(drawerItemCategory.getOriginalId(), drawerItemCategory.getName(), drawerItemCategory.getType());
        else {
            Timber.e(new RuntimeException(), "Creating category with null arguments");
            return null;
        }
    }

    /**
     * Show product list based on search results.
     *
     * @param searchQuery word for searching.
     * @return new fragment instance.
     */
    public static CategoryFragment newInstance(String searchQuery) {
        Bundle args = new Bundle();
        args.putString(SEARCH_QUERY, searchQuery);

        CategoryFragment fragment = new CategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - onCreateView", this.getClass().getSimpleName());
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        /*this.emptyContentView = (TextView) view.findViewById(R.id.category_products_empty);
        this.loadMoreProgress = view.findViewById(R.id.category_load_more_progress);
        this.sortSpinner = (Spinner) view.findViewById(R.id.category_sort_spinner);
        this.switchLayoutManager = (ImageSwitcher) view.findViewById(R.id.category_switch_layout_manager);*/

        Bundle startBundle = getArguments();
        if (startBundle != null) {
            categoryId = startBundle.getLong(CATEGORY_ID, 0);
            String categoryName = startBundle.getString(CATEGORY_NAME, "");
            categoryType = startBundle.getString(TYPE, "category");
            searchQuery = startBundle.getString(SEARCH_QUERY, null);
            boolean isSearch = false;
            if (searchQuery != null && !searchQuery.isEmpty()) {
                isSearch = true;
                categoryId = -10;
                categoryName = searchQuery;
            }

            Timber.d("Category type: %s. CategoryId: %d. FilterUrl: %s.", categoryType, categoryId, filterParameters);

            AppBarLayout appBarLayout = (AppBarLayout) view.findViewById(R.id.category_appbar_layout);
            if (toolbarOffset != -1) appBarLayout.offsetTopAndBottom(toolbarOffset);
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                    toolbarOffset = i;
                }
            });
            MainActivity.setActionBarTitle(categoryName);
            this.filterButton = (ImageView) view.findViewById(R.id.category_filter_button);
            filterButton.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View view) {

                        MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Filter_unavailable), MsgUtils.ToastLength.SHORT);

                }
            });


            if (filterParameters != null && !filterParameters.isEmpty()) {
                filterButton.setImageResource(R.drawable.filter_selected);
            } else {
                filterButton.setImageResource(R.drawable.filter_unselected);
            }

            // Opened first time (not form backstack)

        } else {
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, getString(R.string.Internal_error), MsgUtils.ToastLength.LONG);
            Timber.e(new RuntimeException(), "Run category fragment without arguments.");
        }
        return view;
    }


    /**
     * Animate change of rows in products recycler LayoutManager.
     *
     * @param layoutSpanCount number of rows to display.
     */
    private void animateRecyclerLayoutChange(final int layoutSpanCount) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new DecelerateInterpolator());
        fadeOut.setDuration(400);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                productsRecyclerLayoutManager.setSpanCount(layoutSpanCount);
                productsRecyclerLayoutManager.requestLayout();
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new AccelerateInterpolator());
                fadeIn.setDuration(400);
                productsRecycler.startAnimation(fadeIn);
            }
        });
        productsRecycler.startAnimation(fadeOut);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Animation in = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_slowed);
        Animation out = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
        switchLayoutManager.setInAnimation(in);
        switchLayoutManager.setOutAnimation(out);
    }

    @Override
    public void onStop() {
        if (loadMoreProgress != null) {
            loadMoreProgress.setVisibility(View.GONE);
        }
        MyApplication.getInstance().cancelPendingRequests(CONST.CATEGORY_REQUESTS_TAG);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (productsRecycler != null) productsRecycler.clearOnScrollListeners();
        super.onDestroyView();
    }
}
