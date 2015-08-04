/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.labtech.einvoice.external.pdfserver;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.pdf417.encoder.Compaction;
import com.google.zxing.pdf417.encoder.Dimensions;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import pe.labtech.einvoice.core.entity.Document;
import pe.labtech.einvoice.core.model.PrivateDatabaseManagerLocal;

/**
 * REST Web Service
 *
 * @author Carlos Echeverria
 */
@Path("")
@Stateless
@LocalBean
public class InvoiceResource {

    @Context
    private UriInfo context;

    @EJB
    private PrivateDatabaseManagerLocal db;

    /**
     * Creates a new instance of GenericResource
     */
    public InvoiceResource() {
    }

    /**
     * Retrieves representation of an instance of referenced document.
     *
     *
     * @param it
     * @param ii
     * @param dt
     * @param dn
     * @return an instance of java.lang.String
     * @throws com.itextpdf.text.DocumentException
     */
    @Path("{issuerType}/{issuerId}/{documentType}/{documentNumber}.pdf")
    @GET
    @Produces("application/pdf")
    public byte[] getPDF(
            @PathParam("issuerType") String it,
            @PathParam("issuerId") String ii,
            @PathParam("documentType") String dt,
            @PathParam("documentNumber") String dn
    ) throws DocumentException, IOException {
        //TODO raise not found exception
        VelocityContext vc = db.seek(e -> documentToVelocity(lookup(e, it + "-" + ii, dt, dn)));

        Velocity.init();
        StringWriter sw = new StringWriter();

        String template = "../resources/" + ii + "/template.vm";

        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(template), Charset.forName("UTF-8"))) {
            Velocity.evaluate(vc, sw, ii, isr);
        }

        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        document.open();
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new StringReader(sw.toString()));
        document.close();
        return bos.toByteArray();
    }

    @Path("{issuerType}/{issuerId}/{documentType}/{documentNumber}/barcode.png")
    @GET
    @Produces("image/png")
    public byte[] getPDF417(
            @PathParam("issuerType") String it,
            @PathParam("issuerId") String ii,
            @PathParam("documentType") String dt,
            @PathParam("documentNumber") String dn,
            @QueryParam("min-cols") @DefaultValue(value = "16") Integer minCols,
            @QueryParam("max-cols") @DefaultValue(value = "16") Integer maxCols,
            @QueryParam("min-rows") @DefaultValue(value = "50") Integer minRows,
            @QueryParam("max-rows") @DefaultValue(value = "500") Integer maxRows,
            @QueryParam("margin") @DefaultValue(value = "5") Integer margin,
            @QueryParam("width") @DefaultValue(value = "300") Integer width,
            @QueryParam("height") @DefaultValue(value = "200") Integer height
    ) throws IOException, WriterException {
        Map<String, String> head = db.seek(e -> documentToMap(lookup(e, it + "-" + ii, dt, dn)));
        String t = buildEncodedText(head);
        MultiFormatWriter mfw = new MultiFormatWriter();
        Map<EncodeHintType, Object> eh = new LinkedHashMap<>();
        eh.put(EncodeHintType.ERROR_CORRECTION, 5);
        eh.put(EncodeHintType.MARGIN, margin);
        eh.put(EncodeHintType.PDF417_COMPACT, false);
        eh.put(EncodeHintType.PDF417_COMPACTION, Compaction.BYTE);
        eh.put(EncodeHintType.PDF417_DIMENSIONS, new Dimensions(minCols, maxCols, minRows, maxRows));
        BitMatrix bm = mfw.encode(t, BarcodeFormat.PDF_417, width, height, eh);

        byte[] data;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bm, "png", bos);
            data = bos.toByteArray();
        }
        return data;
    }

    @Path("{issuerType}/{issuerId}/logo.png")
    @GET
    @Produces("image/png")
    public Response getLogo(
            @PathParam("issuerType") String it,
            @PathParam("issuerId") String ii
    ) {
        File logo = new File("../resources/" + ii + "/logo.png");
        if (logo.exists()) {
            return Response.ok(logo, "image/png").build();
        }
        return Response.noContent().status(Response.Status.NOT_FOUND).build();
    }

    private String buildEncodedText(Map<String, String> head) {
        String t
                = head.get("numeroDocumentoEmisor") + "|"
                + head.get("tipoDocumento") + "|"
                + head.get("serieNumero").replace("-", "|") + "|"
                + head.get("totalIgv") + "|"
                + head.get("totalVenta") + "|"
                + head.get("fechaEmision") + "|"
                + head.get("tipoDocumentoAdquiriente") + "|"
                + head.get("numeroDocumentoAdquiriente") + "|"
                + head.get("hashCode") + "|"
                + head.get("signatureValue") + "|";
        return t;
    }

    private VelocityContext documentToVelocity(Document d) {
        Map<String, String> head = documentToMap(d);
        List<Map<String, String>> items = itemsToList(d);
        VelocityContext _vc = new VelocityContext();
        _vc.put("head", head);
        _vc.put("items", items);
        _vc.put("tools", new Tools());
        return _vc;
    }

    private List<Map<String, String>> itemsToList(Document d) {
        List<Map<String, String>> items = new LinkedList<>();
        d.getItems().forEach(i -> {
            Map<String, String> item = new LinkedHashMap<>();
            i.getAttributes().forEach(a -> item.put(a.getName(), a.getValue()));
            i.getAuxiliars().forEach(x -> item.put(x.getCode(), x.getValue()));
            items.add(item);
        });
        return items;
    }

    private Map<String, String> documentToMap(Document d) {
        Map<String, String> head = new LinkedHashMap<>();
        d.getAttributes().forEach(a -> head.put(a.getName(), a.getValue()));
        d.getResponses().forEach(r -> head.put(r.getName(), r.getValue()));
        d.getLegends().forEach(l -> head.put(l.getCode(), l.getValue()));
        d.getAuxiliars().forEach(x -> head.put(x.getCode(), x.getValue()));
        return head;
    }

    private Document lookup(EntityManager e, String ci, String dt, String dn) {
        Document d = e.createQuery(
                "SELECT o FROM Document o WHERE o.clientId = :ci AND o.documentType = :dt AND o.documentNumber = :dn",
                Document.class
        )
                .setParameter("ci", ci)
                .setParameter("dt", dt)
                .setParameter("dn", dn)
                .setMaxResults(1)
                .getSingleResult();
        return d;
    }
}
