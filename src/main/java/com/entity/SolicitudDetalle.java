package com.entity;


public class SolicitudDetalle {
    private int cantidad;
    private int nroFactura;
    private int productoId;
	
	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	
	public int getNroFactura() {
		return nroFactura;
	}

	public void setNroFactura(int Id) {
		this.nroFactura = Id;
	}
	
	public int getProductoId() {
		return productoId;
	}

	public void setProductId(int Id) {
		this.productoId = Id;
	}
	
}
