package swiftshop.glosoftgroup.com.shoppay.ux.adapters;

/**
 * Created by admin on 1/19/2017.
 */

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import swiftshop.glosoftgroup.com.shoppay.R;
import swiftshop.glosoftgroup.com.shoppay.entities.merchants.MerchantsCategories;
import swiftshop.glosoftgroup.com.shoppay.interfaces.MerchantCategoriesInterface;
import swiftshop.glosoftgroup.com.shoppay.listeners.OnSingleClickListener;
import swiftshop.glosoftgroup.com.shoppay.views.ResizableImageView;
import timber.log.Timber;


public class MerchantCategoryRecyclerAdapter extends RecyclerView.Adapter<MerchantCategoryRecyclerAdapter.ViewHolder> {

    private final MerchantCategoriesInterface merchantCategoriesInterface;
    private final Context context;
    private LayoutInflater layoutInflater;
    private List<MerchantsCategories> mcat = new ArrayList<>();

    public MerchantCategoryRecyclerAdapter(Context context, MerchantCategoriesInterface merchantCategoriesInterface) {
        this.merchantCategoriesInterface = merchantCategoriesInterface;
        this.context = context;
    }

    @Override
    public MerchantCategoryRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.getContext());

//        View view = layoutInflater.inflate(R.layout.list_item_banners, parent, false);
        View view = layoutInflater.inflate(R.layout.grid_item_layout, parent, false);
        return new ViewHolder(view, merchantCategoriesInterface);
    }

    /**
     * Clear all data.
     */
    public void clear() {
        mcat.clear();
    }

    public void addMerchantCategories(List<MerchantsCategories> MerchantsCategoriesList) {
        if (MerchantsCategoriesList != null && !MerchantsCategoriesList.isEmpty()) {
            mcat.addAll(MerchantsCategoriesList);
            Timber.d("mcat contains "+ mcat);
            notifyDataSetChanged();
        } else {
            Timber.e("Adding empty category list.");
        }
    }

    /**
     * Check if some merchant categories exist.
     *
     * @return true if content is empty.
     */
    public boolean isEmpty() {
        return mcat == null || mcat.isEmpty();
    }

    @Override
    public void onBindViewHolder(MerchantCategoryRecyclerAdapter.ViewHolder holder, int position) {
        MerchantsCategories mc = getCategoryItem(position);
        holder.bindContent(mc);

        Picasso.with(context).load(mc.getImageUrl())
                .placeholder(R.drawable.placeholder_loading)
                .fit().centerInside()
                .into(holder.mcImage);
    }

    private MerchantsCategories getCategoryItem(int position) {
        return mcat.get(position);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ResizableImageView mcImage;
        private MerchantsCategories mc;

        public ViewHolder(View itemView, final MerchantCategoriesInterface merchantCategoriesInterface) {
            super(itemView);
            //mcImage = (ResizableImageView) itemView.findViewById(R.id.banner_image);
            mcImage = (ResizableImageView) itemView.findViewById(R.id.grid_item_image);
            itemView.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(final View view) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            merchantCategoriesInterface.onMerchantCategorySelected(mc);
                        }
                    }, 200);
                }
            });
        }

        public void bindContent(MerchantsCategories mc) {
            this.mc = mc;
        }
    }
}
