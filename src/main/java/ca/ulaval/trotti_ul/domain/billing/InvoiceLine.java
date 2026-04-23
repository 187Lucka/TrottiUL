package ca.ulaval.trotti_ul.domain.billing;

public record InvoiceLine(String description, int amountInCents) {

    public static InvoiceLine basePass() {
        return new InvoiceLine("Pass de base (30 min/jour incluses)", 4500);
    }

    public static InvoiceLine extraDuration(int extraMinutes, int amountInCents) {
        return new InvoiceLine("Durée supplémentaire (+" + extraMinutes + " min/jour)", amountInCents);
    }

    public static InvoiceLine rideOverage(int amountInCents, String rideId, boolean monthly) {
        String mode = monthly ? "Mensuel" : "Par trajet";
        return new InvoiceLine(mode + " - Frais dépassement trajet (" + rideId + ")", amountInCents);
    }
}
