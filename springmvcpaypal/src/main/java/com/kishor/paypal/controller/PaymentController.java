package com.kishor.paypal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kishor.paypal.entity.Order;
import com.kishor.paypal.service.PaypalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

@Controller
//@RequestMapping("/paypal")
public class PaymentController {
	
	@Autowired
	PaypalService paypalService;
	
	public static final String SUCCESS_URL = "pay/success";
	public static final String CANCEL_URL = "pay/cancel";
	
	@RequestMapping("/home")
	public String home() {
		return "home";
	}
	
//	@PostMapping("/pay")
//	public String payment(@ModelAttribute("order") Order order)
//	{
//		try {
//			Payment payment=paypalService.createPayment(order.getPrice(), order.getCurrency(), order.getMethod(),order.getIntent(), order.getDescription(), 
//					"http://localhost:8080/"+CANCEL_URL,"http://localhost:8080/"+SUCCESS_URL);
//			
//			for(Links link:payment.getLinks()) {
//				if(link.getRel().equals("approval_url")) {
//					return "redirect:"+link.getHref();
//				}
//			}
//		} catch (PayPalRESTException e) {
//			
//			e.printStackTrace();
//		}
//		return "redirect:/";
//		 
//	}
	
	@RequestMapping("/pay")
	public String payment(@ModelAttribute("order") Order order) {
		try {
			Payment payment = paypalService.createPayment(order.getPrice(), order.getCurrency(), order.getMethod(),
					order.getIntent(), order.getDescription(), "http://localhost:8080/" + CANCEL_URL,
					"http://localhost:8080/" + SUCCESS_URL);
			
			for(Links link:payment.getLinks()) {
				System.out.println("link are:"+link);
				if(link.getRel().equals("approval_url")) {
					return "redirect:"+link.getHref();
				}
			}
			
		} catch (PayPalRESTException e) {
		
			e.printStackTrace();
		}
		return "redirect:/";
	}
	

	 @RequestMapping(value = CANCEL_URL)
	    public String cancelPay() {
	        return "cancel";
	    }

	 @RequestMapping(value = SUCCESS_URL)
	    public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
	        try {
	            Payment payment = paypalService.executePayment(paymentId, payerId);
	            System.out.println(payment.toJSON());
	           
	            if (payment.getState().equals("approved")) {
	                return "success";
	            }
	        
	        } catch (PayPalRESTException e) {
	         System.out.println(e.getMessage());
	        }
	        return "redirect:/";
	    }

}
