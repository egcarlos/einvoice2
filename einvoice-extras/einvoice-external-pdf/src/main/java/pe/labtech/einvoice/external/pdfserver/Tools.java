/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.labtech.einvoice.external.pdfserver;

/**
 *
 * @author Carlos Echeverria
 */
public class Tools {

    public String formatAsNumberWithSeparator(String number) {
        if (number == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(number);
        int ib = number.indexOf(".");
        //cuando no hay separador de decimales
        if (ib < 0) {
            ib = number.length();
        }
        //el primer grupo no inserta separador
        ib -= 3;
        //solo inserta mientras esta por encima de la primera posicion
        while (ib > 0) {
            sb.insert(ib, "'");
            ib -= 3;
        }
        return sb.toString();
    }

}
