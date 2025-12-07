package logica;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class Venue implements Serializable{
	public HashMap<LocalDate,Evento> eventos;
	public ArrayList<String> restricciones;
	public int capacidad;
	public String nombre;
	public String ubicacion;
	public static HashMap<String, Venue> venues = new HashMap<String, Venue>();
	
	
	public Venue(int capacidad, String nombre, String ubicacion) {
		this.capacidad = capacidad;
		this.nombre = nombre;
		this.ubicacion = ubicacion;
		this.eventos = new HashMap<LocalDate, Evento>();
		this.restricciones = new ArrayList<String>();
		venues.put(nombre, this);
	}


	public HashMap<LocalDate, Evento> getEventos() {
		return eventos;
	}


	public void setEventos(HashMap<LocalDate,Evento> eventos) {
		this.eventos = eventos;
	}


	public int getCapacidad() {
		return capacidad;
	}


	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getUbicacion() {
		return ubicacion;
	}


	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}


	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("Venue{");
	    sb.append("nombre='").append(nombre).append('\'');
	    sb.append(", ubicacion='").append(ubicacion).append('\'');
	    sb.append(", capacidad=").append(capacidad);
	    sb.append('}');
	    return sb.toString();
	}
	
	public void asociarFecha(LocalDate fecha, Evento evento) {
		this.eventos.put(fecha, evento);
	}
	
	public void anadirRestricciones(String restriccion) {
		this.restricciones.add(restriccion);
	}


	public static void setVenues(HashMap<String, Venue> venues) {
		Venue.venues = venues;
	}
	
}
