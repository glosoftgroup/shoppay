package swiftshop.glosoftgroup.com.shoppay.interfaces;

import swiftshop.glosoftgroup.com.shoppay.entities.delivery.Payment;

public interface PaymentDialogInterface {
    void onPaymentSelected(Payment payment);
}
