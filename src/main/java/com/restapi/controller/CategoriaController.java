package com.restapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.restapi.model.Categoria;
import com.restapi.repository.CategoriaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoryRepository;

    @PostMapping("/")
    public ResponseEntity<Categoria> crearCategoria(@RequestBody Categoria catergoria) {
        try {
        	Categoria nuevaCatengoria = categoryRepository.save(catergoria);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCatengoria);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<Categoria>> obtenerTodas() {
        try {
            List<Categoria> categorias = categoryRepository.findAll();
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtenerCategoria(@PathVariable int id) {
        Optional<Categoria> categoria = categoryRepository.findById(id);
        return categoria.map(ResponseEntity::ok)
                              .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizarCategoria(@PathVariable int id, @RequestBody Categoria newData) {
    	try {
	        Optional<Categoria> categoria = categoryRepository.findById(id);
	        if (categoria.isEmpty()) {
	            return ResponseEntity.notFound().build();
	        }
	
	        Categoria confirmaCategoria = categoria.get();
	        
	        confirmaCategoria.setDescripcion(newData.getDescripcion());
	        confirmaCategoria.setUbicacion(newData.getUbicacion());
	        
        	Categoria updatedCategory = categoryRepository.save(confirmaCategoria);
            return ResponseEntity.ok(updatedCategory);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public boolean eliminarCategoria(@PathVariable int id) {
        try {
            categoryRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
