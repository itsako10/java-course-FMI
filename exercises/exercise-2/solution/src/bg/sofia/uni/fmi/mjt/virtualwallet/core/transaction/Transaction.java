package bg.sofia.uni.fmi.mjt.virtualwallet.core.transaction;

import bg.sofia.uni.fmi.mjt.virtualwallet.core.payment.PaymentInfo;

import java.time.LocalDateTime;

public class Transaction {
    private String cardName;
    private LocalDateTime date;
    private PaymentInfo paymentInfo;

    public String getCardName() {
        return cardName;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public String printPaymentInfo() {
        return cardName + " " + date.toString() + " " + paymentInfo.getReason() + " " + paymentInfo.getLocation() + " " + Double.toString(paymentInfo.getCost());
    }

    public Transaction(String cardName, LocalDateTime date, PaymentInfo paymentInfo) {
        this.cardName = cardName;
        this.date = date;
        this.paymentInfo = paymentInfo;
    }
}
