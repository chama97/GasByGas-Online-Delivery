package com.lmu.gasbygas.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lmu.gasbygas.dto.request.ClientRegisterReqDTO;
import com.lmu.gasbygas.entity.ClientEntity;
import com.lmu.gasbygas.entity.UserEntity;
import com.lmu.gasbygas.repository.ClientRepo;
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
    private PasswordEncoder pwdEncoder;

    @Override
    public ResponseUtil registerNewClient(ClientRegisterReqDTO dto) throws Exception {
        String encryptedPassword = pwdEncoder.encode(dto.getPassword());
        
        ClientEntity clientExist = clientRepo.findClientByEmail(dto.getEmail());
        if (clientExist != null) {
            return new ResponseUtil(200, "This client is already registered. Please log in", null);
        }
        ClientEntity clientEntity = setClientEntity(dto, new ClientEntity());
        ClientEntity clientSave = clientRepo.save(clientEntity);
        if (clientSave != null) {
            UserEntity userEntity = setUserEntity(dto, new UserEntity(), clientEntity, encryptedPassword);
            UserEntity userSave = userRepo.save(userEntity);
            if (userSave != null) {
                return new ResponseUtil(200, "Client registered successfully", null);
            } else {
                throw new RuntimeException("User registration failed");
            }
        } else {
            throw new RuntimeException("Client registration failed");
        }
    }

    private ClientEntity setClientEntity(ClientRegisterReqDTO clientRegisterReqDTO, ClientEntity clientEntity) {
        clientEntity.setName(clientRegisterReqDTO.getName());
        clientEntity.setEmail(clientRegisterReqDTO.getEmail());
        clientEntity.setAddress(clientRegisterReqDTO.getAddress());
        clientEntity.setContact(clientRegisterReqDTO.getContact());
        clientEntity.setRoleId(clientRegisterReqDTO.getRoleId());
        clientEntity.setNic(clientRegisterReqDTO.getNic());
        clientEntity.setRegistrationNumber(clientRegisterReqDTO.getRegistrationNumber());
        clientEntity.setCertification(clientRegisterReqDTO.getCertification());
        clientEntity.setStatus(clientRegisterReqDTO.getRoleId() == 3 ? 1 : 0);
        return clientEntity;
    }

    private UserEntity setUserEntity(ClientRegisterReqDTO clientRegisterReqDTO, UserEntity userEntity,
            ClientEntity clientEntity, String password) {
        userEntity.setRegId("C-" + clientEntity.getUserId());
        userEntity.setName(clientRegisterReqDTO.getName());
        userEntity.setUsername(clientRegisterReqDTO.getEmail());
        userEntity.setPassword(password);
        userEntity.setRole(clientRegisterReqDTO.getRoleId());
        userEntity.setStatus(clientRegisterReqDTO.getRoleId() == 3 ? 1 : 0);
        return userEntity;
    }

    @Override
    public ResponseUtil searchClientByUsername(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchClientByUsername'");
    }

}
