package cl.dsoto.incomes.resources;

import cl.dsoto.incomes.entities.House;
import cl.dsoto.incomes.entities.Year;
import cl.dsoto.incomes.services.HouseService;
import cl.dsoto.incomes.services.YearService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
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
public class HouseResource {

    @Inject
    HouseService houseService;

    static private final Logger logger = Logger.getLogger(HouseResource.class.getName());

    @GET
    public Response getAllHouses() {
        try {
            List<House> houses = houseService.getHouses();
            return Response.ok(houses).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }

    @GET
    @Path("{id}")
    public Response getHouseById(@PathParam("id") int id) {
        try {
            House house = houseService.getHouseById(id);
            return Response.ok(house).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }

    @POST
    @Path("save")
    public Response saveHouse(House house) {
        try {
            House newHouse = houseService.saveHouse(house);
            return Response.ok(newHouse).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }
}
