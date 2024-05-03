package com.restapi.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.restapi.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer>, JpaSpecificationExecutor<Cliente>{
	
	@Query(value = "SELECT * FROM clientes c WHERE c.correo_electronico LIKE %:cadena% OR c.nombre_completo LIKE %:cadena% OR c.identificacion LIKE %:cadena% LIMIT 1", nativeQuery = true)
    Optional<Cliente> findByCorreoElectronicoOrNombreCompletoOrIdentificacionLike(@Param("cadena") String cadena);

}


