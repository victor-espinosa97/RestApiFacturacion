package com.restapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.restapi.model.Cliente;
import com.restapi.repository.ClienteRepository;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @PostMapping("/")
    public ResponseEntity<Cliente> crearCliente(@RequestBody Cliente cliente) {
        try {
        	
        	Date fecha = new Date(System.currentTimeMillis());
        	
        	cliente.setFechaIngreso(fecha);
        	Cliente nuevoUsuario = clienteRepository.save(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> obtenerTodos(
        @RequestParam(value = "search[value]", required = false) String search,
        @RequestParam(value = "order[0][column]", required = false) Integer orderColumn,
        @RequestParam(value = "order[0][dir]", required = false) String orderDirection,
        @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
        @RequestParam(value = "length", required = false, defaultValue = "10") Integer length
    ) {
        try {
            Sort sort = null;
            if (orderColumn != null && orderDirection != null) { 
                String fieldName = ""; 

                switch (orderColumn) {
                    case 0:
                        fieldName = "id";
                        break;
                    case 1:
                        fieldName = "nombreCompleto";
                        break;
                    case 2:
                        fieldName = "correoElectronico";
                        break;
                    case 3:
                        fieldName = "fechaIngreso"; 
                        break;
                    case 4:
                        fieldName = "telefono"; 
                        break;
                    case 5:
                        fieldName = "capacidadCredito"; 
                        break;
                    case 6:
                        fieldName = "identificacion"; 
                        break;
                    case 7:
                        fieldName = "tipoIdentificacion"; 
                        break;
                }
                sort = Sort.by(orderDirection.equals("asc") ? Direction.ASC : Direction.DESC, fieldName);
            }

            Specification<Cliente> spec = null;
            if (search != null && !search.isEmpty()) {
                spec = (root, query, criteriaBuilder) -> criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("nombreCompleto")), "%" + search.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("correoElectronico")), "%" + search.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.toString(root.get("fechaIngreso")), "%" + search + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("direccion")), "%" + search.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.toString(root.get("identificacion")), "%" + search.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.toString(root.get("capacidadCredito")), "%" + search.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("telefono")), "%" + search.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("tipoIdentificacion")), "%" + search.toLowerCase() + "%")
                );
            }
            
            Pageable pageable;
            if (sort != null) {
                pageable = PageRequest.of(start / length, length, sort);
            } else {
                pageable = PageRequest.of(start / length, length); 
            }

            Page<Cliente> page = clienteRepository.findAll(spec, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("total", page.getTotalElements());
            response.put("data", page.getContent());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerCliente(@PathVariable int id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        return cliente.map(ResponseEntity::ok)
                              .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/buscar_por_dato/{dato}")
    public ResponseEntity<Cliente> obtenerCliente(@PathVariable String dato) {
    	try {
    		Optional<Cliente> cliente = clienteRepository.findByCorreoElectronicoOrNombreCompletoOrIdentificacionLike(dato);
            return cliente.map(ResponseEntity::ok)
                                  .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
           
        
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable int id, @RequestBody Cliente newData) {

    	try {
	        Optional<Cliente> clienteEncontrado = clienteRepository.findById(id);
	        if (clienteEncontrado.isEmpty()) {
	            return ResponseEntity.notFound().build();
	        }
	        
	        Cliente cliente = clienteEncontrado.get();
	
	        cliente.setCapacidadCredito(newData.getCapacidadCredito());
	        cliente.setCorreoElectronico(newData.getCorreoElectronico());
	        cliente.setDireccion(newData.getDireccion());
	        cliente.setIDentificacion(newData.getIdentificacion());
	        cliente.setNombreCompleto(newData.getNombreCompleto());
	        cliente.setTelefono(newData.getTelefono());
	        cliente.setTipoIdentificacion(newData.getTipoIdentificacion());
	        
        	Cliente updatedUser = clienteRepository.save(cliente);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public boolean deleteUser(@PathVariable int id) {
        try {
        	clienteRepository.deleteById(id);
            
        	return true;
        } catch (Exception e) {
        	e.printStackTrace();
            return false;
        }
    }
}
