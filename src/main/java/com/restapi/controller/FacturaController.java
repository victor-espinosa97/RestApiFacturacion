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
import com.restapi.model.Cliente;
import com.restapi.model.Detalle;
import com.restapi.repository.FacturaRepository;
import com.restapi.repository.ProductoRepository;
import com.restapi.repository.ClienteRepository;
import com.restapi.repository.DetalleRepository;
import com.entity.ErrorResponse;
import com.entity.ProductoSolicitud;
import com.entity.SolicitudFactura;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

	@Autowired
    private FacturaRepository facturaRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private DetalleRepository detalleRepository;

    @PostMapping("/")
    public ResponseEntity<?> crearFactura(@RequestBody SolicitudFactura solicitud) {
    	try {
    		String clienteId = solicitud.getClienteId();
            Optional<Cliente> clienteOpcional = clienteRepository.findById(Integer.parseInt(clienteId));

            if (clienteOpcional.isEmpty()) {
            	 throw new IllegalArgumentException("Error: no existe este cliente.");
            }
            
            Cliente cliente = clienteOpcional.get();
  
            Factura nuevaFactura = new Factura();
            Date fecha = new Date(System.currentTimeMillis());
        	
            nuevaFactura.setFechaVenta(fecha);
            nuevaFactura.setDescripcion(solicitud.getDescripcion());
            nuevaFactura.setObservacion(solicitud.getObservacion());
            nuevaFactura.setCliente(cliente);

            Factura factura = facturaRepository.save(nuevaFactura);            
         
            double subtotalFactura = 0.0;
            
            for (ProductoSolicitud productoSolicitud : solicitud.getProductos()) {

                Detalle detalle = new Detalle();
               
                int cantidad = Integer.parseInt(productoSolicitud.getCantidad());
                detalle.setCantidad(cantidad);

                Producto producto = productoRepository.findById(Integer.parseInt(productoSolicitud.getId())).orElse(null);
                if (producto != null) {
                	double precio = producto.getPrecio();
                    double subtotalDetalle = precio * cantidad;
                    subtotalFactura += subtotalDetalle;
                    
                    int existenciaActual = producto.getExistencia();
                    int nuevaExistencia = existenciaActual - cantidad;
                    producto.setExistencia(nuevaExistencia);

                    productoRepository.save(producto);

                    detalle.setProducto(producto);
                    detalle.setFactura(factura);
                    detalleRepository.save(detalle);
                } else {
                    System.out.println("Producto no encontrado para el ID: " + productoSolicitud.getId());
                }
            }

            factura.setSubTotal(subtotalFactura);
            facturaRepository.save(factura);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(factura);
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
    
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> obtenerTodas(
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
                        fieldName = "nroFactura";
                        break;
                    case 1:
                        fieldName = "fechaVenta";
                        break;
                    case 3:
                        fieldName = "descripcion";
                        break; 
                }
                sort = Sort.by(orderDirection.equals("asc") ? Direction.ASC : Direction.DESC, fieldName);
            }

            Specification<Factura> spec = null;
            if (search != null && !search.isEmpty()) {
                spec = (root, query, criteriaBuilder) -> criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.toString(root.get("nroFactura")), "%" + search.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.toString(root.get("fechaVenta")), "%" + search.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("descripcion")), "%" + search.toLowerCase() + "%")
                );
            }

            Pageable pageable;
            if (sort != null) {
                pageable = PageRequest.of(start / length, length, sort);
            } else {
                pageable = PageRequest.of(start / length, length); 
            }

            Page<Factura> page = facturaRepository.findAll(spec, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("total", page.getTotalElements());
            response.put("data", page.getContent());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/cliente/")
    public ResponseEntity<Map<String, Object>> obtenerFacturasCliente(
        @RequestParam(value = "search[value]", required = false) String search,
        @RequestParam(value = "order[0][column]", required = false) Integer orderColumn,
        @RequestParam(value = "order[0][dir]", required = false) String orderDirection,
        @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
        @RequestParam(value = "length", required = false, defaultValue = "10") Integer length,
        @RequestParam(value = "cliente", required = false) Integer cliente
    ) {
        try { 
            Sort sort = null;
            
            if (orderColumn != null && orderDirection != null) { 
                String fieldName = ""; 

                switch (orderColumn) {
                    case 0:
                        fieldName = "nroFactura";
                        break;
                    case 1:
                        fieldName = "fechaVenta";
                        break;
                    case 2:
                        fieldName = "descripcion";
                        break; 
                    case 3:
                        fieldName = "subtotal";
                        break; 
                }
                sort = Sort.by(orderDirection.equals("asc") ? Direction.ASC : Direction.DESC, fieldName);
            }

            Specification<Factura> spec = null;
            if (cliente != null) { 
                spec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("cliente").get("id"), cliente);
            }
            
            if (search != null && !search.isEmpty()) {
                spec = (root, query, criteriaBuilder) -> criteriaBuilder.or(
                		criteriaBuilder.like(criteriaBuilder.toString(root.get("nroFactura")), "%" + search.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.toString(root.get("fechaVenta")), "%" + search.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("descripcion")), "%" + search.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("subtotal")), "%" + search.toLowerCase() + "%")
                );
            }

            Pageable pageable;
            if (sort != null) {
                pageable = PageRequest.of(start / length, length, sort);
            } else {
                pageable = PageRequest.of(start / length, length); 
            }

            Page<Factura> page = facturaRepository.findAll(spec, pageable);

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
    public ResponseEntity<Factura> getBuyById(@PathVariable int id) {
        Optional<Factura> buyOptional = facturaRepository.findById(id);
        return buyOptional.map(ResponseEntity::ok)
                              .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/reporte_general/")
    public ResponseEntity<List<Factura>> getAllSellsByRepor() {
        try {
            List<Factura> results = facturaRepository.findAll();
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/reporte_cliente/{id}")
    public ResponseEntity<List<Factura>> getAllPurchaseByRepor(@PathVariable int id) {
        try {
        	List<Factura> results = facturaRepository.findByCiente(id); 
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
