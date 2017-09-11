package bf.io.openshop.api;
import bf.io.openshop.CONST;

public class EndPoints {

    /**
     * Base server url.
     */


    private static final String API_URL                  = "http://android.babaviz.com/PS254/";    // staging
    public static final String SHOPS                    = API_URL.concat(CONST.ORGANIZATION_ID + "/shops");
    public static final String SHOPS_SINGLE             = API_URL.concat(CONST.ORGANIZATION_ID + "/shops/%d");
    public static final String NAVIGATION_DRAWER        = API_URL.concat("%d/navigation_drawer");
    public static final String BANNERS                  = API_URL.concat("%d/banners");
    public static final String PAGES_SINGLE             = API_URL.concat("%d/pages/%d");
    public static final String PAGES_TERMS_AND_COND     = API_URL.concat("%d/pages/terms");
    public static final String PRODUCTS                 = API_URL.concat("%d/products");
    public static final String PRODUCTS_SINGLE          = API_URL+"singleproduct.php?prod=id";
    public static final String PRODUCTS_SINGLE_RELATED  = API_URL.concat("%d/products/%d?include=related");
    public static final String USER_REGISTER            = API_URL.concat("%d/users/register");
    public static final String USER_LOGIN_EMAIL         = API_URL+"login.php";
    public static final String USER_LOGIN_FACEBOOK      = API_URL.concat("%d/login/facebook");
    public static final String USER_RESET_PASSWORD      = API_URL+"login.php?user=";
    public static final String USER_SINGLE              = API_URL+"login.php?user=";
    public static final String USER_CHANGE_PASSWORD     = API_URL+"login.php?user=";
    public static final String CART                     = API_URL+"cart.php";
    public static final String CART_INFO                = API_URL+"cart_info.php";
    public static final String CART_ITEM_UPDATE         = API_URL+"cart.php?update=";
    public static final String CART_ITEM_DELETE         = API_URL+"cart.php?delete=";
    public static final String CART_DELIVERY_INFO       = API_URL.concat("%d/cart/delivery-info");
    public static final String CART_DISCOUNTS           = API_URL.concat("%d/cart/discounts");
    public static final String CART_DISCOUNTS_SINGLE    = API_URL.concat("%d/cart/discounts/%d");
    public static final String ORDERS                   = API_URL+"orders.php";
    public static final String ORDERS_SINGLE            = API_URL.concat("%d/orders/%d");
    public static final String BRANCHES                 = API_URL.concat("%d/branches");
    public static final String WISHLIST                 = API_URL+"wishlist.php";
    public static final String WISHLIST_SINGLE          = API_URL+"wishlist.php?id=";
    public static final String WISHLIST_IS_IN_WISHLIST  = API_URL+"is_in_wish_list.php?id=";
    public static final String REGISTER_NOTIFICATION    = API_URL.concat("%d/devices");


    // Notifications parameters
    public static final String NOTIFICATION_LINK        = "link";
    public static final String NOTIFICATION_MESSAGE     = "message";
    public static final String NOTIFICATION_TITLE       = "title";
    public static final String NOTIFICATION_IMAGE_URL   = "image_url";
    public static final String NOTIFICATION_SHOP_ID     = "shop_id";
    public static final String NOTIFICATION_UTM         = "utm";

    private EndPoints() {}
}
