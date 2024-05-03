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

import com.restapi.model.Factura;
import com.restapi.model.Producto;
import com.restapi.repository.FacturaRepository;
import com.restapi.repository.ProductoRepository;
import com.restapi.repository.DetalleRepository;
import com.entity.ErrorResponse;
import com.entity.SolicitudDetalle;
import com.restapi.model.Detalle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/datelles")
public class DetalleController {

	@Autowired
    private DetalleRepository detalleRepository;
	@Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private FacturaRepository facturaRepository;

    @PostMapping("/")
    public ResponseEntity<?> createBuy(@RequestBody SolicitudDetalle reqPaid) {
    	try {
    		int productoId = reqPaid.getProductoId();
    		int nroFactura = reqPaid.getNroFactura();
    		
            Optional<Producto> productoOpcional = productoRepository.findById(productoId);
            Optional<Factura> facturaOpcional = facturaRepository.findById(nroFactura);
            
            if (productoOpcional.isEmpty()) {
            	 throw new IllegalArgumentException("Error: no existe este producto.");
            }
            
            if (facturaOpcional.isEmpty()) {
            	 throw new IllegalArgumentException("Error: no existe esta factura.");
            }
            
            Producto producto = productoOpcional.get();
            Factura factura = facturaOpcional.get();
  
            Detalle nuevoDetalle = new Detalle();
            nuevoDetalle.setCantidad(reqPaid.getCantidad());
            nuevoDetalle.setProducto(producto);
            nuevoDetalle.setFactura(factura);

            Detalle detalle = detalleRepository.save(nuevoDetalle);
            return ResponseEntity.status(HttpStatus.CREATED).body(detalle);
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

    @GetMapping("/purchase/")
    public ResponseEntity<Map<String, Object>> getAllBuys(
        @RequestParam(value = "search[value]", required = false) String search,
        @RequestParam(value = "order[0][column]", required = false) Integer orderColumn,
        @RequestParam(value = "order[0][dir]", required = false) String orderDirection,
        @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
        @RequestParam(value = "length", required = false, defaultValue = "10") Integer length,
        @RequestParam(value = "userId", required = false) Integer userId
    ) {
        try {
            Sort sort = null;
            if (orderColumn != null && orderDirection != null) { 
                String fieldName = ""; 

                switch (orderColumn) {
                    case 0:
                        fieldName = "purchaseDate";
                        break;
                    case 5:
                        fieldName = "units";
                        break;
                    case 6:
                        fieldName = "value"; 
                        break;
                }
                sort = Sort.by(orderDirection.equals("asc") ? Direction.ASC : Direction.DESC, fieldName);
            }

            Specification<Factura> spec = null;
            if (userId != null) { 
                spec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), userId);
            }
            
            if (search != null && !search.isEmpty()) {
                spec = (root, query, criteriaBuilder) -> criteriaBuilder.or(
            		criteriaBuilder.like(criteriaBuilder.lower(root.get("purchaseDate")), "%" + search.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("units")), "%" + search.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("value")), "%" + search.toLowerCase() + "%")
                );
            }

            Pageable pageable;
            if (sort != null) {
                pageable = PageRequest.of(start / length, length, sort);
            } else {
                pageable = PageRequest.of(start / length, length); 
            }

            Page<Factura> page = detalleRepository.findAll(spec, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("total", page.getTotalElements());
            response.put("data", page.getContent());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/sells/")
    public ResponseEntity<Map<String, Object>> getAllSells(
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
                        fieldName = "purchaseDate";
                        break;
                    case 5:
                        fieldName = "units";
                        break;
                    case 6:
                        fieldName = "value";
                        break; 
                }
                sort = Sort.by(orderDirection.equals("asc") ? Direction.ASC : Direction.DESC, fieldName);
            }

            Specification<Factura> spec = null;
            if (search != null && !search.isEmpty()) {
                spec = (root, query, criteriaBuilder) -> criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("purchaseDate")), "%" + search.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("units")), "%" + search.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("value")), "%" + search.toLowerCase() + "%")
                );
            }

            Pageable pageable;
            if (sort != null) {
                pageable = PageRequest.of(start / length, length, sort);
            } else {
                pageable = PageRequest.of(start / length, length); 
            }

            Page<Factura> page = detalleRepository.findAll(spec, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("total", page.getTotalElements());
            response.put("data", page.getContent());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
//    @GetMapping("/report_purchase/{id}")
//    public ResponseEntity<List<Factura>> getAllPurchaseByRepor(@PathVariable int id) {
//        try {
//        	List<Factura> results = detalleRepository.findByFacturaId(id); 
//            return ResponseEntity.ok(results);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
}
