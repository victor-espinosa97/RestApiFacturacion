package com.restapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.restapi.model.Factura;

public interface FacturaRepository extends JpaRepository<Factura, Integer>, JpaSpecificationExecutor<Factura> {
	
	@Query(value = "SELECT * FROM facturas f WHERE f.cliente_id =:clienteId ", nativeQuery = true)
	List<Factura> findByCiente(int clienteId);
}

