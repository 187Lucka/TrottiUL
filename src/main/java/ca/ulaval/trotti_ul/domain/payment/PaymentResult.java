package ca.ulaval.trotti_ul.domain.payment;

public record PaymentResult(boolean success, String transactionId, String message) {

    public static PaymentResult successful(String transactionId) {
        return new PaymentResult(true, transactionId, "Payment successful");
    }

    public static PaymentResult failed(String message) {
        return new PaymentResult(false, null, message);
    }

    public boolean isSuccess() {
        return success;
    }
}
