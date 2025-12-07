package Marketplace;

import java.io.Serializable;
import Exepciones.SaldoInsuficienteException;
import logica.Cliente;

public class ContraOferta implements Serializable { 

    private static final long serialVersionUID = 1L;

    private Cliente comprador;       
    private Oferta ofertaOriginal;     
    private double nuevoPrecio;
    private boolean aceptada;
    private boolean usarSaldo;


    public ContraOferta(Cliente comprador, Oferta ofertaOriginal, double nuevoPrecio,boolean usarSaldo) {
        this.comprador = comprador;
        this.ofertaOriginal = ofertaOriginal;
        this.nuevoPrecio = nuevoPrecio;
        this.aceptada = false;
        this.usarSaldo=usarSaldo;
    }

    public void aceptar() throws Exception {
        // Solo el usuario que creio la oferta puede ver las contraofertas, hace que es el solo que puede acceptar
    	if (this.usarSaldo == true && this.comprador.getSaldoVirtual() < this.nuevoPrecio){
			throw new SaldoInsuficienteException(this.comprador);
    	}else if (this.usarSaldo == true) {
    		this.comprador.setSaldoVirtual(this.comprador.getSaldoVirtual()-this.nuevoPrecio);
    	}
    	
        this.aceptada = true;
        ofertaOriginal.setVendida(true);
        //transferirTiquete
        Cliente vendedorOferta=getVendedor();
        vendedorOferta.setSaldoVirtual(vendedorOferta.getSaldoVirtual()+this.nuevoPrecio);
        vendedorOferta.transferirTiquete(ofertaOriginal.getTiquete(), this.comprador.getLogin(), this.ofertaOriginal.getVendedor().getContrasena());



        
        
        // Notifiar el comprador ?
    }

    
    public void rechazar() throws Exception {
        // Solo el usuario que creio la oferta puede rechazar

        this.aceptada = false;
        this.ofertaOriginal.removeContraOferta(this);
        //notifiar el comprador ?
    }

    
    // Getters
    public String getComprador() { 
    	return comprador.getLogin(); 
    	}
    
    public double getNuevoPrecio() { 
    	return nuevoPrecio; 
    	}
    
    public boolean isAceptada() { 
    	return aceptada; 
    	}
    
    public Oferta getOfertaOriginal() { 
    	return ofertaOriginal; 
    	}

    public String getDescripcion() {
    	return comprador.getLogin()+ " quiere comprar el tiquete : "+ ofertaOriginal.getTiquete().getId()+ " de " +ofertaOriginal.getVendedor()+
    			" al precio de "+ nuevoPrecio;
    }
    
    public Cliente getVendedor() {
    	return this.ofertaOriginal.getVendedor();
    }
    
    @Override
    public String toString() {
        return "Contraoferta{" +
                "comprador=" + comprador.getLogin() +
                ", precio=" + nuevoPrecio +
                ", aceptada=" + aceptada +
                '}';
    }
}

