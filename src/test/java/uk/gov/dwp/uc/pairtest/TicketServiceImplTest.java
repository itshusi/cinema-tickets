package uk.gov.dwp.uc.pairtest;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

public class TicketServiceImplTest {
    private TicketService ticketService;
    private static final String LOGGER_NAME = "uk.gov.dwp.uc.pairtest";
    private static MemoryAppender memoryAppender;

    @Before
    public void setUp() {
        ticketService = new TicketServiceImpl();

        Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
        memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.setLevel(Level.DEBUG);
        logger.addAppender(memoryAppender);
        memoryAppender.start();
    }

    @Test
    public void whenPurchase1AdultTicket() {
        ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1));

        assertTrue(memoryAppender.contains("£20 payable", Level.INFO));
        assertTrue(memoryAppender.contains("1 tickets booked", Level.INFO));
        assertTrue(memoryAppender.contains("1 seats reserved", Level.INFO));
    }

    @Test
    public void whenPurchase1Adult1ChildTickets() {
        ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1));

        assertTrue(memoryAppender.contains("£30 payable", Level.INFO));
        assertTrue(memoryAppender.contains("2 tickets booked", Level.INFO));
        assertTrue(memoryAppender.contains("2 seats reserved", Level.INFO));
    }

    @Test
    public void whenPurchase1Adult1Child1InfantTickets() {
        ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1));

        assertTrue(memoryAppender.contains("£30 payable", Level.INFO));
        assertTrue(memoryAppender.contains("3 tickets booked", Level.INFO));
        assertTrue(memoryAppender.contains("2 seats reserved", Level.INFO));
    }

    @Test
    public void whenPurchase20AdultTickets() {
        ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 20));

        assertTrue(memoryAppender.contains("£400 payable", Level.INFO));
        assertTrue(memoryAppender.contains("20 tickets booked", Level.INFO));
        assertTrue(memoryAppender.contains("20 seats reserved", Level.INFO));
    }

    @Test
    public void whenPurchase10Adult5Child5InfantTickets() {
        ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 10),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 5));

        assertTrue(memoryAppender.contains("£250 payable", Level.INFO));
        assertTrue(memoryAppender.contains("20 tickets booked", Level.INFO));
        assertTrue(memoryAppender.contains("15 seats reserved", Level.INFO));
    }

    @Test
    public void whenPurchase2Adult2Child2InfantTickets() {
        ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2));

        assertTrue(memoryAppender.contains("£60 payable", Level.INFO));
        assertTrue(memoryAppender.contains("6 tickets booked", Level.INFO));
        assertTrue(memoryAppender.contains("4 seats reserved", Level.INFO));
    }

    @Test(expected = InvalidPurchaseException.class)
    public void whenPurchaseTicketsInvalidId() {
        ticketService.purchaseTickets(0L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2));
    }

    @Test(expected = InvalidPurchaseException.class)
    public void whenPurchaseOver20Tickets() {
        ticketService.purchaseTickets(100L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 15),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1));
    }

    @Test(expected = InvalidPurchaseException.class)
    public void whenPurchaseNoAdultsTickets() {
        ticketService.purchaseTickets(100L,
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 7));
        ticketService.purchaseTickets(100L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 7));
    }

    @Test(expected = InvalidPurchaseException.class)
    public void whenPurchaseMoreInfantsThanAdults() {
        ticketService.purchaseTickets(100L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 10),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 9));
        ticketService.purchaseTickets(100L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2));
    }

    @Test
    public void whenPurchaseTicketCalledVerified() {
        TicketService ticketService1 = mock(TicketService.class);
        doNothing().when(ticketService1).purchaseTickets(isA(Long.class), isA(TicketTypeRequest.class));

        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);

        ticketService1.purchaseTickets(1L, ticketTypeRequest);
        verify(ticketService1, times(1)).purchaseTickets(1L, ticketTypeRequest);
    }
}