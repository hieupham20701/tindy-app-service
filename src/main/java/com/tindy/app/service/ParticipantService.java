package com.tindy.app.service;

import com.tindy.app.dto.request.ParticipantRequest;
import com.tindy.app.dto.respone.ParticipantRespone;

import java.util.List;

public interface ParticipantService {

//    ParticipantRespone addParticipantSingle(ParticipantRequest participantRequest);
    Object addParticipantGroup(ParticipantRequest participantRequest);
    List<ParticipantRespone> getParticipant(Integer conversationId);
}