/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.labtech.einvoice.external.queries;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import static javax.ejb.TransactionManagementType.BEAN;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Root;
import pe.labtech.einvoice.commons.model.DatabaseManager;
import pe.labtech.einvoice.core.entity.Document;
import pe.labtech.einvoice.core.entity.DocumentAttribute;
import pe.labtech.einvoice.core.entity.DocumentAttribute_;
import pe.labtech.einvoice.core.entity.Document_;
import pe.labtech.einvoice.core.model.PrivateDatabaseManagerLocal;

/**
 *
 * @author Carlos Echeverria
 */
@WebService()
@Stateless()
@TransactionManagement(BEAN)
public class Portal {

    @EJB(beanInterface = PrivateDatabaseManagerLocal.class)
    private DatabaseManager db;

    /**
     *
     * @param issuer ruc del emisor
     * @param acquirer ruc o dni del cliente
     * @param period filtra el periodo en formato yyyy-MM
     * @param filters filtra las respuestas
     * @return
     */
    @WebMethod(operationName = "findDocuments")
    public List<WebDocument> findDocuments(
            @WebParam(name = "issuer") String issuer,
            @WebParam(name = "acquirer") String acquirer,
            @WebParam(name = "issueDate") String period,
            @WebParam(name = "filters") List<String> filters
    ) {
        return db.seek(e -> e
                .createQuery(findDocumentsCriteria(e, issuer, acquirer, period))
                .getResultList()
                .stream()
                .map(Portal::map)
                .map(d -> clear(d, filters))
                .collect(Collectors.toList())
        );
    }

    private static WebDocument clear(WebDocument wd, List<String> filters) {
        if (filters == null || filters.isEmpty()) {
            return wd;
        }

        Map<String, String> filtered = wd.getEntries().entrySet().stream()
                .filter(e -> filters.contains(e.getKey()))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        wd.getEntries().clear();
        wd.getEntries().putAll(filtered);

        return wd;
    }

    private static WebDocument map(Document d) {
        WebDocument wd = new WebDocument();

        d.getAttributes().stream()
                .forEach(a -> wd.getEntries().put(a.getName(), a.getValue()));

        d.getAuxiliars().stream()
                .forEach(a -> wd.getEntries().put(a.getCode(), a.getValue()));

        d.getLegends().stream()
                .forEach(a -> wd.getEntries().put(a.getCode(), a.getValue()));

        d.getResponses().stream()
                .forEach(a -> wd.getEntries().put(a.getName(), a.getValue()));

        return wd;
    }

    private CriteriaQuery<Document> findDocumentsCriteria(EntityManager e, String issuer, String acquirer, String period) {
        CriteriaBuilder cb = e.getCriteriaBuilder();
        CriteriaQuery<Document> cq = cb.createQuery(Document.class);
        Root<Document> _d = cq.from(Document.class);
        ListJoin<Document, DocumentAttribute> _f = _d.join(Document_.attributes);
        ListJoin<Document, DocumentAttribute> _a = _d.join(Document_.attributes);
        cq
                .where(
                        cb.like(_d.get(Document_.clientId), cb.concat("%-", cb.literal(issuer))),
                        cb.equal(_a.get(DocumentAttribute_.name), "numeroDocumentoAdquiriente"),
                        cb.equal(_a.get(DocumentAttribute_.value), acquirer),
                        cb.equal(_f.get(DocumentAttribute_.name), "fechaEmision"),
                        cb.like(_f.get(DocumentAttribute_.value), cb.concat(cb.literal(period), "%"))
                )
                .orderBy(
                        cb.asc(_f.get(DocumentAttribute_.name))
                );
        return cq;
    }
}
