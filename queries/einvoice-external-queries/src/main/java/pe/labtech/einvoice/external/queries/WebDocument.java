/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.labtech.einvoice.external.queries;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    private LinkedHashMap<String, String> entries;

    @XmlElement(name = "link")
    private LinkedHashMap<String, String> links;

    @XmlElement(name = "item")
    private LinkedList<LinkedHashMap<String, String>> items;

    public LinkedHashMap<String, String> getEntries() {
        if (entries == null) {
            entries = new LinkedHashMap<>();
        }
        return entries;
    }

    public void setEntries(LinkedHashMap<String, String> entries) {
        this.entries = entries;
    }

    public LinkedHashMap<String, String> getLinks() {
        if (links == null) {
            links = new LinkedHashMap<>();
        }
        return links;
    }

    public void setLinks(LinkedHashMap<String, String> links) {
        this.links = links;
    }

    public LinkedList<LinkedHashMap<String, String>> getItems() {
        if (items == null) {
            items = new LinkedList<>();
        }
        return items;
    }

    public void setItems(LinkedList<LinkedHashMap<String, String>> items) {
        this.items = items;
    }

}
