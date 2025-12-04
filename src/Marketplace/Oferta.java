package Marketplace;

import java.io.Serializable;
import java.util.ArrayList;

import Exepciones.TiqueteNoTransferibleException;
import logica.Tiquete;
import logica.Cliente;

public class Oferta implements Serializable {
    private static final long serialVersionUID = 1L; 

    private Tiquete tiquete;
    private Cliente vendedor;
    private String descripcion;
    private double precio;
    private boolean vendida;
    private ArrayList<ContraOferta> contraOfertas;


    public Oferta(Tiquete tiquete, Cliente vendedor, String descripcion, double precio) throws TiqueteNoTransferibleException {
        if (!tiquete.isTransferible()) {
        	throw new TiqueteNoTransferibleException(tiquete);
        }
        
        this.tiquete = tiquete;
        this.vendedor = vendedor;
        this.descripcion = descripcion;
        this.precio = precio;
        this.vendida = false;
        this.contraOfertas = new ArrayList<>();
    }

    public void agregarContraOferta(ContraOferta contra) {
        contraOfertas.add(contra);
    }


    public Cliente getVendedor() { 
        return vendedor; 
    }

    public void setVendida(boolean vendida) { 
        this.vendida = vendida; 
    }

    public Tiquete getTiquete() {
        return tiquete;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public ArrayList<ContraOferta> getContraOfertas() {
        return contraOfertas;
    }

    public boolean isVendida() {
        return vendida;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public void removeContraOferta(ContraOferta contra) {
    	this.contraOfertas.remove(contra);
    }
    

    @Override
    public String toString() {
        String tipo =  "Tiquete";
        Integer idTiquete = tiquete.getId();
        return "Oferta: " + idTiquete + " (" + tipo + ") - Precio: $" + precio + " - " + descripcion;
    }
}
