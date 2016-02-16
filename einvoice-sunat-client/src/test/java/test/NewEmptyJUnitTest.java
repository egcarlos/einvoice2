package test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.activation.FileDataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Carlos Echeverria
 */
public class NewEmptyJUnitTest {

    public NewEmptyJUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void hello() {

        try { // Call Web Service Operation
            pe.gob.sunat.servicio.registro.comppago.factura.gem.service.BillService_Service service = new pe.gob.sunat.servicio.registro.comppago.factura.gem.service.BillService_Service();
            pe.gob.sunat.servicio.registro.comppago.factura.gem.service.BillService port = service.getBillServicePort();
            // TODO initialize WS operation arguments here
            java.lang.String fileName = "20100047137-01-F098-00000014.zip";
            FileDataSource fds = new FileDataSource(fileName);
            javax.activation.DataHandler contentFile = new javax.activation.DataHandler(fds);
            // TODO process result here
            byte[] result = port.sendBill(fileName, contentFile);
            System.out.println("Result = " + result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
