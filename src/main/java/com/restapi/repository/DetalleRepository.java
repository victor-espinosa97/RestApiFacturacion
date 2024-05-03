package com.restapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.restapi.model.Detalle;
import com.restapi.model.Factura;

public interface DetalleRepository extends JpaRepository<Detalle, Integer>, JpaSpecificationExecutor<Factura> {
	
	//List<Factura> findByFacturaId(int facturaId);
}

