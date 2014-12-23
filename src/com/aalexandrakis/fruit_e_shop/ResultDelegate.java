package com.aalexandrakis.fruit_e_shop;

import com.paypal.android.MEP.PayPalResultDelegate;

import java.io.Serializable;

public class ResultDelegate implements PayPalResultDelegate, Serializable {

    private static final long serialVersionUID = 10001L;
    private String userId;
    public ResultDelegate (String userId){
        super();
        this.userId = userId;
    }
    /**
     * Notification that the payment has been completed successfully.
     *
     * @param payKey
     *            the pay key for the payment
     * @param paymentStatus
     *            the status of the transaction
     */
    public void onPaymentSucceeded(String payKey, String paymentStatus) {
        ViewCart.resultTitle = "SUCCESS";
        ViewCart.resultInfo = "You have successfully completed your transaction.";
        ViewCart.resultExtra = "Key: " + payKey;
        ViewCart.payKey = payKey;
    }

    /**
     * Notification that the payment has failed.
     *
     * @param paymentStatus
     *            the status of the transaction
     * @param correlationID
     *            the correlationID for the transaction failure
     * @param payKey
     *            the pay key for the payment
     * @param errorID
     *            the ID of the error that occurred
     * @param errorMessage
     *            the error message for the error that occurred
     */
    public void onPaymentFailed(String paymentStatus, String correlationID,
                                String payKey, String errorID, String errorMessage) {
        ViewCart.resultTitle = "FAILURE";
        ViewCart.resultInfo = errorMessage;
        ViewCart.resultExtra = "Error ID: " + errorID
                + "\nCorrelation ID: " + correlationID + "\nPay Key: " + payKey;
    }

    /**
     * Notification that the payment was canceled.
     *
     * @param paymentStatus
     *            the status of the transaction
     */
    public void onPaymentCanceled(String paymentStatus) {
        ViewCart.resultTitle = "CANCELED";
        ViewCart.resultInfo = "The transaction has been cancelled.";
        ViewCart.resultExtra = "";
    }
}
