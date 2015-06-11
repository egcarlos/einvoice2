/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.labtech.einvoice.external.queries;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Carlos Echeverria
 */
public class MapAdapter extends XmlAdapter<HashMap<String, String>, Map<String, String>> {

    @Override
    public Map<String, String> unmarshal(HashMap<String, String> v) throws Exception {
        return v;
    }

    @Override
    public HashMap<String, String> marshal(Map<String, String> v) throws Exception {
        if (v instanceof HashMap) {
            return (HashMap<String, String>) v;
        }
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        result.putAll(v);
        return result;
    }

}
