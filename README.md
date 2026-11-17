# Facade Pattern Lab – Flight Booking

Lab task: hide 5 airline booking subsystems behind one class `FlightBookingFacade`.

> **Note:** this solution (`FlightBookingFacade.java`, `Main.java` and this README) was mostly written by AI (Claude), and then reviewed and run by me.
> The 5 subsystem classes are the provided starter code and were not changed.

## Files

| File | What it is |
|------|-----------|
| `SeatInventorySystem.java` | given – seat locks |
| `BaggagePricingEngine.java` | given – baggage fee in USD |
| `CurrencyConverter.java` | given – USD to EUR/GBP/KZT |
| `PaymentProcessor.java` | given – card payment + promo code |
| `TicketIssuer.java` | given – e-ticket |
| `FlightBookingFacade.java` | the facade (my part) |
| `Main.java` | client with the tests |

## How it works

`bookFlight(...)` does everything in order:
1. connect to seat server and lock seat
2. calculate baggage fee (skipped for SILVER / GOLD)
3. convert price to the needed currency
4. init payment gateway, apply `SILVER-FREE-BAG` for SILVER, charge card
5. issue ticket

If something fails after the seat is locked, the lock is released in `catch`.
`disconnect()` is always called in `finally`.

Phase 2: there are two `bookFlight` methods. The old one with 8 params just calls the new one with `"STANDARD"`.

## Run

```
javac *.java
java Main
```

## Tests in Main

- Test 1 – normal booking (seat 14B, 2 bags 24.5 kg, EUR)
- Test 2 – seat 13A is blocked, returns BOOKING_FAILED, program keeps going
- Test 3 / 3b – bad card number, seat lock is released, then disconnect
- Test 4 – SILVER, 3 bags 30 kg: no `[BaggageEngine]` line, 10% promo applied
