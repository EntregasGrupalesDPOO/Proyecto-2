package logica;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import Exepciones.CantidadTiquetesExcedidaException;
import Exepciones.CapacidadLocalidadExcedidaException;
import Exepciones.PasswordIncorrectoException;
import Exepciones.SaldoInsuficienteException;
import Exepciones.TiqueteNoEncontradoException;
import Exepciones.TiqueteNoTransferibleException;
import Exepciones.TiqueteVencidoFecha;
import Exepciones.UsuarioNoEncontradoException;
import Marketplace.Oferta;

public class Cliente implements Serializable{
	protected String login;
	protected String contrasena;
	protected double saldoVirtual;
	protected HashMap<Integer, Tiquete> tiquetes;


	protected String tipoCliente;
	public static HashMap<String, Cliente> clientes = new HashMap<String, Cliente>();
	protected ArrayList<String> beneficios;
	
	
	public Cliente(String login, String contrasena) {
		this.login = login;
		this.contrasena = contrasena;
		this.tipoCliente = "Normal";
		this.tiquetes = new HashMap<Integer, Tiquete>();
		clientes.put(login, this);
	}

	public boolean login(String login, String contrasena) {
		return this.login.equals(login) && this.contrasena.equals(contrasena);
	}
	
	
	
	// funciona si la localidad vende tiquetes multiples
	public ArrayList<Tiquete> comprarTiquete(int cantidad, Evento evento, String localidad, boolean comprarConSaldo) throws Exception {
		ArrayList<Tiquete> log = new ArrayList<Tiquete>();
		Localidad l = evento.getLocalidadPorNombre(localidad);
		if (l.getTipoTiquete().equals("MULTIPLE")) {
			if(cantidad > TiqueteMultiple.getTiquetesMaximosPorTransaccion()) {
				throw new CantidadTiquetesExcedidaException(TiqueteMultiple.getTiquetesMaximosPorTransaccion());
			}
		}
		if (cantidad > Tiquete.getTiquetesMaximosPorTransaccion()) {
			throw new CantidadTiquetesExcedidaException(Tiquete.getTiquetesMaximosPorTransaccion());
		}
		if (cantidad > l.getCantidadTiquetesDisponibles() ) {
			throw new CapacidadLocalidadExcedidaException(cantidad);
		}
		Tiquete ti = l.obtenerTiqueteDisponible();
		if (comprarConSaldo) {
			if (ti.getPrecioReal()*cantidad > this.saldoVirtual) {
				throw new SaldoInsuficienteException(this);
			}
			this.saldoVirtual = this.saldoVirtual - ti.getPrecioReal()*cantidad;
		}
		for (int i = 0; i < cantidad; i++) {
			Tiquete t = l.obtenerTiqueteDisponible();
			if (!(t == null)) {
				if (t instanceof TiqueteMultiple){
					t.setComprado(true);
				    t.setCliente(this);
					for (Tiquete tiq : ((TiqueteMultiple) t).getTiquetes()) {
						log.add(tiq);
						tiquetes.put(tiq.getId(), tiq);
						tiq.setComprado(true);
						tiq.setCliente(this);
					}
				}else {
					log.add(t);
					tiquetes.put(t.getId(), t);
					t.setComprado(true);
					t.setCliente(this);
				}
			}
		}



		return log;
	}
	
	public ArrayList<Tiquete> comprarTiquete(int cantidad, Evento evento, String localidad, ArrayList<Integer> idSillas, boolean comprarConSaldo) throws Exception {
		ArrayList<Tiquete> log = new ArrayList<Tiquete>();
		Localidad l = evento.getLocalidadPorNombre(localidad);
		if(cantidad > Tiquete.getTiquetesMaximosPorTransaccion()) {
			throw new CantidadTiquetesExcedidaException(Tiquete.getTiquetesMaximosPorTransaccion());
		}
		if (cantidad > l.getCantidadTiquetesDisponibles()) {
			throw new CapacidadLocalidadExcedidaException(cantidad);
		}
		Tiquete ti = l.obtenerTiqueteDisponible(0);
		log.add(ti);
		if (comprarConSaldo) {
			if (ti.getPrecioReal() * cantidad > this.saldoVirtual) {
				throw new SaldoInsuficienteException(this);
			}
			this.saldoVirtual = this.saldoVirtual - ti.getPrecioReal() * cantidad;
		}
		for (int i:idSillas) {
			
			Tiquete t = l.obtenerTiqueteDisponible(i);
			if(!(t == null)) {
				log.add(t);
				tiquetes.put(t.getId(), t);
				t.setComprado(true);
				t.setCliente(this);
				
			}
		}
		return log;
	}
	
	public TiqueteMultiEvento comprarTiqueteMultiEvento(HashMap<Evento, String> eventos, boolean comprarConSaldo) throws Exception {
		TiqueteMultiEvento t = new TiqueteMultiEvento(eventos, this);
		if (eventos.size() > TiqueteMultiple.getTiquetesMaximosPorTransaccion()) {
			throw new CantidadTiquetesExcedidaException(TiqueteMultiple.getTiquetesMaximosPorTransaccion());
		}
		if (comprarConSaldo) {
			if (t.getPrecioReal() > this.saldoVirtual) {
				throw new SaldoInsuficienteException(this);
			}
			this.saldoVirtual = this.saldoVirtual - t.getPrecioReal();
		}
		t.setComprado(true);
		t.setCliente(this); 
		tiquetes.put(t.getId(), t);
		
		return t;
	}

	public String getTipoCliente() {
		return tipoCliente;
	}
	
	public PaqueteDeluxe comprarPaqueteDeluxe(Evento evento, String localidad, boolean comprarConSaldo) throws Exception {
		PaqueteDeluxe pd = new PaqueteDeluxe(evento, localidad);
		if (comprarConSaldo) {
			if (pd.getTiquetePrincipal().getPrecioReal() > this.saldoVirtual) {
				throw new SaldoInsuficienteException(this);
			}
		this.saldoVirtual = this.saldoVirtual - pd.getTiquetePrincipal().getPrecioReal();
		}
		tiquetes.put(pd.getTiquetePrincipal().getId(), pd.getTiquetePrincipal());
		pd.getTiquetePrincipal().setComprado(true);
		pd.getTiquetePrincipal().setCliente(this);
		this.beneficios.addAll(pd.getBeneficios());
		for (Tiquete t: pd.getCortesias()) {
			this.tiquetes.put(t.getId(),t);
		}
		return pd;
		
	}
	
	public void transferirTiquete(Tiquete tiquete, String login, String contrasena ) throws Exception {
		if (!contrasena.equals(this.contrasena)) {
			throw new PasswordIncorrectoException(this);
		}
		
		if (!tiquete.isTransferible()) {
			throw new TiqueteNoTransferibleException(tiquete);
		}
		
		if (tiquete.getFecha().isBefore(LocalDate.now())){
			throw new TiqueteVencidoFecha(tiquete);
		}
		if (clientes.get(login) == (null)) {
			throw new UsuarioNoEncontradoException(login);
		}
		eliminarTiquete(tiquete);
		clientes.get(login).agregarTiquete(tiquete);
		
		tiquete.setCliente(clientes.get(login));
	}
	
	public void transferirTiquete(TiqueteMultiple tiqueteMultiple, Tiquete tiquete, String login, String contrasena ) throws Exception {
		Tiquete t = tiqueteMultiple.getTiquete(tiquete);
		if (t.equals(null)) {
			throw new TiqueteNoEncontradoException(tiquete.getId());
		}

		transferirTiquete(tiquete, login, contrasena);
		tiqueteMultiple.setTransferible(false);
	}
	
	public void agregarTiquete(Tiquete tiquete) {
		this.tiquetes.put(tiquete.getId(), tiquete);
		tiquete.setCliente(this);
	}
	
	public void eliminarTiquete(Tiquete tiquete) {
		
		this.tiquetes.remove(tiquete.getId());
	}
	
	public void actualizarSaldoVirtual(double valor) {
		this.saldoVirtual += valor;
	}

	public void acceptarOferta(Oferta oferta, boolean usarSaldo) throws Exception {
		if (usarSaldo == true && this.saldoVirtual < oferta.getPrecio()){
			throw new SaldoInsuficienteException(this);
		} else if (usarSaldo == true) {
			this.setSaldoVirtual(this.getSaldoVirtual()-oferta.getPrecio());
		}
	    oferta.setVendida(true);
	    //transferirTiquete
	    Cliente vendedorOferta= oferta.getVendedor();
	    vendedorOferta.setSaldoVirtual(vendedorOferta.getSaldoVirtual()+oferta.getPrecio());
	    vendedorOferta.transferirTiquete(oferta.getTiquete(),this.login, oferta.getVendedor().getContrasena() );
	    
	}
	
	public String getLogin() {
		return login;
	}
	
	
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	
	
	public String getContrasena() {
		return contrasena;
	}
	
	
	
	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}
	
	
	
	public double getSaldoVirtual() {
		return this.saldoVirtual;
	}
	
	public void setSaldoVirtual(double saldo) {
		this.saldoVirtual=saldo;
	}
	public HashMap<Integer, Tiquete> getTiquetes() {
		return tiquetes;
	}
	
	public void setTiquetes(HashMap<Integer, Tiquete> tiquetes) {
		this.tiquetes = tiquetes;
	}
	
	public static void setClientes(HashMap<String, Cliente> clientes) {
		Cliente.clientes = clientes;
	}
	
	public TiqueteMultiple buscarTiqueteMultiple(Tiquete t) {
		for (Tiquete ti : this.tiquetes.values()) {
			if (ti instanceof TiqueteMultiple) {
				for (Tiquete tiq: ((TiqueteMultiple) ti).getTiquetes()) {
					if (t.getId() == tiq.getId()) {
						return (TiqueteMultiple) ti;
					}
				}
			}
		}
		return null;
	}
}

