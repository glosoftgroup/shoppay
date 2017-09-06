package bf.io.openshop.ux.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import bf.io.openshop.R;
import bf.io.openshop.entities.suppliers.Supplier;
import bf.io.openshop.interfaces.SupplierRecyclerInterface;
import bf.io.openshop.listeners.OnSingleClickListener;

/**
 * Adapter handling list of drawer items.
 */
public class SupplierRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM_CATEGORY = 1;
    private static final int TYPE_ITEM_PAGE = 2;
    private final SupplierRecyclerInterface drawerRecyclerInterface;
    private LayoutInflater layoutInflater;
    private Context context;
    private List<Supplier> SupplierList = new ArrayList<>();

    /**
     * Creates an adapter that handles a list of drawer items.
     *
     * @param context                 activity context.
     * @param drawerRecyclerInterface listener indicating events that occurred.
     */
    public SupplierRecyclerAdapter(Context context, SupplierRecyclerInterface drawerRecyclerInterface) {
        this.context = context;
        this.drawerRecyclerInterface = drawerRecyclerInterface;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.list_item_drawer_category, parent, false);
            return new ViewHolderItemCategory(view, drawerRecyclerInterface);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderItemCategory) {
            ViewHolderItemCategory viewHolderItemCategory = (ViewHolderItemCategory) holder;
            System.err.println("Error Line:"+position);
            Supplier Supplier = getDrawerItem(position);
            viewHolderItemCategory.bindContent(Supplier);
            viewHolderItemCategory.itemText.setText(Supplier.getName());
            //viewHolderItemCategory.menuIcon.setImageDrawable(Supplier.getMenuIcon());

            if (position == 0) {
                viewHolderItemCategory.itemText.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                viewHolderItemCategory.divider.setVisibility(View.VISIBLE);
                return;
            }

            Picasso.with(context).load(Supplier.getImageUrl())
                    .fit().centerInside()
                    .placeholder(R.drawable.placeholder_loading)
                    .error(R.drawable.placeholder_error)
                    .into(viewHolderItemCategory.menuIcon);

            viewHolderItemCategory.itemText.setTextColor(ContextCompat.getColor(context, R.color.textPrimary));
            viewHolderItemCategory.itemText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            viewHolderItemCategory.divider.setVisibility(View.GONE);

        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        // Clear the animation when the view is detached. Prevent bugs during fast scroll.
        if (holder instanceof ViewHolderItemCategory) {
            ((ViewHolderItemCategory) holder).layout.clearAnimation();
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        // Apply the animation when the view is attached
        if (holder instanceof ViewHolderItemCategory) {
            setAnimation(((ViewHolderItemCategory) holder).layout);
        }
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        viewToAnimate.startAnimation(animation);
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return SupplierList.size(); // the number of items in the list will be +1 the titles including the header view.
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        else if (position <= SupplierList.size())
            return TYPE_ITEM_CATEGORY;
        else
            return TYPE_ITEM_PAGE;
    }

    private Supplier getDrawerItem(int position) {
        return SupplierList.get(position);
    }


    public void addDrawerItemList(List<Supplier> drawerItemCategories) {
        if (drawerItemCategories != null)
            SupplierList.addAll(drawerItemCategories);
    }


    public void addDrawerItem(Supplier Supplier) {
        SupplierList.add(Supplier);

    }

  

    // Provide a reference to the views for each data item
    public static class ViewHolderItemCategory extends RecyclerView.ViewHolder {
        public TextView itemText;
        public ImageView subMenuIndicator;
        public ImageView menuIcon;
        public LinearLayout layout;
        private Supplier Supplier;
        private View divider;

        public ViewHolderItemCategory(View itemView, final SupplierRecyclerInterface drawerRecyclerInterface) {
            super(itemView);
            itemText = (TextView) itemView.findViewById(R.id.drawer_list_item_text);
            subMenuIndicator = (ImageView) itemView.findViewById(R.id.drawer_list_item_indicator);
            layout = (LinearLayout) itemView.findViewById(R.id.drawer_list_item_layout);
            divider = itemView.findViewById(R.id.drawer_list_item_divider);
            menuIcon=(ImageView)itemView.findViewById(R.id.menuIcon);
            itemView.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    drawerRecyclerInterface.onSuplierSelected(v, Supplier);
                }
            });
        }

        public void bindContent(Supplier Supplier) {
            this.Supplier = Supplier;
        }
    }


}
