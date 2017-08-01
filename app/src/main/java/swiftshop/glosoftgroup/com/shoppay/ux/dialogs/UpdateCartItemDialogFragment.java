package swiftshop.glosoftgroup.com.shoppay.ux.dialogs;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import swiftshop.glosoftgroup.com.shoppay.CONST;
import swiftshop.glosoftgroup.com.shoppay.MyApplication;
import swiftshop.glosoftgroup.com.shoppay.R;
import swiftshop.glosoftgroup.com.shoppay.SettingsMy;
import swiftshop.glosoftgroup.com.shoppay.api.GsonRequest;
import swiftshop.glosoftgroup.com.shoppay.api.JsonRequest;
import swiftshop.glosoftgroup.com.shoppay.entities.User;
import swiftshop.glosoftgroup.com.shoppay.entities.cart.CartProductItem;
import swiftshop.glosoftgroup.com.shoppay.entities.product.Product;
import swiftshop.glosoftgroup.com.shoppay.entities.product.ProductColor;
import swiftshop.glosoftgroup.com.shoppay.entities.product.ProductQuantity;
import swiftshop.glosoftgroup.com.shoppay.entities.product.ProductVariant;
import swiftshop.glosoftgroup.com.shoppay.interfaces.RequestListener;
import swiftshop.glosoftgroup.com.shoppay.utils.JsonUtils;
import swiftshop.glosoftgroup.com.shoppay.utils.MsgUtils;
import swiftshop.glosoftgroup.com.shoppay.ux.adapters.QuantitySpinnerAdapter;
import timber.log.Timber;

/**
 * Dialog handles update items in the shopping cart.
 */
public class UpdateCartItemDialogFragment extends DialogFragment {

    /**
     * Defined max product quantity.
     */
    private static final int QUANTITY_MAX = 15;

    private CartProductItem cartProductItem;

    private RequestListener requestListener;

    private View dialogProgress;
    private View dialogContent;
    private Spinner itemColorsSpinner;
    private Spinner itemSizesSpinner;
    private Spinner quantitySpinner;

    /**
     * Creates dialog which handles update items in the shopping cart
     *
     * @param cartProductItem item in the cart, which should be updated.
     * @param requestListener listener receiving update request results.
     * @return new instance of dialog.
     */
    public static UpdateCartItemDialogFragment newInstance(CartProductItem cartProductItem, RequestListener requestListener) {
        if (cartProductItem == null) {
            Timber.e(new RuntimeException(), "Created UpdateCartItemDialogFragment with null parameters.");
            return null;
        }
        UpdateCartItemDialogFragment updateCartItemDialogFragment = new UpdateCartItemDialogFragment();
        updateCartItemDialogFragment.cartProductItem = cartProductItem;
        updateCartItemDialogFragment.requestListener = requestListener;
        return updateCartItemDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.dialogFragmentAnimation);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - OnCreateView", this.getClass().getSimpleName());
        View view = inflater.inflate(R.layout.dialog_update_cart_item, container, false);

        dialogProgress = view.findViewById(R.id.dialog_update_cart_item_progress);
        dialogContent = view.findViewById(R.id.dialog_update_cart_item_content);
        itemColorsSpinner = (Spinner) view.findViewById(R.id.dialog_update_cart_item_color_spin);
        itemSizesSpinner = (Spinner) view.findViewById(R.id.dialog_update_cart_item_size_spin);
        TextView itemName = (TextView) view.findViewById(R.id.dialog_update_cart_item_title);
        itemName.setText(cartProductItem.getVariant().getName());

        View btnSave = view.findViewById(R.id.dialog_update_cart_item_save_btn);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantitySpinner != null && itemSizesSpinner != null) {
                    ProductVariant productVariant = (ProductVariant) itemSizesSpinner.getSelectedItem();
                    ProductQuantity productQuantity = (ProductQuantity) quantitySpinner.getSelectedItem();
                    Timber.d("Selected: %s. Quantity: %s", productVariant, productQuantity);
                    if (productVariant != null && productVariant.getSize() != null && productQuantity != null) {
                        updateProductInCart(cartProductItem.getId(), productVariant.getId(), productQuantity.getQuantity());
                    } else {
                        Timber.e(new RuntimeException(), "Cannot obtain info about edited cart item.");
                        MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Internal_error_reload_cart_please), MsgUtils.ToastLength.SHORT);
                        dismiss();
                    }
                } else {
                    Timber.e(new NullPointerException(), "Null spinners in editing item in cart");
                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Internal_error_reload_cart_please), MsgUtils.ToastLength.SHORT);
                    dismiss();
                }
            }
        });

        View btnCancel = view.findViewById(R.id.dialog_update_cart_item_cancel_btn);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // Set item quantity
        QuantitySpinnerAdapter adapterQuantity = new QuantitySpinnerAdapter(getActivity(), getQuantities());
        quantitySpinner = (Spinner) view.findViewById(R.id.dialog_update_cart_item_quantity_spin);
        quantitySpinner.setAdapter(adapterQuantity);

        getProductDetail(cartProductItem);
        return view;
    }

    // Prepare quantity spinner layout
    private List<ProductQuantity> getQuantities() {
        List<ProductQuantity> quantities = new ArrayList<>();
        for (int i = 1; i <= QUANTITY_MAX; i++) {
            ProductQuantity q = new ProductQuantity(i, i + "x");
            quantities.add(q);
        }
        return quantities;
    }

    private void getProductDetail(CartProductItem cartProductItem) {
        String url = "";
        setProgressActive(true);

        GsonRequest<Product> getProductRequest = new GsonRequest<>(Request.Method.GET, url, null, Product.class,
                new Response.Listener<Product>() {
                    @Override
                    public void onResponse(@NonNull Product response) {
                        setProgressActive(false);
                        setSpinners(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setProgressActive(false);
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });

        getProductRequest.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(getProductRequest, CONST.UPDATE_CART_ITEM_REQUESTS_TAG);
    }


    private void setSpinners(final Product product) {
        if (product != null && product.getVariants() != null && product.getVariants().size() > 0) {
            List<ProductColor> productColors = new ArrayList<>();

            for (ProductVariant pv : product.getVariants()) {
                ProductColor pac = pv.getColor();
                if (!productColors.contains(pac)) {
                    productColors.add(pac);
                }
            }

            if (productColors.size() > 1) {
                itemColorsSpinner.setVisibility(View.VISIBLE);


                itemColorsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ProductColor productColor = (ProductColor) parent.getItemAtPosition(position);
                        if (productColor != null) {
                            Timber.d("ColorPicker selected color: %s", productColor.toString());
                            updateSizeSpinner(product, productColor);
                        } else {
                            Timber.e("Retrieved null color from spinner.");
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Timber.d("Nothing selected in product colors spinner.");
                    }
                });
            } else {
                itemColorsSpinner.setVisibility(View.GONE);
                updateSizeSpinner(product, product.getVariants().get(0).getColor());
            }

            int selectedPosition = cartProductItem.getQuantity() - 1;
            if (selectedPosition < 0) selectedPosition = 0;
            if (selectedPosition > (quantitySpinner.getCount() - 1))
                Timber.e(new RuntimeException(), "More item quantity that can be. Quantity: %d, max: %d", (selectedPosition + 1), quantitySpinner.getCount());
            else
                quantitySpinner.setSelection(selectedPosition);
        } else {
            Timber.e("Setting spinners for null product variants.");
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
        }
    }

    /**
     * Update size values in size adapter.
     *
     * @param product      updated product.
     * @param productColor actually selected color.
     */
    private void updateSizeSpinner(Product product, ProductColor productColor) {
        if (product != null) {
            ArrayList<ProductVariant> variantSizeArrayList = new ArrayList<>();

            for (ProductVariant pv : product.getVariants()) {
                if (pv.getColor().equals(productColor)) {
                    variantSizeArrayList.add(pv);
                }
            }


        } else {
            Timber.e("UpdateImagesAndSizeSpinner with null product.");
        }
    }

    private void updateProductInCart(long productCartId, long newVariantId, int newQuantity) {
        User user = SettingsMy.getActiveUser();
        if (user != null) {
            JSONObject jo = new JSONObject();
            try {
                jo.put(JsonUtils.TAG_QUANTITY, newQuantity);
                jo.put(JsonUtils.TAG_PRODUCT_VARIANT_ID, newVariantId);
            } catch (JSONException e) {
                Timber.e(e, "Create update object exception");
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                return;
            }
            Timber.d("update product: %s", jo.toString());

            String url = "";

            setProgressActive(true);
            JsonRequest req = new JsonRequest(Request.Method.PUT, url, jo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Timber.d("Update item in cart: %s", response.toString());
                    if (requestListener != null) requestListener.requestSuccess(0);
                    setProgressActive(false);
                    dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    setProgressActive(false);
                    if (requestListener != null) requestListener.requestFailed(error);
                    dismiss();
                }
            }, getFragmentManager(), user.getAccessToken());

            req.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(req, CONST.UPDATE_CART_ITEM_REQUESTS_TAG);
        } else {
            LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
            loginExpiredDialogFragment.show(getFragmentManager(), "loginExpiredDialogFragment");
        }
    }


    private void setProgressActive(boolean active) {
        if (active) {
            dialogProgress.setVisibility(View.VISIBLE);
            dialogContent.setVisibility(View.INVISIBLE);
        } else {
            dialogProgress.setVisibility(View.GONE);
            dialogContent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        MyApplication.getInstance().getRequestQueue().cancelAll(CONST.UPDATE_CART_ITEM_REQUESTS_TAG);
        super.onStop();
    }
}
