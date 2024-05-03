package com.entity;

import java.util.List;

public class SolicitudFactura {

    private String descripcion;
    private String observacion;
    private String clienteId;
    private List<ProductoSolicitud> productos;

    public List<ProductoSolicitud> getProductos() {
		return productos;
	}

	public void setProductos(List<ProductoSolicitud> productos) {
		this.productos = productos;
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

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	
	public String getClienteId() {
		return clienteId;
	}

	public void setClienteId(String Id) {
		this.clienteId = Id;
	}
	
}
