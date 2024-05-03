package com.restapi.model;

import java.sql.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "facturas")
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nro_factura")
    private int nroFactura;
    
    private String descripcion;
    
    @Column(name = "fecha_venta")
    private Date fechaVenta;
    
    private String observacion;
    
    private double subTotal;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    public int getNroFactura() {
		return nroFactura;
	}

	public void setNroFactura(int nroFactura) {
		this.nroFactura = nroFactura;
	}

	public Date getFechaVenta() {
		return fechaVenta;
	}

	public void setFechaVenta(Date date) {
		this.fechaVenta = date;
	}
	
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public String getObservacion() {
		return observacion;
	}
	
	public void setSubTotal(double subTotal) {
		this.subTotal = subTotal;
	}
	
	public double getSubTotal() {
		return subTotal;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
}
