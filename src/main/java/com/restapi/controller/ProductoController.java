package com.restapi.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.restapi.model.Categoria;
import com.restapi.model.Cliente;
import com.restapi.model.Producto;
import com.restapi.repository.CategoriaRepository;
import com.restapi.repository.ProductoRepository;
import com.entity.ErrorResponse;
import com.entity.SolicitudProducto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.sql.Date;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private CategoriaRepository categoryRepository;

    @PostMapping("/")
    public ResponseEntity<?> createProduct(@RequestBody SolicitudProducto solProducto) {
    	try {
    		int categoriaId = solProducto.getCategoriaId();
    		
            Optional<Categoria> categoryOpcional = categoryRepository.findById(categoriaId);
            if (categoryOpcional.isEmpty()) {
            	 throw new IllegalArgumentException("Error: no existe esa categoría.");
            }
            
            Categoria category = categoryOpcional.get();
            
            Producto nuevoProducto = new Producto();
            Date fecha = new Date(System.currentTimeMillis());
            
            nuevoProducto.setDescripcion(solProducto.getDescripcion());
            nuevoProducto.setDisponible(solProducto.getDisponible());
            nuevoProducto.setExistencia(solProducto.getExistencia());
            nuevoProducto.setultimoIngreso(fecha);
            nuevoProducto.setPrecio(solProducto.getPrecio());
            nuevoProducto.setCategoria(category);

            Producto producto = productoRepository.save(nuevoProducto);
            return ResponseEntity.status(HttpStatus.CREATED).body(producto);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setOk(false);
            errorResponse.setMsg(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/raw_list/")
    public ResponseEntity<List<Producto>> obtenerProductosEnCrudo() {
        try {
        	List<Producto> products = productoRepository.findAll();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/buscar_por_dato/{dato}")
    public ResponseEntity<Producto> obtenerCliente(@PathVariable String dato) {
    	try {
    		Optional<Producto> producto = productoRepository.findByIdORDescripcionLike(dato);
            return producto.map(ResponseEntity::ok)
                                  .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
           
        
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> obtenerProductos(
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
                        fieldName = "descripcion";
                        break;
                    case 4:
                        fieldName = "existencia";
                        break;
                    case 5:
                        fieldName = "ultimoIngreso";
                        break;
                    case 6:
                        fieldName = "precio";
                        break;
                    case 7:
                        fieldName = "categoria";
                        break;
                }
                sort = Sort.by(orderDirection.equals("asc") ? Direction.ASC : Direction.DESC, fieldName);
            }

            Specification<Producto> spec = null;
            if (search != null && !search.isEmpty()) {
                spec = (root, query, criteriaBuilder) -> criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("descripcion")), "%" + search.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.toString(root.get("existencia")), "%" + search.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.toString(root.get("ultimoIngreso")), "%" + search.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.toString(root.get("precio")), "%" + search.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.toString(root.get("categoria")), "%" + search.toLowerCase() + "%")
                );
            }

            Pageable pageable;
            if (sort != null) {
                pageable = PageRequest.of(start / length, length, sort);
            } else {
                pageable = PageRequest.of(start / length, length); 
            }

            Page<Producto> page = productoRepository.findAll(spec, pageable);

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
    public ResponseEntity<Producto> obtenerProducto(@PathVariable int id) {
        Optional<Producto> productOptional = productoRepository.findById(id);
        return productOptional.map(ResponseEntity::ok)
                              .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable int id, @RequestBody SolicitudProducto solProducto) {
    	try {
	        Optional<Producto> existingProductOptional = productoRepository.findById(id);
	        if (existingProductOptional.isEmpty()) {
	            return ResponseEntity.notFound().build();
	        }
	
	        Producto nuevoProducto = existingProductOptional.get();
            Date fecha = new Date(System.currentTimeMillis());
	        
	        nuevoProducto.setDescripcion(solProducto.getDescripcion());
            nuevoProducto.setDisponible(solProducto.getDisponible());
            nuevoProducto.setExistencia(solProducto.getExistencia());
            nuevoProducto.setultimoIngreso(fecha);
            nuevoProducto.setPrecio(solProducto.getPrecio());
	        
	        int categoriaId = solProducto.getCategoriaId();
			
	        Optional<Categoria> categoryOpcional = categoryRepository.findById(categoriaId);
	        if (categoryOpcional.isEmpty()) {
	        	throw new IllegalArgumentException("Error: no existe esa categoría.");
	        }
	        
	        Categoria category = categoryOpcional.get();
	        nuevoProducto.setCategoria(category);
        
            Producto updatedProduct = productoRepository.save(nuevoProducto);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setOk(false);
            errorResponse.setMsg(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public boolean deleteBuy(@PathVariable int id) {
        try {
        	productoRepository.deleteById(id);
        	return true;
        } catch (Exception e) {
        	e.printStackTrace();
            return false;
        }
    }
}
