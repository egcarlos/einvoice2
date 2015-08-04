/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.labtech.einvoice.external.queries;

import java.util.HashMap;
import java.util.LinkedList;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Carlos Echeverria
 */
@XmlAccessorType(FIELD)
@XmlType(propOrder = {"entries", "links", "items"})
public class WebDocument {

    @XmlElement(name = "entry")
    private HashMap<String, String> entries;

    @XmlElement(name = "link")
    private HashMap<String, String> links;

    @XmlElement(name = "item")
    private LinkedList<HashMap<String, String>> items;

    public HashMap<String, String> getEntries() {
        if (entries == null) {
            entries = new HashMap<>();
        }
        return entries;
    }

    public void setEntries(HashMap<String, String> entries) {
        this.entries = entries;
    }

    public HashMap<String, String> getLinks() {
        if (links == null) {
            links = new HashMap<>();
        }
        return links;
    }

    public void setLinks(HashMap<String, String> links) {
        this.links = links;
    }

    public LinkedList<HashMap<String, String>> getItems() {
        if (items == null) {
            items = new LinkedList<>();
        }
        return items;
    }

    public void setItems(LinkedList<HashMap<String, String>> items) {
        this.items = items;
    }

}
