package logica;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Evento implements Serializable{
	private Venue venue;
	private Organizador organizador;
	private ArrayList<Localidad> localidades;
	public static HashMap<String, Double> tiposDeEventos = new HashMap<String,Double>();
	private String nombre;
	private String descripcion;
	private String tipoDeEvento;
	private LocalDate fecha;
	private LocalTime hora;
	private String estado;

	public static final String CULTURAL   = "CULTURAL";
	public static final String DEPORTIVO  = "DEPORTIVO";
	public static final String MUSICAL    = "MUSICAL";
	public static final String RELIGIOSO  = "RELIGIOSO";
	
	public Evento(String nombre,String descripcion,Venue venue, Organizador organizador, String tipoDeEvento, LocalDate fecha, LocalTime hora) {
		this.venue = venue;
		this.organizador = organizador;
		this.tipoDeEvento = tipoDeEvento;
		this.fecha = fecha;
		this.hora = hora;
		this.localidades = new ArrayList<Localidad>();
		this.estado = "AGENDADO";
		this.nombre=nombre;
		this.descripcion=descripcion;
		
		if (Evento.tiposDeEventos.isEmpty()) {
			Evento.tiposDeEventos = new HashMap<>();

			Evento.tiposDeEventos.put("CULTURAL", 0.2);
			Evento.tiposDeEventos.put("DEPORTIVO", 0.15);
			Evento.tiposDeEventos.put("MUSICAL", 0.3);
			Evento.tiposDeEventos.put("RELIGIOSO", 0.4);

	    }
	}

	public void añadirLocalidad(Localidad localidad) {
		this.localidades.add(localidad);
	}
	
	public double getValorTipoDeEvento() {
		return tiposDeEventos.get(tipoDeEvento);
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public LocalTime getHora() {
		return hora;
	}

	public ArrayList<Localidad> getLocalidades() {
		return localidades;
	}
	
	public Localidad getLocalidadPorNombre(String nombre) {
	    for (Localidad l : this.localidades) {
	        if (l.getNombre().equalsIgnoreCase(nombre)) {
	            return l;
	        }
	    }
	    return null;
	}
	
	public static void addTipoEvento(String nombre, double valor) {
		tiposDeEventos.put(nombre, valor);
	}

	public String getEstado() {
		return estado;
	}

	public Venue getVenue() {
		return venue;
	}

	public Organizador getOrganizador() {
		return organizador;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	public int capacidadActual() {
		int capacidad = 0;
		for (Localidad l:this.localidades) {
			capacidad += l.getCantidadCapacidad();
		}
		return capacidad;
	}
	public String getNombre (){
		return this.nombre;
	}
	public String getTipoDeEvento() {
		return this.tipoDeEvento;
	}
	
	@Override
	public String toString() {
	    return this.nombre;  
	}
}
