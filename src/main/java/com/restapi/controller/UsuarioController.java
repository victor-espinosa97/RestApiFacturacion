package com.restapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.entity.ErrorResponse;
import com.entity.SolicitudLogin;
import com.restapi.model.Usuario;
import com.restapi.repository.UsuarioRepository;
import com.utils.MD5;

import java.util.Optional;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/")
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody Usuario nuevoUsuario) {
        try {
        	
        	if (nuevoUsuario.getPassword() == null || nuevoUsuario.getEmail() == null) {
        		throw new IllegalArgumentException("Error: Campos invalidos.");
            }
        	String passencrypt = MD5.generateMD5(nuevoUsuario.getPassword());
        	nuevoUsuario.setPassword(passencrypt);
        	nuevoUsuario.setEstado(true);
        	
        	Usuario usuario = usuarioRepository.save(nuevoUsuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> authUser(@RequestBody SolicitudLogin request) {
    	try {
    		
	        if (request.getEmail()==null||request.getPassword()==null) {
	        	throw new IllegalArgumentException("Error: Campos invalidos.");
	        }
	        
        	String passencrypt = MD5.generateMD5(request.getPassword());
	        
        	Optional<Usuario> userOptional = usuarioRepository.findByEmailAndPassword(request.getEmail(), passencrypt);

            return userOptional.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
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
}
