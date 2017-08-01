package swiftshop.glosoftgroup.com.shoppay.interfaces;

import android.view.View;

import swiftshop.glosoftgroup.com.shoppay.entities.order.Order;

public interface OrdersRecyclerInterface {

    void onOrderSelected(View v, Order order);

}
