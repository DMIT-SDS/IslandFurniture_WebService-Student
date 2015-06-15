package service;

import Entity.Countryentity;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("entity.countryentity")
public class CountryentityFacadeREST extends AbstractFacade<Countryentity> {

    @PersistenceContext(unitName = "WebService_MobilePU")
    private EntityManager em;

    public CountryentityFacadeREST() {
        super(Countryentity.class);
    }

    @POST
    @Override
    @Consumes({"application/xml", "application/json"})
    public void create(Countryentity entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({"application/xml", "application/json"})
    public void edit(@PathParam("id") Long id, Countryentity entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Long id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Countryentity find(@PathParam("id") Long id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({"application/xml", "application/json"})
    public List<Countryentity> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Countryentity> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
    }

    @GET
    @Path("country")
    @Produces({"application/json"})
    public List<Countryentity> listAllCountries() {
        Query q = em.createQuery("Select c from Countryentity c");
        List<Countryentity> list = q.getResultList();
        List<Countryentity> countryList = new ArrayList();
        for (Countryentity country : list) {
            em.detach(country);
            country.setItemCountryentityList(null);
            country.setMemberentityList(null);
            country.setStoreentityList(null);
            country.setWarehouseentityList(null);
            countryList.add(country);
        }
        return countryList;
    }
    
    //#shoppingcart - get the item quantity based on countryID and SKU. 
    //this function is used in the ECommerce_AddFurnitureToListServlet
    @GET
    @Path("getQuantity")
    @Produces({"application/json"})
    public Response getItemQuantityOfCountry(@QueryParam("countryID") Long countryID, @QueryParam("SKU") String SKU) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?zeroDateTimeBehavior=convertToNull&user=root&password=12345");
            String stmt = "Select sum(li.QUANTITY) as sum from country_ecommerce c, warehouseentity w, storagebinentity sb, storagebinentity_lineitementity sbli, lineitementity li, itementity i where li.ITEM_ID=i.ID and sbli.lineItems_ID=li.ID and sb.ID=sbli.StorageBinEntity_ID and w.id=sb.WAREHOUSE_ID and c.warehouseentity_id=w.id and countryentity_id = ? and i.SKU= ?";//25
            PreparedStatement ps = conn.prepareStatement(stmt);
            ps.setLong(1, countryID);
            ps.setString(2, SKU);
            ResultSet rs = ps.executeQuery();
            String qty = "";
            while (rs.next()) {
                qty = rs.getString("sum");
            }
            if (qty == null) {
                qty = "0";
            }
            return Response.ok(qty, MediaType.APPLICATION_JSON).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
