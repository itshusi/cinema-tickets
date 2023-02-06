package uk.gov.dwp.uc.pairtest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */
    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class.getName());

    private int calculatePayment(TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        int totalPayment = 0;
        int totalTickets = 0;

        List<TicketTypeRequest> adultTicket = Arrays.stream(ticketTypeRequests).filter(ticketTypeRequest -> ticketTypeRequest.getTicketType() == TicketTypeRequest.Type.ADULT).collect(Collectors.toList());
        List<TicketTypeRequest> infantTicket = Arrays.stream(ticketTypeRequests).filter(ticketTypeRequest -> ticketTypeRequest.getTicketType() == TicketTypeRequest.Type.INFANT).collect(Collectors.toList());

        if (adultTicket.isEmpty()) {
            throw new InvalidPurchaseException("Adult tickets must be purchased with child or infant tickets.");
        }
        if (!infantTicket.isEmpty()) {
            if (adultTicket.get(0).getNoOfTickets() < infantTicket.get(0).getNoOfTickets()) {
                throw new InvalidPurchaseException("There must be one adult for each infant ticket.");
            }
        }

        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
            int noOfTickets = ticketTypeRequest.getNoOfTickets();
            int costPerTicket = ticketTypeRequest.getTicketType().cost;
            totalPayment += noOfTickets * costPerTicket;
            totalTickets += noOfTickets;

            if (ticketTypeRequest.getTicketType() == TicketTypeRequest.Type.ADULT && noOfTickets == 0) {
                throw new InvalidPurchaseException("Adult tickets must be purchased with child or infant tickets.");
            }


            if (totalTickets > 20) {
                throw new InvalidPurchaseException("Maximum purchase of 20 tickets at one time");
            }
        }
        logger.info("£" + totalPayment + " payable");
        logger.info(totalTickets + " tickets booked");

        return totalPayment;
    }

    private int calculateSeats(TicketTypeRequest... ticketTypeRequests) {
        int totalSeats = 0;

        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
            int noOfTickets = ticketTypeRequest.getNoOfTickets();

            if (ticketTypeRequest.getTicketType() != TicketTypeRequest.Type.INFANT) {
                totalSeats += noOfTickets;
            }
        }
        logger.info(totalSeats + " seats reserved");
        return totalSeats;
    }


    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        TicketPaymentService ticketPaymentService = new TicketPaymentServiceImpl();
        SeatReservationService seatReservationService = new SeatReservationServiceImpl();

        if (accountId <= 0) {
            throw new InvalidPurchaseException("Invalid Account Id");
        }

        ticketPaymentService.makePayment(accountId, calculatePayment(ticketTypeRequests));
        seatReservationService.reserveSeat(accountId, calculateSeats(ticketTypeRequests));

    }

}