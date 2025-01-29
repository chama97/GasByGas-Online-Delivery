package com.lmu.gasbygas.service.impl;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmu.gasbygas.dto.request.ClientRegisterReqDTO;
import com.lmu.gasbygas.dto.request.ClientUpdateReqDTO;
import com.lmu.gasbygas.dto.response.ClientSearchResDTO;
import com.lmu.gasbygas.entity.ClientEntity;
import com.lmu.gasbygas.entity.TempClientEntity;
import com.lmu.gasbygas.entity.UserEntity;
import com.lmu.gasbygas.repository.ClientRepo;
import com.lmu.gasbygas.repository.TempClientRepo;
import com.lmu.gasbygas.repository.UserRepo;
import com.lmu.gasbygas.service.ClientService;
import com.lmu.gasbygas.util.ResponseUtil;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepo clientRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TempClientRepo tempClientRepo;

    @Autowired
    private PasswordEncoder pwdEncoder;

    @Autowired
    private MessangingServiceImpl messangingService;

    @Override
    public ResponseUtil registerNewClient(ClientRegisterReqDTO dto) throws Exception {
        String encryptedPassword = pwdEncoder.encode(dto.getPassword());

        ClientEntity clientExist = clientRepo.findClientByEmailOrContactOrNicOrRegNumber(dto.getEmail(),
                dto.getContact(),
                dto.getNic(),
                dto.getRegistrationNumber());
        if (clientExist != null) {
            return new ResponseUtil(200, "This client is already registered. Please log in", null);
        }

        String emailOtp = generateEmailOtp();
        String contactOtp = generateContactOtp();

        boolean emailSent = messangingService.sendOtpToEmail(dto.getEmail(), emailOtp);
        boolean smsSent = messangingService.sendOtpToSMS(dto.getContact(), contactOtp);
        if (!emailSent && !smsSent) {
            return new ResponseUtil(500, "OTP send Failed to email and contact. Try again.", null);
        } else if (!emailSent) {
            return new ResponseUtil(500, "OTP send Failed to email. Try again.", null);
        } else if (!smsSent) {
            return new ResponseUtil(500, "OTP send Failed to contact number. Try again.", null);
        }
        
        String clientDataJson = new ObjectMapper().writeValueAsString(dto);
        TempClientEntity tempData = setTempClientEntity(dto, encryptedPassword, emailOtp, contactOtp, clientDataJson,
                new TempClientEntity());
        TempClientEntity tempClientSave = tempClientRepo.save(tempData);
        if (tempClientSave != null) {
            return new ResponseUtil(200,
                    "Please verify your email and contct to complete registration.", null);
        } else {
            throw new RuntimeException("Client registration failed");
        }
    }

    private String generateEmailOtp() {
        return String.valueOf(new Random().nextInt(899999) + 100000);
    }

    private String generateContactOtp() {
        return String.valueOf(new Random().nextInt(899999) + 100000);
    }

    @Override
    public ResponseUtil verifiOTP(String email, String emailOtp, String contactOtp) throws Exception {

        TempClientEntity tempData = tempClientRepo.findByEmail(email);
        if (tempData == null) {
            return new ResponseUtil(404, "Invalid email", null);
        }
        if (!tempData.getEmailOtp().equals(emailOtp)) {
            return new ResponseUtil(400, "Invalid email OTP", null);
        }
        if (!tempData.getContactOtp().equals(contactOtp)) {
            return new ResponseUtil(400, "Invalid contact OTP", null);
        }
        ClientRegisterReqDTO dto = new ObjectMapper().readValue(tempData.getClientDataJson(),
                ClientRegisterReqDTO.class);

        UserEntity userEntity = setUserEntity(dto, new UserEntity(), tempData.getEncryptedPassword());
        UserEntity userSave = userRepo.save(userEntity);
        if (userSave != null) {
            ClientEntity clientEntity = setClientEntity(dto, new ClientEntity(), userEntity);
            ClientEntity clientSave = clientRepo.save(clientEntity);
            if (clientSave != null) {
                tempClientRepo.delete(tempData);
                return new ResponseUtil(200, "Client registered successfully", null);
            } else {
                throw new RuntimeException("User registration failed");
            }
        } else {
            throw new RuntimeException("Client registration failed");
        }
    }

    private TempClientEntity setTempClientEntity(ClientRegisterReqDTO dto, String encryptedPassword, String emailOtp,
            String contactOtp, String clientDataJson, TempClientEntity tempClientEntity) {
        tempClientEntity.setEmail(dto.getEmail());
        tempClientEntity.setEncryptedPassword(encryptedPassword);
        tempClientEntity.setEmailOtp(emailOtp);
        tempClientEntity.setContactOtp(contactOtp);
        tempClientEntity.setClientDataJson(clientDataJson);
        return tempClientEntity;
    }

    private UserEntity setUserEntity(ClientRegisterReqDTO clientRegisterReqDTO, UserEntity userEntity,
            String password) {
        userEntity.setEmail(clientRegisterReqDTO.getEmail());
        userEntity.setPassword(password);
        userEntity.setRole(clientRegisterReqDTO.getRoleId());
        userEntity.setStatus(clientRegisterReqDTO.getRoleId().getRoleId() == 3 ? 1 : 0);
        return userEntity;
    }

    private ClientEntity setClientEntity(ClientRegisterReqDTO clientRegisterReqDTO, ClientEntity clientEntity,
            UserEntity userEntity) {
        clientEntity.setUser(userEntity);
        clientEntity.setRole(clientRegisterReqDTO.getRoleId());
        clientEntity.setName(clientRegisterReqDTO.getName());
        clientEntity.setEmail(clientRegisterReqDTO.getEmail());
        clientEntity.setAddress(clientRegisterReqDTO.getAddress());
        clientEntity.setContact(clientRegisterReqDTO.getContact());
        clientEntity.setNic(clientRegisterReqDTO.getNic());
        clientEntity.setRegistrationNumber(clientRegisterReqDTO.getRegistrationNumber());
        clientEntity.setCertification(clientRegisterReqDTO.getCertification());
        clientEntity.setStatus(clientRegisterReqDTO.getRoleId().getRoleId() == 3 ? 1 : 0);
        return clientEntity;
    }

    @Override
    public ResponseUtil searchClientByUsername(String username) {
        ClientEntity clientExist = clientRepo.findClientByUsernameAndStatus(username, 1);
        if (clientExist != null) {
            ClientSearchResDTO clientSearchResDTO = setClientDTO(clientExist, new ClientSearchResDTO());
            if (clientSearchResDTO != null) {
                return new ResponseUtil(200, "Success", clientSearchResDTO);
            } else {
                throw new RuntimeException("Unsuccess");
            }
        } else {
            throw new RuntimeException("Client not found");
        }
    }

    private ClientSearchResDTO setClientDTO(ClientEntity clientEntity, ClientSearchResDTO clientSearchResDTO) {
        clientSearchResDTO.setName(clientEntity.getName());
        clientSearchResDTO.setEmail(clientEntity.getEmail());
        clientSearchResDTO.setAddress(clientEntity.getAddress());
        clientSearchResDTO.setContact(clientEntity.getContact());
        clientSearchResDTO.setRoleId(clientEntity.getRole().getRoleId());
        clientSearchResDTO.setNic(clientEntity.getNic());
        clientSearchResDTO.setRegistrationNumber(clientEntity.getRegistrationNumber());
        clientSearchResDTO.setCertification(clientEntity.getCertification());
        return clientSearchResDTO;
    }

    @Override
    public ResponseUtil updateClientProfile(ClientUpdateReqDTO clientUpdateReqDTO) throws Exception {
        ClientEntity clientEntity = clientRepo.findClientByEmail(clientUpdateReqDTO.getEmail());
        if (clientEntity != null) {
            clientEntity.setName(clientUpdateReqDTO.getName());
            clientEntity.setAddress(clientUpdateReqDTO.getAddress());
            ClientEntity clientUpdate = clientRepo.save(clientEntity);
            if (clientUpdate != null) {
                return new ResponseUtil(200, "Success", null);
            } else {
                throw new RuntimeException("Client update failed");
            }
        } else {
            throw new RuntimeException("Client not found");
        }
    }

    @Override
    public ResponseUtil verifyOrganization(int id, int status) {

        UserEntity userEntity = userRepo.findUsertById(id);
        if (userEntity == null) {
            throw new RuntimeException("User Id not found");
        }
        ClientEntity clientEntity = clientRepo.findClientByUserId(id);
        if (clientEntity == null) {
            throw new RuntimeException("Client Id not found");
        }
        if (status == 1) {
            userEntity.setStatus(1);
            UserEntity userVerify = userRepo.save(userEntity);
            if (userVerify != null) {
                clientEntity.setStatus(1);
                ClientEntity clientVerify = clientRepo.save(clientEntity);
                if (clientVerify != null) {
                    return new ResponseUtil(200, "Client verified successfully", null);
                } else {
                    throw new RuntimeException("Client verification failed");
                }
            } else {
                throw new RuntimeException("User verification failed");
            }
        } else if (status == 0) {
            clientRepo.delete(clientEntity);
            userRepo.delete(userEntity);
            return new ResponseUtil(200, "Client deleted successfully", null);
        }
        throw new IllegalArgumentException("Invalid status provided. Use 1 to verify or 0 to delete.");
    }

}
