package com.ProyectoDesarrolloWeb.spring.app.controllers;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ProyectoDesarrolloWeb.spring.app.DTO.Product;
import com.ProyectoDesarrolloWeb.spring.app.DTO.USD_GTQ;
import com.ProyectoDesarrolloWeb.spring.app.entities.Configuraciones;
import com.ProyectoDesarrolloWeb.spring.app.repositories.ConfiguracionesRepository;

@Controller
public class ProductController {
	
	@Autowired
	private ConfiguracionesRepository configuracionesRepository;
	

	@RequestMapping(value = "/producto")
	public String producto(@RequestParam(required = true) String url, Model model) {
		Long id = 1L;
		Product producto_api=ConsumoApis.consumo_producto(url);   
		USD_GTQ usd_gtq = ConsumoApis.consumo_cambio();
		Configuraciones configuraciones = configuracionesRepository.findById(id).get(); 
		
		BigDecimal aduana, ganancia, cambio;
		BigDecimal total = new BigDecimal (0);
		Double producto_apiPrice = usd_gtq.getInfo().getRate();
		BigDecimal cambioAux = new BigDecimal (producto_apiPrice);
		BigDecimal costo_envio_local = configuraciones.getCosto_Envio_local();
		BigDecimal costo_usa_gt = configuraciones.getCosto_USA_GT();
		BigDecimal porcentaje_ganancia = configuraciones.getPorcentaje_Ganancia();
		BigDecimal porcentaje_aduanal = configuraciones.getPorcentaje_adanual();
		
		cambio = producto_api.getPrice().multiply(cambioAux);
		aduana = cambio.multiply(porcentaje_aduanal);
		ganancia = cambio.multiply(porcentaje_ganancia);
		total = total.add(aduana);
		total = total.add(ganancia);
		total = total.add(costo_usa_gt);
		total = total.add(costo_envio_local);
		
		model.addAttribute("productos",producto_api);
		model.addAttribute("precio",total);
		return "producto";
	}	
}
