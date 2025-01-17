package com.lmu.gasbygas.service;


public interface MessagingService {

    boolean sendOtpToSMS(String contact, String otp) throws Exception;
    boolean sendOtpToEmail(String email, String otp) throws Exception;
    
}
