package swiftshop.glosoftgroup.com.shoppay.interfaces;

import com.android.volley.VolleyError;

public interface RequestListener {

    void requestSuccess(long newId);

    void requestFailed(VolleyError error);
}

