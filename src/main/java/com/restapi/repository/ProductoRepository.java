package com.restapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.restapi.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer>, JpaSpecificationExecutor<Producto> {
	
	@Query(value = "SELECT * FROM productos p WHERE p.descripcion LIKE %:cadena% OR p.id LIKE %:cadena% LIMIT 1", nativeQuery = true)
    Optional<Producto> findByIdORDescripcionLike(@Param("cadena") String cadena);

}

