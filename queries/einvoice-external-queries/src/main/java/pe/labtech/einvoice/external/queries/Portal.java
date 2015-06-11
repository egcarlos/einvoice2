/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.labtech.einvoice.external.queries;

import java.util.List;
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
     * @param issuer
     * @param acquirer
     * @param period
     * @return
     */
    @WebMethod(operationName = "findDocuments")
    public List<WebDocument> findDocuments(
            @WebParam(name = "issuer") String issuer,
            @WebParam(name = "acquirer") String acquirer,
            @WebParam(name = "issueDate") String period
    ) {
        return db.seek(e -> e
                .createQuery(
                        findDocumentsCriteria(
                                e,
                                issuer,
                                acquirer,
                                period
                        )
                )
                .getResultList()
                .stream()
                .map(d -> {
                    WebDocument wd = new WebDocument();
                    d.getAttributes().forEach(a -> wd.getEntries().put(a.getName(), a.getValue()));
                    d.getData().stream()
                    .filter(a -> a.getSource() != null)
                    .forEach(a -> wd.getLinks().put(a.getName(), a.getSource()));
                    return wd;
                })
                .collect(Collectors.toList())
        );
    }

    private CriteriaQuery<Document> findDocumentsCriteria(EntityManager e, String issuer, String acquirer, String period) {
        CriteriaBuilder cb = e.getCriteriaBuilder();
        CriteriaQuery<Document> cq = cb.createQuery(Document.class);
        Root<Document> _d = cq.from(Document.class);
        ListJoin<Document, DocumentAttribute> _f = _d.join(Document_.attributes);
        ListJoin<Document, DocumentAttribute> _a = _d.join(Document_.attributes);
        cq.where(
                cb.like(_d.get(Document_.clientId), cb.concat("%-", cb.literal(issuer))),
                cb.equal(_a.get(DocumentAttribute_.name), "numeroDocumentoAdquiriente"),
                cb.equal(_a.get(DocumentAttribute_.value), acquirer),
                cb.equal(_f.get(DocumentAttribute_.name), "fechaEmision"),
                cb.like(_f.get(DocumentAttribute_.value), cb.concat(cb.literal(period), "%"))
        );
        return cq;
    }

    @WebMethod(operationName = "findDetails")
    public WebDocument findDetails(
            @WebParam(name = "issuer") String issuer,
            @WebParam(name = "acquirer") String acquirer,
            @WebParam(name = "type") String type,
            @WebParam(name = "document") String document
    ) {
        return null;
    }
}
