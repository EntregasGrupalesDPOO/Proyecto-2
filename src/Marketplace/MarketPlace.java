package Marketplace;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import logica.Cliente;

public class MarketPlace implements Serializable{
    private List<Oferta> ofertas;
    private LogReventa log;

    public MarketPlace() {
        this.ofertas = new ArrayList<>();
        this.log = new LogReventa();
    }
    
    public void publicarOferta(Oferta oferta) {
        ofertas.add(oferta);
        log.registrarEvento("Nueva oferta publicada: " + oferta.getDescripcion());
    }

    public void eliminarOferta(Oferta oferta, Cliente autor) {
        if (ofertas.remove(oferta)) {
            log.registrarEvento("Oferta eliminada por " + autor.getLogin() + ": " + oferta.getDescripcion());
        }
    }
    
    public void publicarContraOferta(ContraOferta contra) {
        contra.getOfertaOriginal().agregarContraOferta(contra);
        log.registrarEvento("Nueva contra oferta publicada: " + contra.getDescripcion());
    }


    public void aceptarContraOferta(ContraOferta contra) {
    	try {
			contra.aceptar();
		} catch (Exception e) {
			e.printStackTrace();
		}
        log.registrarEvento("Contraoferta aceptada: " + contra.getDescripcion());
        
    }
    
    public void rechazarContraOferta(ContraOferta contra) {
    	try {
			contra.rechazar();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	log.registrarEvento("Contraoferta rechazada: " + contra.getDescripcion());
    }

    public List<Oferta> getOfertas() {
        return ofertas;
    }

    public LogReventa getLog() {
        return log;
    }
}
