package swiftshop.glosoftgroup.com.shoppay.interfaces;

import swiftshop.glosoftgroup.com.shoppay.entities.cart.CartDiscountItem;
import swiftshop.glosoftgroup.com.shoppay.entities.cart.CartProductItem;

public interface CartRecyclerInterface {

    void onProductUpdate(CartProductItem cartProductItem);

    void onProductDelete(CartProductItem cartProductItem);

    void onDiscountDelete(CartDiscountItem cartDiscountItem);

    void onProductSelect(long productId);

}
