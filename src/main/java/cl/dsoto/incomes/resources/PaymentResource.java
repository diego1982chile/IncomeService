package cl.dsoto.incomes.resources;

import cl.dsoto.incomes.entities.House;
import cl.dsoto.incomes.entities.Payment;
import cl.dsoto.incomes.services.HouseService;
import cl.dsoto.incomes.services.PaymentService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by root on 13-10-22.
 */
@RequestScoped
@Produces(APPLICATION_JSON)
@Path("houses")
public class PaymentResource {

    @Inject
    PaymentService paymentService;

    String errorMsg;

    static private final Logger logger = Logger.getLogger(PaymentResource.class.getName());

    @GET
    public Response getPayments(long feeId) {
        try {
            List<Payment> payments = paymentService.getPayments();
            return Response.ok(payments).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }

    @GET
    @Path("new")
    public Response getNewPayment() {
        try {
            Payment payment = Payment.builder().build();
            return Response.ok(payment).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }

    @GET
    @Path("{id}")
    public Response getPaymentById(@PathParam("id") int id) {
        try {
            Payment payment = paymentService.getPaymentById(id);
            return Response.ok(payment).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }

    @POST
    @Path("save")
    public Response savePayment(Payment payment) {
        try {
            Payment newPayment = paymentService.savePayment(payment);
            return Response.ok(newPayment).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            errorMsg = getRootCause(e);
        }
        return Response.serverError().entity(errorMsg).build();
    }

    @DELETE
    @Path("delete/{id}")
    public Response deletePayment(@PathParam("id") long id) {
        try {
            paymentService.deleteHouse(id);
            return Response.ok(id).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }

    private String getRootCause(Exception e) {
        Throwable cause = e.getCause();

        while(cause.getCause() != null) {
            cause = cause.getCause();
        }

        return cause.getMessage();
    }
}
