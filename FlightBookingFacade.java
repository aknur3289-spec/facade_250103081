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

    public String bookFlight(String passengerName,
                             String flightCode,
                             String seatNumber,
                             double baseFareUSD,
                             int bagCount,
                             double bagWeightKg,
                             String currency,
                             String cardNumber) {
        String lockToken = null;

        try {
            // TODO: lock seat, baggage, currency, payment, ticket
            return "";
        } catch (RuntimeException ex) {
            // TODO: release seat
            return "BOOKING_FAILED: " + ex.getMessage();
        } finally {
            // TODO: disconnect
        }
    }
}
