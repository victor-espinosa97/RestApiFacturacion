package com.restapi.model;

import jakarta.persistence.Entity;

import java.sql.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "capacidad_credito")
    private int capacidadCredito;

    @Column(name = "correo_electronico")
    private String correoElectronico;
    
    private String direccion;
    
    @Column(name = "fecha_ingreso")
    private Date fechaIngreso;
    
    private int identificacion;
    
    @Column(name = "nombre_completo")
    private String nombreCompleto;
    
    private String telefono;
    
    @Column(name = "tipo_identificacion")
    private String tipoIdentificacion;
    

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCapacidadCredito() {
		return capacidadCredito;
	}

	public void setCapacidadCredito(int credito) {
		this.capacidadCredito = credito;
	}
	
	public String getCorreoElectronico() {
		return correoElectronico;
	}

	public void setCorreoElectronico(String correo) {
		this.correoElectronico = correo;
	}
	
	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	
	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}
	
	public int getIdentificacion() {
		return identificacion;
	}

	public void setIDentificacion(int identificacion) {
		this.identificacion = identificacion;
	}
	
	public String getNombreCompleto() {
		return nombreCompleto;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	
	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	
	public String getTipoIdentificacion() {
		return tipoIdentificacion;
	}

	public void setTipoIdentificacion(String tipoIdentificacion) {
		this.tipoIdentificacion = tipoIdentificacion;
	}

}

