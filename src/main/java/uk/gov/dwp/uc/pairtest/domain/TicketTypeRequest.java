package uk.gov.dwp.uc.pairtest.domain;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Immutable Object
 */

public class TicketTypeRequest {

    private int noOfTickets;
    private Type type;

    public TicketTypeRequest(Type type, int noOfTickets) {
        this.type = type;
        this.noOfTickets = noOfTickets;
    }

    public int getNoOfTickets() {
        return noOfTickets;
    }

    public Type getTicketType() {
        return type;
    }

    public enum Type {
        ADULT(20),
        CHILD(10) ,
        INFANT(0);

        public final int cost;

        Type(int cost) {
            this.cost = cost;
        }

    }


}
