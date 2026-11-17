// Facade for the airline booking subsystems.
// Main only talks to this class, all the subsystem stuff is hidden here.
public class FlightBookingFacade {

    private final SeatInventorySystem seatSystem;
    private final BaggagePricingEngine baggageEngine;
    private final CurrencyConverter currencyConverter;
    private final PaymentProcessor paymentProcessor;
    private final TicketIssuer ticketIssuer;

    public FlightBookingFacade() {
        this.seatSystem = new SeatInventorySystem();
        this.baggageEngine = new BaggagePricingEngine();
        this.currencyConverter = new CurrencyConverter();
        this.paymentProcessor = new PaymentProcessor();
        this.ticketIssuer = new TicketIssuer();
    }

    // old version (8 params) - still works, just uses STANDARD tier
    public String bookFlight(String passengerName,
                             String flightCode,
                             String seatNumber,
                             double baseFareUSD,
                             int bagCount,
                             double bagWeightKg,
                             String currency,
                             String cardNumber) {
        return bookFlight(passengerName, flightCode, seatNumber, baseFareUSD,
                bagCount, bagWeightKg, currency, cardNumber, "STANDARD");
    }

    // phase 2 version with loyalty tier
    public String bookFlight(String passengerName,
                             String flightCode,
                             String seatNumber,
                             double baseFareUSD,
                             int bagCount,
                             double bagWeightKg,
                             String currency,
                             String cardNumber,
                             String loyaltyTier) {
        String lockToken = null;
        String tier = (loyaltyTier == null) ? "STANDARD" : loyaltyTier.trim().toUpperCase();

        try {
            // 1. connect and lock the seat
            seatSystem.connectToReservationServer();
            lockToken = seatSystem.lockSeat(flightCode, seatNumber, passengerName);

            // 2. baggage fee (silver and gold dont pay for bags)
            double baggageFee = 0.0;
            if (!tier.equals("SILVER") && !tier.equals("GOLD")) {
                baggageFee = baggageEngine.calculateBaggageFeeUSD(bagCount, bagWeightKg);
            }
            double totalUSD = baseFareUSD + baggageFee;

            // 3. convert to passenger currency
            double convertedTotal = currencyConverter.convertFromUSD(totalUSD, currency);

            // 4. payment
            paymentProcessor.initializeGateway();
            if (tier.equals("SILVER")) {
                paymentProcessor.applyPromoCode("SILVER-FREE-BAG");
            }
            String receiptId = paymentProcessor.chargeCard(cardNumber, convertedTotal, currency);

            // 5. ticket
            return ticketIssuer.generateTicket(passengerName, flightCode, lockToken, receiptId);

        } catch (RuntimeException ex) {
            // something failed -> give the seat back
            if (lockToken != null) {
                seatSystem.releaseSeatLock(lockToken);
            }
            System.out.println("[Facade] Booking failed for " + passengerName + ": " + ex.getMessage());
            return "BOOKING_FAILED: " + ex.getMessage();
        } finally {
            seatSystem.disconnect();
        }
    }
}
