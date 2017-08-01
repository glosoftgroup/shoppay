package swiftshop.glosoftgroup.com.shoppay.interfaces;

import android.view.View;

import swiftshop.glosoftgroup.com.shoppay.entities.drawerMenu.DrawerItemCategory;
import swiftshop.glosoftgroup.com.shoppay.entities.drawerMenu.DrawerItemPage;

public interface DrawerRecyclerInterface {

    void onCategorySelected(View v, DrawerItemCategory drawerItemCategory);

    void onPageSelected(View v, DrawerItemPage drawerItemPage);

    void onHeaderSelected();
}
