package logica;

import java.io.Serializable;
import java.util.ArrayList;

public class Localidad implements Serializable{
	private String nombre;
	private double precioTiquete ;
	private String tipoTiquete;
	private ArrayList<Tiquete> tiquetes;
	private double descuento;
	
	public Localidad(String nombre, int capacidad, double precioTiquete, String tipoTiquete, Evento evento) {
		this.tiquetes = new ArrayList<Tiquete>();
		this.nombre = nombre;
		this.precioTiquete = precioTiquete;
		this.tipoTiquete = tipoTiquete;
		this.descuento = 0;
		if (tipoTiquete.equals("BASICO")) {tiquetesBasicos(capacidad, evento);}
		if (tipoTiquete.equals("ENUMERADO")) {tiquetesEnumerados(capacidad, evento);}
		evento.añadirLocalidad(this);
	}
	
	public Localidad(String nombre, int capacidad, double precioTiquete, String tipoTiquete, Evento evento, double descuento) {
		this.tiquetes = new ArrayList<Tiquete>();
		this.nombre = nombre;
		this.precioTiquete = precioTiquete;
		this.tipoTiquete = tipoTiquete;
		this.descuento = descuento;
		if (tipoTiquete.equals("BASICO")) {tiquetesBasicos(capacidad, evento);}
		if (tipoTiquete.equals("ENUMERADO")) {tiquetesEnumerados(capacidad, evento);}
		evento.añadirLocalidad(this);
	}
	
	public Localidad(String nombre, int capacidad, double precioTiquete, String tipoTiquete, Evento evento, int capacidadTiquetesMultiples) {
		this.tiquetes = new ArrayList<Tiquete>();
		this.nombre = nombre;
		this.precioTiquete = precioTiquete;
		this.tipoTiquete = tipoTiquete;
		this.descuento = 0;
		if (tipoTiquete.equals("MULTIPLE")) {tiquetesMultiples(capacidad, evento, capacidadTiquetesMultiples);}
		evento.añadirLocalidad(this);
	}
	
	public Localidad(String nombre, int capacidad, double precioTiquete, String tipoTiquete, Evento evento, double descuento, int capacidadTiquetesMultiples) {
		this.tiquetes = new ArrayList<Tiquete>();
		this.nombre = nombre;
		this.precioTiquete = precioTiquete;
		this.tipoTiquete = tipoTiquete;
		this.descuento = descuento;
		if (tipoTiquete.equals("MULTIPLE")) {tiquetesMultiples(capacidad, evento, capacidadTiquetesMultiples);}
		evento.añadirLocalidad(this);
	}
	
	private void tiquetesBasicos(int capacidad, Evento evento) {
		for (int i = 0; i < capacidad; i++) {
			this.tiquetes.add(new TiqueteBasico(precioTiquete*(1-this.descuento), evento.getValorTipoDeEvento(), evento.getFecha(), evento.getHora()));
		}
	}
	
	private void tiquetesEnumerados(int capacidad, Evento evento) {
		for (int i = 1; i <= capacidad; i++) {
			this.tiquetes.add(new TiqueteEnumerado(precioTiquete*(1-this.descuento), evento.getValorTipoDeEvento(), evento.getFecha(), evento.getHora(), i));
		}
	}
	
	private void tiquetesMultiples(int capacidad, Evento evento, int capacidadTiquetesMultiples) {
		for (int i = 1; i <= capacidad; i++) {
			this.tiquetes.add(new TiqueteMultiEntrada(precioTiquete*(1-this.descuento), evento.getValorTipoDeEvento(), evento.getFecha(), evento.getHora(), capacidadTiquetesMultiples));
		}
	}

	public String getNombre() {
		return nombre;
	}

	public ArrayList<Tiquete> getTiquetes() {
		return tiquetes;
	}
	
	public Tiquete obtenerTiqueteDisponible() {
	    for (Tiquete t : this.tiquetes) {
	        if (!t.isComprado()) {
	            return t;
	        }
	    }
	    return null; 
	}
	
	public Tiquete obtenerTiqueteDisponible(int idSilla) {
	    for (Tiquete t : this.tiquetes) {
	        if (!t.isComprado() && ((TiqueteEnumerado) t).getIdSilla() == idSilla) {
	            return t;
	        }
	    }
	    return null; 
	}
	
	public int getCantidadCapacidad() {
		return this.tiquetes.size();
	}

	public String getTipoTiquete() {
		return tipoTiquete;
	}
	
	public int getCantidadTiquetesDisponibles() {
		int tiquetesDisponibles = 0;
		for (Tiquete t: this.tiquetes) {
			if (!t.comprado) {
				tiquetesDisponibles++;
			}
		}
		return tiquetesDisponibles;
	}
	
	@Override
	public String toString() {
	    return this.nombre;
	}

}
