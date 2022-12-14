package com.tindy.app.service.impl;

import com.tindy.app.dto.request.ParticipantRequest;
import com.tindy.app.dto.respone.ParticipantRespone;
import com.tindy.app.mapper.MapData;
import com.tindy.app.model.entity.Conversation;
import com.tindy.app.model.entity.Participant;
import com.tindy.app.model.entity.User;
import com.tindy.app.model.enums.ParticipantRole;
import com.tindy.app.model.enums.ParticipantSatus;
import com.tindy.app.model.enums.ParticipantType;
import com.tindy.app.repository.ConversationRepository;
import com.tindy.app.repository.ParticipantRepository;
import com.tindy.app.repository.UserRepository;
import com.tindy.app.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
//    @Override
//    public ParticipantRespone addParticipantSingle(ParticipantRequest participantRequest) {
//        Participant participant = MapData.mapOne(participantRequest, Participant.class);
//        Conversation conversation = conversationRepository.findById(participantRequest.getConversation().getId()).get();
//        User user = userRepository.findByPhone(participantRequest.getUser().getPhone()).orElseThrow(()-> new UsernameNotFoundException("Not found user!"));
//        conversation.setTitle(user.getFullName());
//        participant.setConversation(conversation);
//        participant.setUser(user);
//        participant.setType(ConversationType.SINGLE);
//        participant.setCreatedAt(new Date(System.currentTimeMillis()));
//        if(conversation.getCreator().getId().equals(user.getId())){
//            participant.setRole(ParticipantRole.ADMIN);
//        }else{
//            participant.setRole(ParticipantRole.MEM);
//        }
//        return MapData.mapOne(participantRepository.save(participant), ParticipantRespone.class);
//    }

    @Override
    public Object addParticipantGroup(ParticipantRequest participantRequest) {
        Conversation conversation = conversationRepository.findById(participantRequest.getConversationId()).get();
        List<ParticipantRespone> participantRespones = new ArrayList<>();
        if(participantRequest.getPhones().size() >= 1 ){
            for(String phone: participantRequest.getPhones()){
                User user = userRepository.findByPhone(phone).orElse(null);
                Participant temp = participantRepository.findParticipantByUserIdAndConversationId(user.getId(), conversation.getId()).orElse(null);

                if(temp == null){
                    Participant participant = new Participant();
                    participant.setConversation(conversation);
                    participant.setUser(user);

                    participant.setRole(ParticipantRole.MEM);

                    participant.setCreatedAt(new Date(System.currentTimeMillis()));
                    participant.setType(ParticipantType.GROUP);
                    participant.setStatus(ParticipantSatus.STABLE);
                    participant.setNickName(user.getFullName());
                    Participant participantSaved = participantRepository.save(participant);
                    participantRespones.add(MapData.mapOne(participantSaved, ParticipantRespone.class));
                }
            }
        }

        return participantRespones;
    }

    @Override
    public List<ParticipantRespone> getParticipant(Integer conversationId) {
        return MapData.mapList(participantRepository.getParticipantByConversationId(conversationId), ParticipantRespone.class);
    }

    @Override
    public Boolean removeParticipant(Integer userId, Integer participantId) {
        try {
            Participant admin = participantRepository.findById(userId).orElseThrow(()-> new UsernameNotFoundException("Participant not found"));
            if(admin.getRole().equals(ParticipantRole.ADMIN)){
                participantRepository.delete(participantRepository.findById(participantId).orElse(null));
                return true;
            }
            return false;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public Boolean outGroupConversation(Integer participantId) {
        try {
            participantRepository.delete(participantRepository.findById(participantId).orElseThrow(() -> new UsernameNotFoundException("Participant not found")));
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public ParticipantRespone grantPermission(Integer adminId, Integer participantId, String role) {
        Participant admin = participantRepository.findById(adminId).orElseThrow(() -> new UsernameNotFoundException("Not found admin"));
        if(admin.getRole().equals(ParticipantRole.ADMIN)){
            Participant participant = participantRepository.findById(participantId).orElseThrow(() -> new UsernameNotFoundException("Participant not found!"));
            participant.setRole(ParticipantRole.valueOf(role));
            return MapData.mapOne(participantRepository.save(participant),ParticipantRespone.class);
        }
        return null;
    }

    @Override
    public ParticipantRespone muteParticipant(Integer adminId, Integer participantId, String status) {
        Participant admin = participantRepository.findById(adminId).orElseThrow(() -> new UsernameNotFoundException("Not found participant"));
        if(admin.getRole().equals(ParticipantRole.ADMIN)){
            Participant participant = participantRepository.findById(participantId).orElseThrow(() -> new UsernameNotFoundException("Participant not found"));
            participant.setStatus(ParticipantSatus.valueOf(status));
            return MapData.mapOne(participantRepository.save(participant),ParticipantRespone.class);
        }
        return null;
    }
}
