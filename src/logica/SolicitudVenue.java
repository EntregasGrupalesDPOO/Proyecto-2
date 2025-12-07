package logica;

import java.io.Serializable;

public class SolicitudVenue extends Solicitud implements IAprobable,Serializable {
	private int capacidad;
	private String nombre;
	private String ubicacion;
	
    private BoletasMaster sistema;
    public SolicitudVenue(Cliente solicitante, String descripcion, BoletasMaster sistema, int capacidad, String nombre, String ubicacion) {
        super(solicitante, descripcion);
        this.sistema = sistema;
        this.tipo = "PropuestaVenue";
        this.capacidad = capacidad;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        
    }

    @Override
    public void aceptarSolicitud() {
        Venue venue = new Venue(this.capacidad, this.nombre, this.ubicacion);
        this.estado = Solicitud.ESTADO_ACEPTADA;
        System.out.println("Solicitud de venue aceptada. El venue " + venue.getNombre() + " ha sido aprobado.");
        
    }


    public void rechazarSolicitud() {
        this.estado = Solicitud.ESTADO_RECHAZADA;
        System.out.println("Solicitud de venue rechazada para el usuario " + this.solicitante.getLogin());
        
    }

}
